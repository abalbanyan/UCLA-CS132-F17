import java.util.regex.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Scanner;

class Parse {
    public enum Token {
        // For some reason using Strings as the constructors messes up syntax highlighting.
        CURLYBOI_L(0), CURLYBOI_R(1),
        ROUNDBOI_L(2), ROUNDBOI_R(3),
        PRINTLN(4), SEMICOLON(5),
        IF(6), ELSE(7), WHILE(8),
        TRUE(9), FALSE(10), NOT(11);

        private final String[] regexStrings = {
            "^(\\{)", "^(\\})", "^(\\()", "^(\\))",
            "^(System.out.println)",
            "^(;)", "^(if)", "^(else)", "^(while)",
            "^(true)", "^(false)", "^(!)"
        };

        private final Pattern regex;

        private Token(int regexID) {
            this.regex = Pattern.compile(this.regexStrings[regexID]);
        }
    }

    public LinkedList<Token> tokens;

    // Did the parse succeed?
    private Boolean parseSucceed = true;
    public Boolean parseSuccessful() {
        return this.parseSucceed;
    }

    // Driver.
    public Parse(String program) {
        try {
            this.tokens = this.tokenize(program);
        } catch (Exception e) {
            this.parseSucceed = false;
        }
    }

    // Tokenizing logic.
    public LinkedList<Token> tokenize(String program) throws Exception {
        program = program.replaceAll("\\s",""); // Remove all spacing.
        
        LinkedList<Token> tokens = new LinkedList<Token>();

        while (!program.equals("")) {
            Boolean tokenFound = false;
            // Iterate through each member of the alphabet until a match is found.
            for (Token tokenType : Token.values()) {
                Matcher matcher = tokenType.regex.matcher(program);
                if (matcher.find()) {
                    tokenFound = true;
                    tokens.add(tokenType); // Add this symbol to the tokens list.
                    program = matcher.replaceFirst(""); // Cut out the matcher symbol.
                }
            }
            // If no match was found, there must have been an unexpected token. Throw an error.
            if (!tokenFound) {
                throw new Exception("Parse error. Unexpected token found.");
            }
        }

        return tokens;
    }

    // Parsing logic.
    Iterator tokensQueue = tokens.iterator();
    private Token curToken;
    private Boolean eat(Token t) {
        if (this.curToken == t) {
            // Advance to next token.
            this.curToken = tokensQueue.next();
            return true;
        } else {
            return false;
        }
    }
/*

S ::= { L }
| System.out.println ( E ) ;
| if ( E ) S else S
| while ( E ) S
L ::= S L | ϵ    (nullable)
E ::= true | false | ! E

*/
    private void S() {
        
    }

    public static void main(String [] args) {
        String program = "";
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()) {
            program += input.nextLine();
        }

        Parse parser = new Parse(program);
        System.out.println(parser.parseSuccessful());
    }
}