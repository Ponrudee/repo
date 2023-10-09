package thaiword2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Benq
 */
public class NEFilter {

    private ArrayList<String> NEtag1 = null;
    private ArrayList<String> NEtag2 = null;
    public static ArrayList<String> NEDb = null;

    public NEFilter() throws UnsupportedEncodingException, IOException {
        NEtag1 = new ArrayList<String>();
        NEtag2 = new ArrayList<String>();
        NEDb = new ArrayList<String>();
        initTag();
    }

    public String doNETagFilter(String input) {
        input = doTagV1(input);
        input = doTagV2(input);
        return input;
    }

    @SuppressWarnings("empty-statement")
    private String doTagV1(String input) {
        //System.out.println("input = " + input);
        for (String s : NEtag1) {
            int index = 0;
            //System.out.println(s);
            while (((index = input.indexOf(s + "|", index)) != -1) && (index == 0 || input.charAt(index - 1) == '|')) {
                int i;
                int pipecount = 0;
                int space = 0;
                for (i = index; i < index + 21 + s.length() + pipecount && i < input.length(); i++) {
                    //&& (input.charAt(i) == '|' || !ThaiWordFileReader.isSymbol(input.charAt(i)))
                    if (input.charAt(i) == '|') {
                        pipecount++;
                    } else if (Character.isWhitespace(input.charAt(i))) {
                        space++;
                        if (space == 2) {
                            break;
                        }
                        for (; Character.isWhitespace(input.charAt(i)); i++);
                    }
                    //System.out.println(i + " = " + input.charAt(i));
                }
                //System.out.println("space:"+space);
                // Pipe settings
                if (space == 2 || (space == 1 && i == input.length())) {
                    // name & sirname

                    String scope = input.substring(index, i);
                    String replaceString = scope;

                    replaceString = replaceString.replaceAll("\\|", "\\\\|");
                    scope = scope.replaceAll("\\|", "").trim();

//                    System.out.println("target:"+replaceString);
//                    System.out.println("replace with:"+scope);
                    if (!NEtag1.contains(scope) && !NEtag1.contains(scope.split(" ")[0]) && scope.split(" ")[0].length()-s.length() < 10){
                        addToDB(scope);
                        //addToDB(scope.substring(s.length()));
                        addToDB(scope.split(" ")[0]);
                        //addToDB(scope.split(" ")[0].substring(s.length()));

                        input = input.replaceAll(replaceString, scope + "|");
                    }
                } else if (space == 1 || i == input.length()) {
                    // name only

                    String scope = input.substring(index, i);
                    scope = scope.split(" ")[0];
                    String replaceString = scope;

                    replaceString = replaceString.replaceAll("\\|", "\\\\|");
                    scope = scope.replaceAll("\\|", "");

                    if (!NEtag1.contains(scope) && scope.length()-s.length() < 10) {
                        addToDB(scope);
                        //addToDB(scope.substring(s.length()));

                        input = input.replaceAll(replaceString, scope + "|");
                    }
                } else {
                    // a pipe
                    int j;
                    for (j = index + s.length() + 2; j < input.length() && input.charAt(j) != '|'; j++);
                    
                    String scope = input.substring(index,j+1);
                    String replaceString = scope;
                    
                    replaceString = replaceString.replaceAll("\\|", "\\\\|");
                    scope = scope.replaceAll("\\|", "");

//                    System.out.println("target:"+replaceString);
//                    System.out.println("replace with:"+scope);
                    if(!NEtag1.contains(scope)){
                        addToDB(scope);
                        //addToDB(scope.substring(s.length()));

                        input = input.replaceAll(replaceString, scope + "|");
                    }
                }

                index += s.length();
            }
        }
        return input;
    }

    @SuppressWarnings("empty-statement")
    private String doTagV2(String input) {
        for(String s : NEtag2){
            int index = 0;
            //System.out.println(s);
            while (((index = input.indexOf(s + "|", index)) != -1) && (index == 0 || input.charAt(index - 1) == '|')) {
                //System.out.println("found :" + s);
                int i;
                int pipecount = 0;
                int space = 0;
                for (i = index; i < index + 21 + s.length() + pipecount && i < input.length(); i++) {
                    //&& (input.charAt(i) == '|' || !ThaiWordFileReader.isSymbol(input.charAt(i)))
                    if (input.charAt(i) == '|') {
                        pipecount++;
                    } else if (Character.isWhitespace(input.charAt(i))) {
                        space++;
                        break;
                    }
                    //System.out.println(i + " = " + input.charAt(i));
                }
                //System.out.println(i);
                if(space == 1 || i== input.length()){

                    String scope = input.substring(index, i);
                    scope = scope.split(" ")[0];
                    String replaceString = scope;

                    replaceString = replaceString.replaceAll("\\|", "\\\\|");
                    scope = scope.replaceAll("\\|", "");

                    if (!NEtag2.contains(scope) && scope.length() > 10) {
                        addToDB(scope);

                        input = input.replaceAll(replaceString, scope + "|");
                    }
                }else{
                    // a pipe
                    int j;
                    for (j = index + s.length() + 2; j < input.length() && input.charAt(j) != '|'; j++);

                    String scope = input.substring(index,j+1);
                    String replaceString = scope;

                    replaceString = replaceString.replaceAll("\\|", "\\\\|");
                    scope = scope.replaceAll("\\|", "");

//                    System.out.println("target:"+replaceString);
//                    System.out.println("replace with:"+scope);
                    if(!NEtag2.contains(scope) && scope.length() > 10){
                        addToDB(scope);

                        input = input.replaceAll(replaceString, scope + "|");
                    }
                }
                index += s.length();
            }
        }
        return input;
    }

    private void addToDB(String addingString) {
        if(!NEDb.contains(addingString))
            NEDb.add(addingString);
        Collections.sort(NEDb,new Comparator<String>(){

            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }

            });
    }

    private void initTag() throws UnsupportedEncodingException, IOException {
        BufferedReader tagReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ne_tag1.txt"), "UTF-8"));
        String temp;
        tagReader.read(new char[1], 0, 1);
        while ((temp = tagReader.readLine()) != null) {
            NEtag1.add(temp);
        }
        tagReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ne_tag2.txt"), "UTF-8"));
        tagReader.read(new char[1], 0, 1);
        while ((temp = tagReader.readLine()) != null) {
            NEtag2.add(temp);
        }
    }
}
