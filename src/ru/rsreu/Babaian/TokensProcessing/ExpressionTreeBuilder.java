package ru.rsreu.Babaian.TokensProcessing;

import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor.appendTextToFile;

class TreeNode {
    String value;
    List<TreeNode> children;

    StringBuilder builder = new StringBuilder();

    public TreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public void print(String prefix, boolean isTail, String file) throws IOException {
        //System.out.println(prefix + (isTail ? "└── " : "├── ") + value);
        appendTextToFile(file, prefix + (isTail ? "└── " : "├── ") + value +"\n");
        //builder.append(prefix + (isTail ? "└── " : "├── ") + value).append("\n");
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false, file);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true, file);
        }
    }
}

public class ExpressionTreeBuilder {
    private List<Token> tokens;
    private int current = 0;

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
            TreeNode expressionNode = new TreeNode(operator.getToken());
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
            TreeNode termNode = new TreeNode(operator.getToken());
            termNode.addChild(left);
            termNode.addChild(right);
            left = termNode;
        }

        return left;
    }

    private TreeNode factor() {
        if (match(TokenType.TOKEN_ID, TokenType.TOKEN_DOUBLE, TokenType.TOKEN_INT)) {
            return new TreeNode(previous().getToken());
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

    public void treeToFile(String fileOut) throws IOException {
        TreeNode tree = buildTree();
        FileReadWriteProcessor.writeToFile(fileOut, "");
        tree.print("", true, fileOut);

    }
//    public static void main(String[] args) throws IOException {
//        TokenGenerator tokenGenerator = new TokenGenerator();
//        ExpressionTreeBuilder builder = new ExpressionTreeBuilder(tokenGenerator.getTokens("expr.txt"));
//
//
//
//    }
}
