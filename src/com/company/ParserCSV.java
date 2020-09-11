package com.company;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ParserCSV implements Parser  {
    private Reader reader;
    private CSVReader CSVreader;
    private String[] header;

    public ParserCSV(String fileName) throws IOException{
            Path path = Paths.get(fileName);
            this.reader = Files.newBufferedReader(path);
            this.CSVreader = new CSVReader(this.reader);
            this.header = this.CSVreader.readNext();
    }
    
    @Override
    public String[] readLine() throws IOException{
            String[] line = this.CSVreader.readNext();
            return line;
    }

    @Override
    public String[] readHeader(){
        return this.header;
    }

    @Override
    public boolean hasNextLine() throws IOException{
            String[] line = this.CSVreader.peek();
            return line != null;
    }

    @Override
    public void close() throws Exception{
        this.reader.close();
        this.CSVreader.close();
    }
}
