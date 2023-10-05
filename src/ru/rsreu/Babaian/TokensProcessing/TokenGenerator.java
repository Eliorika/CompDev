package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TokenGenerator {
    private static Map<String, String> tokenFormats = new HashMap<String,String>();
    private int id = 0;
    static {
        tokenFormats.put("id", "<id,%d> - identifier %s");
        tokenFormats.put("+", "addition operation");
        tokenFormats.put("-", "subtraction operation");
        tokenFormats.put("*", "multiply operation");
        tokenFormats.put("/", "divide operation");
        tokenFormats.put("(", "opening bracket");
        tokenFormats.put(")", "closing bracket");
        tokenFormats.put("const", "const %s type");
    }

    private String defineElement(String element){
        String type = element;
        if (tokenFormats.containsKey(element))
            return tokenFormats.get(type);

        String token = defineNum(element);
        if(token == null)
            token = isIdentifier(element);
        return token;
    }

    private String writeTokens(String input){
        String[] elements = input.split(" ");
        StringBuilder result = new StringBuilder();
        for (String el: elements)
            result.append(defineElement(el)).append("\n");
        return result.toString();
    }

    public void writeToken(String fileNameIn, String fileNameOut) throws IOException {
        String content = writeTokens(FileReadWriteProcessor.readFromFile(fileNameOut));
        FileReadWriteProcessor.writeToFile(fileNameOut, content);
    }

    private String defineNumInt(String el){
        try {
            Integer.parseInt(el);
            return String.format(tokenFormats.get("const"), "integer");
        } catch (NumberFormatException e){
            return null;
        }
    }

    private String defineNumDouble(String el){
        try {
            Integer.parseInt(el);
            return String.format(tokenFormats.get("const"), "double");
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

    public String isIdentifier(String input) {
        String regex = "^[a-zA-Z_][a-zA-Z0-9_]*$";
        if (Pattern.matches(regex, input)){
            this.id++;
            return String.format(tokenFormats.get("id"), this.id, input);
        }
            return null;
    }



}
