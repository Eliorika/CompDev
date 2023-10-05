package ru.rsreu.Babaian.ExpretionsProcessing;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.Random;

public class ExpressionGenerator extends ExpressionProcessor {
    private final String operations = "+-*:";
    private int minOperands;
    private int maxOperands;
    private int numberStrings;

    public ExpressionGenerator() {
    }

    private static boolean validateInt(String[] args, int a, int b) {
        for (int i = a; i <= b; ++i) {
            try {
                if (Integer.parseInt(args[i]) <= 0) {
                    return false;
                }
            } catch (Exception var5) {
                return false;
            }
        }

        return true;
    }

    public void generateToFile(String file) throws IOException {
        FileReadWriteProcessor.writeToFile(file, this.generate());
    }

    private String generate() {
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < this.numberStrings; ++i) {
            res.append(this.generateSeq()).append("\n");
        }

        return res.toString();
    }

    private String generateSeq() {
        Random random = new Random();
        StringBuilder seq = new StringBuilder();
        int max = random.nextInt(this.minOperands, this.maxOperands + 1);

        for (int i = 0; i < max; ++i) {
            seq.append(random.nextInt(1, 10));
            if (i != max - 1) {
                seq.append("+-*:".toCharArray()[random.nextInt(0, 3)]);
            }
        }

        return seq.toString();
    }

    public boolean validateInput(String[] args) {
        boolean res = true;
        if (args.length != 5) {
            super.setError("Must be exactly 5 args!");
            res = false;
        } else if (!validateInt(args, 2, 4)) {
            super.setError("Args from 3 to 5 must be int and greater than 0!");
            res = false;
        }

        if (res) {
            this.numberStrings = Integer.parseInt(args[2]);
            this.minOperands = Integer.parseInt(args[3]);
            this.maxOperands = Integer.parseInt(args[4]);
        }

        return res;
    }
}
