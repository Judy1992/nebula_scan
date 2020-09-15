package com.example.graph.service;

import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.DefaultWeightedEdge;

@Setter
@Getter
public class MyEdge extends DefaultWeightedEdge{
    public String getSrc(){
       return super.getSource().toString();
    }

    public String getDst(){
        return super.getTarget().toString();
    }

    public double getWeight(){
        return super.getWeight();
    }
}
