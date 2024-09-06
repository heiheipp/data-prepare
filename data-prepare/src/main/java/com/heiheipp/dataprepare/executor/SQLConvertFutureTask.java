package com.heiheipp.dataprepare.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.heiheipp.common.config.ConfigModel;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.service.SQLConvertService;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.dataprepare.model.SQLConvertConfigModel;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className SQLConvertFutureTask
 * @desc TODO
 * @date 2024/3/7
 */
@Slf4j
public class SQLConvertFutureTask extends AbstractFutureTask<String> {

    private int targetType;

    private TaskLogService taskLogService;

    private SQLConvertService sqlConvertService;

    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    private String subThreadId;

    private AtomicLong processedNums = new AtomicLong(0L);

    private SQLConvertConfigModel configModel;

    /**
     * 构造函数
     * @param parentThreadId
     * @param threadOrder
     * @param configModel
     */
    public SQLConvertFutureTask(long parentThreadId, int threadOrder, ConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.sqlConvertService = SpringContextUtil.getBean(SQLConvertService.class);
        this.configModel = (SQLConvertConfigModel) configModel;
    }

    /**
     * 任务执行方法
     *
     * @return
     */
    @Override
    protected String submit(long subThreadId) {
        String result = "";

        this.subThreadId = String.valueOf(subThreadId);
        long startTime = System.currentTimeMillis();

        log.info("Sub thread[{}] start, .",
                this.subThreadId);

        /**
         * 执行流程包括：
         * 1. 识别需要扫描的 xml mapper 文件列表，通过 PathMatchingResourcePatternResolver 的
         *      findPathMatchingResources 方法实现，并转换为 Resource 对象
         * 2. 循环处理每一个 Resource：
         * 2.1 创建 XMLMapperBuilder 类，并通过 parse 方法解析出 select|insert|update|delete 语句对应的 XNode，
         *      并循环处理每个 XNode：
         * 2.1.1 SQL 解析模块，解析 XNode 中的 SQL 语句，通过 XMLStatementBuilder 类，得到 SqlSource 对象
         * 2.1.2 规则校验模块，针对每条 SQL，按照提前规则好的固定规则以及可临时制定的自定义规则对 SQL 语句进行不兼容项校验。
         *      若存在且可修改，则标记为可修改，若没有则标记为需手动调整；
         *      若不存在，则标记为无需修改；
         *      混合场景则按照最坏情况标记
         * 2.2 结果输出模块，对涉及可修改的 mapper 重新生成修改后的 xml，其余情况均在结果清单中提示
         */
        // 1. 数据校验
        if (!isConfigurationValid()) {
            log.info("Sub thread {} finish, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);
            // 计数器操作
            XxxxDataPrepareServiceImpl.getCountDownLatch().countDown();

            return result;
        }

        // 2. 初始化需反射的对象成员变量
        Class cls_dynamicSqlSource, cls_rawSqlSource, cls_mixedSqlNode, cls_textSqlNode, cls_staticTextSqlNode, cls_staticSqlSource;
        Field rootSqlNode, contents, text_textSqlNode, text_staticTextSqlNode, f_sqlSource, f_sql;
        try {
            cls_dynamicSqlSource = Class.forName("org.apache.ibatis.scripting.xmltags.DynamicSqlSource");
            rootSqlNode = cls_dynamicSqlSource.getDeclaredField("rootSqlNode");
            rootSqlNode.setAccessible(true);

            cls_mixedSqlNode = Class.forName("org.apache.ibatis.scripting.xmltags.MixedSqlNode");
            contents = cls_mixedSqlNode.getDeclaredField("contents");
            contents.setAccessible(true);

            cls_textSqlNode = Class.forName("org.apache.ibatis.scripting.xmltags.TextSqlNode");
            text_textSqlNode = cls_textSqlNode.getDeclaredField("text");
            text_textSqlNode.setAccessible(true);

            cls_staticTextSqlNode = Class.forName("org.apache.ibatis.scripting.xmltags.StaticTextSqlNode");
            text_staticTextSqlNode = cls_staticTextSqlNode.getDeclaredField("text");
            text_staticTextSqlNode.setAccessible(true);

            cls_rawSqlSource = Class.forName("org.apache.ibatis.scripting.defaults.RawSqlSource");
            f_sqlSource = cls_rawSqlSource.getDeclaredField("sqlSource");
            f_sqlSource.setAccessible(true);

            cls_staticSqlSource = Class.forName("org.apache.ibatis.builder.StaticSqlSource");
            f_sql = cls_staticSqlSource.getDeclaredField("sql");
            f_sql.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Sub thread {} finish, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);
            // 计数器操作
            XxxxDataPrepareServiceImpl.getCountDownLatch().countDown();

            return result;
        }

        // 3. 遍历mapper文件
        Map<String, String> totalResultMap = new HashMap<>();
        for (File file : FileUtil.loopFiles(this.configModel.getSourceFileLocation())) {
            log.info("当前文件为[{}]", file.getName());

            try (InputStream inputStream = IoUtil.toStream(file)) {
                // 3. 解析文件
                Configuration configuration = new Configuration();
                XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, file.getName(), configuration.getSqlFragments());
                builder.parse();

                // 4. 遍历SQL
                Map<String, String> resultMap = new HashMap<>();
                for (Object value : builder.getConfiguration().getMappedStatements()) {
                    if (value instanceof MappedStatement) {
                        MappedStatement mappedStatement = (MappedStatement) value;

                        if (!resultMap.containsKey(mappedStatement.getId())) {
                            Map<String, String> sqlMap = new HashMap<>();
                            StringBuilder sb = new StringBuilder();
//                            sb.append(StringUtils.rightPad(mappedStatement.getId(), 100, " "))
//                                    .append(this.configModel.getDelimiter());

                            try {
                                SqlSource sqlSource = mappedStatement.getSqlSource();

                                // 5. 按SqlSource类型做不同处理
                                if (sqlSource instanceof DynamicSqlSource) {
                                    // 5.1 DynamicSqlSource分支，获取rootSqlNode对象下的contents列表，并遍历该列表拼装sql

                                    for (SqlNode subSqlNode : (List<SqlNode>) contents.get(rootSqlNode.get(sqlSource))) {
                                        if (subSqlNode instanceof TextSqlNode) {
                                            sb.append(((String) text_textSqlNode.get(subSqlNode)).replaceAll("\\n", " "));
                                        } else if (subSqlNode instanceof StaticTextSqlNode) {
                                            sb.append(((String) text_staticTextSqlNode.get(subSqlNode)).replaceAll("\\n", " "));
                                        } else if (subSqlNode instanceof ForEachSqlNode) {
                                            // foreach语法直接拼接"(?)"
                                            sb.append(" (?) ");
                                        } else if (subSqlNode instanceof IfSqlNode) {
                                            Field if_contents = Class.forName("org.apache.ibatis.scripting.xmltags.IfSqlNode").
                                                    getDeclaredField("contents");
                                            if_contents.setAccessible(true);
                                            for (SqlNode if_subSqlNode : (List<SqlNode>) contents.get(if_contents.get(subSqlNode))) {
                                                if (if_subSqlNode instanceof StaticTextSqlNode) {
                                                    sb.append(((String) text_staticTextSqlNode.get(if_subSqlNode)).replaceAll("\\n", " "));
                                                } else if (if_subSqlNode instanceof TextSqlNode) {
                                                    sb.append(((String) text_textSqlNode.get(if_subSqlNode)).replaceAll("\\n", " "));
                                                }
                                            }
                                        } else {
                                            sb.delete(0, sb.length());
                                            sb.append("手动处理");
                                            break;
                                        }
                                    }
                                } else if (sqlSource instanceof RawSqlSource) {
                                    // 5.2 RawSqlSource分支，获取sqlSource对象下的sql
                                    sb.append(((String) f_sql.get(f_sqlSource.get(sqlSource))).replaceAll("\\n", " "));
                                }

                                // 6. 汇总结果
                                sqlMap.put(mappedStatement.getId(), sb.toString());
                                resultMap.put(mappedStatement.getId(), sb.toString());
                                log.debug(sb.toString());

                                // 7.1 SQL级文件输出
                                if (this.configModel.getTargetFileSplitType().equalsIgnoreCase("sql")) {
                                    log.info(outToTargetFile(sqlMap,
                                            this.configModel.getTargetFileLocation() +
                                                    File.separator + file.getName() + "-" +
                                                    mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1) + "-out"));
                                }
                            } catch (Exception e) {
                                // ignore
                                log.error("文件[{}]，处理失败[{}]", mappedStatement.getId(), e.getMessage());
                            }
                        }
                    }
                }

                // 7.2 mapper级文件输出
                if (this.configModel.getTargetFileSplitType().equalsIgnoreCase("mapper")) {
                    log.info(outToTargetFile(resultMap, this.configModel.getTargetFileLocation() +
                            File.separator + file.getName() + "-out"));
                } else if (this.configModel.getTargetFileSplitType().equalsIgnoreCase("none")) {
                    totalResultMap.putAll(resultMap);
                }
            } catch (Exception e) {
                log.error("文件[{}]读取失败[{}]", file.getName(), e.getMessage());
            }
        }

        // 7.3 全量输出到一个文件
        if (this.configModel.getTargetFileSplitType().equalsIgnoreCase("none")) {
            log.info(outToTargetFile(totalResultMap, this.configModel.getTargetFileLocation() +
                    File.separator + "allFiles" + "-out"));
        }

        log.info("Sub thread {} finish, and execution time is {}ms.", this.subThreadId,
                System.currentTimeMillis() - startTime);
        // 计数器操作
        XxxxDataPrepareServiceImpl.getCountDownLatch().countDown();

        return result;
    }

    private boolean isConfigurationValid() {
        // sourceType 必须为 mybatis
        if (!this.configModel.getSourceType().equalsIgnoreCase("mybatis")) {
            log.error("sourceType必须是mybatis");
            return false;
        }

        // sourceFileLocation和targetFileLocation不能相同
        if (StringUtils.isBlank(this.configModel.getSourceFileLocation()) ||
                StringUtils.isBlank(this.configModel.getTargetFileLocation()) ||
                this.configModel.getSourceFileLocation().equals(this.configModel.getTargetFileLocation())) {
            log.error("sourceFileLocation和targetFileLocation不能为空，且不能相同");
            return false;
        }

        // targetFileSplitType只能是none、mapper、sql其中之一
        if (!(this.configModel.getTargetFileSplitType().equalsIgnoreCase("none") ||
            this.configModel.getTargetFileSplitType().equalsIgnoreCase("mapper") ||
                this.configModel.getTargetFileSplitType().equalsIgnoreCase("sql"))) {
            log.error("targetFileSplitType只能是none、mapper、sql其中之一");
            return false;
        }

        return true;
    }

    private String outToTargetFile (Map<String, String> request, String fileName) {
        String prefix = "文件[" + fileName + "]写入";
        try {
            if (request.size() > 0) {
                if (FileUtil.exist(fileName)) {
                    return prefix + "失败：该文件已存在，暂不允许覆盖";
                }

                FileUtil.writeMap(request, FileUtil.file(fileName), Charset.defaultCharset(),
                        this.configModel.getDelimiter(), true);
            }
        } catch (IORuntimeException e) {
            return prefix + "失败：" + e.getMessage();
        }

        return prefix + "成功";
    }
}
