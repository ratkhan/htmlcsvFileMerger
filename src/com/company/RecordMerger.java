package com.company;


import org.jcp.xml.dsig.internal.dom.DOMSubTreeData;

import java.util.*;

public class RecordMerger {

    public static final String FILENAME_COMBINED = "combined.csv";
    private static List<String> fileArray = new ArrayList<>();
    private static List<String> sortedFileArray = new ArrayList<>();
    /**
     * Entry point of this test.
     *
     * @param args command line arguments: first.html and second.csv.
     * @throws Exception bad things had happened.
     */
    public static void main(final String[] args) throws Exception {

        /*if (args.length == 0) {
            System.err.println("Usage: java RecordMerger file1 [ file2 [...] ]");
            System.exit(1);
        }*/

        // your code starts here.
        for (String arg:args) {
            System.out.println("Working on " + arg);
            transformToCSV(arg, fileArray);
        }

        for (String fileName:fileArray){
            System.out.println("Sorting " + fileName);
            sortCSVFile(fileName);
        }

        mergeCSVFiles(sortedFileArray);

    }

    //Transform the given file (if exists) into a csv file with the same name.
    public static void transformToCSV(String fileName, List<String> fileArray){
        //return if file already .csv
        if (Utils.getFileExtension(fileName).equalsIgnoreCase("csv")){
            fileArray.add(fileName);
            return;
        }

        //generate name for a new file
        String newFileName = Utils.changeFileExtension(fileName, "csv");


        ParserFactory parserFactory = new ParserFactory();
        Parser parser = parserFactory.getParser(fileName);
        parser.parse();


        WriterCSV writer = new WriterCSV(newFileName);
        writer.writeLine(parser.readHeader());
        while(parser.hasNextLine()){
            writer.writeLine(parser.readLine());
        }
        fileArray.add(newFileName);
        writer.closeWriter();
    }

    public static void sortCSVFile(String fileName){
        ParserFactory parserFactory = new ParserFactory();
        Parser parser = parserFactory.getParser(fileName);
        parser.parse();

        String[] header = parser.readHeader();
        final int indexToSortBy = Utils.indexOfString(header, "id");

        ArrayList<ArrayList<String>> dataToSort = new ArrayList<>();
        while (parser.hasNextLine()){
            String[] arrayLine = parser.readLine();
            if (arrayLine == null){
                break;
            }
            ArrayList<String> listLine = new ArrayList<>();
            for (String str:arrayLine){
                listLine.add(str);
            }
            dataToSort.add(listLine);
        }

        Collections.sort(dataToSort, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.get(indexToSortBy).compareTo(o2.get(indexToSortBy));
            }
        });

        String newFileName = Utils.extendFileName(fileName, "_sorted");
        WriterCSV writer = new WriterCSV(newFileName);
        writer.writeLine(header);
        for (ArrayList<String> list:dataToSort){
            String[] line = new String[list.size()];
            for (int i = 0; i < list.size(); i++){
                line[i] = list.get(i);

            }
            writer.writeLine(line);
        }
        sortedFileArray.add(newFileName);

        writer.closeWriter();
    }

    public static void mergeCSVFiles(List<String> sortedFileArray){
        List<Parser> parsers = new ArrayList<>();
        ParserFactory parserFactory = new ParserFactory();
        ArrayList<List<String>> currentLines = new ArrayList<List<String>>();
        ArrayList<List<String>> headers = new ArrayList<List<String>>();

        int[] indexOfId = new int [sortedFileArray.size()];

        for (String fileName:sortedFileArray){
            Parser parser = parserFactory.getParser(fileName);
            parsers.add(parser);
        }

        for (int i = 0; i < parsers.size(); i++){
            parsers.get(i).parse();
            String[] header = parsers.get(i).readHeader();
            indexOfId[i] = Utils.indexOfString(header, "id");
        }

        for (Parser parser:parsers){
            headers.add(Arrays.asList(parser.readHeader()));
        }

        ArrayList<ArrayList<Integer>> headerIndex = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < headers.size(); i++){
            headerIndex.add(new ArrayList<Integer>(10));
            for (int j = 0; j < headers.get(i).size(); j++){
                headerIndex.get(i).add(-1);
            }
        }


        ArrayList<String> newHeader = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < headers.size(); i++){
            for (int j = 0; j < headers.get(i).size(); j++){
                String column = headers.get(i).get(j);
                if (!newHeader.contains(column)){
                    newHeader.add(column);
                    headerIndex.get(i).set(j,count++);
                }
                if (newHeader.contains(column)){
                    headerIndex.get(i).set(j,newHeader.indexOf(column));
                }

            }
        }

        WriterCSV writer = new WriterCSV("combined.csv");
        String[] header = new String[newHeader.size()];
        header = newHeader.toArray(header);
        writer.writeLine(header);

        for (Parser parser:parsers){
            currentLines.add(Arrays.asList(parser.readLine()));
        }


        while(true){
            List<Integer> linesToMerge = Utils.chooseNextLines(currentLines,indexOfId);
            if (linesToMerge.isEmpty()){
                System.out.println("Done");
                break;
            }
            List<String> newLineToWrite = assembleLine(currentLines,linesToMerge,headerIndex,newHeader.size());
            String[] newLine = new String[newLineToWrite.size()];
            newLine = newLineToWrite.toArray(newLine);
            writer.writeLine(newLine);
            for (int i = 0; i < linesToMerge.size(); i++){
                if (parsers.get(linesToMerge.get(i)).hasNextLine()) {
                    List<String> lineToReplace = Arrays.asList(parsers.get(linesToMerge.get(i)).readLine());
                    currentLines.set(linesToMerge.get(i), lineToReplace);
                } else {
                    currentLines.set(linesToMerge.get(i), null);
                }
            }
        }

        writer.closeWriter();



    }

    public static List<String> assembleLine(ArrayList<List<String>> currentLines,
                                            List<Integer> linesToMerge,
                                            ArrayList<ArrayList<Integer>> headerIndex,
                                            int size){
        List<String> lineToWrite = new ArrayList<>(Collections.nCopies(size, " - "));
        for (Integer lineNumber:linesToMerge){
            for (int i = 0; i < headerIndex.get(lineNumber).size(); i++){
                if (headerIndex.get(lineNumber).get(i) != -1) {
                    lineToWrite.set(headerIndex.get(lineNumber).get(i), currentLines.get(lineNumber).get(i));
                }
            }
        }

        return lineToWrite;
    }
}
