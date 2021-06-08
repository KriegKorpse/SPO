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
}
