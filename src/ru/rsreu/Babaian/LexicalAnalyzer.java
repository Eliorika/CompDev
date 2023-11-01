package ru.rsreu.Babaian;

import java.util.*;

public class LexicalAnalyzer {
    public static void main(String[] args) {
        String expression = "var1 + (9.5 - 5 * (var2 - 0.6)) / var3";

        List<String> tokens = new ArrayList<>();
        Map<String, Integer> symbolTable = new HashMap<>();

        List<String> lines = Arrays.asList(expression.split("\\n"));
        int lineNum = 1;

        for (String line : lines) {
            List<String> lineTokens = tokenizeLine(line);

            for (String token : lineTokens) {
                if (!isValidToken(token)) {
                    System.out.println("Лексическая ошибка! Недопустимый символ \"" + token + "\" на позиции " + line.indexOf(token) + " в строке " + lineNum);
                    return;
                }

                if (!token.trim().isEmpty()) {
                    tokens.add(token);

                    if (isIdentifier(token)) {
                        symbolTable.put(token, symbolTable.size() + 1);
                    }
                }
            }

            lineNum++;
        }

        // Вывод токенов
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (isIdentifier(token)) {
                System.out.println("<id," + symbolTable.get(token) + ">");
            } else {
                System.out.println("<" + token + ">");
            }
        }

        // Вывод таблицы символов
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
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
