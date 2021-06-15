package interpreter;

enum TokenType {
   OTHER,
   KEYWORD,
   OPERATION,
   VALUE
}

public class Token {
   public final String lexema;
   public final String value;
   public final MethodCalculator func;
   public final TokenType type;
   private final Integer priority;
   public final int lineNum;
   public final int posNum;

   public Token(String lexema, String value, TokenType type, Integer priority, int lineNum, int posNum) {
      this.lexema = lexema;
      this.value = value;
      this.func = null;
      this.type = type;
      this.priority = priority;
      this.lineNum = lineNum;
      this.posNum = posNum;
   }

   public Token(MethodCalculator func) {
      this.lexema = "FUNCTOR";
      this.value = null;
      this.func = func;
      this.type = TokenType.VALUE;
      this.priority = null;
      this.lineNum = -1;
      this.posNum = -1;
   }

   public int getPriority() {
      if(null == priority)
         throw new InterpreterException("Для токена " + toString() + "приоритет расчета не задан");
      return priority;
   }

   public String toString() {
      if(lexema.equals("FUNCTOR"))
        return lexema + " \"" + func.toString() + "\"";
      else
        return lexema + " \"" + value + "\"" + " [" + lineNum + "," + posNum + "]";
   }
   
   public VarValue getVarValue(VarTable varTable) {
      VarValue r;
      switch (lexema) {
         case "NUMBER":
            return getNumberValue();
         case "VAR":
            return varTable.getValue(value);
         case "CLASSNAME":
            return createObjectValue();
         case "FUNCTOR":
            return func.Calc();
         default:
            throw new InterpreterException("Токен не является значением: " + toString());
      }
   }
   
   private VarValue getNumberValue() {
      String strValue = value;
      if(strValue.contains("."))
         return new VarValue(Double.valueOf(strValue));
      else
         return new VarValue(Integer.valueOf(strValue));
   }
   
   private VarValue createObjectValue() {
      String className = value;
      if(className.equals("List"))
         return new VarValue(className, new List());
      else
         throw new InterpreterException("Недопустимое имя класса: " + toString());
   }
};
