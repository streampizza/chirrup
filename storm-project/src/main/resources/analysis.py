import storm
from afinn import Afinn

afinn = Afinn(emoticons=True)

class SentimentAnalysisBolt(storm.BasicBolt):

    def process(self,tuple):
        tweetid = tuple.values[0]
        tweet_date = tuple.values[1]
        tweettext = tuple.values[2]
        country = tuple.values[3]
        hashtags = tuple.values[4]
        tweet_text = tuple.values[5]
        score = afinn.score(tweettext)
        storm.emit([tweetid, tweet_date, score, country, hashtags, tweet_text])

SentimentAnalysisBolt().run()