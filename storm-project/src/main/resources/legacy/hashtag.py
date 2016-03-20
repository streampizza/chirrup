#from mongoengine import *

class Hashtag(Document):
    hashtag = StringField(required=True);
    overall_sentiment = FloatField(required=True, min_value=-5, max_value=5)
    count = LongField(min_value=0, default=1)
    country_sentiment = DictField();

    meta = {
        'indexes': [
            'title',
            '$title',
        ]
    }
    def get_overall_sentiment(self):
        return self.overall_sentiment

    def get_count(self):
        return self.count

    def get_country_sentiment(self, country):
        return self.country_sentiment[country]

