package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;

public class TokenGenerator {
    private static Map<String, String> tokenFormats = new HashMap<String,String>();
    private List<String> tokens = new ArrayList<String>();
    private Map<String, Integer> idNames = new LinkedHashMap<String, Integer>();
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

//    private String defineElement(String element){
//        String type = element;
//        if (tokenFormats.containsKey(element))
//            return tokenFormats.get(type);
//
//        String token = defineNum(element);
//        if(token == null)
//            token = isIdentifier(element);
//        return token;
//    }

    private void writeTokens(String input){
//        String[] elements = input.split(" ");
//        try {
//            for (String el: elements){
//                String def = defineElement(el);
//                if(def != null)
//                    this.token.add(def);
//                else
//                    throw new RuntimeException(el);
//            }
//        } catch (RuntimeException e){
//            System.out.println("Lexical error! Cant process symbol: " + e.getMessage());
//            System.exit(0);
//        }

        List<String> lineTokens = tokenizeLine(input);
        int id = 0;
        for (String token : lineTokens) {

            if (!isValidToken(token)) {
                System.out.println("Lexical error! Wrong symbol " + token);
                return;
            }

            if (!token.trim().isEmpty()) {

                //tokens.add(token);
                if (tokenFormats.containsKey(token))
                    tokens.add(tokenFormats.get(token));
                else if (isIdentifier(token)) {
                    if(!idNames.containsKey(token)){
                        id++;
                        idNames.put(token, id);
                    }
                    tokens.add(String.format(tokenFormats.get("id"), idNames.get(token), token));
                } else {
                    String type = defineNum(token);
                    tokens.add(String.format(tokenFormats.get("const"), token, type));
                };
            }
        }

    }

    private static List<String> tokenizeLine(String line) {
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
        writeTokens(FileReadWriteProcessor.readFromFile(fileNameIn));
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

//    public String isIdentifier(String input) {
//        String regex = "^[a-zA-Z_][a-zA-Z0-9_]*$";
//        input = input.replace("\n", "");
//        if (Pattern.matches(regex, input)){
//            this.id++;
//            this.idNames.add(id + " - " + input);
//            return String.format(tokenFormats.get("id"), this.id, input);
//
//        }
//            return null;
//    }

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
