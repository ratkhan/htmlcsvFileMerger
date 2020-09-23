import jdk.nashorn.internal.ir.annotations.Ignore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;


public class ParserHTML implements Parser {
    private Elements rows;
    private int currentRow;
    private int tableWidth;
    private int tableLength;

    public ParserHTML(String fileName) throws IOException{
        this.currentRow = 1;

            Document htmlFile = Jsoup.parse(new File(fileName), "UTF-8");
            Element table = htmlFile.getElementById("directory");
            this.rows = table.select("tr");
            this.tableWidth = this.rows.select("th").size();
            this.tableLength = this.rows.select("tr").size();

    }

    @Override
    public String[] readLine() throws IOException{
        String[] dataRow = new String[this.tableWidth];

        //return null if reached end of the table(file)
        if (this.currentRow >= this.tableLength ){
            return null;
        }

        for (int j = 0; j < this.tableWidth; j++) {
            dataRow[j] = rows.select("tr").get(this.currentRow).select("td").get(j).text();
        }

        this.currentRow++;
        return dataRow;
    }

    @Override
    public boolean hasNextLine() throws IOException{
        return this.currentRow < this.tableLength;
    }

    @Override
    public String[] readHeader() throws IOException{
        Elements cols = this.rows.select("th");

        String[] HEADERS = new String[cols.size()];

        for (int i = 0; i < cols.size(); i++){
            HEADERS[i] = cols.get(i).text();
        }
        return HEADERS;
    }

    //Jsoup reader does not require explicit closing
    @Override
    public void close() throws IOException{
        return;
    }
}
