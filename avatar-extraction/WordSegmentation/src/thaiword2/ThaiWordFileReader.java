/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package thaiword2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**Z /,mnbv
 *
 * @author Benq
 */
public class ThaiWordFileReader {
    BufferedReader bufferReader;

    public ThaiWordFileReader(String filePath){
        try{
            bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    public String readNext(){
        try {
            return bufferReader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void closeFile(){
        try {
            bufferReader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isThaiAlpha(char c){
        if(c>='ก'&&c<='์')
            return true;
        return false;
    }

    public static boolean isSymbol(char c){
        if(!Character.isLetterOrDigit(c) && !ThaiWordFileReader.isThaiAlpha(c) && !Character.isWhitespace(c))
            return true;
        return false;
    }
}
