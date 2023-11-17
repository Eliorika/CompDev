package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Instructions;
import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.*;

public class ThreeAddressCodeGenerator {
    private int tempVarCount = 0; // Счетчик временных переменных

    private Map<String, Integer> symbolTable = new HashMap<>(); // Таблица символов
    //private List<String> codeLines = new ArrayList<>(); // Трехадресный код

    private List<ThreeAddressInstructions> codeLines = new ArrayList<>();



    // Генерация трехадресного кода и заполнение таблицы символов
    public void generateCode(TreeNode node) {
        generateCodeRecursive(node);
    }

    public ThreeAddressCodeGenerator(int tempVarCount, Map<String, Integer> symbolTable) {
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
        Token op = node.value;
        List<Token> ls = new ArrayList<>();
        Token operand1 = generateCodeRecursive(node.children.get(0));
        ls.add(operand1);
        Token operand2 = null;
        if(node.children.size() == 2) {
            operand2 = generateCodeRecursive(node.children.get(1));
            ls.add(operand2);
        }

        String result = getTempVar(); // Результат операции или временная переменная
        if(!symbolTable.containsKey(result+type))
            symbolTable.put(result+type, tempVarCount);

        if("[float]".equalsIgnoreCase(type))
            tk = new Token(TokenType.TOKEN_ID_F, result+type);
        else tk = new Token(TokenType.TOKEN_ID_I, result+type);



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
        FileReadWriteProcessor.writeToFile(filename, String.join("\n", codeLines.toString()));
    }

    // Вывод таблицы символов в файл
    public void writeSymbolTableToFile(String filename) throws IOException {
        List<String> symbolTableContent = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            symbolTableContent.add(entry.getValue() + " - " + entry.getKey());
        }
        FileReadWriteProcessor.writeToFile(filename, String.join("\n", symbolTableContent));
    }

    public String toPrefix(TreeNode node) {
        StringBuilder postfixExpression = new StringBuilder();
        Stack<TreeNode> stack = new Stack<>();

        stack.push(node);

        while (!stack.isEmpty()) {
            TreeNode current = stack.pop();

            postfixExpression.append(current.value.getToken()).append(" ");

            for (int i = current.children.size() - 1; i >= 0; i--) {
                stack.push(current.children.get(i));
            }
        }

        return postfixExpression.toString().trim();
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

    public void processV1(TreeNode root) throws IOException {
        generateCode(root);
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



    // Пример использования
//    public static void main(String[] args) throws IOException {
//        // Предполагаем, что у вас есть объект TreeNode, представляющий синтаксическое дерево
//        TokenGenerator tokenGenerator = new TokenGenerator();
//        var tok = tokenGenerator.getTokens("expr.txt");
//        var st = tokenGenerator.getIdNames();
//        ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
//        TreeNode root = tree.buildTree();
//        root.convertOperands();
//
//        ThreeAddressCodeGenerator generator = new ThreeAddressCodeGenerator(st.size(), st);
//        generator.generateCode(root);
//        //String code = generator.generateCode(root);
//
//        // Вывод трехадресного кода в файл
//        generator.writeCodeToFile("portable_code.txt");
//
//        // Вывод таблицы символов в файл
//        generator.writeSymbolTableToFile("symbols.txt");
//
//        String postfixExpression = generator.toPostfix(root);
//        System.out.println("Postfix Expression: " + postfixExpression);
//    }
}
