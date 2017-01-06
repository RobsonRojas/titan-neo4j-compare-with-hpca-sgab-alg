/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.robson.sparkseetest;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.sparksee.SparkseeGraph;

/**
 *
 * @author adminuser
 */
public class Sparksee {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KeyIndexableGraph graph = new SparkseeGraph("/tmp/teste7.gdb");

//        ((SparkseeGraph) graph).typeScope.set(Boolean.TRUE);
//        ((SparkseeGraph) graph).label.set("people");
//        
//        Vertex v1 = graph.addVertex(null);
//        //assert v1.getProperty(StringFactory.LABEL).equals(SparkseeGraph.DEFAULT_SPARKSEE_VERTEX_LABEL));
//
//        v1.setProperty("name", "foo");
//        // v2 and v3 are 'people' node, so 'people/name' is set
//        // v4 is a 'thing' node, so 'thing/name' is set
//
//        Vertex v2 = graph.addVertex(null);
//        v2.setProperty("name", "foo");
//
//        ((SparkseeGraph) graph).label.set("edge");
//        Edge edg = v1.addEdge("edge", v2);
//        edg.setProperty("eName", "know");
//        ((SparkseeGraph) graph).label.set("people");
        

        ((SparkseeGraph) graph).typeScope.set(Boolean.TRUE);        
        for (Integer j = 0; j < 10; j++) {
            ((SparkseeGraph) graph).label.set("people");

            try {
                for (Vertex v : graph.getVertices("name", j.toString())) {
                    String prop = v.getProperty("name");
                    
                }
            } catch (Exception e) {
                
            }
            Vertex v1 = graph.addVertex(null);
            Integer i1 = j;
            v1.setProperty("name", i1.toString());

            Vertex v2 = graph.addVertex(null);
            Integer i3 = j+3;
            v2.setProperty("name", i3.toString());

            ((SparkseeGraph) graph).label.set("edge");
            Edge edg = v1.addEdge("edge", v2);
            Integer i2 = j+2;
            edg.setProperty("eName", i2.toString());
            
            ((SparkseeGraph) graph).commit();
        }

        ((SparkseeGraph) graph).label.set("people");
        for (int j = 0; j < 10; j++) {
            Integer i = j;
            for(Vertex v : graph.getVertices("name", i.toString())) {
                String prop = v.getProperty("name");
            }
        }
        
        // Restore the normal property graph behaviour behaviour
        ((SparkseeGraph) graph).typeScope.set(Boolean.FALSE);
        
        ((SparkseeGraph) graph).shutdown();
        
    }
    
}
