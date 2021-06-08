package interpreter;

import java.io.FileReader;
import java.io.IOException;

public class TextReader {
   String text = "";
   public TextReader(String fileName) {
      try (FileReader reader = new FileReader(fileName)) {
         // читаем посимвольно
         int c;
         while((c = reader.read()) != -1)
            text += (char)c;
      }
      catch(IOException ex){
          throw new InterpreterException("Ошибка чтения файла " + fileName + ": " + ex.getMessage());
      }
   }

   public String getText() {
      return text;
   }

}
