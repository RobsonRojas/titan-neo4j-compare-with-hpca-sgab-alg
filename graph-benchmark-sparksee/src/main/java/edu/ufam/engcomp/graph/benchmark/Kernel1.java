package edu.ufam.engcomp.graph.benchmark;

import au.com.bytecode.opencsv.CSVReader;
import com.tinkerpop.blueprints.impls.sparksee.SparkseeGraph;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.FileReader;
import java.util.logging.FileHandler;
import java.util.logging.Handler;


public class Kernel1 implements Closeable {

	private String db_path = "neo-db-scale";
	private Logger log;
       	private static final String OPT_HELP = "Help";
	private static final String OPT_INPUT = "Input file";
	private static final String OPT_SCALE = "Scale";
	private static final String ATTR_WEIGHT = "WEIGHT";
	private static final String ATTR_ID_NODE = "ID_NODE";

	private CSVReader reader;
        private IBechmarkGraph graphDb;
    private long time;
    private int countEdges;

	public Kernel1(String inputFile, IBechmarkGraph gdb, Logger log) throws Exception {
            //this.optimize = (1 << scale) / 10;
            //optimize = (optimize > 1000000) ? 1000000
            //		: (optimize < 100000) ? 100000 : optimize;

            this.log = log;
            File input = new File(inputFile);

            FileReader rd = new FileReader(input);
            reader = new CSVReader(rd);

            graphDb = gdb;
	}
        
        public boolean execute(){
            try {
                load();
            } catch (Exception ex) {
                Logger.getLogger(Kernel1.class.getName()).log(Level.SEVERE, null, ex);
                log.log(Level.SEVERE, "KERNEL1 exception"+ ex.getMessage());
                return false;
            }
        
            return true;
        }
        
        
	public void load() throws Exception {
		long start = System.currentTimeMillis();
		String[] row = reader.readNext();
                
                countEdges = 0;
                long src = 0;
                long tar = 0;
                long weight = 0;

		while (row != null) {
                    try {
                        src = Long.parseLong(row[0], 10);
                        tar = Long.parseLong(row[1], 10);
                        weight = Long.parseLong(row[2], 10);
                    } catch (NumberFormatException nfe) {
                        log.log(Level.INFO, "Exception com os valores: src {0},  tar {1}, weight {2}. Mensagem: {3}.", new Object[]{row[0], row[1], row[2], nfe.getMessage()});
                    }
                    catch (Exception ex)
                    {
                        log.log(Level.INFO, "Exception com os valores: src {0},  tar {1}, weight {2}. Mensagem: {3}.", new Object[]{row[0], row[1], row[2], ex.getMessage()});
                    }

                    graphDb.createEdge(src, tar, weight);
                    row = reader.readNext();
                    countEdges++;
		}
                
                time = System.currentTimeMillis()-start;
//
//		log.log(Level.INFO, "{0} edges loaded in {1}ms", new Object[]{countEdges, time});
//		log.log(Level.INFO, "{0} nodes.", graphDb.getNodesCreated());
	}

	public void close() throws IOException {
		
	}
        

        public long getTime() {
            return time;
        }
        
        public long getNodesCreated(){
            return graphDb.getNodesCreated();
        }
        
        public long getEdgesCreated(){
            return countEdges;
        }
    
    
    	public static void main(String args[]) throws Exception {

	}

    void cleanVertexAndEdges() {
        graphDb.clean();
        graphDb.commit();
    }


}
