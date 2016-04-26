package org.rtsa.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.slf4j.Logger;
import twitter4j.*;


import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by anish on 5/3/16.
 */
public class WrangleBolt extends BaseRichBolt {
    Logger logger = org.slf4j.LoggerFactory.getLogger(WrangleBolt.class);
    OutputCollector collector;


    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    private String filterOutURLFromTweet(final Status status) {
        final String tweet = status.getText();


        final URLEntity[] urlEntities = status.getURLEntities();
        int startOfURL;
        int endOfURL;
        String truncatedTweet = "";
        for(final URLEntity urlEntity: urlEntities){
            startOfURL = urlEntity.getStart();
            endOfURL = urlEntity.getEnd();
            truncatedTweet += tweet.substring(0, startOfURL) + tweet.substring(endOfURL);
        }
        return truncatedTweet;
    }

    public void execute(Tuple input) {
        Long tweetId;
        String tweetText, tweetCountry;
        HashtagEntity hashtagEntities[];
        ArrayList<String> hashtags =  new ArrayList<String>();
        Status tweet = (Status) input.getValueByField("tweet");

        tweetId = tweet.getId();
        tweetText = filterOutURLFromTweet(tweet);
        Place tweetPlace = tweet.getPlace();
        String originalTweetText = tweet.getText();
        Long tweetDate = tweet.getCreatedAt().getTime();
        User user = tweet.getUser();
        if (tweetPlace == null) {
            tweetCountry = "Not Available";
        }
        else tweetCountry = tweetPlace.getCountry();
        //tweetCountry="my country";
        hashtagEntities = tweet.getHashtagEntities();
        if (hashtagEntities.length == 0) {
            hashtags.add("none");
            collector.ack(input);
        }
        else {
            for (HashtagEntity hashtagEntity : hashtagEntities) {

                hashtags.add(hashtagEntity.getText());
            }

            Values values = new Values(tweetId, tweetDate, tweetText, tweetCountry, hashtags, originalTweetText);
            collector.emit(values);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet-id", "tweet-date", "tweet-text","tweet-country","hashtags", "original-tweet-text"));
    }
}
