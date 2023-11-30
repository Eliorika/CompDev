package ru.rsreu.Babaian.Tokens;

import java.util.Objects;

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

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

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

    public static TokenType defineTokenNum(String t){
        if("integer".equals(t))
            return TOKEN_INT;
        else return TOKEN_DOUBLE;
    }

    public static String formatToken(String s){
        return "<"+s+">";
    }

    public static String formatToken(int i){
        return "<id,"+i+">";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Token tokenObj = (Token) obj;
        return (tokenType == tokenObj.tokenType && Objects.equals(token, tokenObj.token));
    }

    public boolean isOne(){
        var res = Double.parseDouble(token.substring(1, token.length() - 1));
        return Math.abs(res - 1) < 1e-5;
    }

    public boolean isZero(){
        var res = Double.parseDouble(token.substring(1, token.length() - 1));
        return Math.abs(res) < 1e-5;
    }


}
