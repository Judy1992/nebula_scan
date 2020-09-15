package com.example.graph.service;

import java.util.Comparator;

public class MyEdgeComp implements Comparator<MyEdge> {
    @Override
    public int compare(MyEdge o1, MyEdge o2) {
        if(Integer.parseInt(o1.getSrc()) < Integer.parseInt(o2.getSrc())) {
            return -1;
        } else if (Integer.parseInt(o1.getSrc()) > Integer.parseInt(o2.getSrc())) {
            return 1;
        } else {
            return Integer.valueOf(o1.getDst()).compareTo(Integer.valueOf(o2.getDst()));
        }
    }
}
