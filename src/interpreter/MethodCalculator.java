package interpreter;

import java.util.Iterator;

public abstract class MethodCalculator {
   private VarTable varTable;
   protected Object obj;
   private String objVarName;
   protected String methodName;
   protected Iterator<AstNode> args;
   
   static MethodCalculator Create(AstNode rootNode, VarTable varTable) {
      String varName = rootNode.getChildToken("OBJ_VAR").value;
      VarValue val = varTable.getValue(varName);
      
      if(val.isObject())
         return new ListMethodCalculator(rootNode, varTable);
      else
         throw new InterpreterException("MethodCalculator не может выполнить ноду " + rootNode.getName() );
   }
   
   public MethodCalculator(AstNode rootNode, VarTable varTable) {
      this.varTable = varTable;
      
      Iterator<AstNode> childs = rootNode.getChilds();
      this.objVarName = childs.next().getTerminal().value;
      this.obj = varTable.getValue(objVarName).getObject();
      this.methodName = childs.next().getTerminal().value;
      this.args = childs.next().getChilds();
      args.next(); // "("
   }
   
   public abstract VarValue Calc();
   
   public String toString() {
      return objVarName + "." + methodName + "()";
   }
   
   protected VarValue getNextArg() {
      VarValue result = null;
      boolean found = false;

      if(args.hasNext()) {
         AstNode arg = args.next();

         if(arg.getName().equals("math_expr")) {
            PolizCalculator math_expr = new PolizCalculator(arg.getChilds(), varTable);
            System.out.println(math_expr.getPolizString());
            result = math_expr.Calculate();

            if(args.hasNext()) {
               AstNode temp = args.next();
               if(temp.isTerminal())
                  found = temp.getTerminal().lexema.equals("COMMA") ||
                          temp.getTerminal().lexema.equals("R_BR");
            }
         }
      }

      if(!found)
         throw new InterpreterException("Некорректные параметры метода " + objVarName + "." + methodName);
      return result;
   }
   
}

class ListMethodCalculator extends MethodCalculator {
   
   public ListMethodCalculator(AstNode rootNode, VarTable varTable) {
      super(rootNode, varTable);
   }
   
   @Override
   public VarValue Calc() {
      VarValue r = null;
      List listObj = (List)obj;
      switch (methodName) {
         case "get":
            r = listObj.get();
            break;
         case "remove":
            r = listObj.remove();
            break;
         case "gotoNext":
            r = new VarValue(Operations.Bool2Int(listObj.gotoNext()));
            break;
         case "gotoPrev":
            r = new VarValue(Operations.Bool2Int(listObj.gotoPrev()));
            break;
         case "add":
            listObj.add(getNextArg());
            break;
         case "reset":
            listObj.reset();
            break;
         default:
            throw new InterpreterException("У класса List отсутствует метод " + methodName);
      }
      
      return r;
   }
}
