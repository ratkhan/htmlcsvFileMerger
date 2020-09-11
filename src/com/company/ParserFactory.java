package com.company;

import com.google.common.io.Files;

public class ParserFactory {

    public ParserFactory(){
    }

    public Parser getParser(String fileName){
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
