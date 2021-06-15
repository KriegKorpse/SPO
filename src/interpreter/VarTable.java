package interpreter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class VarTable {
   private final LinkedHashMap<String, VarValue> table = new LinkedHashMap<>();

   public void addVariable(String varName) {
      if(!table.containsKey(varName))
         table.put(varName, new VarValue());
   }

   public void setValue(String varName, VarValue varValue) {
      if(!isExist(varName))
         throw new InterpreterException("Не найдена переменная с именем " + varName);
      table.put(varName, varValue);
   }

   public VarValue getValue(String varName) {
      VarValue value = table.get(varName);
      if(null == value)
         throw new InterpreterException("Не найдена переменная с именем " + varName);
      return value;
   }

   public boolean isExist(String varName) {
      return table.containsKey(varName);
   }

   public void Print() {
      Set<String> keys = table.keySet();
      for(String key: keys) {
        System.out.println(key + " = " + getValue(key).toString());
      }
   }
}
