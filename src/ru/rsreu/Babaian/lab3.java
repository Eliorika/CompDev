package ru.rsreu.Babaian;

import ru.rsreu.Babaian.TokensProcessing.ExpressionTreeBuilder;
import ru.rsreu.Babaian.TokensProcessing.SyntaxAnalyzer;
import ru.rsreu.Babaian.TokensProcessing.TokenGenerator;

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
                tree.treeToFileSyn(args[2]);

            } else if(args[0].equalsIgnoreCase("sem")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], args[2], args[3]);
                var tok = tokenGenerator.getTokens(args[1]);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                //tree.treeToFileSem(args[2]);

                tree.treeToFileSyn("tree.txt");
                tree.treeToFileSem("syntax_tree_mod.txt");
            } else
                throw new Exception();
        } catch (Exception e){
            System.out.println("Wrong input!");
        }
    }
}
