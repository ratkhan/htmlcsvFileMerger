package com.company;

import au.com.bytecode.opencsv.CSVReader;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.IOException;
import java.io.Reader;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ParserCSV implements Parser  {
    private String fileName;
    private Reader reader;
    private CSVReader CSVreader;
    private String[] header;
    public ParserCSV(String fileName){
        this.fileName = fileName;

    }
    
    @Override
    public String[] readLine(){
        try {
            String[] line = this.CSVreader.readNext();
            return line;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] readHeader(){
        return this.header;
    }

    @Override
    public boolean hasNextLine(){
        return true;
    }

    @Override
    public String parse() {
        try {
            Path path = Paths.get(fileName);
            this.reader = Files.newBufferedReader(path);
            this.CSVreader = new CSVReader(this.reader);
            this.header = this.CSVreader.readNext();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public void closeFile() throws Exception{
        this.reader.close();
        this.CSVreader.close();

    }
}
