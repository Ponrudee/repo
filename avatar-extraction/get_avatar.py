# -*- encoding: utf-8 -*-
#!/usr/bin/env python
import string
import re
import nltk
from nltk.tree import *

#####################################
# usage:
# 1. set text
# 2. np_list = ie_process(text)
#####################################
"""	
def ne_extract(pattern, text):          
     token = nltk.word_tokenize(text)
     wordlist = [w for w in token if re.search(pat,w)] 
     return wordlist
"""
grammar = r"""
NP: {<NTIT><NCN>|<NTIT><NCN>|<PREF3><NCN>|<PREF2><NCN>|<NTIT><VT>|<NCN><NCN><NCN>|<NCN><VT><NCN>|<NCN><PPER>|<NCN><VI>|<NCN><NCN>|<NCN><NPN>|<PPER>|<NCN>|<NPN>|}
"""

"""
def get_avatar(wordlist,count):
     fd = nltk.FreqDist(wordlist)
     return [w for w in set(wordlist) if fd[w] > count]
"""     

def wordtag_sep(wordtaglist):
     #taggedtoken = nltk.word_tokenize(text)
     sep = []
     for wt in wordtaglist:
          (w,t) = nltk.tag.str2tuple(wt)
          sep.append((w,t))
     return sep

def ne_extract(grammar, wordtaglist):
     cp = nltk.RegexpParser(grammar)
     ne = cp.parse(wordtaglist)
     return ne

def np_extract(tree):
     if len(tree) > 0:
          np = [t.leaves() for t in tree if type(t) is nltk.Tree and 'NP' in t.label()]
     return np

def merge_sub_np(np):
     new_np = ""
     for n in np:
          #print n[0], n[1]
          new_np += n[0]
     return new_np

def merge_np(np_list):
     new_np_list = []
     for np in np_list:
          #print np
          new_np = merge_sub_np(np)
          #print new_np
          new_np_list.append(new_np)
     return new_np_list

def count_np_freq(new_np_list, count):
     fd = nltk.FreqDist(new_np_list)
     #print fd.items()
     ava = [w for w in set(new_np_list) if fd[w] > count]
     print 'len np', len(new_np_list), 'len avatar ', len(ava)
     for a in ava:
          print a
     return ava

def np_matching(text):
     sentences = text.split('\n')
     print 'len of sentences', len(sentences)
     #remove punctuation
     sentences = [sent for sent in sentences if '/punc' not in sent]
     print 'len of sentences after remove punc', len(sentences)
     list_token = [nltk.word_tokenize(sent) for sent in sentences]
     cp = nltk.RegexpParser(grammar)
     np_list = []
     for w in list_token:
          #print w, 'len of word', len(w)
          if len(w) > 0 and '/punc' not in w:
               wordtaglist = wordtag_sep(w)
               #print wordtaglist, 'len of wordlist', len(wordtaglist)
               #print 'len of wordlist', len(wordtaglist)
               tree = cp.parse(wordtaglist)
               np = np_extract(tree)
               #print 'len of np', len(np)
               for n in np:
                    #print n
                    np_list.append(n)
     print 'list np len:', len(np_list)
     return np_list

#########################################
# input text file "word1/pos1 word2/pos2 ..."
# output list of np
#########################################
def get_ava(count):
     fpath = 'wordpos//tale'
     for i in range(1,41):
          fname = fpath+str(i)+'.txt'
          print fname
          fread = open(fname)
          tale = fread.read()
          fread.close()
          ava_extract(tale, count)
          print '===========================\n'

def ava_extract(text, count):
     #np_list = np_matching(text)
     #new_np_list = merge_np(np_matching(text))
     #ava = count_np_freq(new_np_list,count)
     ava = count_np_freq(merge_np(np_matching(text)), count)
     
          


          
     

          



          
     
     

	
