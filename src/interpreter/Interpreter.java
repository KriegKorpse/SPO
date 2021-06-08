package interpreter;

import java.util.Iterator;

public class Interpreter {

   public static void main(String[] args) {

      try {

         // Чтение текста программы из файла
         System.out.println("--- Program text ----");
//         TextReader reader = new TextReader("test_math.txt");
//         TextReader reader = new TextReader("test_logical.txt");
//         TextReader reader = new TextReader("test_if.txt");
//         TextReader reader = new TextReader("test_while.txt");
//         TextReader reader = new TextReader("err_test_brackets.txt");
//         TextReader reader = new TextReader("err_test_KW.txt");
           TextReader reader = new TextReader("err_test_operation.txt");
         String text = reader.getText();
         System.out.println(text);

         // Лексер. На вход получает обрабатываемый текст, на выходе итератор токенов lexer.getIterator()
         System.out.println("--- Lexer ----");
         Lexer lexer = new Lexer(text);
         Iterator<Token> tokens;

         // Это печать токенов
         tokens = lexer.getIterator();
         while(tokens.hasNext())
            System.out.println(tokens.next().toString());

         // Парсинг
         System.out.println("--- Parser ----");
         tokens = lexer.getIterator();
         Parser parser = new Parser(tokens);

         AstNode lang = parser.lang();
         PrintAst(lang, 0);

         System.out.println("--- Initial VarTable ----");
         parser.getVarTable().Print();

         // Это вычислитель
         System.out.println("--- ComputeVM ----");
         ComputeVM vm = new ComputeVM(lang, parser.getVarTable());
         vm.Compute();

         System.out.println("--- Result VarTable ----");
         parser.getVarTable().Print();
      }
      catch(InterpreterException e) {
         System.out.println(e.getMessage());
      }

   }

   private static String makeIndent(int level) {
      String result = "";
      for(int i = 0; i < level; i++)
         result += " ";
      return result;
   }

   private static void PrintAst(AstNode node, int level) {
      System.out.println(makeIndent(level) + node.getName());
      Iterator<AstNode> childs = node.getChilds();
      while(childs.hasNext()) {
         AstNode n = childs.next();
         if(!n.isTerminal())
            PrintAst(n, level+1);
         else
            System.out.println(makeIndent(level) + " " + n.getName() + " " + n.getTerminal().toString());
      }
   }

}
