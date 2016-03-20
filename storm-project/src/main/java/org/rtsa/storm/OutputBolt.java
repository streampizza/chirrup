package org.rtsa.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Created by anish on 5/3/16.
 */
public class OutputBolt extends BaseRichBolt implements IRichBolt {

    OutputCollector collector;
    Producer producer = null;
    Logger logger = LoggerFactory.getLogger(OutputBolt.class);

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        //TODO Kafka imports not working. Need some fixing
        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(properties);

    }

    public void execute(Tuple input) {
        Long tweetId = (Long) input.getValueByField("tweet-id");
        double sentiment = (Double) input.getValueByField("sentiment");
        String country = (String) input.getValueByField("country");
        ArrayList<String> hashtags = (ArrayList<String>) input.getValueByField("hashtags");
        String ret = "{ "+
                "\"tweet-id\": " + tweetId.toString() + ", " +
                "\"sentiment\": " + Double.toString(sentiment)+", "+
                "\"country\": " + "\""+ country + "\""+ ", " +
                "\"hashtags\": " + hashtags.toString() +
                " }";
        ProducerRecord<String, String> record = new ProducerRecord("storm-topic", ret.toString());
        producer.send(record);
        logger.info(ret);
        collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
