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
      textSize = text.length();
      int pos0 = 0, pos1;
      lineBegins.add(pos0);
      while( (pos1 = text.indexOf("\r\n", pos0)) != -1) {
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
   public final Matcher matcher;
   public final String lexema;
   public final TokenType type;
   public final Integer priority;

   public Tokenizer(String regex, String lexema, TokenType type, Integer priority, String text) {
      this.matcher = Pattern.compile(regex).matcher(text);
      this.lexema = lexema;
      this.type = type;
      this.priority = priority;
   }
};

enum TokenType {
   OTHER,
   KEYWORD,
   OPERATION,
   VALUE
}

public class Lexer {
   private List<Token> tokens_array = new ArrayList<>();

   public Lexer(String text) {
      PosToLinePos posCalc = new PosToLinePos(text);

      List<Tokenizer> tokenizers = new ArrayList<>();
      tokenizers.add(new Tokenizer("if", "IF_KW", TokenType.KEYWORD, new Integer(0), text));
      tokenizers.add(new Tokenizer("else", "ELSE_KW", TokenType.KEYWORD, new Integer(1), text));
      tokenizers.add(new Tokenizer("while", "WHILE_KW", TokenType.KEYWORD, null, text));
      tokenizers.add(new Tokenizer("do", "DO_KW", TokenType.KEYWORD, null, text));
      tokenizers.add(new Tokenizer("\\(", "L_BR", TokenType.OPERATION, new Integer(0), text));
      tokenizers.add(new Tokenizer("\\)", "R_BR", TokenType.OPERATION, new Integer(1), text));
      tokenizers.add(new Tokenizer("\\{", "L_S_BR", TokenType.OTHER, new Integer(0), text));
      tokenizers.add(new Tokenizer("\\}", "R_S_BR", TokenType.OTHER, new Integer(1), text));
      // Для значений приоритет не имеет смысла
      tokenizers.add(new Tokenizer("[a-zA-Z_][a-zA-Z_0-9]*", "VAR", TokenType.VALUE, null, text));
      tokenizers.add(new Tokenizer("[0-9]+(\\.[0-9]+)?", "NUMBER", TokenType.VALUE, null, text));
      tokenizers.add(new Tokenizer("(?<!>)(?<!<)(?<!=)(?<!!)=(?!=)", "ASSIGN_OP", TokenType.OPERATION, new Integer(2), text)); // Операция присваивания
      tokenizers.add(new Tokenizer("(\\*|/)", "MATH_OP", TokenType.OPERATION, new Integer(7), text)); // Математические операции
      tokenizers.add(new Tokenizer("(\\+|-)", "MATH_OP", TokenType.OPERATION, new Integer(6), text)); // Математические операции
      tokenizers.add(new Tokenizer("(\\|\\|)", "LOGICAL_OP", TokenType.OPERATION, new Integer(3), text)); //Логические операции
      tokenizers.add(new Tokenizer("(&&)", "LOGICAL_OP", TokenType.OPERATION, new Integer(4), text)); //Логические операции
      tokenizers.add(new Tokenizer("(>=|>|<=|<|==|!=)", "LOGICAL_OP", TokenType.OPERATION, new Integer(5), text)); //Логические операции

      for(Tokenizer tokenizer : tokenizers) {
         while(tokenizer.matcher.find())
         {
            boolean skip = false;
            if(tokenizer.lexema.equals("VAR"))
            {
               if(tokenizer.matcher.group().equals("if")|
                  tokenizer.matcher.group().equals("while")|
                  tokenizer.matcher.group().equals("else")|
                  tokenizer.matcher.group().equals("do"))
                  skip = true;
            }
            if(!skip) {
               posCalc.calculate(tokenizer.matcher.start());
               tokens_array.add(new Token(tokenizer.lexema, tokenizer.matcher.group(),
                                          tokenizer.type, tokenizer.priority,
                                          posCalc.getLineNum(), posCalc.getPosNum()));
            }
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
      Collections.sort(tokens_array, comparator);
   }

   public Iterator<Token> getIterator() {
      return tokens_array.iterator();
   }
}
