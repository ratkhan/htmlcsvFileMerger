package com.company;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class WriterCSV {
    private CSVWriter writer;

    public WriterCSV(String fileName){
        try {
            FileWriter outputFile = new FileWriter(fileName);
            this.writer = new CSVWriter(outputFile);
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
