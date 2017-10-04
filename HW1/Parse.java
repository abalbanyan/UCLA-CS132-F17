import java.util.regex.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Scanner;

class Parse {
    public HashMap<String, Pattern> tokenTypes; // Since we don't have identifiers, we're just going to represent tokens using unique string IDs.

    private void addTokenType(String tokenID, String regex) {
        this.tokenTypes.put(tokenID, Pattern.compile(regex));
    }

    public Parse() {
        this.tokenTypes = new HashMap<String, Pattern>();

        // Define the alphabet. Note we only match the first instance of the symbol in the string.
        this.addTokenType("{", "^(\\{)"); // {
        this.addTokenType("}", "^(\\})"); // }
        this.addTokenType("System.out.println", "^(System.out.println)"); // System.out.println
        this.addTokenType("(", "^(\\()"); // (
        this.addTokenType(")", "^(\\))"); // )
        this.addTokenType(";", "^(;)"); // ;
        this.addTokenType("if", "^(if)"); // if
        this.addTokenType("else", "^(else)"); // else
        this.addTokenType("while", "^(while)"); // while
        this.addTokenType("true", "^(true)"); // true
        this.addTokenType("false", "^(false)"); // false
        this.addTokenType("!", "^(!)"); // !
    }

    public LinkedList<String> tokenize(String program) throws Exception {
        program = program.replaceAll("\\s",""); // Remove all spacing.
        
        LinkedList<String> tokens = new LinkedList<String>();

        while (!program.equals("")) {
            Boolean tokenFound = false;
            // Iterate through each member of the alphabet until a match is found.
            for(Entry<String, Pattern> tokenType : tokenTypes.entrySet()) {
                Matcher matcher = tokenType.getValue().matcher(program);
                if (matcher.find()) {
                    tokenFound = true;
                    tokens.add(tokenType.getKey()); // Add this symbol to the tokens list.
                    program = matcher.replaceFirst(""); // Cut out the matched symbol.
                    break;
                }
            }
            // If no match was found, there must have been an unexpected token. Throw an error.
            if (!tokenFound) {
                throw new Exception("Parse error. Unexpected token found.");
            }
        }

        return tokens;
    }

    public static void main(String [] args) {
        Parse parser = new Parse();

        String program = "";
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()) {
            program += input.nextLine();
        }
        System.out.println(program);

        try {
            LinkedList<String> tokens = parser.tokenize(program);
            for (String i : tokens) {
                System.out.println(i);
            }
        } catch (Exception e) {
            System.out.println("Parse error.");
        }
    }
}