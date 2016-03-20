package org.rtsa.storm;

/**
 * Created by Anish Mashankar on 5/3/16.
 * Website: http://anishm.co
 */

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import com.google.common.io.Resources;

public class TheTopology {

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();
        String keywords[] = { "trump", "india" };
        String custKey = "D90JVJkIzMYBSU4K1zCvP2FXE";
        String custSecret = "r1GWAmHsTtwuFzQFOdGj0Ad7jBq2CynlwRyNiIEvbQpfzOOsTG";
        String accessKey = "612666274-cQQ4ICpXco0pSQU3Zl7IprEuF9jhgaSE2gXhQzkc";
        String accessSecret = "wFytlnD0xolGvOrqGGCm80ZiKcnsNsj3XwnbEbtO1ukz4";
        TwitterSpout twitterSpout = new TwitterSpout(custKey, custSecret, accessKey, accessSecret, keywords);
        String pythonFile = Resources.getResource("analysis.py").toString().substring(5);
        builder.setSpout("tweet-spout", twitterSpout, 1);
        builder.setBolt("wrangle-bolt", new WrangleBolt(), 1).shuffleGrouping("tweet-spout");
        //builder.setBolt("sentiment-bolt", new SentimentBolt(), 1).shuffleGrouping("wrangle-bolt");
        builder.setBolt("sentiment-bolt", new SentBoltPython(pythonFile), 1).shuffleGrouping("wrangle-bolt");
        //String outputBoltFile = Resources.getResource("output-bolt.py").toString().substring(5);
        builder.setBolt("output-bolt", new OutputBolt(), 1).shuffleGrouping("sentiment-bolt");


        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("twitter-sentiment", conf, builder.createTopology());
    }

}
