package interpreter;

import java.util.Iterator;

class BracketCounter {
   private final String leftBr; 
   private final String rightBr; 
   private int count = 0;
   
   public BracketCounter(String leftBr, String rightBr) {
      this.leftBr = leftBr;
      this.rightBr = rightBr;
   }

   public void Count(String br) {
      if     (br.equals(leftBr))
         count++;
      else if(br.equals(rightBr))
         count--;
   } 
   
   public boolean isError() {
      return count != 0;
   }
}

public class Parser {

   private Iterator<Token> tokens;
   private Token currentToken;
   private VarTable varTable = new VarTable();
   
   private BracketCounter count_BR = new BracketCounter("L_BR", "R_BR");
   private BracketCounter count_S_BR = new BracketCounter("L_S_BR", "R_S_BR");

   private Token nextToken() {
      if(tokens.hasNext())
         return tokens.next();
      else
         return new Token("EOF", "", TokenType.OTHER, null, -1, -1);
   }

   public Parser(Iterator<Token> tokens) {
      this.tokens = tokens;
      currentToken = nextToken();
      if(currentToken.lexema.equals("EOF"))
         throw new InterpreterException("Отсутствуют токены");
   }

   public VarTable getVarTable() {
      return varTable;
   }

   // lang -> expr*
   public AstNode lang() {
      AstNode node = new AstNode("lang");
      node.addChild(expr());
      while(!currentToken.lexema.equals("EOF"))
         node.addChild(expr());
      
      if(count_BR.isError())
         throw new InterpreterException("Несогласованность круглых скобок");
      if(count_S_BR.isError())
         throw new InterpreterException("Несогласованность фигурных скобок");

      return node;
   }

   // expr -> assign_expr | if_expr
   private AstNode expr() {
      if      (currentToken.lexema.equals("VAR"))
         return assign_expr();
      else if (currentToken.lexema.equals("IF_KW"))
         return if_expr();
      else if (currentToken.lexema.equals("WHILE_KW"))
         return while_expr();
      else
         throw new InterpreterException(currentToken);
   }

   // assign_expr -> VAR ASSIGN_OP value (0P value)* // TODO: как в продукциях учитываются скобки?
   private AstNode assign_expr() {
      AstNode node = new AstNode("assign_expr");

      match("VAR", node);
      match("ASSIGN_OP", node);
      match_value(node);

      boolean stop = false;
      while(!stop) {
         if     (currentToken.lexema.equals("MATH_OP"))
            match("MATH_OP", node);
         else if(currentToken.lexema.equals("LOGICAL_OP"))
            match("LOGICAL_OP", node);
         else
            stop = true;

         if(!stop) {
            // Учет скобок в коде
            while(currentToken.lexema.equals("L_BR"))
               match("L_BR", node);
            match_value(node);
            while(currentToken.lexema.equals("R_BR"))
               match("R_BR", node);
         }
      }

      return node;
   }

   // value -> NUMBER | VAR
   private void match_value(AstNode node) {
      if      (currentToken.lexema.equals("NUMBER"))
         match("NUMBER", node);
      else if (currentToken.lexema.equals("VAR"))
         match("VAR", node);
      else
         throw new InterpreterException(currentToken);
   }

   //if_expr -> IF_KW if_head block (ELSE_KW block)?
   private AstNode if_expr() {
      AstNode node = new AstNode("if_expr");

      match("IF_KW", node);
      node.addChild(if_head());
      node.addChild(block("if_body"));
      if(currentToken.lexema.equals("ELSE_KW"))
      {
          match("ELSE_KW", node);
          node.addChild(block("else_body"));
      }

      return node;
   }

   //if_head -> L_BR logical_expr R_BR
   private AstNode if_head() {
      AstNode node = new AstNode("if_head");
      match("L_BR", node);
      node.addChild(logical_expr());
      match("R_BR", node);

      return node;
   }

   //logical_expr -> value (LOGICAL_0P value)*
   private AstNode logical_expr() {
      AstNode node = new AstNode("logical_expr");

      match_value(node);

      boolean stop = false;
      while(!stop) {
         if(currentToken.lexema.equals("LOGICAL_OP"))
            match("LOGICAL_OP", node);
         else
            stop = true;

         if(!stop)
            match_value(node);
      }

      return node;
   }

   //while_expr -> WHILE_KW if_head if_body
   private AstNode while_expr() {
      AstNode node = new AstNode("while_expr");
      match("WHILE_KW", node);
      node.addChild(if_head());
      node.addChild(block("if_body"));

      return node;
   }

   //block -> L_S_BR expr* R_S_BR
   private AstNode block(String nodeName) {
      AstNode node = new AstNode(nodeName);
      match("L_S_BR", node);

      node.addChild(expr());
      while(!currentToken.lexema.equals("EOF") && !currentToken.lexema.equals("R_S_BR"))
         node.addChild(expr());

      match("R_S_BR", node);
      return node;
   }

   private void match(String terminal, AstNode node) {
      if(!currentToken.lexema.equals(terminal))
         throw new InterpreterException("Ожидался токен " + terminal + " вместо " + currentToken.toString());
      node.addChild(currentToken);

      // Если это переменная, то добавляем её в таблицу;
      if(currentToken.lexema.equals("VAR"))
         varTable.addVariable(currentToken.value);
      
      count_BR.Count(terminal);
      count_S_BR.Count(terminal);

      currentToken = nextToken();
   }
}
