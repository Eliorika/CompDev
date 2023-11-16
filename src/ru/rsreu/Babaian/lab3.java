package ru.rsreu.Babaian;

import ru.rsreu.Babaian.TokensProcessing.*;
import ru.rsreu.Babaian.fileIOProcessor.FileReadWriteProcessor;

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
                tree.treeToFile(root, args[2]);

            } else if(args[0].equalsIgnoreCase("sem")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], args[2], args[3]);
                var tok = tokenGenerator.getTokens(args[1]);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                //tree.treeToFileSem(args[2]);
                var root = tree.processSyn();
                tree.treeToFile(root, "tree.txt");

                root = tree.processSem();
                tree.treeToFile(root, "syntax_tree_mod.txt");

            } if(args[0].equalsIgnoreCase("gen1")) {
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], args[2], args[3]);
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
                tokenGenerator.writeToken(args[1], args[2], args[3]);
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

                String postfixExpression = generator.toPostfix(root);
                FileReadWriteProcessor.writeToFile("postfix.txt", postfixExpression);
                //System.out.println("Postfix Expression: " + postfixExpression);
                generator.writeSymbolTableToFile("symbols.txt");

            } else
                throw new Exception();
        } catch (Exception e){
            System.out.println("Wrong input!");
            e.printStackTrace();
        }
    }
}
