package interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AstNode {
    private String name;
    private Token token;
    private List<AstNode> childs = new ArrayList<AstNode>();

    public AstNode(String name) {
        this.name = name;
        this.token = null;
    }

    private AstNode(Token token) {
        this.name = "terminal";
        this.token = token;
    }

    public void addChild(AstNode node) {
        childs.add(node);
    }

    public void addChild(Token token) {
        childs.add(new AstNode(token));
    }

    public String getName() {
        return name;
    }

    public boolean isTerminal() {
       return null != token;
    }

    public Iterator<AstNode> getChilds() {
       return childs.iterator();
    }

    public Token getTerminal() {
       return token;
    }

   public Token findChildToken(String tokenLexema) {
      AstNode node = null;
      boolean found = false;
      Iterator<AstNode> childs = getChilds();
      while(!found && childs.hasNext()) {
         node = childs.next();
         found = node.isTerminal() && node.getTerminal().lexema.equals(tokenLexema);
      }

      if(!found)
         return null;
      return node.getTerminal();
   }

   public Token getChildToken(String tokenLexema) {
      Token t = findChildToken(tokenLexema);
       if(null == t)
          throw new InterpreterException("Среди потомков ноды " + name + " не найден токен " + tokenLexema);
       return t;
    }
    
   public AstNode findChild(String nodeName) {
      AstNode node = null;
      boolean found = false;
      Iterator<AstNode> childs = getChilds();
      while(!found && childs.hasNext()) {
         node = childs.next();
         found = node.getName().equals(nodeName);
      }

      if(!found)
         return null;
      return node;
   }
   
   public AstNode getChild(String nodeName) {
      AstNode node = findChild(nodeName);
      if(null == node)
         throw new InterpreterException("Среди потомков ноды " + name + " не найдена нода с именем " + nodeName);
      return node;
   }
}
