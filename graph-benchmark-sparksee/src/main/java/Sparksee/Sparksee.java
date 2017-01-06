/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sparksee;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.sparksee.SparkseeGraph;
import com.tinkerpop.blueprints.util.StringFactory;
import edu.ufam.engcomp.graph.benchmark.IBechmarkGraph;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author adminuser
 */
public class Sparksee implements IBechmarkGraph {

        
	private static final Logger log = Logger.getAnonymousLogger();
//	private static final String PROP_WEIGHT = "WEIGHT";
//        private static final String ATTR_LABEL = "ARC";
//	private static final String PROP_ID_NODE = "ID_NODE";

	private int countNodes = 0;
        KeyIndexableGraph graph;

    public Sparksee(Logger fileLog) {
     
    }
    
    public boolean conectarRemote(String ip) {
        return false;
    }

    public boolean conectarLocal(String localConnectionString) {
        graph = new SparkseeGraph(localConnectionString);
        
        if(graph == null)
            return false;
        else
        {
            ((SparkseeGraph)graph).typeScope.set(Boolean.TRUE);
            return true;
        }
    }
    
    
    public boolean checkKeys() {
        return true;
    }

    public void InitKeys() {
        if (graph!=null) {

            // NODE LABEL
            ((SparkseeGraph)graph).label.set(NODE_LABEL);
            
            // CREATE INDEX FOR ID NODE
            Set<String> vertexKeys = ((SparkseeGraph)graph).getIndexedKeys(Vertex.class);
            if(!vertexKeys.contains(PROP_ID_NODE))
                ((SparkseeGraph)graph).createKeyIndex(PROP_ID_NODE, Vertex.class);
            
            // EDGE LABEL
            ((SparkseeGraph)graph).label.set(ARC_LABEL);
            
            // CREATE INDEX FOR WEIGHT
            vertexKeys = graph.getIndexedKeys(Edge.class);
            if(!vertexKeys.contains(PROP_WEIGHT))
                ((SparkseeGraph)graph).createKeyIndex(PROP_WEIGHT, Edge.class);
        }
    }

    
    
    public boolean createEdge(long src, long tar, long weight) {
        Vertex tail = null;
        Iterable<Vertex> verticesIterator = null;
        ((SparkseeGraph)graph).label.set(NODE_LABEL);    
        
        // TAIL //////////////////////////////////////////////////////
        try {
            verticesIterator = graph.getVertices(PROP_ID_NODE, ((Long)src).toString());
            
            if (verticesIterator.iterator().hasNext()) {
                tail = verticesIterator.iterator().next();
            }

        } catch (Exception e) {
            tail = graph.addVertex(null);
            tail.setProperty(PROP_ID_NODE, ((Long)src).toString());
            String temp = tail.getProperty(PROP_ID_NODE);
            countNodes++;
        }
        
        if (tail == null) {
            tail = graph.addVertex(null);
            tail.setProperty(PROP_ID_NODE, ((Long)src).toString());
            countNodes++;
        }
        
        
        // HEAD /////////////////////////////////////////////////////
        Vertex head = null;
        try {
            verticesIterator = graph.getVertices(PROP_ID_NODE, ((Long)tar).toString());
            if (verticesIterator.iterator().hasNext()) {
                head = verticesIterator.iterator().next();
            }
        } catch (Exception e) {
            head = graph.addVertex(null);
            head.setProperty(PROP_ID_NODE, ((Long)tar).toString());
            String temp = head.getProperty(PROP_ID_NODE);
            countNodes++;
        }
        
        if (head == null) {
            head = graph.addVertex(null);
            head.setProperty(PROP_ID_NODE, ((Long)tar).toString());
            countNodes++;
        }
         
        // EDGE ////////////////////////////////////////////////////
         Iterable<Edge> edgesIterator = null;
        ((SparkseeGraph) graph).label.set(ARC_LABEL);
        Edge edge = null;
        try {
            edgesIterator = graph.getEdges(PROP_WEIGHT, ((Long) weight).toString());
            
            if (edgesIterator.iterator().hasNext()) {
                edge = edgesIterator.iterator().next();
            }
        } catch (Exception ex) {
            edge = head.addEdge(((Long) weight).toString(), tail);
            edge.setProperty(PROP_WEIGHT, ((Long) weight).toString());
        }
        
        if(edge == null){
            edge = head.addEdge(((Long) weight).toString(), tail);
            edge.setProperty(PROP_WEIGHT, ((Long) weight).toString());
        }
        ((SparkseeGraph) graph).commit();
        
        return true;
    }

    public void commit() {

    }

    public void desconectar() {
        ((SparkseeGraph)graph).typeScope.set(Boolean.FALSE);
        this.graph.shutdown();
    }

    public int getNodesCreated() {
        return countNodes;
    }

    public Iterator<Vertex> getAllVertices() {
        ((SparkseeGraph)graph).label.set(NODE_LABEL);
        return graph.getVertices().iterator();
    }

    public void rollback() {
        ((SparkseeGraph)graph).rollback();
    }

    public Edge getEdgeById(Object id) {
        return graph.getEdge(id);
    }

    public Vertex getNodeById(Object id) {
        return graph.getVertex(id);
    }

    public Vertex getSingleNode(String ID_NODE, long nextInt) {
        ((SparkseeGraph)graph).label.set(NODE_LABEL);
        return graph.getVertices(ID_NODE, nextInt).iterator().next();
    }

    public void clean() {
        
        ((SparkseeGraph)graph).label.set(NODE_LABEL);

        CloseableIterable<Vertex> vertices = (CloseableIterable<Vertex>)graph.getVertices();
        CloseableIterable<Edge> edges = null;
        for (final Vertex vtx : vertices){
            ((SparkseeGraph)graph).label.set(ARC_LABEL);
            edges = (CloseableIterable<Edge>) vtx.getEdges(Direction.OUT, ARC_LABEL);
            for(final Edge edg : edges){
                graph.removeEdge(edg);
            }
            graph.removeVertex(vtx);
        }
        ((SparkseeGraph)graph).commit();
    }
    
    public Long getEdgeProperty(Edge edg, String label){
        ((SparkseeGraph)graph).label.set(label);
        return Long.parseLong((String)edg.getProperty(PROP_WEIGHT),10);
    }
    
    public Long getVertexProperty(Vertex vtx, String label)
    {
        ((SparkseeGraph)graph).label.set(label);
        return Long.parseLong((String)vtx.getProperty(PROP_ID_NODE), 10);
    }
    
}
