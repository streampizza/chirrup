# chirrup
The Chirrup Repo. Coming soon! The following instructions are available to run the environment locally.

## Prerequisites:
* [Apache Zookeeper](http://zookeeper.apache.org)
* [Apache Kafka](http://kafka.apache.org)
* [Apache Storm](http://storm.apache.org)
* Python packages: flask, json, kafka, afinn

## Building
* Install python packages:
```sh
$ sudo pip install flask json kafka afinn
```
* Clone this repository
```sh
$ git clone https://github.com/anishmashankar/chirrup
```
* Build maven project (Storm topology)
```sh
$ cd chirrup
$ export CHIRRUP_HOME = $(pwd)
#we will refer this directory as CHIRRUP_HOME in later stages
$ cd storm-project
$ mvn package
```
## Running
* Run Apache Zookeeper:
```sh
$ cd {$ZOOKEEPER_HOME}
$ mv conf/zoo.cfg.sample conf/zoo.cfg
$ bin/zkServer.sh start conf/zoo.cfg
```
* Start a Kafka Broker
```sh
$ cd {$KAFKA_HOME}
$ bin/kafka-server-start.sh config/server.properties
```
* Create a topic
```sh
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic storm-topic
```
* Run storm topology:
```sh
$ {$STORM_HOME}/bin/storm jar ${CHIRRUP_HOME}/target/storm-*-SNAPSHOT-jar-with-dependencies.jar
```
* Run the web application:
```sh
$ python {$CHIRRUP_HOME}/frontend/app.py
```

Access the applcation at http://127.0.0.1:5000/analysis
