package ru.rsreu.Babaian.fileIOProcessor;

import java.io.*;

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
}
