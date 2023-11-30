package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.rsreu.Babaian.Tokens.Token.defineTokenNum;
import static ru.rsreu.Babaian.Tokens.Token.formatToken;
import static ru.rsreu.Babaian.Tokens.TokenType.TOKEN_ID_F;
import static ru.rsreu.Babaian.Tokens.TokenType.getTokenIDs;

public class TokenGenerator {
    private static final Map<String, String> tokenFormats = new HashMap<String,String>();
    private final List<String> tokens = new ArrayList<String>();
    private final List<Token> tokensTypes = new ArrayList<Token>();

//    public Map<String, Integer> getIdNames() {
//        return idNames;
//    }

    //private final Map<String, Integer> idNames = new LinkedHashMap<String, Integer>();

    public Map<Integer, Token> getIdNames() {
        return idNames;
    }

    private final Map<Integer, Token> idNames = new HashMap<>();


    static {
        tokenFormats.put("id", "%s - identifier %s");
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
                    boolean hasType = identifierHasType(token);
                    TokenType t = TokenType.TOKEN_ID_I;
                    String inc = "[int]";
                    if(hasType){
                        t = getTokenIDs(token);
                        token = token.substring(0, token.length() - 3);
                    }



                    if(t == TOKEN_ID_F) {
                        inc = "[float]";
                    }



                    String finalToken = token;
                    var inTokens = idNames.values().stream().filter(tk -> finalToken.equals(tk.getToken())).collect(Collectors.toList());
                    if(!inTokens.isEmpty()&&inTokens.get(0).getTokenType()!=t){
                        //containsKey(token+checkT)){

                        System.err.println("Same var with different type: "+ token);
                        System.exit(0);
                    }

                    Token tokenVar;

                    if(!idNames.values().stream().anyMatch(tk -> finalToken.equals(tk.getToken()))){
                        id++;
                        tokenVar = new Token(t, formatToken(id));
                        idNames.put(id, tokenVar);
                    } else {
                        tokenVar = inTokens.get(0);
                    }
                    tokens.add(String.format(tokenFormats.get("id"), tokenVar.getToken(), token+inc));
                    tokensTypes.add(tokenVar);
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

    private String fromMapNames(Map<Integer, Token> m){
        StringBuilder stringBuilder = new StringBuilder();
        for(Integer obj : m.keySet()){
            String type = ", integer";
            stringBuilder.append(obj).append(" - ").append(m.get(obj).getToken());
            if(m.get(obj).getTokenType() == TOKEN_ID_F){
                type = ", float";
            }
            stringBuilder.append(type).append("\n");
        }
        return stringBuilder.toString();
    }

    public void writeToken(String fileNameIn, String fileNameOutTok,String fileNameOutId) throws IOException {
        formTokens(FileReadWriteProcessor.readFromFile(fileNameIn));
        FileReadWriteProcessor.writeToFile(fileNameOutTok, fromColl(this.tokens));
        FileReadWriteProcessor.writeToFile(fileNameOutId, fromMapNames(this.idNames));
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
        return token.matches("[-+*/()]|^[a-zA-Z_][a-zA-Z0-9_]*(\\[i\\]|\\[I\\]|\\[f\\]|\\[F\\])?$|(\\d+\\.?\\d*)|\\d+");
    }

    private static boolean isOperator(char c) {
        return "+-*/".indexOf(c) != -1;
    }

    private static boolean isBracket(char c) {
        return "()".indexOf(c) != -1;
    }
    private static boolean isIdentifier(String token) {
        return token.matches("^[a-zA-Z_][a-zA-Z0-9_]*(\\[i\\]|\\[I\\]|\\[f\\]|\\[F\\])?$");
    }

    private static boolean identifierHasType(String token){
        return token.matches(".*\\[(i|I|f|F)\\]$");
    };



}
