package interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


public class PolizCalculator {
   private List<Token> polizArr = new ArrayList<>();
   private VarTable varTable;
   
   private static Token getToken(AstNode node, VarTable varTable) {
      if(node.isTerminal())
         return node.getTerminal();
      else if(node.getName().equals("method_call"))
         return new Token(MethodCalculator.Create(node, varTable));
      else
         throw new InterpreterException("PolizCalculator: нода не может быть рассчитана " + node.getName());
   } 

   public PolizCalculator(Iterator<AstNode> nodes, VarTable varTable) throws InterpreterException {
      this.varTable = varTable;

      Stack<Token> st = new Stack<>();

      while(nodes.hasNext()) {
         Token token = getToken(nodes.next(), varTable);
         
         if     (TokenType.VALUE == token.type)
            polizArr.add(token);
         else if(TokenType.OPERATION == token.type) {
            int priority = token.getPriority();

            if(st.empty() || (priority == 0) ) 
               st.push(token);
            else {
               if(priority > st.peek().getPriority()) 
                  st.push(token);
               else { 
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
         if(t.lexema.equals("FUNCTOR"))
            result = result + t.func.toString() + " ";
         else      
            result = result + t.value + " ";
      return result;
   }

   public VarValue Calculate() throws InterpreterException {
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
      if(result.type != TokenType.VALUE)
         throw new InterpreterException("Ошибка. Стек после вычислений не содержит результата");

      if(!st.empty())
         throw new InterpreterException("Ошибка. Стек после вычислений не пуст");
      
      return result.getVarValue(varTable);
   }
   
   private VarValue CalcAssignOperation(Stack<Token> st) {
      VarValue right = st.pop().getVarValue(varTable); // правая часть (число, переменная или имя класса)
      String leftVarName = st.pop().value;
      varTable.setValue(leftVarName, right);           // левая часть, должна быть переменной
      if(right.isObject())
         st.push(new Token("VAR", leftVarName, TokenType.VALUE, null, -1, -1));
      else
         st.push(new Token("NUMBER", right.toString(), TokenType.VALUE, null, -1, -1));
      return right;
   }
   
   private VarValue CalcMathOperation(String op, Stack<Token> st) {
      Token b = st.pop();
      Token a = st.pop();
      VarValue r = Operations.calcMath(a.getVarValue(varTable), b.getVarValue(varTable), op);
      st.push(new Token("NUMBER", r.toString(), TokenType.VALUE, null, -1, -1));
      return r;
   }

   private VarValue CalcLogicalOperation(String op, Stack<Token> st) {
      Token b = st.pop();
      Token a = st.pop();
      VarValue r = Operations.calcLogical(a.getVarValue(varTable), b.getVarValue(varTable), op);
      st.push(new Token("NUMBER", r.toString(), TokenType.VALUE, null, -1, -1));
      return r;
   }

   private void addToPoliz(Token t) {
      boolean isBracket = t.lexema.equals("L_BR") || t.lexema.equals("R_BR");
      if(!isBracket)
         polizArr.add(t);
   }
  
}
