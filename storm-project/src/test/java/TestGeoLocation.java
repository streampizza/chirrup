/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

@SuppressWarnings("serial")
public class TestGeoLocation {

    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Status> queue = null;
    TwitterStream _twitterStream;
    String custKey = "D90JVJkIzMYBSU4K1zCvP2FXE";
    String custSecret = "r1GWAmHsTtwuFzQFOdGj0Ad7jBq2CynlwRyNiIEvbQpfzOOsTG";
    String accessKey = "612666274-cQQ4ICpXco0pSQU3Zl7IprEuF9jhgaSE2gXhQzkc";
    String accessSecret = "wFytlnD0xolGvOrqGGCm80ZiKcnsNsj3XwnbEbtO1ukz4";
    String[] keyWords;
    Logger log = LoggerFactory.getLogger(TestGeoLocation.class);

    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        queue = new LinkedBlockingQueue<Status>(1000);
        _collector = collector;

        StatusListener listener = new StatusListener() {


            public void onStatus(Status status) {

                if( status.getText().length() != 0 && status.getLang().equals("en")) {
                    queue.offer(status);
                    log.info("Getting Lattitude: "+(status.getGeoLocation().getLatitude()));
                }
            }


            public void onDeletionNotice(StatusDeletionNotice sdn) {
            }


            public void onTrackLimitationNotice(int i) {
            }


            public void onScrubGeo(long l, long l1) {
            }


            public void onException(Exception ex) {
            }


            public void onStallWarning(StallWarning arg0) {
                // TODO Auto-generated method stub

            }

        };

        TwitterStream twitterStream = new TwitterStreamFactory(
                new ConfigurationBuilder().setJSONStoreEnabled(true).build())
                .getInstance();

        twitterStream.addListener(listener);
        twitterStream.setOAuthConsumer(custKey, custSecret);
        AccessToken token = new AccessToken(accessKey, accessSecret);
        twitterStream.setOAuthAccessToken(token);

        if (keyWords.length == 0) {

            twitterStream.sample();
        }

        else {

            FilterQuery query = new FilterQuery().track(keyWords);
            twitterStream.filter(query);
        }

    }

    public void nextTuple() {
        Status ret = queue.poll();
        if (ret == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(ret));

        }
    }

    public void close() {
        _twitterStream.shutdown();
    }


    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }


    public void ack(Object id) {

    }


    public void fail(Object id) {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }

}