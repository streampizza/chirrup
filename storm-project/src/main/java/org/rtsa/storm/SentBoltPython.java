package org.rtsa.storm;

import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import com.google.common.io.Resources;

import java.net.URL;
import java.util.Map;

/**
 * Created by anish on 6/3/16.
 */
public class SentBoltPython extends ShellBolt implements IRichBolt {
    public SentBoltPython(String resourceFile){
        super("python",resourceFile);
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet-id","sentiment","country","hashtags"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
