
package interpreter;

public class Operations {
   
   public static VarValue calcMath(VarValue a, VarValue b, String op) {
      if(     a.isInteger() && b.isInteger())
         return calcMath(a.getInteger(), b.getInteger(), op);
      else if(a.isInteger() && b.isDouble())
         return calcMath((double)a.getInteger(), b.getDouble(), op);
      else if(a.isDouble() && b.isInteger())
         return calcMath(a.getDouble(), (double)b.getInteger(), op);
      else if(a.isDouble() && b.isDouble())
         return calcMath(a.getDouble(), b.getDouble(), op);
      else
         throw new InterpreterException("Операция не определена: " + a.toString() + " " + op + " " + b.toString());
   }

   public static VarValue calcLogical(VarValue a, VarValue b, String op) {
      if(     a.isInteger() && b.isInteger())
         return calcLogical(a.getInteger(), b.getInteger(), op);
      else if(a.isInteger() && b.isDouble())
         return calcLogical((double)a.getInteger(), b.getDouble(), op);
      else if(a.isDouble() && b.isInteger())
         return calcLogical(a.getDouble(), (double)b.getInteger(), op);
      else if(a.isDouble() && b.isDouble())
         return calcLogical(a.getDouble(), b.getDouble(), op);
      else
         throw new InterpreterException("Операция не определена: " + a.toString() + " " + op + " " + b.toString());
   }
   
   private static VarValue calcMath(int a, int b, String op) {
      VarValue r = new VarValue();
      switch(op) {
         case "+":
            r.set(a + b);
            break;
         case "-":
            r.set(a - b);
            break;
         case "*":
            r.set(a * b);
            break;
         case "/":
            r.set(a / b);
            break;
         default:
            throw new InterpreterException("Операция " + op + " не является математической");
      }
      
      return r;
   }

   private static VarValue calcMath(double a, double b, String op) {
      VarValue r = new VarValue();

      switch(op) {
         case "+":
            r.set(a + b);
            break;
         case "-":
            r.set(a - b);
            break;
         case "*":
            r.set(a * b);
            break;
         case "/":
            r.set(a / b);
            break;
         default:
            throw new InterpreterException("Операция " + op + " не является математической");
      }
      
      return r;
   }
   
   private static VarValue calcLogical(int a, int b, String op) {
         VarValue r = new VarValue();

         switch(op) {
         case "&&":
            r.set(Bool2Int(Int2Bool(a) && Int2Bool(b)));
            break;
         case "||":
            r.set(Bool2Int(Int2Bool(a) || Int2Bool(b)));
            break;
         case "<":
            r.set(Bool2Int(a < b));
            break;
         case "<=":
            r.set(Bool2Int(a <= b));
            break;
         case ">":
            r.set(Bool2Int(a > b));
            break;
         case ">=":
            r.set(Bool2Int(a >= b));
            break;
         case "==":
            r.set(Bool2Int(a == b));
            break;
         case "!=":
            r.set(Bool2Int(a != b));
            break;
         default:
            throw new InterpreterException("Операция " + op + " не является логической");
      }
      return r;
   }

   private static VarValue calcLogical(double a, double b, String op) {
         VarValue r = new VarValue();

         switch(op) {
         case "&&":
            r.set(Bool2Int(Dbl2Bool(a) && Dbl2Bool(b)));
            break;
         case "||":
            r.set(Bool2Int(Dbl2Bool(a) || Dbl2Bool(b)));
            break;
         case "<":
            r.set(Bool2Int(a < b));
            break;
         case "<=":
            r.set(Bool2Int(a <= b));
            break;
         case ">":
            r.set(Bool2Int(a > b));
            break;
         case ">=":
            r.set(Bool2Int(a >= b));
            break;
         case "==":
            r.set(Bool2Int(a == b));
            break;
         case "!=":
            r.set(Bool2Int(a != b));
            break;
         default:
            throw new InterpreterException("Операция " + op + " не является логической");
      }
      return r;
   }
   
   public static boolean Int2Bool(int a) {
      return a != 0;
   }
   public static int Bool2Int(boolean a) {
      if(a)
         return 1;
      else
         return 0;
   }
   public static boolean Dbl2Bool(double a) {
      return a != 0.0;
   }
  
}
