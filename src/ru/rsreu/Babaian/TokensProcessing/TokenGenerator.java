package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.awt.im.InputContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TokenGenerator {
    private static Map<String, String> tokenFormats = new HashMap<String,String>();
    private StringBuilder token = new StringBuilder();
    private StringBuilder idNames = new StringBuilder();
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

    private void writeTokens(String input){
        String[] elements = input.split(" ");
        try {
            for (String el: elements){
                if(defineElement(el) != null)
                    this.token.append(defineElement(el)).append("\n");
                else
                    throw new RuntimeException(el);
            }
        } catch (RuntimeException e){
            System.out.println("Lexical error! Cant process symbol: " + e.getMessage());
            System.exit(0);
        }

    }

    public void writeToken(String fileNameIn, String fileNameOutTok,String fileNameId) throws IOException {
        writeTokens(FileReadWriteProcessor.readFromFile(fileNameIn));
        FileReadWriteProcessor.writeToFile(fileNameOutTok, this.token.toString());
        FileReadWriteProcessor.writeToFile(fileNameOutTok, this.idNames.toString());
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
            this.idNames.append(id + " - " + input).append("\n");
            return String.format(tokenFormats.get("id"), this.id, input);

        }
            return null;
    }


}
