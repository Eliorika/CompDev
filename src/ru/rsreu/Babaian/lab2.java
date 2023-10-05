package ru.rsreu.Babaian;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

public class lab2 {
    public static void main(String[] args) {
        try {
            String[] line = FileReadWriteProcessor.readFromFile(args[0]).split(" ");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
