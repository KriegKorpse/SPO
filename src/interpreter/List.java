package interpreter;

class ListElem {
   private ListElem nextElem = null;
   private ListElem prevElem = null;
   private VarValue val = null;
   
   public ListElem getHead() {
      ListElem head = this;
      while(null != head.prevElem)
         head = head.prevElem;
      return head;
   }

   public ListElem getTail() {
      ListElem tail = this;
      while(null != tail.nextElem)
         tail = tail.nextElem;
      return tail;
   }
   
   public ListElem getNext() {
      if(null != nextElem)
         return nextElem;
      return null;
   }
   
   public ListElem getPrev() {
      if(null != prevElem)
         return prevElem;
      return null;
   }
   
   public VarValue getElem() {
      return val;
   }

   // Вставка после текущего   
   public ListElem add(VarValue newVal) {
      if(val != null) {
         ListElem newElem = new ListElem();
         newElem.nextElem = nextElem;
         nextElem = newElem;
         newElem.prevElem = this;
         newElem.val = new VarValue(newVal);
         return newElem;
      }
      else {
         val = new VarValue(newVal);
         return this;
      }
   }

   // Удаление текущего
   public VarValue removeElem() {
      VarValue temp = val;
      val = null;
      if(prevElem != null)
         prevElem.nextElem = nextElem;
      if(nextElem != null)
         nextElem.prevElem = prevElem;
      if(prevElem == nextElem)
         prevElem = nextElem = null;
      return temp;
   }

}

public class List {
   ListElem current = new ListElem();
   boolean isReset = true;
   
   public void reset() {
      isReset = true;
   }
   public boolean gotoNext() {
      ListElem temp;
      if(isReset)
         temp = current.getHead();
      else
         temp = current.getNext();
      isReset = (null == temp) || (null == temp.getElem());
      if(!isReset)
         current = temp;
      return !isReset;
   }
   public boolean gotoPrev() {
      ListElem temp;
      if(isReset)
         temp = current.getTail();
      else
         temp = current.getPrev();
      isReset = (null == temp) || (null == temp.getElem());
      if(!isReset)
         current = temp;
      return !isReset;
   }
   
   public VarValue get() {
      if(isReset)
         throw new RuntimeException("Попытка доступа к несуществующему элементу списка");
      return current.getElem();
   }

   public void add(VarValue newVal) {
      current = current.add(newVal);
   }

   public VarValue remove() {
      if(isReset)
         throw new RuntimeException("Попытка удаления несуществующего элемента списка");
      ListElem temp = current;
      gotoNext();
      return temp.removeElem();
   }

}
