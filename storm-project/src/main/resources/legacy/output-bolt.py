import storm
from mongoengine import *
from hashtag import Hashtag

connect('rtsa')

class OutputBolt(storm.BasicBolt):


    def process(self, tuple):
        tweet_id = tuple.values["tweet-id"]
        sentiment = tuple.values["sentiment"]
        country = tuple.values["country"]
        hashtags = tuple.values["hashtags"]
        storm.logInfo("Received tweet with tweet id: "+str(tweet_id))
        for h in hashtags:
            try:
                db_obj = Hashtag.get(hashtag=h)
                db_obj_count = db_obj.count
                db_obj_overall_sentiment = (((db_obj.overall_sentiment * db_obj_count) + sentiment) / (db_obj.count + 1))
                db_obj_country_sentiment = db_obj.country_sentiment["country"]
                db_obj_country_sentiment = (((db_obj_country_sentiment["sentiment"] * db_obj_country_sentiment["count"]) +
                                             sentiment) / (db_obj.country_sentiment["count"] + 1))
                db_obj_country_sentiment["count"] += 1
                db_obj.count += 1
                db_obj.overall_sentiment = db_obj_overall_sentiment
                db_obj.country_sentiment = db_obj_country_sentiment
                db_obj.save()
                storm.logInfo("Updating Hashtag: "+h)

            except:

                db_obj = Hashtag(hashtag=h, overall_sentiment=sentiment, country_sentiment={country: {
                    "sentiment": sentiment, "count": 1}}, count=1)
                db_obj.save()
                storm.logInfo("Inserting New Hashtag: "+h)


OutputBolt().run()

