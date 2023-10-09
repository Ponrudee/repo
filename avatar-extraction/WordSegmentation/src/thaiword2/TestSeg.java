/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thaiword2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author Administrator
 */
public class TestSeg {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        FileInputStream fstream = new FileInputStream(args[0]);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        String str="";
        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            str +=strLine;
            System.out.println(strLine);
        }
        //Close the input stream
        in.close();



        String input = str;
        input.replaceAll("\\|", "");
        try {
            SubMain.init();
            System.out.println(SubMain.subMain(input));
            File file = new File("out.txt");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.write(SubMain.subMain(input));
            //Close the output styoัีream
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
