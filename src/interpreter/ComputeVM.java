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
         else if(nodeName.equals("while_expr"))
            ComputeWhileExpr(node);
         else if(nodeName.equals("method_call"))
            MethodCalculator.Create(node, varTable).Calc();
         else if(Is_S_BR(node))
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
      AstNode node_logical_expr = node.getChild("if_head").getChild("logical_expr");
      
      PolizCalculator logical_expr = new PolizCalculator(node_logical_expr.getChilds(), varTable);
      System.out.println(logical_expr.getPolizString());
      
      boolean cond = 0 != logical_expr.Calculate().getInteger();
      AstNode rootNode = null;
      if(cond)
         rootNode = node.getChild("if_body");
      else 
         rootNode = node.getChild("else_body");
      ComputeVM vm = new ComputeVM(rootNode, varTable);
      vm.Compute();
   }

   private void ComputeWhileExpr(AstNode node) {
      AstNode node_logical_expr = node.getChild("if_head").getChild("logical_expr");
      
      PolizCalculator logical_expr = new PolizCalculator(node_logical_expr.getChilds(), varTable);
      System.out.println(logical_expr.getPolizString());
      rootNode = node.getChild("if_body");
      ComputeVM vm = new ComputeVM(rootNode, varTable);
      
      while(0 != logical_expr.Calculate().getInteger())
         vm.Compute();
   }
   
   private boolean Is_S_BR(AstNode node) {
      if(node.isTerminal()) {
         String lexema = node.getTerminal().lexema;
         return lexema.equals("L_S_BR") || lexema.equals("R_S_BR");
      }
      return false;
   }
   
}
