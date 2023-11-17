package ru.rsreu.Babaian;

import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.TokensProcessing.*;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

import java.util.List;

public class lab3 {
    public static void main(String[] args) {
        try {
            if(args[0].equalsIgnoreCase("lex")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], args[2], args[3]);
            } else if(args[0].equalsIgnoreCase("syn")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                var tok = tokenGenerator.getTokens(args[1]);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                var root = tree.processSyn();
                tree.treeToFile(root, "tree.txt");

            } else if(args[0].equalsIgnoreCase("sem")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], "token.txt", "ids.txt");
                var tok = tokenGenerator.getTokens(args[1]);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                //tree.treeToFileSem(args[2]);
                var root = tree.processSyn();
                tree.treeToFile(root, "tree.txt");

                root = tree.processSem();
                tree.treeToFile(root, "syntax_tree_mod.txt");

            } else if(args[0].equalsIgnoreCase("gen1")) {
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], "token.txt", "ids.txt");
                var tok = tokenGenerator.getTokens(args[1]);
                var st = tokenGenerator.getIdNames();
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                //tree.treeToFileSem(args[2]);
                var root = tree.processSyn();
                tree.treeToFile(root, "tree.txt");

                root = tree.processSem();
                tree.treeToFile(root, "syntax_tree_mod.txt");

                ThreeAddressCodeGenerator generator = new ThreeAddressCodeGenerator(st.size(), st);
                generator.generateCode(root);
                generator.writeCodeToFile("portable_code.txt");
                generator.writeSymbolTableToFile("symbols.txt");

            } else if(args[0].equalsIgnoreCase("gen2")) {

                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], "token.txt", "ids.txt");
                var tok = tokenGenerator.getTokens(args[1]);
                var st = tokenGenerator.getIdNames();
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                //tree.treeToFileSem(args[2]);
                var root = tree.processSyn();
                tree.treeToFile(root, "tree.txt");

                root = tree.processSem();
                tree.treeToFile(root, "syntax_tree_mod.txt");

                ThreeAddressCodeGenerator generator = new ThreeAddressCodeGenerator(st.size(), st);

                //List<Token> postfixExpression = generator.toPostfix(root);
                //FileReadWriteProcessor.writeToFile("postfix.txt", postfixExpression);
                generator.processV2(root);
                //System.out.println("Postfix Expression: " + postfixExpression);
                generator.writeSymbolTableToFile("symbols.txt");

            } else
                throw new Exception();
        } catch (Exception e){
            System.out.println("Wrong input!");
            //e.printStackTrace();
        }
    }
}
