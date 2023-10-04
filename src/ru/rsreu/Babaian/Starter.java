package ru.rsreu.Babaian;

import ru.rsreu.Babaian.WorkEntities.ExpressionGenerator;
import ru.rsreu.Babaian.WorkEntities.ExpressionTranslator;

import java.io.IOException;

public class Starter {
    public Starter() {
    }

    public static void main(String[] args) {
        try {
            if ("G".equalsIgnoreCase(args[0])) {
                ExpressionGenerator expressionGenerator = new ExpressionGenerator();
                if (expressionGenerator.validateInput(args)) {
                    expressionGenerator.generateToFile(args[1]);
                } else {
                    System.out.println(expressionGenerator.getMessage());
                }
            } else if ("T".equalsIgnoreCase(args[0])) {
                ExpressionTranslator expressionTranslator = new ExpressionTranslator();
                if (expressionTranslator.validateData(args)) {
                    expressionTranslator.translateToFile(args[1], args[2]);
                    if (expressionTranslator.isError()) {
                        System.out.println(expressionTranslator.getMessage());
                    }
                } else {
                    System.out.println(expressionTranslator.getMessage());
                }
            } else {
                System.out.println("Wrong operation mode");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Wrong input!");
        }

    }
}
