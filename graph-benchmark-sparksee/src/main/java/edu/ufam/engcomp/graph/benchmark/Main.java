package edu.ufam.engcomp.graph.benchmark;

import Sparksee.Sparksee;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    private Kernel1 k1;
    private Kernel2 k2;
    private Kernel3 k3;
    private Kernel4 k4;
    private static final Logger log = Logger.getAnonymousLogger();
    //IBechmarkGraph graphDatabase;

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
        //System.out.println(args[0]);
        
	
        Main main = new Main();
        main.run();
	
    }

    public Main() {
        
    }

    private void run() {
        
        
        Scanner s = new Scanner(System.in);
        System.out.println("ESCALA: ");
        int scala = s.nextInt();
        
        System.out.println("EXECUTAR K1? (1=sim; 0=nao): ");
        boolean execK1 = s.nextInt() == 1;
        
        System.out.println("EXECUTAR K2? (1=sim; 0=nao): ");
        boolean execK2 = s.nextInt() == 1;
        
        System.out.println("EXECUTAR K3? (1=sim; 0=nao): ");
        boolean execK3 = s.nextInt() == 1;
        
        System.out.println("EXECUTAR K4? (1=sim; 0=nao): ");
        boolean execK4 = s.nextInt() == 1;
        
        System.out.println("numero de amostras no k4? (1 a 16): ");
        int numAmostras = s.nextInt();

        //String scala = "20";
        String filePath_ = "/opt/hpcData/HpcData"+scala+".csv";
        String fileLog = "benchmark_log_scala"+scala+".log";
        String dbFilePath = "/tmp/teste16.gdb";

        Handler handler;
        try {
            handler = new FileHandler(fileLog);
        } catch (IOException ex) {
            System.out.println("io exception: "+ ex.getMessage());
            return;
        } catch (SecurityException ex) {
            System.out.println("security exception: "+ ex.getMessage());
            return;
        }
        
        log.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();  
        handler.setFormatter(formatter); 

        try {

            IBechmarkGraph graphdb;
            
            graphdb = new Sparksee(log);
            
            
            // kerne1
            if (execK1 && graphdb.conectarLocal(dbFilePath)) {
                log.log(Level.INFO,"graph init keys");
                graphdb.InitKeys();

                log.log(Level.INFO,"graph cleaning");
                graphdb.clean();
                graphdb.commit();

                k1 = new Kernel1(filePath_, graphdb, log);
                System.out.println();
                System.out.println();
                log.log(Level.INFO,"Starting Kernel1 loading execution");
                k1.execute();

                log.log(Level.INFO, "Kernel1 executed in {0}ms. {1} edges and {1} nodes loaded.", new Object[]{k1.getTime(), k1.getEdgesCreated(), k1.getNodesCreated()});
                
                File resultk1 = new File("HpcData"+scala+"_k1_results.csv");
                FileWriter wtk1 = new FileWriter(resultk1);
                String line = k1.getTime()+ "\n";
                wtk1.write(line);
                wtk1.close();
                k1.close();
                k1 = null;
                graphdb.desconectar();
                graphdb = null;
                
            } // if conectar 
            else {
                log.log(Level.WARNING,"kernel 1 não executado ou graph not conected");    
            }

            
            // esperar 1 minuto
            // chamar o garbage collector
            System.gc();
            Thread.sleep(1000);
            Object[] largeEdges = new Object[1];
            // nova instancia do grafo
            
            graphdb = new Sparksee(log);
            
            if (execK2 && graphdb.conectarLocal(dbFilePath)) {
                // kernel 2
                System.out.println();
                System.out.println();
                k2 = new Kernel2(graphdb, log);
                //log.log(Level.INFO,"Starting Kernel2");
                k2.execute();
                largeEdges = k2.getResult();
                log.log(Level.INFO,"Kernel2 executed in {0}ms\n{1} is max weight.\n{2} edges with max weight.\n{3}"+
                        " unique nodes.", new Object[]{k2.getTime(), k2.getMaxWeight(), k2.getResult().length, k2.getNodesCount()});
                
                File resultk2 = new File("HpcData"+scala+"_k2_results.csv");
                FileWriter wtk2 = new FileWriter(resultk2);
                String line = k2.getTime()+ "\n";
                wtk2.write(line);
                wtk2.close();
                k2.close();
                k2 =null;

                graphdb.desconectar();
                graphdb = null;
            } // if conectar 
            else {
                log.log(Level.WARNING,"kernel 2 não executado ou graph not conected");    
            }

            
            // chamar o garbage collector
            System.gc();
            Thread.sleep(1000);
            
            // nova instancia do grafo
            graphdb = new Sparksee(log);
            
            // reconectando o grafo
            if (execK3 && graphdb.conectarLocal(dbFilePath)) {
                // kernel 3
                System.out.println();
                System.out.println();
                
                k3 = new Kernel3(graphdb, log);
                k3.setEdges(largeEdges);
                k3.setSubGraphPathLenght(10);// TODO: configuravel
                //log.log(Level.INFO,"Starting Kernel3");
                k3.execute();
                log.log(Level.INFO, "Kernel3 executed in {0}ms", k3.getTime());
                File resultk3 = new File("HpcData"+scala+"_k3_results.csv");
                FileWriter wtk3 = new FileWriter(resultk3);
                String line = k3.getTime()+ "\n";
                wtk3.write(line);
                wtk3.close();
                k3.close();
                k3 = null;
                graphdb.desconectar();
                graphdb = null;
            } // if conectar 
            else {
                log.log(Level.WARNING,"kernel 3 não executado ou graph not conected");    
            }

            // kernel 4
            // sobre as N amostras
            // esperar 1 minuto
            // chamar o garbage collector
            System.gc();
            Thread.sleep(1000);
            System.out.println();
            System.out.println();
            
            // nova instancia do grafo
            graphdb = new Sparksee(log);
            
            
            // reconectando o grafo
            if (execK4 && graphdb.conectarLocal(dbFilePath)) {
                // arquivo de amostras
                String sampleFile = "/opt/hpcData/HpcData"+scala+"_Samples.csv";
                File sample = new File(sampleFile);
                //arquivo de resultado por amostra
                File resultk4 = new File("HpcData"+scala+"_k4_"+numAmostras+"Amostras_results.csv");
                FileWriter wtk4 = new FileWriter(resultk4);
                
                
                // executando
                long time1 = System.currentTimeMillis();
                // recriando o kernel 4 para nova amostra
                k4 = new Kernel4(graphdb, log);

                // executando sobre a amostra
                k4.setSampleFile(sample);
                k4.setSampleSize(numAmostras);
                k4.execute();
                // fim da execucao

                
                // gravando no arquivo de resultados
                long time2 = System.currentTimeMillis();
                log.log(Level.INFO, "Kernel4 executed in {0} ms; SampleSize: {1}; SampleFile: {2}", new Object[]{time2 - time1, numAmostras, sampleFile});
                String line = numAmostras+", "+(time2 - time1)+ "\n";
                wtk4.write(line);

                // fechando o kernel 4 e desconectando o grafo
                k4.close();
                k4 = null;
                graphdb.desconectar();
                graphdb = null;
  
                wtk4.close();

            } // if conectar 
            else {
                log.log(Level.WARNING,"kernel 4 não executado ou graph not conected");    
            }

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

}
