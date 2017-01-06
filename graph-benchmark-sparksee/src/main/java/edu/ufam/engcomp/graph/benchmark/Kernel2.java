package edu.ufam.engcomp.graph.benchmark;

import com.tinkerpop.blueprints.Direction;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.upc.dama.dex.utils.CmdLineArgs;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Kernel2 implements Closeable {

    private static final String OPT_HELP = "Help";
    private static final String OPT_IMAGE = "Neo database";
    private static final String ATTR_WEIGHT = "WEIGHT";
    private static final String ATTR_ID_NODE = "ID_NODE";
    private  Logger log;
    private List<Object> result;
    private long maxWeight;
    private long nbNodes;
    private String edgeList;
    private long time;
    private IBechmarkGraph graphDb;
    
    public Kernel2(IBechmarkGraph gdb, Logger log) throws Exception {
    	graphDb = gdb;
        this.log = log;
    }
    
    public void execute(){
	long start = System.currentTimeMillis();
    	result = new ArrayList<Object>();
    	maxWeight = 0;
    	edgeList = new String();
    	try{
    		Iterator<Vertex> nodes = graphDb.getAllVertices();
    		for(Iterator<Vertex> it = nodes; it.hasNext();){
    			Vertex src = it.next();
    			Iterable<Edge> edges = src.getEdges(Direction.OUT);
                        for (Edge currentRelationship : edges) {
                            Vertex tar = currentRelationship.getVertex(Direction.OUT);
                            
                            Long weight = graphDb.getEdgeProperty(currentRelationship, ATTR_WEIGHT);
                            if(maxWeight < weight){
                                maxWeight = weight;
                                edgeList = graphDb.getVertexProperty(src, ATTR_ID_NODE)+" "+ graphDb.getVertexProperty(tar, ATTR_ID_NODE)+" "+weight+"\n";
                                result.clear();
                            } else if (maxWeight == weight){
                                edgeList = edgeList.concat(graphDb.getVertexProperty(src, ATTR_WEIGHT)+" "+ graphDb.getVertexProperty(tar, ATTR_ID_NODE)+" "+weight+"\n");
                            }
                            
                            //System.out.println(currentRelationship.getId().getClass().getName());
                            result.add(currentRelationship.getId());
                        }
    		}
    		graphDb.commit();
    	}finally {
    		graphDb.rollback();
    	}
    	time = System.currentTimeMillis()-start;
    }
    
    public Object[] getResult(){
	return result.toArray(new Object[0]);
    }
    
    public long getMaxWeight() {
        return maxWeight;
    }

    public long getNodesCount() {
        return nbNodes;
    }

    public String getEdgeList() {
        return edgeList;
    }

    public long getTime() {
	return time;
    }

    @Override
    public void close() throws IOException {
    
    }
    
	
	 public static void main(String[] args) throws Exception{
		
         }
}
