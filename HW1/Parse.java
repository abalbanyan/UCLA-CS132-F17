import java.util.regex.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Iterator;

class Parse {
    public enum Token {
        // For some reason using Strings as the constructors messes up syntax highlighting.
        CURLYBOI_L(0), CURLYBOI_R(1),
        ROUNDBOI_L(2), ROUNDBOI_R(3),
        PRINTLN(4), SEMICOLON(5),
        IF(6), ELSE(7), WHILE(8),
        TRUE(9), FALSE(10), NOT(11),
        EMPTY(12);

        private final String[] regexStrings = {
            "^(\\{)", "^(\\})", "^(\\()", "^(\\))",
            "^(System.out.println)",
            "^(;)", "^(if)", "^(else)", "^(while)",
            "^(true)", "^(false)", "^(!)", "^$"
        };

        private final Pattern regex;

        private Token(int regexID) {
            this.regex = Pattern.compile(this.regexStrings[regexID]);
        }
    }

    public LinkedList<Token> tokens;

    // Did the parse succeed?
    public Boolean parseSucceed = true;
    public String errorMsg = "";

    public Parse(String program) {
        try {
            this.tokens = this.tokenize(program);
            this.parseSucceed = this.parseTokens();
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
                if (tokenType == Token.EMPTY) continue;

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
    Iterator<Token> tokensQueue;
    private int tokenNum = 0;
    private Token curToken; // null if no more tokens.

    // Call to begin parsing tokens. We're not building an AST here.
    private Boolean parseTokens() {
        tokensQueue = this.tokens.iterator();
        if (tokensQueue.hasNext()) {
            // Determine what the first non-terminal of the program is and begin parsing.
            this.curToken = tokensQueue.next(); this.tokenNum++;        
            try {
                if (this.curToken == Token.CURLYBOI_L || this.curToken == Token.PRINTLN 
                        || this.curToken == Token.IF || this.curToken == Token.WHILE) {
                    this.statement();
                } else if (this.curToken == Token.TRUE || this.curToken == Token.FALSE || this.curToken == Token.NOT) {
                    this.expression();
                }
            } catch (Exception e) {
                this.errorMsg = e.toString();
                return false;
            }
            
            // If all tokens have been consumed, the parsing succeeded.
            return (this.curToken == Token.EMPTY);
        } else {
            // Empty program. Vacuously valid.
            return true;
        }
    }

    // Returns false if no more tokens in token list.
    private void eat(Token token) throws Exception {
        if (this.curToken == token) {
            if (tokensQueue.hasNext()) {            
                this.curToken = tokensQueue.next(); this.tokenNum++; // Advance to next token.
            } else {
                this.curToken = Token.EMPTY;
            }
        } else {
            throw new Exception("Missing token \"" + token + "\".");
        }
    }
    private void statement() throws Exception {
        if (this.curToken == Token.CURLYBOI_L) {
            eat(Token.CURLYBOI_L);
            list();
            eat(Token.CURLYBOI_R);
        } else if (this.curToken == Token.PRINTLN) {
            eat(Token.PRINTLN);
            eat(Token.ROUNDBOI_L);
            expression();
            eat(Token.ROUNDBOI_R);
            eat(Token.SEMICOLON);
        } else if (this.curToken == Token.IF) {
            eat(Token.IF);
            eat(Token.ROUNDBOI_L);
            expression();
            eat(Token.ROUNDBOI_R);
            statement();
            eat(Token.ELSE);
            statement();
        } else if (this.curToken == Token.WHILE) {
            eat(Token.WHILE);
            eat(Token.ROUNDBOI_L);
            expression();
            eat(Token.ROUNDBOI_R);
            statement();
        } else {
            throw new Exception("Invalid statement. Invalid token: " + this.curToken + " tokenNum: " + this.tokenNum);
        }
    }
    private void list() throws Exception {
        if (this.curToken == Token.CURLYBOI_R) {
            // End of list.
            return;
        } else {
            statement();
            list();
        }
    }
    private void expression() throws Exception {
        if (this.curToken == Token.TRUE) {
            eat(Token.TRUE);
        } else if (this.curToken == Token.FALSE) {
            eat(Token.FALSE);
        } else if (this.curToken == Token.NOT) {
            eat(Token.NOT);
            expression();
        } else {
            throw new Exception ("Invalid expression. Invalid token: " + this.curToken + " tokenNum: " + this.tokenNum);
        }
    }

    public static void main(String [] args) {
        String program = "";
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()) {
            program += input.nextLine();
        }

        Parse parser = new Parse(program);

        System.out.println(parser.parseSucceed? "Program parsed successfully" : "Parse error");
    }
}