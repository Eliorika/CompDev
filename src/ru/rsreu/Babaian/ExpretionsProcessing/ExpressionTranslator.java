package ru.rsreu.Babaian.ExpretionsProcessing;

import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExpressionTranslator extends ExpressionProcessor {
    private static final String[] dictionaryNums = new String[]{"один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"};
    private static final Map<Character, String> dictionaryOperations = new HashMap<Character, String>();

    static {
        dictionaryOperations.put('*', "умножить на");
        dictionaryOperations.put('/', "делить на");
        dictionaryOperations.put('+', "плюс");
        dictionaryOperations.put('-', "минус");
    }

    public ExpressionTranslator() {
    }

    public boolean validateData(String[] args) {
        if (args.length != 3) {
            super.setError("There must be 3 args");
            return false;
        } else if (args[1].contains(".txt") && args[2].contains(".txt")) {
            return true;
        } else {
            super.setError("The files must be *.txt");
            return false;
        }
    }

    public void translateToFile(String fileIn, String fileOut) throws IOException {
        String seq = FileReadWriteProcessor.readFromFile(fileIn);
        String sentence = this.translate(seq);
        if (!this.isError()) {
            FileReadWriteProcessor.writeToFile(fileOut, sentence);
        }

    }

    private String translate(String seq) {
        StringBuilder result = new StringBuilder();

        try {
            char[] var3 = seq.toCharArray();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Character c = var3[var5];
                if (Character.isWhitespace(c)) {
                    result.append(c);
                } else if (dictionaryOperations.containsKey(c)) {
                    result.append(dictionaryOperations.get(c)).append(" ");
                } else if (Integer.parseInt(c.toString()) >= 1 && Integer.parseInt(c.toString()) <= 9) {
                    result.append(dictionaryNums[Integer.parseInt(c.toString()) - 1]).append(" ");
                }
            }
        } catch (Exception var7) {
            super.setError("Wrong input!");
            return null;
        }

        return result.toString();
    }
}
