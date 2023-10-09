# -*- encoding: utf-8 -*-
#!/usr/bin/env python
import string
import os
import nltk
from nltk.tree import *
from pre1 import *


dir_main = os.getcwd()

def writetxt(path, text, option):
    fwrite = open(path,option)
    fwrite.write(text)
    fwrite.close()
    

def readtxt(path, option):
    fread = open(path,option)
    strTxt = fread.read()
    fread.close()
    return strTxt

def wordpos():
       for story_id in range(1,41):
              os.chdir(dir_main)
              storystr = readtxt('tale\\tale' + str(story_id) + '.txt', 'r')
              phraselist = storystr.split('\n')

              new_phraselist = []
              for phrase in phraselist:
                     fileinput = 'input.txt'
                     writetxt(fileinput, phrase, 'w')
                     wordSegment(fileinput)
                     taggingPOS('WordSegmentation\\out.txt')
                     os.chdir(dir_main)
                     phrasepos = readtxt('wordpos.txt', 'r')
                     new_phraselist.append(phrasepos)

              new_phraseliststr = '\n'.join(new_phraselist)
              writetxt('wordpos\\tale' + str(story_id) + '.txt', new_phraseliststr, 'w')
                          
       
