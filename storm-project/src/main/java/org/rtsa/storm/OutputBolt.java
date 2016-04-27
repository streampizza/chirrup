package org.rtsa.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by anish on 5/3/16.
 */
public class OutputBolt extends BaseRichBolt implements IRichBolt {

    OutputCollector collector;
    Producer producer = null;
    Logger logger = LoggerFactory.getLogger(OutputBolt.class);
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        mongoClient = new MongoClient("localhost");
        database = mongoClient.getDatabase("chirrup");
        collection = database.getCollection("tweets");

    }

    public void execute(Tuple input) {
        Long tweetId = (Long) input.getValueByField("tweet-id");
        double sentiment = (Double) input.getValueByField("sentiment");
        Long tweetDateInput = (Long) input.getValueByField("tweet-date");
        Date tweetDate = new Date(tweetDateInput);
        String tweetText = (String) input.getValueByField("tweet-text");
        String country = (String) input.getValueByField("country");
        ArrayList<String> hashtags = (ArrayList<String>) input.getValueByField("hashtags");

        Document doc = new Document("tweetid",tweetId.toString())
                .append("timestamp", tweetDate)
                .append("sentiment", sentiment)
                .append("tweetText", tweetText)
                .append("country", country.toString())
                .append("hashtags", hashtags);
        collection.insertOne(doc);
        String ret = "{ "+
                "\"tweet-id\": " + tweetId.toString() + ", " +
                "\"tweetText\": " + tweetText +" , " +
                "\"sentiment\": " + Double.toString(sentiment)+", "+
                "\"country\": " + "\""+ country + "\""+ ", " +
                "\"hashtags\": " + hashtags.toString() +
                " }";
        logger.info(ret);
        collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
