# -*- encoding: utf-8 -*-
#!/usr/bin/env python
import string
import os
import nltk
from nltk.tree import *


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


def chunkingNP(listwordpos):
       grammars = 'NP : ' + readtxt('rulechunking.txt', 'r')
       cp = nltk.RegexpParser(grammars)
       result = cp.parse(listwordpos)
       return result


       
#input  name of textfile that contain word with pos such as "หนู/ncn น้อย/adj จะ/prev ไป/vpost กิน/vt ข้าว/ncn"
#output word are chunk "หนูน้อย/NP(ncn adj) จะ/prev ไป/vpost กิน/vt ข้าว/ncn"
def NPchunking(textfile):
       os.chdir(dir_main)
       text = readtxt(textfile, 'r')

       #transform "word1/pos1 word2/pos2 word3/pos3 word4/pos4...." to [(word1,pos1),(word2,pos2),(word3,pos3),(word4,pos4),...]
       listofword = [] #[(word1,pos1),(word2,pos2),(word3,pos3),(word4,pos4),...]
       wordlist = text.split(' ')
       for word in wordlist:
              wordpos = word.split('/')
              word = wordpos[0]
              pos = wordpos[1]
              listofword.append((word,pos))
       
       wordchunked = chunkingNP(listofword)
       #wordchunked.draw()
       
       listofword = []
       for w in wordchunked:
              if isinstance(w, tuple):
                     word = w[0]
                     pos = w[1]
                     print word, pos 
                     listofword.append((word,pos))
              elif w.node == 'NP':
                     nounphrase = ''
                     posofNP = []

                     for l in w.leaves():
                            nounphrase += l[0]
                            posofNP.append(l[1])
                     listofword.append((nounphrase,'NP(' +' '.join(posofNP)+')'))

       
       wordposlist = []
       for pair in listofword:
              wordposlist.append(pair[0] + '/' + pair[1])

       text = ' '.join(wordposlist)
       writetxt('wordchunked.txt', text, 'w')
