package com.example.graph.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public class VertexDomain {
    public static String STOCK_ID= "stockid";
    public static String NAME = "name";
    public static String INDUSTRY = "industry";


    Long vid;
    String stockid;
    String name;
    String industry;
}
