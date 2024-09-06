package com.heiheipp.ctiqbusiapi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PagerInput {

    private int unnTrno;

    private String txnAccno;

    private String beginDateStr;

    private String endDateStr;

    private Date lowerDate;

    private Date upperDate;

    private Integer offset;

    private Integer pageSize;
}
