//package thaiword2;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///**
// *
// * @author Benq
// */
//public class PipesetByWrongCase {
//
//    BufferedReader wrongsource;
//    ArrayList<String> wrongStorage = new ArrayList<String>();
//    ArrayList<String> correctStorage = new ArrayList<String>();
//
//    public PipesetByWrongCase() throws Exception {
//        String wrongWord;
//        String correctWord;
//        wrongsource = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("WrongWordCorrectWord.txt"), "UTF-8"));
//        wrongsource.read(new char[1], 0, 1);
//        while ((wrongWord = wrongsource.readLine()) != null && (correctWord = wrongsource.readLine()) != null) {
//            wrongStorage.add(wrongWord);
//            correctStorage.add(correctWord);
//        }
//    }
//
//    public String set(String settingString) throws IOException {
//        StringBuilder builder = new StringBuilder(settingString);
//        int index;
//        for (int i = 0; i < wrongStorage.size(); i++) {
//            String wrongWord = wrongStorage.get(i);
//            String correctWord = correctStorage.get(i);
//            index = 0;
//            while ((index = builder.indexOf(wrongWord, index)) != -1) {
//                builder.replace(index, index + wrongWord.length(), correctWord);
//                index += correctWord.length();
//                System.out.println("W:"+wrongWord);
//                System.out.println("C:"+correctWord);
//                System.out.println("AT:"+i);
//            }
//        }
//        return builder.toString();
//    }
//}
