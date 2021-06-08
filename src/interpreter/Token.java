package interpreter;

public class Token {
   public final String lexema;
   public final String value;
   public final TokenType type;
   private final Integer priority;
   public final int lineNum;
   public final int posNum;

   public Token(String lexema, String value, TokenType type, Integer priority, int lineNum, int posNum) {
      this.lexema = lexema;
      this.value = value;
      this.type = type;
      this.priority = priority;
      this.lineNum = lineNum;
      this.posNum = posNum;
   }

   public int getPriority() {
      if(null == priority)
         throw new InterpreterException("Для токена " + toString() + "приоритет расчета не имеет смысла");
      return priority;
   }

   public String toString() {
        return lexema + " \"" + value + "\"" + " [" + lineNum + "," + posNum + "]";
   }
};
