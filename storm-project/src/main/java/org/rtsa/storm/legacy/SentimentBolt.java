package org.rtsa.storm.legacy;

/**
 * Created by anish on 5/3/16.
 */

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class SentimentBolt implements IRichBolt {

    private SortedMap<String,Integer> afinnSentimentMap = null;
    final private static Logger logger = LoggerFactory.getLogger(SentimentBolt.class);

    OutputCollector collector;

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        afinnSentimentMap = Maps.newTreeMap();
        try {
            final URL url = Resources.getResource("legacy/afinn-scores.txt");
            final String text = Resources.toString(url, Charset.defaultCharset());
            final Iterable<String> lineSplit = Splitter.on("\n").trimResults().omitEmptyStrings().split(text);
            List<String> tabSplit;
            for (final String str: lineSplit) {
                tabSplit = Lists.newArrayList(Splitter.on("\t").trimResults().omitEmptyStrings().split(str));
                afinnSentimentMap.put(tabSplit.get(0), Integer.parseInt(tabSplit.get(1)));
            }

        }
        catch(IOException e)
        {
            logger.debug("Inside Sentiment Bolt: File error when reading the afinn sentiment analysis");
            e.printStackTrace();
        }

    }

    private final int getSentimentOfTweet(final String status) {
        final String tweet = status.replaceAll("\\p{Punct}|\\n", " ").toLowerCase();
        final Iterable<String> words = Splitter.on(' ')
                .trimResults()
                .omitEmptyStrings()
                .split(tweet);
        int sentimentOfCurrentTweet = 0;
        for (final String word : words) {
            if(afinnSentimentMap.containsKey(word)){
                sentimentOfCurrentTweet += afinnSentimentMap.get(word);
            }
        }

        logger.debug("Tweet+ "+tweet+sentimentOfCurrentTweet);
        return sentimentOfCurrentTweet;
    }

    public void execute(Tuple input) {

        Long tweetID = (Long) input.getValueByField("tweet-id");
        String status = (String) input.getValueByField("tweet-text");
        String country = (String) input.getValueByField("tweet-country");
        ArrayList<String> hashtags = (ArrayList<String>) input.getValueByField("hashtags");
        int sentimentOfTweet;
        if(status.length() < 1) sentimentOfTweet = 0;
        else sentimentOfTweet = getSentimentOfTweet(status);
        logger.info("Sentiment: ", status, sentimentOfTweet);
        collector.emit(new Values(tweetID, sentimentOfTweet, country, hashtags));
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet-id","sentiment", "country", "hashtags"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
