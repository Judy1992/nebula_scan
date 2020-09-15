package com.example.graph.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class EdgeDomain {
    public static String WEIGHT= "weight";

    Double weight;
    Long srcid;
    Long dstid;

}
