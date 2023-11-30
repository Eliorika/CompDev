package ru.rsreu.executor;

import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class Runner {
    private static List<ThreeAddressInstructions> codeLines;
    private static Map<Integer, Token> symbolTable;
    public static void readBinaryFile(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            // Читаем List<ThreeAddressInstructions>
            codeLines = (List<ThreeAddressInstructions>) objectInputStream.readObject();

            // Читаем Map<Integer, Token>
            symbolTable = (Map<Integer, Token>) objectInputStream.readObject();
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        readBinaryFile("post_code.bin");
        Expression expression = new Expression(codeLines, symbolTable);
        expression.process();


    }

}
