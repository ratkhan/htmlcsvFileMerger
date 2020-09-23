import com.google.common.io.Files;

import java.io.IOException;

/**
 *  if need to extend list of supported formats
 *  add Parser interface implementation to Parser factory
 */
public class ParserFactory {

    public ParserFactory(){
    }

    public Parser getParser(String fileName) throws IOException {
        String fileExtension = Files.getFileExtension(fileName);
        if (fileExtension.equals("csv")){
            return new ParserCSV(fileName);
        } else if (fileExtension.equals("html")){
            return new ParserHTML(fileName);
        } else {
            return null;
        }
    }


}
