/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thaiword2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.BreakIterator;
import java.util.Locale;

/**
 *
 * @author Benq
 */
public class SubMain {
    static Filter f = null;
    //static PipesetByWrongCase pipeSet = null;
    static Locale thaiLocale ;
    static SymbolLink sLink;
    static NEFilter nCheck;
    public static void init() throws Exception{
        f = new Filter();
        //pipeSet = new PipesetByWrongCase();
        thaiLocale = new Locale("th");
        sLink = new SymbolLink();
        nCheck = new NEFilter();
    }

    public static void subMain(File fileInstance,File newFile) throws Exception{
        OutputStreamWriter outstream = new OutputStreamWriter(new FileOutputStream(newFile),"UTF-8");
        String currentString="";
        String input;
        ThaiWordFileReader thaiReader = new ThaiWordFileReader(fileInstance.getPath());
        while((input=thaiReader.readNext())!=null){
            BreakIterator boundary = BreakIterator.getWordInstance(thaiLocale);
            String[] splittedInput = f.filter(input);
            for(int i=0;i<splittedInput.length;i++){
                //System.out.println(splittedInput[i]);
                if(splittedInput[i].startsWith("~")){
                    outstream.write(splittedInput[i].substring(1).substring(0,splittedInput[i].length()-2));
                    outstream.write("|");
                }else if(!splittedInput[i].equals("")){
                    boundary.setText(splittedInput[i]);
                    currentString = printSegmentedWord(boundary, splittedInput[i]).toString();
                    //currentString = pipeSet.set(currentString);
                    currentString = sLink.Link(currentString);
                    //System.out.println(currentString);
                    currentString = nCheck.doNETagFilter(currentString);
                    
                    outstream.write(currentString);
                }
                //System.out.println(splittedInput[i]);
            }
            outstream.write("\n");
        }
        outstream.close();
        thaiReader.closeFile();
        for(String s : NEFilter.NEDb){
            System.out.println(s);
        }
    }

    public static String subMain(String input) throws Exception{
        StringBuffer buffer = new StringBuffer();
        String currentString = "";
        for(String s:input.split("\\n")){
            BreakIterator boundary = BreakIterator.getWordInstance(thaiLocale);
            //System.out.println("input:"+s);
            String[] splittedInput = f.filter(s);
            for(int i=0;i<splittedInput.length;i++){
                if(splittedInput[i].startsWith("~")){
                    buffer.append(splittedInput[i].substring(1).substring(0,splittedInput[i].length()-2));
                    buffer.append("|");
                }else if(!splittedInput[i].equals("")){
                    boundary.setText(splittedInput[i]);
                    currentString = printSegmentedWord(boundary, splittedInput[i]).toString();
                    //System.out.println("currentString:"+currentString);
                    //currentString = pipeSet.set(currentString);
                    currentString = sLink.Link(currentString);
                    currentString = nCheck.doNETagFilter(currentString);
                    //System.out.println(currentString);
                    buffer.append(currentString);
                }
                //System.out.println(splittedInput[i]);
            }
            buffer.append("\n");
        }
        for(String s : NEFilter.NEDb){
            System.out.println(s);
        }
        return buffer.toString();
    }

    public static StringBuffer printSegmentedWord(BreakIterator boundary, String source) {
        StringBuffer strout = new StringBuffer();
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
                strout.append(source.substring(start, end) + "|");
        }
        return strout;
    }
}