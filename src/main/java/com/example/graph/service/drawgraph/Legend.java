package com.example.graph.service.drawgraph;

import com.example.graph.domain.VertexDomain;
import com.example.graph.service.MyEdge;
import com.example.graph.service.MyEdgeComp;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.example.graph.service.drawgraph.MyColor.COLOR_MAP;

@Slf4j
public class Legend {

    public static mxStylesheet setMsStylesheet(mxStylesheet originaStylesheet){
        Map<String, Object> vertexStyle = originaStylesheet.getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_FONTSIZE, 25);
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        originaStylesheet.setDefaultVertexStyle(vertexStyle);

        Map<String, Object> edgeStyle = originaStylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        originaStylesheet.setDefaultEdgeStyle(edgeStyle);
        return originaStylesheet;
    }

    public static void drawGraph(Set<MyEdge> edges, String filename, Map<String, VertexDomain> idVertexMap) throws IOException {
        // Creates graph with model
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        // set style
        graph.getModel().beginUpdate();
        mxStylesheet myStylesheet =  graph.getStylesheet();
        graph.setStylesheet(setMsStylesheet(myStylesheet));

        Map<String, Object> idMap = new HashMap<>();
        ArrayList<MyEdge> edgeList = new ArrayList(edges);

        Collections.sort(edgeList, new MyEdgeComp());

        log.info(edgeList.toString());

        for (MyEdge edge : edgeList) {
            Object src, dst;
            if (!idMap.containsKey(edge.getSrc())) {
                VertexDomain srcNode = idVertexMap.get(edge.getSrc());
                String nodeColor = COLOR_MAP.get(srcNode.getIndustry());

                src = graph.insertVertex(parent, null, srcNode.getName(), 0, 0, 105, 50, "fillColor=" + nodeColor);
                idMap.put(edge.getSrc(), src);
            } else {
                src = idMap.get(edge.getSrc());
            }

            if (!idMap.containsKey(edge.getDst())) {
                VertexDomain dstNode = idVertexMap.get(edge.getDst());

                String nodeColor = COLOR_MAP.get(dstNode.getIndustry());

                dst = graph.insertVertex(parent, null, dstNode.getName(), 0, 0, 105, 50, "fillColor=" + nodeColor);
                idMap.put(edge.getDst(), dst);
            } else {
                dst = idMap.get(edge.getDst());
            }
            graph.insertEdge(parent, null, "", src, dst);
        }

        log.info("vertice " + idMap.size());

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        layout.setMaxIterations(2000);
        //layout.setMinDistanceLimit(10D);
        layout.execute(parent);


//        mxOrganicLayout layout = new mxOrganicLayout(graph);
//        layout.setOptimizeEdgeLength(true);
//        layout.setMaxIterations(2000);
//        layout.setMaxDistanceLimit(300D);
//        layout.setOptimizeEdgeCrossing(true);
//        layout.execute(parent);


        graph.getModel().endUpdate();


        // Creates an image than can be saved using ImageIO
        BufferedImage image = createBufferedImage(graph, null, 1, Color.WHITE,
                true, null);
//		/*BufferedImage image = mxCellRenderer.createBufferedImage(graph, null,
//				1, Color.WHITE, true, null);*/
//

        // For the sake of this example we display the image in a window
        // Save as JPEG
        File file = new File(filename);
        ImageIO.write(image, "JPEG", file);

    }

    public static BufferedImage createBufferedImage(mxGraph graph,
                                                    Object[] cells, double scale, final Color background,
                                                    final boolean antiAlias, mxRectangle clip) {
        mxImageCanvas canvas = (mxImageCanvas) mxCellRenderer.drawCells(graph,
                cells, scale, clip, new CanvasFactory() {
                    public mxICanvas createCanvas(int width, int height) {
                        int border = 10;
                        int dx = 0;
                        int dy = 60;

                        mxImageCanvas canvas = new mxImageCanvas(
                                new mxGraphics2DCanvas(), width + dx + border,
                                height + dy + border, background, antiAlias);
                        canvas.getGraphicsCanvas().getGraphics().translate(dx, dy);

                        return canvas;
                    }

                });

        return (canvas != null) ? canvas.destroy() : null;
    }
}
