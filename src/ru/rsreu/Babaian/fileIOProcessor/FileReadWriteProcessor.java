package ru.rsreu.Babaian.fileIOProcessor;

import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileReadWriteProcessor {

    public static String readFromFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "windows-1251"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static void writeToFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }

    public static void appendTextToFile(String filePath, String text) throws IOException {
            FileWriter writer = new FileWriter(filePath, true); // Второй параметр true позволяет дозаписывать в файл
            writer.write(text);
            writer.close();
    }


    public static void writeBinaryFile(String filename, List<ThreeAddressInstructions> codeLines, Map<Integer, Token> symbolTable) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            // Записываем List<ThreeAddressInstructions>
            objectOutputStream.writeObject(codeLines);

            // Записываем Map<Integer, Token>
            objectOutputStream.writeObject(symbolTable);
        }
    }
}
