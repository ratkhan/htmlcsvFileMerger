package com.company;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String changeFileExtension(String fileName, String extension){
        int indexFileExtension =  fileName.lastIndexOf('.');
        if (indexFileExtension < 0){
            return fileName + "." + extension;
        }
        String newFileName = fileName.substring(0,indexFileExtension) + ".csv";
        return newFileName;
    }

    public static String getFileExtension(String fileName){
        int indexFileExtension =  fileName.lastIndexOf('.');
        if (indexFileExtension < 0){
            return "";
        }
        return fileName.substring(indexFileExtension + 1);
    }

    public static String extendFileName(String oldName, String extenstion){
        int indexFileExtension = oldName.lastIndexOf('.');
        String fileExtension = oldName.substring(indexFileExtension);
        String nameWithoutExtension = oldName.substring(0,indexFileExtension);
        return nameWithoutExtension + extenstion + fileExtension;
    }

    public static int indexOfString(String[] data, String word){
        for (int i = 0; i < data.length; i++){
            if (data[i].equalsIgnoreCase("id")){
                return i;
            }
        }
        return -1;
    }

    public static List<Integer> chooseNextLines(ArrayList<List<String>> currentLines,
                                                int[] indexOfId){
        List<Integer> nextLines = new ArrayList<>();
        int i = 0;
        int currentMin = 999999999;
        int temp = 0;
        for (List<String> line: currentLines){
            if(line != null){
                temp = Integer.valueOf(line.get(indexOfId[i]));
                if (temp < currentMin){
                    currentMin = temp;
                }
            }
            i++;
        }
        i = 0;
        for (List<String> line: currentLines){
            if(line != null){
                temp = Integer.valueOf(line.get(indexOfId[i]));
                if (temp == currentMin){
                    nextLines.add(i);
                }
            }
            i++;
        }
        return nextLines;
    }

}
