package com.heiheipp.ctiqbusidemo.controller;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.ImmutableMap;
import com.heiheipp.ctiqbusiapi.entity.PagerInput;
import com.heiheipp.ctiqbusiapi.entity.PagerOutput;
import com.heiheipp.ctiqbusiapi.entity.TestDetailAgg;
import com.heiheipp.ctiqbusiapi.mapper.TestDetailAggMapper;
import com.heiheipp.ctiqbusicommon.context.DynamicDataSourceContextHolder;
import com.heiheipp.ctiqbusidemo.vo.ResponseResult;
import com.heiheipp.routecomponent.constant.Constants;
import com.heiheipp.routecomponent.context.BusinessExecutionResult;
import com.heiheipp.routecomponent.engine.DBServiceEngine;
import com.heiheipp.routecomponent.engine.ICallBack;
import com.heiheipp.routecomponent.route.context.BusinessRouteRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/testDetailAgg")
@Slf4j
public class TestDetailAggController {

    @Autowired
    TestDetailAggMapper testDetailAggMapper;

    /**
     * 聚合查询1
     */
    @PostMapping("/selectSumAmtByYear1")
    public Object selectSumAmtByYear1(PagerInput input) {
        //默认查询db1
        input.setLowerDate(StringUtils.isNotBlank(input.getBeginDateStr()) ? DateUtil.parse(input.getBeginDateStr()) : null);
        input.setUpperDate(StringUtils.isNotBlank(input.getEndDateStr()) ? DateUtil.parse(input.getEndDateStr()) : null);
//        log.info("Lower date is [{}], upper date is [{}]", input.getLowerDate().toString(), input.getUpperDate().toString());

        long startTime = System.currentTimeMillis();

        List<Map<String, Object>> resultDatas = testDetailAggMapper.selectSumAmtByYear(input);

        log.info("直接执行处理耗时[{}]ms", System.currentTimeMillis() - startTime);

        //返回数据
        PagerOutput<List<Map<String, Object>>> result = new PagerOutput<>();
        result.setData(resultDatas);

        return ResponseResult.success(result);
    }

    /**
     * 聚合查询2
     */
    @PostMapping("/selectSumAmtByYear2")
    public Object selectSumAmtByYear2(PagerInput input) {
        // 默认查询db1
        input.setLowerDate(StringUtils.isNotBlank(input.getBeginDateStr()) ? DateUtil.parse(input.getBeginDateStr()) : null);
        input.setUpperDate(StringUtils.isNotBlank(input.getEndDateStr()) ? DateUtil.parse(input.getEndDateStr()) : null);
        //log.info("Lower date is [{}], upper date is [{}]", input.getLowerDate().toString(), input.getUpperDate().toString());

        // 1-初始化请求对象
        BusinessRouteRequest request = BusinessRouteRequest.newBuilder()
                .lowerDate(input.getBeginDateStr())
                .upperDate(input.getEndDateStr())
                .busiType(Constants.BusiType.QUERY_AGGREGATION)
                .isSupportJumpToAnyPage(false)
                .groupByColumns(Stream.of("year", "TRAN_TYPE").collect(Collectors.toList()))
                .groupByResultColumns(Stream.of("sum").collect(Collectors.toList()))
                .mybatisMapper(testDetailAggMapper)
                .mapperMethodParameterMap(ImmutableMap.of("selectSumAmtByYear", input))
                .parameterSetMethods(ImmutableMap.of(Constants.SQLConditionType.START_DATE, "setLowerDate",
                        Constants.SQLConditionType.END_DATE, "setUpperDate"))
                .build();

        // 2-调用路由组件
        BusinessExecutionResult<List<Map<String, Object>>> businessExecutionResult = null;
        try {
            long startTime = System.currentTimeMillis();
            businessExecutionResult = DBServiceEngine.getInstance().executeWithCallback(request, new ICallBack() {
                @Override
                public List<Map<String, Object>> callback() {
                    log.info("执行业务回调方法");
                    return testDetailAggMapper.selectSumAmtByYear(input);
                }
            });
            log.info("路由组件整体处理耗时[{}]ms", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("DBServiceEngine execute failed.");
            e.printStackTrace();
        }

        // 返回数据
        return ResponseResult.success(businessExecutionResult);
    }

    /**
     * 单笔查询1
     */
    @PostMapping("/selectByUnnTrno1")
    public Object selectByUnnTrno1(PagerInput input) {
        //默认查询db1
        input.setLowerDate(StringUtils.isNotBlank(input.getBeginDateStr()) ? DateUtil.parse(input.getBeginDateStr()) : null);
        input.setUpperDate(StringUtils.isNotBlank(input.getEndDateStr()) ? DateUtil.parse(input.getEndDateStr()) : null);
        log.info("selectByUnnTrno1 service start, unnTrno is [{}]", input.getUnnTrno());

        long startTime = System.currentTimeMillis();
        TestDetailAgg resultData = testDetailAggMapper.selectByPrimaryKey(input);
        log.info("直接执行处理耗时[{}]ms", System.currentTimeMillis() - startTime);

        //返回数据
        PagerOutput<TestDetailAgg> result = new PagerOutput<>();
        result.setData(resultData);

        return ResponseResult.success(result);
    }

    /**
     * 单笔查询2
     */
    @PostMapping("/selectByUnnTrno2")
    public Object selectByUnnTrno2(PagerInput input) {
        //默认查询db1
        input.setLowerDate(StringUtils.isNotBlank(input.getBeginDateStr()) ? DateUtil.parse(input.getBeginDateStr()) : null);
        input.setUpperDate(StringUtils.isNotBlank(input.getEndDateStr()) ? DateUtil.parse(input.getEndDateStr()) : null);
        log.info("selectByUnnTrno1 service start, unnTrno is [{}]", input.getUnnTrno());

        // 1-初始化请求对象
        BusinessRouteRequest request = BusinessRouteRequest.newBuilder()
                .lowerDate(input.getBeginDateStr())
                .upperDate(input.getEndDateStr())
                .busiType(Constants.BusiType.QUERY_SINGLEROW)
                .isSupportJumpToAnyPage(false)
                .mybatisMapper(testDetailAggMapper)
                .mapperMethodParameterMap(ImmutableMap.of("selectByPrimaryKey", input))
                .parameterSetMethods(ImmutableMap.of(Constants.SQLConditionType.START_DATE, "setLowerDate",
                        Constants.SQLConditionType.END_DATE, "setUpperDate"))
                .build();

        // 2-调用路由组件
        BusinessExecutionResult<TestDetailAgg> businessExecutionResult = null;
        try {
            long startTime = System.currentTimeMillis();
            businessExecutionResult = DBServiceEngine.getInstance().executeWithCallback(request, new ICallBack() {
                @Override
                public TestDetailAgg callback() {
                    log.info("执行业务回调方法");
                    return testDetailAggMapper.selectByPrimaryKey(input);
                }
            });
            log.info("路由组件整体处理耗时[{}]ms", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("DBServiceEngine execute failed.");
            e.printStackTrace();
        }

        // 返回数据
        return ResponseResult.success(businessExecutionResult);
    }
}
