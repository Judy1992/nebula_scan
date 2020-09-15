package com.example.graph;

import com.example.graph.domain.ClientDomain;
import com.example.graph.domain.VertexDomain;
import com.example.graph.service.drawgraph.DrawGraph;
import com.example.graph.service.GetVertexEdge;
import com.example.graph.domain.EdgeDomain;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


@SpringBootApplication
@Slf4j
public class GraphApplication {

				public static void main(String[] args) {
					String host= "xxxx";
					int port = 45510;
					String filename = "graph_file_name";

					try {
						GetVertexEdge getVertexEdge = new GetVertexEdge();
						ClientDomain clientDomain = getVertexEdge.initClient(host, port);
						List<EdgeDomain> edgeDomains = getVertexEdge.getEdges(clientDomain);
						List<VertexDomain> vertices = getVertexEdge.getVertice(clientDomain);

						DrawGraph drawGraph = new DrawGraph();
						Graph graph = drawGraph.initGraph(vertices, edgeDomains);
						drawGraph.drawSpanning(graph, filename);
					} catch (Exception e) {
						e.printStackTrace();
		}
	}
}
