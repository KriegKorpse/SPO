package interpreter;

class InterpreterException extends RuntimeException {
   public InterpreterException(String text) {
       super(text);
   }
   public InterpreterException(Token token) {
       super("Ошибка в токене " + token.toString());
   }
}