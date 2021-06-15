package interpreter;

public class VarValue {
   public static final char INT_TYPE = 'I';
   public static final char DBL_TYPE = 'D';
   public static final char OBJ_TYPE = 'O';
   
   private char type;
   private int intVal;
   private double dblVal;
   private Object objVal;
   private String className;
   
   public VarValue() {
      Init(INT_TYPE);
   }

   public VarValue(int val) {
      set(val);
   }

   public VarValue(double val) {
      set(val);
   }
   public VarValue(String className, Object val) {
      set(className, val);
   }

   public VarValue(VarValue val) {
      set(val);
   }
   public final void set(int val) {
      Init(INT_TYPE);
      intVal = val;
   }

   public final void set(double val) {
      Init(DBL_TYPE);
      dblVal = val;
   }

   public final void set(String className, Object val) {
      Init(OBJ_TYPE);
      objVal = val;
      this.className = className;
   }

   public final void set(VarValue val) {
      Init(val.getType());
      intVal = val.getInteger();
      dblVal = val.getDouble();
      objVal = val.getObject();
      className = val.getClassName();
   }
   
   public char getType() {
      return type;
   }
   
   public boolean isInteger() {
      return type == INT_TYPE;
   }

   public boolean isDouble() {
      return type == DBL_TYPE;
   }

   public boolean isObject() {
      return type == OBJ_TYPE;
   }
   
   public int getInteger() {
      return intVal;
   }
   
   public double getDouble() {
      return dblVal;
   }

   public Object getObject() {
      return objVal;
   }
   
   public String getClassName() {
      return className;
   }
   
   public String toString() {
      if   (isInteger())
         return Integer.toString(intVal);
      else if(isDouble())
         return Double.toString(dblVal);
      else if(isObject())
         return className;
      else
         return "unknown VarValue";
   }
   
   private void Init(char type) {
      this.type = type;
      intVal = 0;
      dblVal = 0.0;
      objVal = null;
      className = null;
   }
}
