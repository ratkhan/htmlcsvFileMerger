package com.company;

import com.opencsv.CSVWriter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.FileWriter;
import java.io.IOException;

public class WriterCSV {
    private String file;
    private FileWriter outputFile;
    private CSVWriter writer;

    public WriterCSV(String file){
        try {
            this.file = file;
            this.outputFile = new FileWriter(this.file);
            this.writer = new CSVWriter(this.outputFile);
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
