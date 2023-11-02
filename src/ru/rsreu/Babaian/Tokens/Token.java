package ru.rsreu.Babaian.Tokens;

import static ru.rsreu.Babaian.Tokens.TokenType.*;

public class Token {
    public Token(TokenType tokenType, String token) {
        this.tokenType = tokenType;
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    private TokenType tokenType;

    public String getToken() {
        return token;
    }

    private String token;


    public static TokenType defineTypeOperand(String token){
        if("<+>".contains(token))
            return TOKEN_PLUS;
        else if("<->".contains(token))
            return TOKEN_MINUS;
        else if("<*>".contains(token))
            return TOKEN_MULT;
        else if("<(>".contains(token))
            return TOKEN_LEFT_BR;
        else if("<)>".contains(token))
            return TOKEN_RIGHT_BR;
        else return TOKEN_DIV;
    }

    public static String formatToken(String s){
        return "<"+s+">";
    }

    public static String formatToken(int i){
        return "<id,"+i+">";
    }
}
