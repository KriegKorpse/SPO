package interpreter;

import java.util.HashMap;
import java.util.Map.Entry;

public class VarTable {
   private final HashMap<String, Integer> table = new HashMap<>();
   private final int defIntVal = 0;

   public void addVariable(String varName) {
      if(!table.containsKey(varName))
         table.put(varName, defIntVal);
   }

   public void setValue(String varName, int varValue) {
      if(!table.containsKey(varName))
         throw new InterpreterException("Не найдена переменная с именем " + varName);
      table.put(varName, varValue);
   }

   public int getValue(String varName) {
      Integer value = table.get(varName);
      if(null == value)
         throw new InterpreterException("Не найдена переменная с именем " + varName);
      return value;
   }

   public void Print() {
      for(Entry entry: table.entrySet()) {
        System.out.println(entry.getKey() + " = " + entry.getValue());
      }
   }
}
