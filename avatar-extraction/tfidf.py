from __future__ import division, unicode_literals

import math
from textblob import TextBlob as tb
import string
import nltk

def maxfreq(doc):
    count = nltk.defaultdict(int)
    for word in doc.words:
        count[word] += 1
    sorted_freq = sorted(count.items(), key = lambda x: x[1], reverse=True)
    #print sorted_freq
    return sorted_freq[0][1]  #return integer value of first item
        
def tf(word, doc):
    #return doc.words.count(word) / len(doc.words)
    return 0.5 + (0.5 * doc.words.count(word) / maxfreq(doc))

def n_containing(word, doclist):
    return sum(1 for doc in doclist if word in doc)

def idf(word, doclist):
    # return 0 if the word is in every document
    # more document contains the word, less idf value
    # add one to prevent zero division, get log base 10
    return math.log((1+len(doclist))/(1.0 + n_containing(word,doclist)), 10)

def tfidf(word, doc, doclist):
    return tf(word, doc) * idf(word, doclist)

def print_tfidf(doclist):
    for i, doc in enumerate(doclist):
        print "----------------------------"
        print("top words in document {}".format(i+1))
        scores = {word: tfidf(word, doc, doclist) for word in doc.words}
        #scores = {word: [tf(word, doc), idf(word,doclist)] for word in doc.words}
        #print "scores: ", scores
        #print "scores items: ", scores.items()
        #sorted_words = sorted(scores.items(), key = lambda x: x[1], reverse=True)
        sorted_words = sorted(scores.items(), key = lambda x: x[1])
        #for word, score in sorted_words[:7]:
            #print("\tWord: {}, TF-IDF: {}", format(word, round(score,5)))
            #print word, score
        return sorted_words[:10]






            



