
package thaiword2;

/**
 *
 * @author Benq
 */
public class SymbolLink {

    public SymbolLink(){
    }

    public String Link(String input){
        StringBuilder builder = new StringBuilder(input);
        //System.out.println(input);
        //String[] stringlist = input.split("\\|");
        for(int i=0;i<builder.length();i++){
            if(builder.charAt(i) == '|' ){
                // symbol linking
                if(i!=0 && !ThaiWordFileReader.isThaiAlpha(builder.charAt(i-1)) && !Character.isLetterOrDigit(builder.charAt(i-1))){
                    if(i+1 != builder.length() && builder.charAt(i-1) == builder.charAt(i+1)){
                        builder.deleteCharAt(i);
                    }
                }
                input = builder.toString();
                // one character link
                boolean isNotSingle = true;
                int singles = 0;
                int j;
                int tempi = i;
                while(true){
                    int count = 0;
                    for(j=tempi+1;j<input.length() && input.charAt(j)!='|' && ThaiWordFileReader.isThaiAlpha(input.charAt(j));j++){
                        if(input.charAt(i)!='์')
                            count++;
                        if(count > 1){
                            isNotSingle = false;
                        }
                        //System.out.println("char at j = " + input.charAt(j) );
                    }
                    tempi = j;
                    //System.out.println(j);
//                    System.out.println("tempi = "+ tempi);
//                    System.out.println("single = " + singles);
                    if(count == 1)
                        singles ++;
                    if(count==0 || !isNotSingle || j== input.length())
                        break;
                }
                //System.out.println(singles);
                if(singles > 1){
                    String newString = input.substring(i+1,j);
                    String oldString = newString;
                    oldString = oldString.replaceAll("\\|", "\\\\|");
                    newString = newString.replaceAll("\\|", "");
                    input.replaceAll(oldString, newString);
                }
                else if(singles == 1 && input.charAt(i+1)!='ๆ'){
                     builder = new StringBuilder(input);
                     builder.deleteCharAt(i);
                     input = builder.toString();
                }
                //System.out.println(input);
            }
        }
        return input;
    }
}
