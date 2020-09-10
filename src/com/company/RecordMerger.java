package com.company;


import java.util.*;

public class RecordMerger {

    public static final String FILENAME_COMBINED = "combined.csv";
    private static List<String> fileArray = new ArrayList<>();
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

        sortCSVFile(args[0]);
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
                return o1.get(0).compareTo(o2.get(0));
            }
        });

        WriterCSV writer = new WriterCSV("hallo2.csv");
        for (ArrayList<String> list:dataToSort){
            String[] line = new String[list.size()];
            for (int i = 0; i < list.size(); i++){
                line[i] = list.get(i);

            }
            writer.writeLine(line);
        }

        writer.closeWriter();

    }
}
