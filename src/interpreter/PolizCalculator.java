package interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


public class PolizCalculator {
   private List<Token> polizArr = new ArrayList<>();
   private VarTable varTable;

   public PolizCalculator(Iterator<AstNode> nodes, VarTable varTable) throws InterpreterException {
      this.varTable = varTable;

      Stack<Token> st = new Stack<>();

      while(nodes.hasNext()) {
         AstNode node = nodes.next();
         if(!node.isTerminal())
            throw new InterpreterException("PolizCalculator: вложенные выражения не поддерживаются");
         
         Token token = node.getTerminal();

         if     (TokenType.VALUE == token.type)
            polizArr.add(token);
         else if(TokenType.OPERATION == token.type) {
            int priority = token.getPriority();

            if(st.empty() || (priority == 0) ) 
               st.push(token);
            else {
               if(priority > st.peek().getPriority()) 
                  st.push(token);
               else { // (3b)
                  while(!st.empty() && priority <= st.peek().getPriority())
                     addToPoliz(st.pop());
                  st.push(token);
               }
            }
         }
         else
            throw new InterpreterException("PolizCalculator не поддерживает токен " + token.toString());
      }

      while(!st.empty()) {
         addToPoliz(st.pop());
      }
   }

   public String getPolizString() {
      String result = "";
      for(Token t : polizArr)
         result += t.value;
      return result;
   }

   public int Calculate() throws InterpreterException {
      Stack<Token> st = new Stack<>();
      
      for(Token token : polizArr) {
         if     (TokenType.VALUE == token.type)
            st.push(token);
         else if(TokenType.OPERATION == token.type) {
            if     (token.lexema.equals("ASSIGN_OP")) 
               CalcAssignOperation(st);
            else if(token.lexema.equals("MATH_OP"))
               CalcMathOperation(token.value, st);
            else if(token.lexema.equals("LOGICAL_OP"))
               CalcLogicalOperation(token.value, st);
            else
               throw new InterpreterException("PolizCalculator не поддерживает операцию " + token.toString());
         }
      }
      
      // В стеке должно остаться одно значение - результат последней операции
      Token result = st.pop();
      if(!result.lexema.equals("NUMBER"))
         throw new InterpreterException("Какая-то ошибка. Стек после вычислений не содержит результата");

      if(!st.empty())
         throw new InterpreterException("Какая-то ошибка. Стек после вычислений не пуст");
      
      return Integer.valueOf(result.value);
   }
   
   private int CalcAssignOperation(Stack<Token> st) {
      int a = getValue(st.pop());           // правая часть
      varTable.setValue(st.pop().value, a); // левая часть, должна быть переменной
      st.push(new Token("NUMBER", Integer.toString(a), TokenType.VALUE, null, -1, -1));
      return a;
   }
   
   private int CalcMathOperation(String op, Stack<Token> st) {
      int b = getValue(st.pop());
      int a = getValue(st.pop());

      switch(op) {
         case "+":
            a = a + b;
            break;
         case "-":
            a = a - b;
            break;
         case "*":
            a = a * b;
            break;
         case "/":
            a = a / b;
            break;
         default:
            throw new InterpreterException("PolizCalculator: операция " + op + " не является математической");
      }

      st.push(new Token("NUMBER", Integer.toString(a), TokenType.VALUE, null, -1, -1));
      return a;
   }

   private int CalcLogicalOperation(String op, Stack<Token> st) {
      int b = getValue(st.pop());
      int a = getValue(st.pop());

      switch(op) {
         case "&&":
            a = Bool2Int(Int2Bool(a) && Int2Bool(b));
            break;
         case "||":
            a = Bool2Int(Int2Bool(a) || Int2Bool(b));
            break;
         case "<":
            a = Bool2Int(a < b);
            break;
         case "<=":
            a = Bool2Int(a <= b);
            break;
         case ">":
            a = Bool2Int(a > b);
            break;
         case ">=":
            a = Bool2Int(a >= b);
            break;
         case "==":
            a = Bool2Int(a == b);
            break;
         case "!=":
            a = Bool2Int(a != b);
            break;
         default:
            throw new InterpreterException("PolizCalculator: операция " + op + " не является логической");
      }

      st.push(new Token("NUMBER", Integer.toString(a), TokenType.VALUE, null, -1, -1));
      return a;
   }

   private void addToPoliz(Token t) {
      boolean isBracket = t.lexema.equals("L_BR") || t.lexema.equals("R_BR");
      if(!isBracket)
         polizArr.add(t);
   }

   private int getValue(Token token) {
      if     (token.lexema.equals("NUMBER"))
         return Integer.valueOf(token.value);
      else if(token.lexema.equals("VAR"))
         return varTable.getValue(token.value);
      else
         throw new InterpreterException("PolizCalculator: значение может быть получено только для токена с типом VALUE. " + token.toString() + " не может быть получено ");
   }
   
   private boolean Int2Bool(int a) {
      return a != 0;
   }
   private int Bool2Int(boolean a) {
      if(a)
         return 1;
      else
         return 0;
   }
   
}
