package interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Превращает абсолютную позицию лексемы в файле в номер строки\номер начального символа в строке
class PosToLinePos {
   private List<Integer> lineBegins = new ArrayList<>();
   int absolutePos, lineNum, posNum, textSize;

   public PosToLinePos(String text) {
      if(!text.substring(text.length()-2).equals("\r\n"))
         text += "\r\n";
      
      textSize = text.length();
      int pos0 = 0, pos1;
      lineBegins.add(pos0);
      while( (pos1 = text.indexOf("\n", pos0)) != -1) {
         pos0 = pos1 + 2;
         lineBegins.add(pos0);
      }
   }

   public void calculate(int absolutePos) {
      if(absolutePos >= textSize)
         throw new InterpreterException("Неверная абсолютная позиция лексемы в файле: pos = " + absolutePos);

      lineNum = 0;
      while(absolutePos >= lineBegins.get(lineNum))
         lineNum++;
      lineNum--;
      posNum = absolutePos - lineBegins.get(lineNum);

      // Переход от нумерации с нуля к нумерации с единицы
      lineNum++;
      posNum++;
   }

   public int getLineNum() {
      return lineNum;
   }

   public int getPosNum() {
      return posNum;
   }
}

class Tokenizer {
   public final String lexema;
   public final TokenType type;
   public final Integer priority;
   private Pattern pattern;
   private Matcher matcher;

   public Tokenizer(String regex, String lexema, TokenType type, Integer priority) {
      this.pattern = Pattern.compile(regex);
      this.lexema = lexema;
      this.type = type;
      this.priority = priority;
   }

   public boolean find(String text) {
      matcher = pattern.matcher(text);
      return matcher.find();
   }
   
   public Matcher getMatcher() {
      return matcher;
   }
};

public class Lexer {
   private List<Token> tokens = new ArrayList<>();

   public Lexer(String text) {
      PosToLinePos posCalc = new PosToLinePos(text);

      List<Tokenizer> tokenizers = new ArrayList<>();

      tokenizers.add(new Tokenizer("if(?=\\s*\\()",    "IF_KW",      TokenType.KEYWORD, null));
      tokenizers.add(new Tokenizer("else(?=\\s*\\{)",  "ELSE_KW",    TokenType.KEYWORD, null));
      tokenizers.add(new Tokenizer("while(?=\\s*\\()", "WHILE_KW",   TokenType.KEYWORD, null));
      tokenizers.add(new Tokenizer("List(?=\\s*)",     "CLASSNAME",  TokenType.VALUE,   null));
      tokenizers.add(new Tokenizer("add|reset|get|remove|gotoNext|gotoPrev(?=\\s*\\()", "METHOD_KW",  TokenType.KEYWORD, null));
      
      tokenizers.add(new Tokenizer("[a-zA-Z_][a-zA-Z_0-9]*(?=\\s*\\.)", "OBJ_VAR",  TokenType.OTHER, null));
      tokenizers.add(new Tokenizer("[a-zA-Z_][a-zA-Z_0-9]*(?!\\s*\\.)", "VAR",      TokenType.VALUE, null));
      tokenizers.add(new Tokenizer("[0-9]+(\\.[0-9]+)?",                "NUMBER",   TokenType.VALUE, null));

      // Приоритет используется только для операций и скобок
      tokenizers.add(new Tokenizer("\\(", "L_BR",   TokenType.OPERATION, new Integer(0)));
      tokenizers.add(new Tokenizer("\\)", "R_BR",   TokenType.OPERATION, new Integer(1)));
      tokenizers.add(new Tokenizer("\\{", "L_S_BR", TokenType.OTHER,     new Integer(0)));
      tokenizers.add(new Tokenizer("\\}", "R_S_BR", TokenType.OTHER,     new Integer(1)));
      
      tokenizers.add(new Tokenizer("\\*|/",           "MATH_OP",    TokenType.OPERATION, new Integer(7))); // Математические операции
      tokenizers.add(new Tokenizer("\\+|-",           "MATH_OP",    TokenType.OPERATION, new Integer(6))); // Математические операции
      tokenizers.add(new Tokenizer("\\|\\|",          "LOGICAL_OP", TokenType.OPERATION, new Integer(3))); //Логические операции
      tokenizers.add(new Tokenizer("&&",              "LOGICAL_OP", TokenType.OPERATION, new Integer(4))); //Логические операции
      tokenizers.add(new Tokenizer(">=|>|<=|<|==|!=", "LOGICAL_OP", TokenType.OPERATION, new Integer(5))); //Логические операции
      tokenizers.add(new Tokenizer("=", "ASSIGN_OP", TokenType.OPERATION, new Integer(2))); // Операция присваивания
      
      tokenizers.add(new Tokenizer(",", "COMMA",     TokenType.OTHER, null)); // Запятая в параметрах методов
 
      for(Tokenizer tokenizer : tokenizers) {
         while(tokenizer.find(text)) {
            Matcher matcher = tokenizer.getMatcher();
            posCalc.calculate(matcher.start());
            tokens.add(new Token(tokenizer.lexema, matcher.group(), 
                                 tokenizer.type, tokenizer.priority,
                                 posCalc.getLineNum(), posCalc.getPosNum()));
            text = SetSpaces(text, matcher.start(), matcher.group().length());
         }
      }

      Comparator<Token> comparator = new Comparator<Token>() {
         @Override
         public int compare(Token left, Token right) {
            if      (left.lineNum < right.lineNum) return -1;
            else if (left.lineNum > right.lineNum) return 1;
            else {
               if      (left.posNum < right.posNum) return -1;
               else if (left.posNum > right.posNum) return 1;
               return 0;
            }
         }
      };
      Collections.sort(tokens, comparator);
   }

   public Iterator<Token> getIterator() {
      return tokens.iterator();
   }
   
   private static String SetSpaces(String text, int pos, int count) {
      String spaces = "";
      for(int i = 0; i < count; i++)
         spaces += " ";
      return text.substring(0, pos) + spaces + text.substring(pos + count);
   }
   
}
