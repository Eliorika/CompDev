package ru.rsreu.Babaian;

import ru.rsreu.Babaian.TokensProcessing.TokenGenerator;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

public class lab2 {
    public static void main(String[] args) {
        try {
            TokenGenerator tokenGenerator = new TokenGenerator();
            tokenGenerator.writeToken(args[0], args[1], args[2]);
        } catch (Exception e){
            System.out.println("Wrong input!");
        }
    }
}
