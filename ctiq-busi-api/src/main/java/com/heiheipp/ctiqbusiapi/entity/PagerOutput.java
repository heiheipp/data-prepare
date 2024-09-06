package com.heiheipp.ctiqbusiapi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagerOutput<T> {

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 页码
     */
    private int pageNum;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 总条数
     */
    private int total;

    /**
     * 当前页的数据
     */
    private T data;
}
