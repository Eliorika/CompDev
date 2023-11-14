package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;

import java.text.ParseException;
import java.util.*;

public class SyntaxAnalyzer {
    private List<Token> tokens;
    private int current = 0;
    private boolean error = false;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analyze() {
        try {
            expression();
            if (!isAtEnd()) {
                error("Text after the end of the expression");
            }
            //System.out.println("Syntax analyses ended successfully.");
        } catch (ParseException e) {
            int offset = e.getErrorOffset();
            if(isAtEnd())
                offset-=1;
            System.err.println("Syntax error: " + e.getMessage() + " at " + tokens.get(offset).getToken());
            System.exit(0);
            error = true;
        }
    }

    private void expression() throws ParseException {
        term();
        while (match(TokenType.TOKEN_PLUS, TokenType.TOKEN_MINUS, TokenType.TOKEN_MULT, TokenType.TOKEN_DIV)) {
            term();
        }
    }

    private void term() throws ParseException {
        factor();
        while (match(TokenType.TOKEN_INT, TokenType.TOKEN_DOUBLE, TokenType.TOKEN_ID_I, TokenType.TOKEN_ID_F, TokenType.TOKEN_LEFT_BR)) {
            // Empty body, just matching tokens.
        }
    }

    private void factor() throws ParseException {
        if (match(TokenType.TOKEN_INT, TokenType.TOKEN_DOUBLE, TokenType.TOKEN_ID_I, TokenType.TOKEN_ID_F)) {
            // Empty body, just matching tokens.
        } else if (match(TokenType.TOKEN_LEFT_BR)) {
            expression();
            consume(TokenType.TOKEN_RIGHT_BR, "Waited for closing bracket");
        } else {
            error("Waited for operand");
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().getTokenType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private void consume(TokenType type, String errorMessage) throws ParseException {
        if (check(type)) {
            advance();
        } else {
            error(errorMessage);
        }
    }

    private void error(String message) throws ParseException {
        throw new ParseException(message, current);
    }

//    public static void main(String[] args) {
//        String input = "var1 + ((9.5 - 5 * (var2 - 0.6)) / var3)";
//        TokenGenerator lexer = new TokenGenerator();
//        List<Token> tokens = lexer.getTokens(input);
//        if(tokens != null){
//            SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
//            analyzer.analyze();
//        }
//
//    }
}
