package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.ExpretionsProcessing.BoolContainer;
import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import javax.sound.sampled.BooleanControl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor.appendTextToFile;

class TreeNode {
    Token value;
    List<TreeNode> children;

    public TreeNode(Token value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public void print(String prefix, boolean isTail, String file) throws IOException {
        //System.out.println(prefix + (isTail ? "└── " : "├── ") + value);
        appendTextToFile(file, prefix + (isTail ? "└── " : "├── ") + value.getToken() +"\n");
        //builder.append(prefix + (isTail ? "└── " : "├── ") + value).append("\n");
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false, file);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true, file);
        }
    }

    public boolean hasDoubleChildren(Boolean flag){
        //boolean hasFloatOperand = false;
        for (TreeNode child : children) {
            if (child.value.getTokenType() == TokenType.TOKEN_DOUBLE
                    || child.value.getTokenType() == TokenType.TOKEN_ID_F
                    || child.value.getTokenType() == TokenType.TOKEN_INT_TO_FLOAT) {
                return true;
            }
            if(flag)
                return true;
            flag = child.hasDoubleChildren(false);
        }
        return flag;
    }
    public void convertOperands() {
        for (TreeNode child : children) {
            child.convertOperands();
        }
        if (value.getTokenType() == TokenType.TOKEN_PLUS ||
                value.getTokenType() == TokenType.TOKEN_MULT ||
                value.getTokenType() == TokenType.TOKEN_DIV ||
                value.getTokenType() == TokenType.TOKEN_MINUS ) {
            // Проверяем, есть ли хотя бы один операнд вещественного типа
            //boolean hasFloatOperand = false;
            boolean hasFloatOperand;
            if(value.getTokenType() != TokenType.TOKEN_INT_TO_FLOAT)
                hasFloatOperand = hasDoubleChildren(false);
            else hasFloatOperand = true;
//            for (TreeNode child : children) {
//                if (child.value.getTokenType() == TokenType.TOKEN_DOUBLE || child.value.getTokenType() == TokenType.TOKEN_ID_F) {
//                    hasFloatOperand = true;
//                    break;
//                }
//            }

            // Если есть хотя бы один вещественный операнд, конвертируем все целые в Int2Float
            if (hasFloatOperand) {


                List<TreeNode> newChildren = new ArrayList<>();
                for (TreeNode child : children) {
                    BoolContainer c = new BoolContainer();
                    c = child.isConverted(c);
                    if (child.value.getTokenType() != TokenType.TOKEN_DOUBLE
                            && child.value.getTokenType() != TokenType.TOKEN_ID_F
                            && child.value.getTokenType() != TokenType.TOKEN_INT_TO_FLOAT
                            && !c.getRes()) {
                        TreeNode conversionNode = new TreeNode(new Token(TokenType.TOKEN_INT_TO_FLOAT, "Int2Float"));
                        conversionNode.addChild(child);
                        newChildren.add(conversionNode);
                    } else {
                        newChildren.add(child);
                    }
                }
                children = newChildren;
            }
        }


    }


    public BoolContainer isConverted(BoolContainer c){
        //this.isConverted(converted, allDouble);
        if (this.value.getTokenType() == TokenType.TOKEN_INT_TO_FLOAT) {
            c.converted = true;
        }
        c.allDouble = c.allDouble && (this.value.getTokenType() != TokenType.TOKEN_INT && this.value.getTokenType() != TokenType.TOKEN_ID_I);
        for (TreeNode child : children) {
            c = child.isConverted(c);
            if(c.getRes())
                break;
        }
        return c;
    }

    public void checkDivisionByZero() {
        if (value.getTokenType() == TokenType.TOKEN_DIV && children.size() == 2) {
            TreeNode rightChild = children.get(1);
            if (rightChild.value.getTokenType() == TokenType.TOKEN_INT && rightChild.value.getToken().equals("<0>")) {
                System.err.println("Error: Division by constant 0");
                System.exit(0);
            }
        }

        for (TreeNode child : children) {
            child.checkDivisionByZero();
        }
    }

}

public class ExpressionTreeBuilder {
    private List<Token> tokens;
    private int current = 0;

    private TreeNode root;
    private TreeNode rootCoverted;

    public ExpressionTreeBuilder(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TreeNode buildTree() {
        return expression();
    }

    private TreeNode expression() {
        TreeNode left = term();

        while (match(TokenType.TOKEN_PLUS, TokenType.TOKEN_MINUS)) {
            Token operator = previous();
            TreeNode right = term();
            TreeNode expressionNode = new TreeNode(operator);
            expressionNode.addChild(left);
            expressionNode.addChild(right);
            left = expressionNode;
        }

        return left;
    }

    private TreeNode term() {
        TreeNode left = factor();

        while (match(TokenType.TOKEN_MULT, TokenType.TOKEN_DIV)) {
            Token operator = previous();
            TreeNode right = factor();
            TreeNode termNode = new TreeNode(operator);
            termNode.addChild(left);
            termNode.addChild(right);
            left = termNode;
        }

        return left;
    }

    private TreeNode factor() {
        if (match(TokenType.TOKEN_ID_I, TokenType.TOKEN_ID_F, TokenType.TOKEN_DOUBLE, TokenType.TOKEN_INT)) {
            return new TreeNode(previous());
        } else if (match(TokenType.TOKEN_LEFT_BR)) {
            TreeNode inner = expression();
            consume(TokenType.TOKEN_RIGHT_BR, "Expect ')' after expression.");
            return inner;
        } else {
            System.err.println("Syntax error");
            System.exit(0);
            return null;
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().getTokenType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private void consume(TokenType type, String errorMessage) {
        if (check(type)) {
            advance();
        } else {
            System.err.println(errorMessage);
            System.exit(0);
        }
    }

    public void treeToFile(TreeNode treeNode, String fileOut) throws IOException {
        //TreeNode tree = buildTree();
        FileReadWriteProcessor.writeToFile(fileOut, "");
        root.print("", true, fileOut);

    }

    public TreeNode processSem(){
//        root = null;
//        root = buildTree();

        rootCoverted = root;
        rootCoverted.convertOperands();
        rootCoverted.checkDivisionByZero();
        return rootCoverted;
    }

    public TreeNode processSyn(){
        root = null;
        root = buildTree();
        return root;
    }
}
