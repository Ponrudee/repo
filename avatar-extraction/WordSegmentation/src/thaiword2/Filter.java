package thaiword2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Benq
 */
public class Filter {

    private BufferedReader ABReader;
    private String[] ABWordList = null;
//    private BufferedReader NEReader;
//    private String[] NEWordList = null;

    public Filter() throws UnsupportedEncodingException, IOException {
        ABinit();
        //NEinit();
    }

    private void ABinit() throws IOException, UnsupportedEncodingException {
        StringBuffer ABbuffer = new StringBuffer();
        String current;
        ABReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ab_tag1.txt"), "UTF-8"));
        while ((current = ABReader.readLine()) != null) {
            ABbuffer.append(current);
        }
        ABWordList = ABbuffer.toString().split("\\|");
    }
//
//    private void NEinit() throws IOException , UnsupportedEncodingException{
//         StringBuffer NEbuffer = new StringBuffer();
//         String current;
//         NEReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ne_tag.txt"),"UTF-8"));
//         while((current = ABReader.readLine()) != null){
//             NEbuffer.append(current);
//         }
//         NEWordList = NEbuffer.toString().split("\\|");
//    }

    public String[] filter(String inputX) {
        StringBuilder input = new StringBuilder(inputX);
        input = exception(input);
        input = NE(input);
        input = AB(input);
        input = splitSymbol(input);
        //System.out.println(input);
        return input.toString().split("\\|");
    }

    public StringBuilder NE(StringBuilder input) {
        for (String s : NEFilter.NEDb) {
            int index = 0;
            while ((index = input.indexOf(s, index)) != -1) {
                if (index == 0 || input.charAt(index - 1) != '~') {
                    input.insert(index + s.length(), "~|");
                    input.insert(index, "|~");
                    index += 4;
                }
                index += s.length();
            }
        }
        return input;
    }

    private StringBuilder exception(StringBuilder input) {
        String[] exceptionSet = {"ความ"};
        for (String s : exceptionSet) {
            int index = 0;
            while ((index = input.indexOf(s, index)) != -1) {
                input.insert(index + s.length(), "|");
                input.insert(index, "|");
                index += s.length() + 2;
            }
        }
        return input;
    }

    private StringBuilder AB(StringBuilder input) {
        for (String s : ABWordList) {
            int index = 0;
            while ((index = input.indexOf(s, index)) != -1) {
                //System.out.println("Found:"+s);
                if (index == 0 || Character.isWhitespace(input.charAt(index - 1))) {
                    input.insert(index + s.length(), "~|");
                    input.insert(index, "|~");
                    index += 4;
                }
                index += s.length();
            }
        }
        return input;
    }

    @SuppressWarnings("empty-statement")
    private StringBuilder splitSymbol(StringBuilder input) {
        for (int i = 0; i < input.length(); i++) {
            char currentchar = input.charAt(i);
            if (currentchar == '~') {
                for (i = i + 1; input.charAt(i) != '~'; i++);
                i++;
            } else if (!ThaiWordFileReader.isSymbol(currentchar) && i + 1 != input.length() && ThaiWordFileReader.isSymbol(input.charAt(i + 1))) {
                input.insert(i + 1, "|");
                i++;
            } else if (currentchar != '|' && ThaiWordFileReader.isSymbol(currentchar) && i + 1 != input.length() && currentchar != input.charAt(i + 1)) {
                input.insert(i + 1, "|");
                i++;
            }
        }
        return input;
    }
}
