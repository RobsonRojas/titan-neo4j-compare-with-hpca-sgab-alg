Executar teste 

git clone https://github.com/RobsonRojas/titan-neo4j-compare-with-hpca-sgab-alg.git

cd titan-neo4j-compare-with-hpca-sgab-alg

sudo cp cassandra.yaml /opt

sudo cp -r hpcData /opt

cd graph-benchmark-Neo4j-2.1.6

./multtest.sh

cd ..

cd graph-benchmark-titan

./multitest.sh
