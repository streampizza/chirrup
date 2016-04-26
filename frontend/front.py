from flask import Flask
from flask import request, render_template, redirect
from datetime import datetime
from pymongo import MongoClient
import html
import random
import json
import ast
from flask.ext.pymongo import PyMongo
from flask import make_response, request, current_app
from functools import update_wrapper
app = Flask(__name__)

mongo = PyMongo(app)

app.config['MONGO_HOST'] = 'localhost'
app.config['MONGO_PORT'] = 27017
app.config['MONGO_DBNAME'] = 'chirrup'

mClient = MongoClient('localhost',27017)
collection = mClient['chirrup']['tweets']

@app.route('/', methods=['GET','POST'])
def home():
    if request.method=='POST':
        var = request.form['query']
        return redirect('/'+var, code=302)
    else:
        distincthashtags = collection.distinct("hashtags")
        return render_template("home.html",distincthashtags=distincthashtags)

@app.route('/<input>', methods=['GET','POST'])
def analyze(input):
    hashtag = input
    country_sentiment_query = list(collection.aggregate([{"$match":{"hashtags":hashtag}},{"$group":{'_id':'$country',"avgsentiment": {"$avg":"$sentiment"}}}]))
    average_sentiment_query = list(collection.aggregate([{"$match":{"hashtags":hashtag}},{"$group":{'_id':'sentiment',"avgsentiment": {"$avg":"$sentiment"}}}]))
    if len(average_sentiment_query)==0:
        return render_template('fourohfour.html')
    country_wise_sentiment = json.dumps(country_sentiment_query)
    average_sentiment = json.dumps(average_sentiment_query[0])
    sorter = [('timestamp', 1)]
    last_ten_tweets = list(collection.find({"hashtags":hashtag},{'timestamp':0, '_id': 0}).sort(sorter))[:10]
    return render_template("analysis.html",country_wise_sentiment=country_wise_sentiment, average_sentiment=average_sentiment, hashtag=hashtag, last_ten_tweets=last_ten_tweets)
if __name__=="__main__":
    app.run(debug=True)
