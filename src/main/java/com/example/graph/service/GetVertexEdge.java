package com.example.graph.service;

import com.example.graph.domain.ClientDomain;
import com.example.graph.domain.EdgeDomain;
import com.example.graph.domain.VertexDomain;
import com.facebook.thrift.TException;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.client.storage.processor.ScanEdgeProcessor;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.ScanVertexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GetVertexEdge {

    static String SPACENAME = "test2018";
    static String TAGNAME = "company";
    static String EDGETYPE = "relation";
    static String VERTEXID_KEY = "_vertexId";

    @Bean
    public ClientDomain initClient(String metaHost, int metaPort) throws TException {
        MetaClientImpl metaClientImpl = new MetaClientImpl(metaHost, metaPort);
        metaClientImpl.connect();
        StorageClient storageClient = new StorageClientImpl(metaClientImpl);

        ClientDomain clientDomain = new ClientDomain();
        clientDomain.setMetaClient(metaClientImpl);
        clientDomain.setStorageClient(storageClient);
        return clientDomain;
    }

    @Bean
    public List<VertexDomain> getVertice(ClientDomain clientDomain) throws IOException, InterruptedException {

        Processor processor  = new ScanVertexProcessor(clientDomain.getMetaClient());

        Map<String, List<String>> returnCols = new HashMap<>();

        List<String> propNames = new ArrayList<>();
        propNames.add(VertexDomain.STOCK_ID);
        propNames.add(VertexDomain.NAME);
        propNames.add(VertexDomain.INDUSTRY);
        returnCols.put(TAGNAME, propNames);


        List<Row> results = new ArrayList<>();

        Iterator<ScanVertexResponse> iterator =
                clientDomain.getStorageClient().scanVertex(SPACENAME, returnCols, false, 200, 0L, Long.MAX_VALUE);
        while (iterator.hasNext()) {
            ScanVertexResponse response = iterator.next();
            if (response == null) {
                log.error("Error occurs while scan vertex");
                break;
            }

            Result result =  processor.process(SPACENAME, response);
            results.addAll(result.getRows(TAGNAME));
        }


        return formatVertexOutput(results);
    }


    @Bean
    List<VertexDomain> formatVertexOutput(List<Row> results) throws InterruptedException {
        List<VertexDomain> vertexDomains = new ArrayList<>();

        for (Row result: results){
            VertexDomain vertexDomain = new VertexDomain();

            // get vid
            for (Property property : result.getDefaultProperties()){
                String name = property.getName();
                if (VERTEXID_KEY == name){
                    vertexDomain.setVid(property.getValueAsLong());
                }
            }

            for (Property property : result.getProperties()) {
                if (VertexDomain.NAME.equals(property.getName())) {
                    vertexDomain.setName(property.getValueAsString());
                }else if (VertexDomain.STOCK_ID.equals(property.getName())){
                    vertexDomain.setStockid(property.getValueAsString());
                } else if (VertexDomain.INDUSTRY.equals(property.getName())){
                    vertexDomain.setIndustry(property.getValueAsString());
                }
            }
            vertexDomains.add(vertexDomain);
        }

        return vertexDomains;
    }

    @Bean
    public List<EdgeDomain> getEdges(ClientDomain clientDomain) throws IOException {

        Processor processor  = new ScanEdgeProcessor(clientDomain.getMetaClient());

        Map<String, List<String>> returnCols = new HashMap<>();

        List<String> propNames = new ArrayList<>();
        propNames.add(EdgeDomain.WEIGHT);
        returnCols.put(EDGETYPE, propNames);


        Iterator<ScanEdgeResponse> iterator =
                clientDomain.getStorageClient().scanEdge(SPACENAME, returnCols, false, 2000, 0L, Long.MAX_VALUE);
        ScanEdgeResponse result = iterator.next();

        // Convert the response to a Result
        Result processResult1 = processor.process(SPACENAME, result);
        List<EdgeDomain> edgeDomains = formatEdgeOutput(processResult1);
        while (iterator.hasNext()) {
            ScanEdgeResponse response = iterator.next();
            if (response == null) {
                log.error("Error occurs while scan edge");
                break;
            }
            Result processResult2 = processor.process(SPACENAME, response);
            edgeDomains.addAll(formatEdgeOutput(processResult2));
        }

        return edgeDomains;
    }


    // FormatEdgeOutput
    private List<EdgeDomain> formatEdgeOutput(Result result) {
        List<EdgeDomain> edgeDomains = new ArrayList<>();

        // Get the corresponding rows by edgetype name
        List<Row> edgeRows = result.getRows(EDGETYPE);
        for (Row row : edgeRows) {
            EdgeDomain edgeDomain = new EdgeDomain();
            // For an edge, we have 3 default properties: src, type, dst,
            edgeDomain.setSrcid((Long) row.getDefaultProperties()[0].getValue());
            edgeDomain.setDstid((Long) row.getDefaultProperties()[2].getValue());
            edgeDomain.setWeight((Double) row.getProperties()[0].getValue());
            // Get the specified property: "weight"
            edgeDomains.add(edgeDomain);
        }
        return edgeDomains;
    }
}
