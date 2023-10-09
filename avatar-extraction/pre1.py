# -*- encoding: utf-8 -*-
#!/usr/bin/env python
import string
import os



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

        
#input  name of textfile that contain the sentence suchas หนูน้อยกล่าวว่า...
#output word are segmented by | [หนู|น้อย|กล่าว|ว่า|...]  
def wordSegment(textfile): #Hybrid from the best 2010
    
    os.chdir(dir_main)
    fileName = '\"' + str(dir_main) + '\\' + textfile + '"'      # full path of input

    os.chdir(str(dir_main)+ '\\WordSegmentation') 
    Construction_1 = 'java -jar "dist\WordSegmentation.jar" ' + fileName
    os.system(Construction_1) # Output file is in WordSegmentation\out.txt


def wordSegment_swath(textfile): #Swath from NECTECH
    
    os.chdir(dir_main)
    fileName = '\"' + str(dir_main) + '\\' + textfile + '"'      # full path of input
    os.chdir(str(dir_main) + '\\swath\\Program')
    DirPathCur = dir_main + '\\swath'
    Construction_1 = "swath.exe -m long <" + fileName +">" + DirPathCur + "\\out.txt" # Output file is in swath\out.txt
    os.system(Construction_1)

#==========================================POS Tagging===========================
#input  string of word segmentation pharse  หนู|น้อย|กล่าว|ว่า| ....
#output list WordTagged = [(word, POS),...]

    
def taggingPOS(textfile):
        
    os.chdir(dir_main)
    wordseg = readtxt(textfile, 'r') #Read input file
    wordlist = ' '.join(wordseg.split('|'))

    os.chdir(dir_main) #Write word to text file for input of Jjitar
    writetxt('wordtosegment.txt', wordlist, 'w')

    #call tager "Jitar" with thai corpus from KU
    infileName = '\"'+ str(dir_main) + '\\wordtosegment.txt\"'   #Name(path) of input to Jitar
    outfileName = '\"'+ str(dir_main) + '\\postagonly.txt\"' #Name(path) of output from Jitar, You can write path where Jitar write to.
    os.chdir(str(dir_main) + '\\jitar\dist')
    Construction_2 = 'java -cp jitar-0.0.4.jar org.langkit.tagger.cli.Tag lexicon ngrams <\"' + infileName +'\">' + outfileName
    os.system(Construction_2)

    os.chdir(dir_main)
    fileName ='postagonly.txt'
    posonly = readtxt(fileName,'r') #Read output from Jitar
    posonly = posonly.strip('\n')

   
    #words คือ listของคำจาก wordlist.split(' ') , poslist คือ list of pos from posonly.split(' ')
    words = wordlist.split(' ')
    poslist = posonly.split(' ')

    wordTagged=[] #Keep to [(word1,pos1),(word2,pos2),(word3,pos3),....]
    i=0
    j=0
    while (i<len(words)-1 or j<len(poslist)-1):
        # ถ้าไม่ใช่ spacebar ใน wordlist ให้กำกับ pos ให้กับ คำ
        if (words[i] != ' ' and words[i] != ''):
            wordTagged.append((words[i],poslist[j]))
            i+=1
            j+=1
        else: #ถ้าเป็น spacebar ให้ข้ามคำใน wordlist
            i+=1

    #tranform to word/pos
    wordposlist = []
    for pair in wordTagged:
        wordposlist.append(pair[0] + '/' + pair[1])

    wordpos = ' '.join(wordposlist)
    writetxt('wordpos.txt', wordpos, 'w')
