package com.example.graph.service.drawgraph;

import com.example.graph.domain.EdgeDomain;
import com.example.graph.domain.VertexDomain;
import com.example.graph.service.MyEdge;
import com.example.graph.service.drawgraph.Legend;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;


import java.io.IOException;
import java.util.*;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class DrawGraph {

    public static Map<String, VertexDomain> stockIdToName = new HashMap<>();

    public Graph initGraph(List<VertexDomain> vertexDomainList,
                           List<EdgeDomain> edgeDomainList) {

        Graph<String, MyEdge> graph = GraphTypeBuilder
                .undirected()
                .weighted(true)
                .allowingMultipleEdges(true)
                .allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.createSupplier(MyEdge.class))
                .buildGraph();

        for (VertexDomain vertex : vertexDomainList){
            graph.addVertex(vertex.getVid().toString());
            stockIdToName.put(vertex.getVid().toString(), vertex);
        }

        for (EdgeDomain edgeDomain : edgeDomainList){

            if (edgeDomain.getSrcid() <= 129 && edgeDomain.getDstid() <= 129) {
                graph.addEdge(edgeDomain.getSrcid().toString(), edgeDomain.getDstid().toString());
                MyEdge newEdge = graph.getEdge(edgeDomain.getSrcid().toString(), edgeDomain.getDstid().toString());
                graph.setEdgeWeight(newEdge, edgeDomain.getWeight());
            }
        }

        return graph;
    }

    public void drawSpanning(Graph graph, String filename) throws IOException {

        SpanningTreeAlgorithm.SpanningTree pMST = new PrimMinimumSpanningTree(graph).getSpanningTree();
        Legend.drawGraph(pMST.getEdges(), filename, stockIdToName);
    }

}
