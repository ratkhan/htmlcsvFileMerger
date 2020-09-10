package com.company;

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
}
