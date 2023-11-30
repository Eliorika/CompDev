package ru.rsreu.Babaian.Tokens;

import java.util.ArrayList;
import java.util.List;

import static ru.rsreu.Babaian.Tokens.TokenType.isFloat;

public class ThreeAddressInstructions {
    private Instructions instruction;

    private List<Token> operands = new ArrayList<>();



    public Instructions getInstruction() {
        return instruction;
    }

    public List<Token> getOperands() {
        return operands;
    }

    public ThreeAddressInstructions(Instructions instruction, List<Token> operands) {
        this.instruction = instruction;
        this.operands = operands;
    }

    @Override
    public String toString(){
        String res = instruction + " ";
        for (Token tk: operands){
            res += tk.getToken();
            res+=" ";
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
                    break;
                default:
                    System.out.println("Ошибка: Неподдерживаемая инструкция.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Один из операндов не является числом.");
        }


        if(instruction!=Instructions.I2F){
            if(!this.operands.stream().anyMatch(t-> isFloat(t.getTokenType()))){
                result = new ThreeAddressInstructions(Instructions.ASSIGN,List.of(holder, new Token(holder.getTokenType(), "<" + (int)res+ ">")));
            }
        } else {
            result = new ThreeAddressInstructions(Instructions.ASSIGN,List.of(holder, new Token(TokenType.TOKEN_DOUBLE, "<" + res+ ">")));
        }

        return result;
    }

    private double performAddition() {
        checkOperandsCount(2);
        double result = getNumericValue(operands.get(1)) + getNumericValue(operands.get(2));
        return result;
        //System.out.println("Результат сложения: " + result);
    }

    private double performCast(){
        checkOperandsCount(1);
        double result = getNumericValue(operands.get(1));
        return result;
    }

    private double performSubtraction() {
        checkOperandsCount(2);
        double result = getNumericValue(operands.get(1)) - getNumericValue(operands.get(2));
        return result;
    }

    private double performMultiplication() {
        checkOperandsCount(2);
        double result = getNumericValue(operands.get(1)) * getNumericValue(operands.get(2));
        return result;
    }

    private double performDivision() {
        checkOperandsCount(2);
        double denominator = getNumericValue(operands.get(2));
        double result = Double.MIN_VALUE;
        if (denominator != 0) {
            result = getNumericValue(operands.get(1)) / denominator;
        }
        return result;
    }

    private double getNumericValue(Token token) {
        String tokenValue = token.getToken();
        // Убираем угловые скобки и преобразуем строку в число
        return Double.parseDouble(tokenValue.substring(1, tokenValue.length() - 1));
    }

    private void checkOperandsCount(int expectedCount) {
        if (operands.size() != expectedCount) {
            System.out.println("Ошибка: Неверное количество операндов для данной операции.");
        }
    }



}
