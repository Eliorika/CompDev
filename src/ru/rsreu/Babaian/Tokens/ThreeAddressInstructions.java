package ru.rsreu.Babaian.Tokens;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ru.rsreu.Babaian.Tokens.TokenType.isFloat;

public class ThreeAddressInstructions implements Serializable {
    private static final long serialVersionUID = 1l;
    private Instructions instruction;

    private List<Token> operands = new ArrayList<>();


    public ThreeAddressInstructions(Instructions instruction, List<Token> operands) {
        this.instruction = instruction;
        this.operands = operands;
    }

    public Instructions getInstruction() {
        return instruction;
    }

    public List<Token> getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        String res = instruction + " ";
        for (Token tk : operands) {
            res += tk.getToken();
            res += " ";
        }
        return res;
    }

    public ThreeAddressInstructions execute() {
        ThreeAddressInstructions result = null;
        var holder = this.operands.get(0);
        double res = 0;
        try {
            switch (instruction) {
                case ADD:
                    res = performAddition();
                    break;
                case SUB:
                    res = performSubtraction();
                    break;
                case MUL:
                    res = performMultiplication();
                    break;
                case DIV:
                    res = performDivision();
                    break;
                case I2F:
                    res = performCast();
                    break;
                case ASSIGN:
                    performAssign();
                    break;
                default:
                    System.out.println("Ошибка: Неподдерживаемая инструкция.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Один из операндов не является числом.");
        }

        if (instruction != Instructions.ASSIGN) {
            if (instruction != Instructions.I2F && !this.operands.stream().anyMatch(t -> isFloat(t.getTokenType()))) {
                result = new ThreeAddressInstructions(Instructions.ASSIGN, List.of(holder, new Token(TokenType.TOKEN_INT, "<" + (int) res + ">")));
            } else {
                result = new ThreeAddressInstructions(Instructions.ASSIGN, List.of(holder, new Token(TokenType.TOKEN_DOUBLE, "<" + res + ">")));
            }
        } else {
            return this;
        }

        return result;
    }

    private double performAddition() {
        checkOperandsCount(3);
        double result = getNumericValue(operands.get(1)) + getNumericValue(operands.get(2));
        return result;
        //System.out.println("Результат сложения: " + result);
    }

    private double performCast() {
        checkOperandsCount(2);
        double result = getNumericValue(operands.get(1));
        return result;
    }

    private double performSubtraction() {
        checkOperandsCount(3);
        double result = getNumericValue(operands.get(1)) - getNumericValue(operands.get(2));
        return result;
    }

    private double performMultiplication() {
        checkOperandsCount(3);
        double result = getNumericValue(operands.get(1)) * getNumericValue(operands.get(2));
        return result;
    }

    private double performDivision() {
        checkOperandsCount(3);
        double denominator = getNumericValue(operands.get(2));
        double result = Double.MIN_VALUE;
        if (denominator != 0) {
            result = getNumericValue(operands.get(1)) / denominator;
        } else {
            System.err.println("Divided by zero!");
            System.exit(1);
        }
        return result;
    }

    public void performAssign() {
        if(!checkOperandsCount(1)){
        double ass = getNumericValue(operands.get(1));
        if (!this.operands.stream().anyMatch(t -> isFloat(t.getTokenType()))) {
            operands.get(0).setToken("<" + (int) ass + ">");
        } else {
            operands.get(0).setToken("<" + ass + ">");
        }}
    }

    private double getNumericValue(Token token) {
        String tokenValue = token.getToken();
        // Убираем угловые скобки и преобразуем строку в число
        return Double.parseDouble(tokenValue.substring(1, tokenValue.length() - 1));
    }

    private boolean checkOperandsCount(int expectedCount) {
        return operands.size() <= expectedCount;//) {
//            System.out.println("Ошибка: Неверное количество операндов для данной операции.");
//        }
    }


}
