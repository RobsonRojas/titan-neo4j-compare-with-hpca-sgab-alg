if [ $# -ne 1 ]
then
echo "Nombre de paràmetres no vàlid!"
else
#java -cp hpc-sgab-Titan-0.4.4-1.0.jar -Xms512m edu.upc.dama.Titan.benchmark.Kernel1 -i "../data/scale$1.txt" -s $1
#mvn "-Dexec.args=-classpath  -Xms1536m %classpath edu.upc.dama.Neo4j.benchmark.Main" -Dexec.executable=/opt/jdk/bin/java -Dexec.workingdir=. org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
mvn clean install
mvn "-Dexec.args=-classpath %classpath edu.ufam.engcomp.graph.benchmark.Main" -Dexec.executable=java -Dexec.workingdir=. org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
fi
