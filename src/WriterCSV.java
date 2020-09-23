import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WriterCSV {
    private CSVWriter writer;

    public WriterCSV(String fileName){
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(fileName));

            this.writer = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        } catch ( IOException e){
            e.printStackTrace();
        }
    }

    public void writeLine(String[] newLine){
        writer.writeNext(newLine);
    }

    public void closeWriter(){
        try {
            this.writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
