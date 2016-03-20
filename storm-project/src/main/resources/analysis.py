import storm
from afinn import Afinn

afinn = Afinn(emoticons=True)

class SentimentAnalysisBolt(storm.BasicBolt):

    def process(self,tuple):
        tweetid = tuple.values[0]
        tweettext = tuple.values[1]
        country = tuple.values[2]
        hashtags = tuple.values[3]
        score = afinn.score(tweettext)
        storm.emit([tweetid,score,country,hashtags])

SentimentAnalysisBolt().run()