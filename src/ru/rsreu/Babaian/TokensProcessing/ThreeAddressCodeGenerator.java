package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Instructions;
import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.rsreu.Babaian.Tokens.TokenType.TOKEN_ID_F;
import static ru.rsreu.Babaian.Tokens.TokenType.isConst;

public class ThreeAddressCodeGenerator {
    private int tempVarCount = 0; // Счетчик временных переменных

    private Map<Integer, Token> symbolTable = new HashMap<>(); // Таблица символов
    //private List<String> codeLines = new ArrayList<>(); // Трехадресный код

    private List<ThreeAddressInstructions> codeLines = new ArrayList<>();



    // Генерация трехадресного кода и заполнение таблицы символов
    public void generateCode(TreeNode node) {
        generateCodeRecursive(node);
    }

    public ThreeAddressCodeGenerator(int tempVarCount, Map<Integer, Token> symbolTable) {
        this.tempVarCount = tempVarCount;
        this.symbolTable = symbolTable;
    }

    // Рекурсивная генерация трехадресного кода
    private Token generateCodeRecursive(TreeNode node) {
        Token tk;
        String type= "[integer]";
        if (node.children.isEmpty()) {
            // Листовой узел (операнд)
            //String operand = node.value.getToken();
            Token operand = node.value;
//            if (operand.startsWith("<id,")) {
//                // Это переменная
//                String type = operand.endsWith("_F") ? "[float]" : "[integer]";
//                if(!symbolTable.containsKey(operand+type))
//                    symbolTable.put(operand+type, tempVarCount);
//            }
            return operand;


        } else {
            if(isChildFloat(node)){
                type = "[float]";
            }

        }

        // Внутренний узел (оператор)


         // Результат операции или временная переменная



        Token op = node.value;
        List<Token> ls = new ArrayList<>();
        Token operand1 = generateCodeRecursive(node.children.get(0));
        Token operand2 = null;
        if(node.children.size() == 2) {
            operand2 = generateCodeRecursive(node.children.get(1));
            //ls.add(operand2);
        }

        String result = getTempVar();
        if("[float]".equalsIgnoreCase(type))
            tk = new Token(TokenType.TOKEN_ID_F, result);
        else tk = new Token(TokenType.TOKEN_ID_I, result);

        if(!symbolTable.values().stream().anyMatch(varTk -> varTk.getToken().equals(tk.getToken())))
            symbolTable.put(tempVarCount, tk);
        ls.add(tk);
        ls.add(operand1);
        if(operand2 != null)
            ls.add(operand2);




        // Генерация трехадресного кода
        switch (op.getToken()) {
            case "+":
                //codeLines.add("add " + result + " " + operand1 + " " + operand2);
                codeLines.add(new ThreeAddressInstructions(Instructions.ADD, ls));
                break;
            case "-":
                //codeLines.add("sub " + result + " " + operand1 + " " + operand2);
                codeLines.add(new ThreeAddressInstructions(Instructions.SUB, ls));
                break;
            case "*":
                //codeLines.add("mul " + result + " " + operand1 + " " + operand2);
                codeLines.add(new ThreeAddressInstructions(Instructions.MUL, ls));
                break;
            case "/":
                //codeLines.add("div " + result + " " + operand1 + " " + operand2);
                codeLines.add(new ThreeAddressInstructions(Instructions.DIV, ls));
                break;
            case "Int2Float":
                //codeLines.add("i2f " + result + " " + operand1);
                codeLines.add(new ThreeAddressInstructions(Instructions.I2F, ls));
                break;

        }

        return tk;
    }

    public boolean isChildFloat(TreeNode node){
        for(TreeNode nd : node.children)
            if (TokenType.isFloat(nd.value.getTokenType()))
                return true;
        return false;
    }

    // Получение нового имени временной переменной
    private String getTempVar() {
        tempVarCount++;
        String tempVar = "#T" + tempVarCount;
        return tempVar;
    }

    // Вывод трехадресного кода в файл
    public void writeCodeToFile(String filename) throws IOException {
        FileReadWriteProcessor.writeToFile(filename, String.join("\n", codeLines.stream().map(ThreeAddressInstructions::toString).collect(Collectors.toList())));
    }

    // Вывод таблицы символов в файл
    public void writeSymbolTableToFile(String filename) throws IOException {
        List<String> symbolTableContent = new ArrayList<>();
        for (Map.Entry<Integer, Token> entry : symbolTable.entrySet()) {
            String type = ", integer";
            if(entry.getValue().getTokenType() == TOKEN_ID_F){
                type = ", float";
            }
            symbolTableContent.add(entry.getKey() + " - " + entry.getValue().getToken()+type);

        }
        FileReadWriteProcessor.writeToFile(filename, String.join("\n", symbolTableContent));
    }

    public List<Token> toPostfix(TreeNode node) {
        List<Token> postfixExpression = new ArrayList<>();
        toPostfixRecursive(node, postfixExpression);
        return postfixExpression;
    }

    // Рекурсивный обход дерева в постфиксном порядке
    private void toPostfixRecursive(TreeNode node, List<Token> postfixExpression) {
        if (node == null) {
            return;
        }

        // Рекурсивно обрабатываем левое и правое поддеревья
        if(!node.children.isEmpty()){

            for(TreeNode nd : node.children)
                toPostfixRecursive(nd, postfixExpression);
        //toPostfixRecursive(node.children.get(1), postfixExpression);
        }

        // Добавляем текущий узел к постфиксному выражению
        postfixExpression.add(node.value);
    }

    public void processV1(TreeNode root, boolean optNeeded) throws IOException {
        generateCode(root);
        if(optNeeded){
            optimizeCode();
        }
        writeCodeToFile("portable_code.txt");
        writeSymbolTableToFile("symbols.txt");
    }

    public void processV2(TreeNode root) throws IOException {
        //generateCode(root);
        List<Token> postfixExpression = toPostfix(root);
        StringBuilder res = new StringBuilder();
        for (Token tk : postfixExpression){
            res.append(tk.getToken()).append(" ");
        }
        FileReadWriteProcessor.writeToFile("postfix.txt", res.toString());
        writeSymbolTableToFile("symbols.txt");
    }


    // Оптимизация: Замена всех частей выражений с константными операндами пересчитанными заранее значениями
    private void optimizeConstantExpressions() {
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            List<Token> operands = instruction.getOperands();

            // Проверяем, являются ли все операнды константами
            boolean allConstants  = false;
            if(operands.size()!=2) {
                var oper = new ArrayList<Token>(operands);
                oper.remove(0);
                allConstants = oper.stream()
                        .allMatch(op -> op.getTokenType() == TokenType.TOKEN_INT || op.getTokenType() == TokenType.TOKEN_DOUBLE);
            }

            if (allConstants) {
                // Вычисляем значение выражения заранее
                var result = instruction.execute();

                // Заменяем текущую инструкцию на инструкцию присваивания константного значения
                codeLines.set(i, result);
            }
        }
    }

    // Оптимизация: Замена все преобразования int2float с целочисленной константой на вещественную константу
    private void optimizeInt2FloatWithConstant() {
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            if (instruction.getInstruction() == Instructions.I2F && (isConst(instruction.getOperands().get(1).getTokenType()))) {
                var res = instruction.execute();
                codeLines.set(i, res);
//                Token operand = instruction.getOperands().get(0);
//
//                // Проверяем, является ли операнд целочисленной константой
//                if (operand.getTokenType() == TokenType.TOKEN_INT) {
//                    // Заменяем текущую инструкцию на инструкцию присваивания вещественной константы
//                    operand.setTokenType(TokenType.TOKEN_DOUBLE);
//
//                }
            }
        }
    }

    // Оптимизация: Замена всех операций с заранее известным результатом фактическим значением
    private void replaceWithActualValues() {
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);

            // Проверяем, является ли операция заранее известной
            if (isKnownResultOperation(instruction.getInstruction(), instruction.getOperands())) {
                Token result = instruction.getOperands().get(0);

                //codeLines.set(i, new ThreeAddressInstructions(Instructions.ASSIGN, updatedOperands));

                // Заменяем все вхождения результата в последующих инструкциях
                for (int j = i + 1; j < codeLines.size(); j++) {
                    ThreeAddressInstructions nextInstruction = codeLines.get(j);
                    List<Token> updatedOperands = nextInstruction.getOperands().stream()
                            .map(op -> op.equals(result) ? result : op)
                            .collect(Collectors.toList());

                    codeLines.set(j, new ThreeAddressInstructions(nextInstruction.getInstruction(), updatedOperands));
                }
            }
        }
    }

    // Оптимизация: Переиспользование временных переменных из таблицы символов
    private void reuseTempVariables() {
        Map<String, Token> tempVarMap = new HashMap<>();

        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            List<Token> operands = instruction.getOperands();

            // Заменяем временные переменные в операндах
            List<Token> updatedOperands = operands.stream()
                    .map(op -> tempVarMap.getOrDefault(op.getToken(), op))
                    .collect(Collectors.toList());

            // Заменяем результат операции, если он временная переменная
            Token result = operands.get(0);
            if (result.getTokenType() == TokenType.TOKEN_ID_I || result.getTokenType() == TokenType.TOKEN_ID_F) {
                tempVarMap.put(result.getToken(), result);
            }

            //codeLines.set(i, new ThreeAddressInstructions(instruction.getOp(), updatedOperands));
        }
    }

    // Метод для проверки, является ли операция заранее известной
    private boolean isKnownResultOperation(Instructions op, List<Token> operands) {
        switch (op) {
            case ADD:
                return operands.get(1).isZero() || operands.get(0).isZero(); // Сложение с 0
            case SUB:
                return operands.get(1).isZero(); // Вычитание 0
            case MUL:
                return operands.stream().anyMatch(Token::isOne); // Умножение на 1
            case DIV:
                return operands.get(1).isOne(); // Деление на 1
            // Добавьте другие операции по необходимости
            default:
                return false;
        }
    }



    //Основной метод для выполнения всех оптимизаций
    public void optimizeCode() {
        optimizeInt2FloatWithConstant();
        optimizeConstantExpressions();
        replaceWithActualValues();
        reuseTempVariables();
    }

}
