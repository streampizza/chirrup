from kafka import KafkaConsumer
import os
import json
#from mongoengine import *

#connect("rtsa")

# class TweetEvents:
#     _id = LongField()
#     sentiment = FloatField()
#     country = StringField()
#     hashtag = ListField(StringField())

consumer = KafkaConsumer('storm-topic', metadata_broker_list=['localhost:9092'])

print "Started running consumer. Waiting for messages"

for message in consumer:
    print message.value
