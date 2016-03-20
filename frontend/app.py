import json
from flask import Flask, render_template, Response, stream_with_context
#from flask.ext.mongoengine import MongoEngine
from kafka import KafkaConsumer


def messages_stream():
    consumer = KafkaConsumer("storm-topic", group_id="web-consumer", metadata_broker_list=['localhost:9092'])
    for message in consumer:
        yield "data: "+message.value.decode('utf-8')+"\n\n"

app = Flask(__name__)

@app.route('/')
def home():
    return 'It works!'

@app.route('/analysis')
def analysis(hashtag=None):
    return render_template('analysis.html')

@app.route("/stream")
def stream():
    response =  Response(stream_with_context(messages_stream()), mimetype="text/event-stream")
    try:
        return response
    except:
        return 'string'

app.run(threaded=True, debug=True)
