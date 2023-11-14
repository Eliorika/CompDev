package ru.rsreu.Babaian.Tokens;

public enum TokenType {
    TOKEN_MINUS,
    TOKEN_PLUS,
    TOKEN_DIV,
    TOKEN_MULT,
    TOKEN_CONST,
    TOKEN_INT,
    TOKEN_DOUBLE,
    TOKEN_ID_I,
    TOKEN_ID_F,
    TOKEN_LEFT_BR,
    TOKEN_RIGHT_BR,
    TOKEN_INT_TO_FLOAT;

    public static TokenType getTokenIDs(String token){
        char c = token.charAt(token.length() - 2);
        if('f' == c || 'F' == c)
            return TOKEN_ID_F;
        else if ('i' == c || 'I' == c)
            return TOKEN_ID_I;
        return null;
    }

}
