package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ParserHTML implements Parser {
    private String fileName;
    private File file;
    private Document htmlFile;
    private Element table;
    private Elements rows;
    private int currentRow;
    private int tableWidth;
    private int tableLength;

    public ParserHTML(String fileName){
        this.fileName = fileName;
        this.currentRow = 1;
    }

    @Override
    public String[] readLine(){
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
    public boolean hasNextLine(){
        return this.currentRow < this.tableLength;
    }

    @Override
    public String[] readHeader(){
        Elements cols = this.rows.select("th");

        String[] HEADERS = new String[cols.size()];

        for (int i = 0; i < cols.size(); i++){
            HEADERS[i] = cols.get(i).text();
        }
        return HEADERS;
    }

    @Override
    public String parse() {
        this.file = new File(this.fileName);
        try {
            this.htmlFile = Jsoup.parse(this.file, "ISO-8859-1");
            this.table = this.htmlFile.getElementById("directory");
            this.rows = this.table.select("tr");
            this.tableWidth = this.rows.select("th").size();
            this.tableLength = this.rows.select("tr").size();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void closeFile(){

    }
}
