package com.heiheipp.ctiqbusidemo.controller;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.ImmutableMap;
import com.heiheipp.ctiqbusiapi.entity.PagerInput;
import com.heiheipp.ctiqbusiapi.entity.PagerOutput;
import com.heiheipp.ctiqbusiapi.entity.TestDetail;
import com.heiheipp.ctiqbusiapi.entity.TestDetailAgg;
import com.heiheipp.ctiqbusiapi.mapper.TestDetailMapper;
import com.heiheipp.ctiqbusicommon.context.DynamicDataSourceContextHolder;
import com.heiheipp.ctiqbusidemo.vo.ResponseResult;
import com.heiheipp.routecomponent.constant.Constants;
import com.heiheipp.routecomponent.engine.DBServiceEngine;
import com.heiheipp.routecomponent.context.BusinessExecutionResult;
import com.heiheipp.routecomponent.engine.ICallBack;
import com.heiheipp.routecomponent.route.context.BusinessRouteRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testDetail")
@Slf4j
public class TestDetailController {

    @Autowired
    TestDetailMapper testDetailMapper;

    /**
     * 分页查询1
     */
    @PostMapping("/selectPage1")
    public Object selectPage1(PagerInput input) {
        //默认查询db1
        input.setLowerDate(StringUtils.isNotBlank(input.getBeginDateStr()) ? DateUtil.parse(input.getBeginDateStr()) : null);
        input.setUpperDate(StringUtils.isNotBlank(input.getEndDateStr()) ? DateUtil.parse(input.getEndDateStr()) : null);
        input.setOffset(input.getOffset() - 1);
//        log.info("Lower date is [{}], upper date is [{}]", input.getLowerDate().toString(), input.getUpperDate().toString());

        List<TestDetail> resultDatas = testDetailMapper.selectByPage(input);
        int count = testDetailMapper.selectPageCount(input);

        //返回数据
        PagerOutput<List<TestDetail>> result = new PagerOutput<>();
        result.setData(resultDatas);
        result.setTotal(count);
        result.setPageNum(input.getOffset());
        result.setPageSize(input.getPageSize());

        return ResponseResult.success(result);
    }

    /**
     * 分页查询2
     */
    @PostMapping("/selectPage2")
    public Object selectPage2(PagerInput input) {
        // 默认查询db1
        input.setLowerDate(DateUtil.parse(input.getBeginDateStr()));
        input.setUpperDate(DateUtil.parse(input.getEndDateStr()));
        log.info("Lower date is [{}], upper date is [{}]", input.getLowerDate().toString(), input.getUpperDate().toString());

        // 1-初始化请求对象
        BusinessRouteRequest request = BusinessRouteRequest.newBuilder()
                .lowerDate(input.getBeginDateStr())
                .upperDate(input.getEndDateStr())
                .busiType(Constants.BusiType.QUERY_DETAIL_WITH_ORDERBY_LIMIT)
                .pagingOffset(input.getOffset())
                .pageSize(input.getPageSize())
                .isSupportJumpToAnyPage(false)
                .isNeedOrderBy(true)
                .mybatisMapper(testDetailMapper)
                .mapperMethodParameterMap(ImmutableMap.of("selectPageCount", input, "selectByPage", input))
                .parameterSetMethods(ImmutableMap.of(Constants.SQLConditionType.START_DATE, "setLowerDate",
                        Constants.SQLConditionType.END_DATE, "setUpperDate",
                        Constants.SQLConditionType.LIMIT_OFFSET, "setOffset",
                        Constants.SQLConditionType.LIMIT_PAGE_SIZE, "setPageSize"))
                .build();

        // 2-调用路由组件
        BusinessExecutionResult<List<TestDetail>> businessExecutionResult = null;
        try {
            businessExecutionResult = DBServiceEngine.getInstance().executeWithCallback(request, new ICallBack() {
                @Override
                public List<TestDetail> callback() {
                    input.setOffset(input.getOffset() - 1);
                    return testDetailMapper.selectByPage(input);
                }
            });
        } catch (Exception e) {
            log.error("DBServiceEngine execute failed.");
            e.printStackTrace();
        }

        // 返回数据
        return ResponseResult.success(businessExecutionResult);
    }
}
