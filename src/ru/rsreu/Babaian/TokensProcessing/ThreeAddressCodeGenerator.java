package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Instructions;
import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.rsreu.Babaian.Tokens.TokenType.*;
import static ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor.writeBinaryFile;

public class ThreeAddressCodeGenerator {
    private int tempVarCount = 0; // Счетчик временных переменных

    private Map<Integer, Token> symbolTable = new HashMap<>(); // Таблица символов
    //private List<String> codeLines = new ArrayList<>(); // Трехадресный код

    private List<ThreeAddressInstructions> codeLines = new ArrayList<>();


    public ThreeAddressCodeGenerator(int tempVarCount, Map<Integer, Token> symbolTable) {
        this.tempVarCount = tempVarCount;
        this.symbolTable = symbolTable;
    }

    // Генерация трехадресного кода и заполнение таблицы символов
    public void generateCode(TreeNode node) {
        generateCodeRecursive(node);
        codeLines.add(new ThreeAddressInstructions(Instructions.ASSIGN, List.of(codeLines.get(codeLines.size() - 1).getOperands().get(0))));
    }

    // Рекурсивная генерация трехадресного кода
    private Token generateCodeRecursive(TreeNode node) {
        Token tk;
        String type = "[integer]";
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
            if (isChildFloat(node)) {
                type = "[float]";
            }

        }

        // Внутренний узел (оператор)


        // Результат операции или временная переменная


        Token op = node.value;
        List<Token> ls = new ArrayList<>();
        Token operand1 = generateCodeRecursive(node.children.get(0));
        Token operand2 = null;
        if (node.children.size() == 2) {
            operand2 = generateCodeRecursive(node.children.get(1));
            //ls.add(operand2);
        }

        String result = getTempVar();
        if ("[float]".equalsIgnoreCase(type))
            tk = new Token(TokenType.TOKEN_ID_F, result);
        else tk = new Token(TokenType.TOKEN_ID_I, result);

        //if (!symbolTable.values().stream().anyMatch(varTk -> varTk.getToken().equals(tk.getToken())))
            symbolTable.put(tempVarCount, tk);
        ls.add(tk);
        ls.add(operand1);
        if (operand2 != null)
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

    public boolean isChildFloat(TreeNode node) {
        for (TreeNode nd : node.children)
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
            if (entry.getValue().getTokenType() == TOKEN_ID_F) {
                type = ", float";
            }
            symbolTableContent.add(entry.getKey() + " - " + entry.getValue().getToken() + type);

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
        if (!node.children.isEmpty()) {

            for (TreeNode nd : node.children)
                toPostfixRecursive(nd, postfixExpression);
            //toPostfixRecursive(node.children.get(1), postfixExpression);
        }

        // Добавляем текущий узел к постфиксному выражению
        postfixExpression.add(node.value);
    }

    public void processV1(TreeNode root, boolean optNeeded) throws IOException {
        generateCode(root);

        if (optNeeded) {
            optimizeCode();
        }
        writeCodeToFile("portable_code.txt");
        writeSymbolTableToFile("symbols.txt");
    }


    public void processV3(TreeNode root, boolean optNeeded) throws IOException {
        generateCode(root);
        optimizeCode();
        writeBinaryFile("post_code.bin", codeLines, symbolTable);

        writeCodeToFile("portable_code.txt");
        writeSymbolTableToFile("symbols.txt");
    }

    public void processV2(TreeNode root) throws IOException {
        //generateCode(root);
        List<Token> postfixExpression = toPostfix(root);
        StringBuilder res = new StringBuilder();
        for (Token tk : postfixExpression) {
            res.append(tk.getToken()).append(" ");
        }
        FileReadWriteProcessor.writeToFile("postfix.txt", res.toString());
        writeSymbolTableToFile("symbols.txt");
    }


    // Оптимизация: Замена всех частей выражений с константными операндами пересчитанными заранее значениями
    private boolean optimizeConstantExpressions() {
        var lnToDel = new ArrayList<ThreeAddressInstructions>();
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            List<Token> operands = instruction.getOperands();

            // Проверяем, являются ли все операнды константами
            boolean allConstants = false;
            if (operands.size() > 2) {
                var oper = new ArrayList<Token>(operands);
                oper.remove(0);
                allConstants = oper.stream()
                        .allMatch(op -> op.getTokenType() == TokenType.TOKEN_INT || op.getTokenType() == TokenType.TOKEN_DOUBLE);
            }

            if (allConstants) {
                lnToDel.add(instruction);
                // Вычисляем значение выражения заранее
                var result = instruction.execute();

                for (int j = i + 1; j < codeLines.size(); j++) {
                    ThreeAddressInstructions nextInstruction = codeLines.get(j);
                    List<Token> updatedOperands = nextInstruction.getOperands().stream()
                            .map(op -> op.equals(result.getOperands().get(0)) ? result.getOperands().get(1) : op)
                            .collect(Collectors.toList());

                    codeLines.set(j, new ThreeAddressInstructions(nextInstruction.getInstruction(), updatedOperands));
                }

                // Заменяем текущую инструкцию на инструкцию присваивания константного значения
                //codeLines.set(i, result);

            }
        }

        deleteCodeRows(lnToDel);
        return lnToDel.size() > 0;
    }

    private void deleteCodeRows(List<ThreeAddressInstructions> lines) {
        for (ThreeAddressInstructions num : lines) {
            codeLines.remove(num);
            Iterator<Map.Entry<Integer, Token>> iteratorS = symbolTable.entrySet().iterator();
            while (iteratorS.hasNext()) {
                Map.Entry<Integer, Token> entry = iteratorS.next();
                if (entry.getValue().equals(num.getOperands().get(0))) {
                    iteratorS.remove();
                }
            }

//            Iterator<ThreeAddressInstructions> iteratorC = codeLines.iterator();
//            while (iteratorC.hasNext()) {
//                ThreeAddressInstructions el = iteratorC.next();
//                for (Token t : num.getOperands()) {
//                    if (el.getOperands().stream().anyMatch(tt -> tt.equals(t) && isID(t.getTokenType()))) {
//                        iteratorC.remove();
//                    }
//                }
//            }

        }
    }

    // Оптимизация: Замена все преобразования int2float с целочисленной константой на вещественную константу
    private boolean optimizeInt2FloatWithConstant() {
        var lnToDel = new ArrayList<ThreeAddressInstructions>();
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            if (instruction.getInstruction() == Instructions.I2F && (isConst(instruction.getOperands().get(1).getTokenType()))) {
                var res = instruction.execute();
                lnToDel.add(instruction);
                //codeLines.set(i, res);

                for (int j = i + 1; j < codeLines.size(); j++) {
                    ThreeAddressInstructions nextInstruction = codeLines.get(j);
                    List<Token> updatedOperands = nextInstruction.getOperands().stream()
                            .map(op -> op.equals(res.getOperands().get(0)) ? res.getOperands().get(1) : op)
                            .collect(Collectors.toList());

                    codeLines.set(j, new ThreeAddressInstructions(nextInstruction.getInstruction(), updatedOperands));
                }
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

        deleteCodeRows(lnToDel);
        return lnToDel.size() > 0;
    }

    // Оптимизация: Замена всех операций с заранее известным результатом фактическим значением
    private boolean replaceWithActualValues() {
        var lnToDel = new ArrayList<ThreeAddressInstructions>();
        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);

            // Проверяем, является ли операция заранее известной
            Token changeTo = knownResultOperation(instruction.getInstruction(), instruction.getOperands());
            if (changeTo != null) {
                Token result = instruction.getOperands().get(0);
                lnToDel.add(instruction);

                //codeLines.set(i, new ThreeAddressInstructions(Instructions.ASSIGN, updatedOperands));

                // Заменяем все вхождения результата в последующих инструкциях
                for (int j = i + 1; j < codeLines.size(); j++) {
                    ThreeAddressInstructions nextInstruction = codeLines.get(j);
                    List<Token> updatedOperands = nextInstruction.getOperands().stream()
                            .map(op -> op.equals(result) ? changeTo : op)
                            .collect(Collectors.toList());

                    codeLines.set(j, new ThreeAddressInstructions(nextInstruction.getInstruction(), updatedOperands));
                }
            }
        }

        deleteCodeRows(lnToDel);

        return lnToDel.size() > 0;

    }

    // Метод для проверки, является ли операция заранее известной
    private Token knownResultOperation(Instructions op, List<Token> operands) {
        Token token = null;
        switch (op) {
            case ADD:
                if (operands.get(1).isZero() || operands.get(2).isZero()) {
                    if (operands.get(1).isZero()) {
                        token = operands.get(2);
                    } else token = operands.get(1);
                }
                break;
            case SUB:
                if (operands.get(2).isZero()) {
                    token = operands.get(1);
                }
                break;
            case MUL:
                if (operands.stream().anyMatch(Token::isZero)) {
                    token = new Token(TokenType.TOKEN_INT, "<" + 0 + ">");
                } else if (operands.get(1).isOne() || operands.get(2).isOne()) {
                    if (operands.get(1).isZero()) {
                        token = operands.get(2);
                    } else token = operands.get(1);
                }
                break;
            case DIV:
                if (operands.get(2).isOne()) {
                    token = operands.get(1);
                } else if (operands.get(1).isZero()) {
                    token = new Token(TokenType.TOKEN_INT, "<" + 0 + ">");
                }
                break;// Деление на 1

        }

        return token;
    }

    // Оптимизация: Переиспользование временных переменных из таблицы символов
    private void reuseTempVariables() {
        Map<Token, Integer> tempVarLastUsage = new HashMap<>();


        for (int i = 0; i < codeLines.size(); i++) {
            ThreeAddressInstructions instruction = codeLines.get(i);
            List<Token> operands = instruction.getOperands();

            // Заменяем временные переменные в операндах
            int finalI = i;
            operands.stream()
                    .map(op -> {
                        if ((op.getTokenType() == TokenType.TOKEN_ID_I || op.getTokenType() == TokenType.TOKEN_ID_F)
                                && op.getToken().contains("#")) {
                            tempVarLastUsage.put(op, finalI);
                        }
                        return op;
                    })
                    .collect(Collectors.toList());
        }

        for (int i = 1; i < codeLines.size(); i++) {
            // Проверяем, используется ли результат операции дальше
            ThreeAddressInstructions instruction = codeLines.get(i);
            List<Token> operands = instruction.getOperands();
            Token result = operands.get(0);

            if (isID(result.getTokenType()) && instruction.getInstruction()!= Instructions.ASSIGN) {
                int finalI = i;
                Token token = tempVarLastUsage.keySet().stream().filter(k -> tempVarLastUsage.get(k) < finalI && k.getTokenType() == result.getTokenType()).findFirst().orElse(null);
                if(token != null){
                        // Переназначаем значение временной переменной
                    String name = instruction.getOperands().get(0).getToken();
                    String numberString = name.replaceAll("[^0-9]", "");

                    // Преобразуем строку в целое число
                    int id = Integer.parseInt(numberString);
                    Token newTokenF = new Token(TOKEN_ID_F, name);
                    Token newTokenI = new Token(TOKEN_ID_I, name);
                    int lst = tempVarLastUsage.get(instruction.getOperands().get(0));
                    instruction.getOperands().get(0).setToken(token.getToken());
                        //symbolTable.remove()
                        tempVarLastUsage.replace(token, lst);
                        tempVarLastUsage.remove(instruction.getOperands().get(0));

                        //symbolTable.put(id, newToken);

                        //symbolTable.
                        //codeLines.set(i, new ThreeAddressInstructions(Instructions.ASSIGN, List.of(result)));

                }

            }

            //codeLines.set(i, new ThreeAddressInstructions(instruction.getInstruction(), updatedOperands));
        }
    }



    //Основной метод для выполнения всех оптимизаций
    public void optimizeCode() {
        boolean edited = true;
        while (edited) {
            var i2fE = optimizeInt2FloatWithConstant();
            var constE = optimizeConstantExpressions();
            var actVE = replaceWithActualValues();
            edited = i2fE || constE || actVE;
        }
        reuseTempVariables();
        removeDuplicateTokens();
    }

    private void removeDuplicateTokens() {
        Map<Integer, Token> newSymbolTable = new HashMap<>();
        List<Token> tokenValues = new ArrayList<>();
        int currentKey = 1;

        for (Map.Entry<Integer, Token> entry : symbolTable.entrySet()) {
            Token token = entry.getValue();

            if (!tokenValues.contains(token)) {
                // Если такого значения еще нет, добавляем его в новую карту

                String name = token.getToken();
                String numberString = name.replaceAll("[^0-9]", "");
                int id = Integer.parseInt(numberString);
                if(id != currentKey)
                    token.setToken("#T" + currentKey);

                if(!tokenValues.contains(token)){
                    int result = Integer.parseInt(numberString);
                    newSymbolTable.put(currentKey++, token);
                    tokenValues.add(token);
                }

                // Преобразуем строку в целое число

            }
        }

        // Заменяем исходную карту новой
        symbolTable = newSymbolTable;
    }

}
