package interpreter;

import java.util.Iterator;

public class ComputeVM {
   private AstNode rootNode;
   private VarTable varTable;

   public ComputeVM(AstNode rootNode, VarTable varTable) {
      this.rootNode = rootNode;
      this.varTable = varTable;
   }
   
   public void Compute() {
      Iterator<AstNode> childs = rootNode.getChilds();
      while(childs.hasNext()) {
         AstNode node = childs.next();
         String nodeName = node.getName();

         if     (nodeName.equals("assign_expr"))
            ComputeAssignExpr(node);
         else if(nodeName.equals("if_expr"))
            ComputeIfExpr(node);
         else if(node.isTerminal() && node.getTerminal().lexema.equals("L_S_BR"))
            ; // пропуск фигурной скобки, если это блок
         else if(node.isTerminal() && node.getTerminal().lexema.equals("R_S_BR"))
            ; // пропуск фигурной скобки, если это блок
         else
            throw new InterpreterException("ComputeVM: не поддерживается нода " + nodeName);
      }
   }

   private void ComputeAssignExpr(AstNode node) {
      PolizCalculator calc = new PolizCalculator(node.getChilds(), varTable);
      System.out.println(calc.getPolizString());
      calc.Calculate();
   }

   private void ComputeIfExpr(AstNode node) {
      Iterator<AstNode> childs = node.getChilds();
      
      AstNode temp_node;
      Iterator<AstNode> temp_childs;
      
      temp_node = childs.next();         // terminal IF_KW 
      temp_node = childs.next();         // if_head
      temp_childs = temp_node.getChilds();
      temp_node = temp_childs.next();    // terminal L_BR
      
      AstNode node_logical_expr = temp_childs.next(); // logical_expr
      PolizCalculator calc = new PolizCalculator(node_logical_expr.getChilds(), varTable);
      System.out.println(calc.getPolizString());
      boolean cond = 0 != calc.Calculate();
      AstNode rootNode;
      if(cond)
         rootNode = childs.next(); // if_body
      else {
         temp_node = childs.next(); // if_body
         temp_node = childs.next(); // terminal ELSE_KW
         rootNode = childs.next(); // else_body
      }
      ComputeVM vm = new ComputeVM(rootNode, varTable);
      vm.Compute();
   }

}
