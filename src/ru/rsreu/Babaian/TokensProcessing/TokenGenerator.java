package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;

import static ru.rsreu.Babaian.Tokens.Token.defineTokenNum;
import static ru.rsreu.Babaian.Tokens.Token.formatToken;

public class TokenGenerator {
    private static final Map<String, String> tokenFormats = new HashMap<String,String>();
    private final List<String> tokens = new ArrayList<String>();
    private final List<Token> tokensTypes = new ArrayList<Token>();
    private final Map<String, Integer> idNames = new LinkedHashMap<String, Integer>();
    static {
        tokenFormats.put("id", "<id,%d> - identifier %s");
        tokenFormats.put("+", "<+> - addition operation");
        tokenFormats.put("-", "<-> - subtraction operation");
        tokenFormats.put("*", "<*> - multiply operation");
        tokenFormats.put("/", "</> - divide operation");
        tokenFormats.put("(", "<(> - opening bracket");
        tokenFormats.put(")", "<)> - closing bracket");
        tokenFormats.put("const", "<%s> - const %s type");
    }


    private void formTokens(String input){

        List<String> lineTokens = tokenizeLine(input);
        int id = 0;
        for (String token : lineTokens) {

            if (!isValidToken(token)) {
                System.out.println("Lexical error! Wrong symbol " + token);
                System.exit(0);
                return;
            }

            if (!token.trim().isEmpty()) {
                if (tokenFormats.containsKey(token)){
                    tokens.add(tokenFormats.get(token));
                    tokensTypes.add(new Token(Token.defineTypeOperand(formatToken(token)), token));
                } else if (isIdentifier(token)) {
                    if(!idNames.containsKey(token)){
                        id++;
                        idNames.put(token, id);
                    }
                    tokens.add(String.format(tokenFormats.get("id"), id, token));
                    tokensTypes.add(new Token(TokenType.TOKEN_ID, formatToken(id)));
                } else {
                    String type = defineNum(token);
                    tokens.add(String.format(tokenFormats.get("const"), token, type));

                    tokensTypes.add(new Token(defineTokenNum(type), formatToken(token)));
                }
            }
        }

    }


    public List<Token> getTokens(String fileNameIn) throws IOException {
        formTokens(FileReadWriteProcessor.readFromFile(fileNameIn));
        return tokensTypes;
    }

    public List<String> tokenizeLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (isOperator(c) || isBracket(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (!Character.isWhitespace(c)) {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private String fromColl(Collection c){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object o : c)
            stringBuilder.append(o).append("\n");
        return stringBuilder.toString();
    }

    private String fromMap(Map m){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object obj : m.keySet())
            stringBuilder.append(m.get(obj)).append(" - ").append(obj).append("\n");
        return stringBuilder.toString();
    }

    public void writeToken(String fileNameIn, String fileNameOutTok,String fileNameOutId) throws IOException {
        formTokens(FileReadWriteProcessor.readFromFile(fileNameIn));
        FileReadWriteProcessor.writeToFile(fileNameOutTok, fromColl(this.tokens));
        FileReadWriteProcessor.writeToFile(fileNameOutId, fromMap(this.idNames));
    }

    private String defineNumInt(String el){
        try {
            Integer.parseInt(el);
            return "integer";
        } catch (NumberFormatException e){
            return null;
        }
    }

    private String defineNumDouble(String el){
        try {
            Double.parseDouble(el);
            return "double";
        } catch (NumberFormatException e){
            return null;
        }
    }

    private String defineNum(String el){
        String res = defineNumInt(el);
        if (res == null)
            res = defineNumDouble(el);
        return res;
    }


    private static boolean isValidToken(String token) {
        return token.matches("[-+*/()]|[a-zA-Z_][a-zA-Z0-9_]*|(\\d+\\.?\\d*)|\\d+");
    }

    private static boolean isOperator(char c) {
        return "+-*/".indexOf(c) != -1;
    }

    private static boolean isBracket(char c) {
        return "()".indexOf(c) != -1;
    }
    private static boolean isIdentifier(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }



}
