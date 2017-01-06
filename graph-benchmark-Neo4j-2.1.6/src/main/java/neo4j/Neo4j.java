/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package neo4j;

//import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
//import com.tinkerpop.blueprints.impls.neo4j.Neo4jHaGraph;
import edu.ufam.engcomp.graph.benchmark.IBechmarkGraph;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author wesllen
 */
public class Neo4j implements IBechmarkGraph {
    
	private static final Logger log = Logger.getAnonymousLogger();
//	private static final String ATTR_WEIGHT = "WEIGHT";
//        private static final String ATTR_LABEL = "ARC";
//	private static final String ATTR_ID_NODE = "ID_NODE";

	private int countNodes = 0;

        Neo4jGraph graph;
    
    
    public Neo4j(Logger fileLog) {
        
    }

        
    public boolean conectarRemote(String uri) {
            Map<String, String> configuration;
            configuration = new HashMap<String, String>();
            configuration.put("ha.server_id", "1");
            configuration.put("ha.server", "127.0.0.1:6001");
            configuration.put("ha.initial_hosts", "127.0.0.1:6001");
                        
        
        graph = new Neo4jGraph("/tmp/hsperfdata_adminuser");
        
        return graph !=null;
    }
    
    public boolean conectarLocal(String localConnectionString) {
        // "/tmp/hsperfdata_neo4j"
        graph = new Neo4jGraph(localConnectionString);
        return graph !=null;
    }
    
    public void InitKeys(){
        if (graph!=null) {
            //graph.dropKeyIndex(ATTR_ID_NODE, Vertex.class);
            //graph.dropKeyIndex(ATTR_WEIGHT, Edge.class);
            graph.createKeyIndex(ATTR_ID_NODE, Vertex.class);
            graph.createKeyIndex(ATTR_WEIGHT, Edge.class);
        }
        
    }
    
        
    public boolean createEdge(long src, long tar, long weight) {
        //tail
        Vertex tail = null;
        Iterable<Vertex> it = graph.getVertices(ATTR_ID_NODE, src);

        if (it.iterator().hasNext()) {
            tail = it.iterator().next();
        }
        
        if (tail == null) {
                tail = graph.addVertex(null);
                tail.setProperty(ATTR_ID_NODE, src);
                countNodes++;
        }
        
        //head
        Vertex head = null;
        it =  graph.getVertices(ATTR_ID_NODE, tar);
        if (it.iterator().hasNext()) {
            head = it.iterator().next();
        }
                
        
        if (head == null) {

                head = graph.addVertex(null);
                head.setProperty(ATTR_ID_NODE, tar);

                countNodes++;
        }

        
        //edge
        Edge e = head.addEdge(ATTR_LABEL, tail);
        //e.setProperty(ATTR_WEIGHT, weight);
        e.setProperty(ATTR_WEIGHT, weight);

        return true;
    }
    
    public void commit(){
        graph.commit();
    }

    public void desconectar() {
        this.graph.shutdown();
    }

    public int getNodesCreated() {
        return countNodes;
    }
    
    public Iterator<Vertex> getAllVertices(){
        return graph.getVertices().iterator();
    }

    public void rollback() {
        
        graph.rollback();
    }

    public Edge getEdgeById(Object id) {
        return graph.getEdge(id);
    }

    public Vertex getSingleNode(String ID_NODE, long nextInt) {
        return graph.getVertices(ID_NODE, nextInt).iterator().next();
    }
        
    public void clean() {

        for ( Iterator<Edge> edges = graph.getEdges().iterator(); edges.hasNext();) {
            graph.removeEdge(edges.next());
}
        
        for (Iterator<Vertex> vertices = graph.getVertices().iterator(); vertices.hasNext();) {
            graph.removeVertex(vertices.next());
        }
//        
//        
//        if (graph.getType(ATTR_ID_NODE) != null) {
//            graph.dropKeyIndex(ATTR_ID_NODE, Vertex.class);    
//        }
//        
//        if (graph.getType(ATTR_WEIGHT) != null) {
//            graph.dropKeyIndex(ATTR_WEIGHT, Edge.class);
//        }
        
    }

    public Vertex getNodeById(Object id) {
        return graph.getVertex(id);
    }


    public boolean checkKeys() {
        return true;
    }


        
}
