import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class RecordMerger {

    public static final String FILENAME_COMBINED = "combined.csv";
    public static final String COLUMN_TO_MERGE_BY = "ID";
    public static final String PLACE_HOLDER_EMPTY_FIELD = "  ";
    public static final String FILE_NAME_EXTENSION = "_sorted";
    private static final Logger LOGGER = Logger.getLogger(RecordMerger.class.getName());

    private static List<String> fileArray = new ArrayList<>();
    private static List<String> sortedFileArray = new ArrayList<>();

    /**
     * Entry point of this test.
     *
     * @param args command line arguments: first.html and second.csv.
     * @throws Exception bad things had happened.
     */

    public static void main(final String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Usage: java RecordMerger file1 [ file2 [...] ]");
            System.exit(1);
        }

        // your code starts here.
        LOGGER.info("Files will be sorted and merged by column " + COLUMN_TO_MERGE_BY +
                " case insensitive");

        for (String arg : args) {
            LOGGER.info("Checking " + arg);
            if (new File(arg).isFile()) {
                transformToCSV(arg, fileArray);
            } else {
                LOGGER.warning(arg + " is not a file");
            }

        }

        for (String fileName : fileArray) {
            LOGGER.info("Sorting " + fileName);
            sortCSVFile(fileName);
        }

        mergeCSVFiles(sortedFileArray);
        cleanUp(args);
    }


    /**
     * Transform the given file (if exists) into a csv file with the same name.
     *
     * @param fileName Name of the file to transform to CSV format: first.html
     * @throws IOException bad things had happened.
     */
    public static void transformToCSV(String fileName, List<String> fileArray) throws IOException {
        //return if file already .csv
        if (Files.getFileExtension(fileName).equalsIgnoreCase("csv")) {
            LOGGER.info(fileName + " is csv, skipping");
            fileArray.add(fileName);
            return;
        }

        //generate name for a new file
        String newFileName = Utils.changeFileExtension(fileName, "csv");

        LOGGER.info("Transforming file to a csv " + fileName);

        ParserFactory parserFactory = new ParserFactory();

        Parser parser = parserFactory.getParser(fileName);
        WriterCSV writer = new WriterCSV(newFileName);
        writer.writeLine(parser.readHeader());
        while (parser.hasNextLine()) {
            writer.writeLine(parser.readLine());
        }
        fileArray.add(newFileName);
        writer.closeWriter();


    }

    /**
     * Sorts passed file and saves it as [fileName]_sorted.csv
     *
     * @param fileName Name of the file to sort: first.csv
     * @throws Exception bad things had happened.
     */
    public static void sortCSVFile(String fileName) throws Exception {
        ParserFactory parserFactory = new ParserFactory();
        Parser parser = parserFactory.getParser(fileName);

        String[] header = parser.readHeader();

        final int indexToSortBy = Utils.indexOfString(header, COLUMN_TO_MERGE_BY);

        ArrayList<ArrayList<String>> dataToSort;
        dataToSort = readAll(parser);

        if (dataToSort == null) {
            return;
        }

        Collections.sort(dataToSort, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return Integer.valueOf(o1.get(indexToSortBy)) - Integer.valueOf(o2.get(indexToSortBy));
            }
        });

        String sortedFileName = Utils.extendFileName(fileName, FILE_NAME_EXTENSION);
        writeAll(header, dataToSort, sortedFileName);
        sortedFileArray.add(sortedFileName);
    }

    /**
     * Accepts list of sorted files and merges them line by line
     *
     * @param sortedFileArray list of sorted csv files
     * @throws IOException bad things had happened.
     */
    public static void mergeCSVFiles(List<String> sortedFileArray) throws IOException {
        LOGGER.info("Preparing for merging");
        List<Parser> parsers = new ArrayList<>();
        ParserFactory parserFactory = new ParserFactory();
        ArrayList<List<String>> currentLines = new ArrayList<List<String>>();
        ArrayList<List<String>> headers = new ArrayList<List<String>>();

        int[] indexOfId = new int[sortedFileArray.size()];

        LOGGER.info("Opening sorted files");
        for (String fileName : sortedFileArray) {
            Parser parser = parserFactory.getParser(fileName);
            parsers.add(parser);
        }

        LOGGER.info("Reading headers of opened files");
        for (int i = 0; i < parsers.size(); i++) {
            String[] header = parsers.get(i).readHeader();
            headers.add(Arrays.asList(header));
            indexOfId[i] = Utils.indexOfString(header, COLUMN_TO_MERGE_BY);
        }

        LOGGER.info("Indexing " + COLUMN_TO_MERGE_BY + " column in file headers");
        ArrayList<ArrayList<Integer>> headerIndex = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < headers.size(); i++) {
            headerIndex.add(new ArrayList<Integer>(10));
            for (int j = 0; j < headers.get(i).size(); j++) {
                headerIndex.get(i).add(-1);
            }
        }

        LOGGER.info("Maping headers into a new combined header");
        List<String> newHeader = mergeHeaders(headers, headerIndex);

        LOGGER.info("Opening " + FILENAME_COMBINED + " for writing");
        WriterCSV writer = new WriterCSV(FILENAME_COMBINED);
        writer.writeLine(newHeader.toArray(new String[newHeader.size()]));

        LOGGER.info("Reading sorted files and merging them line by line");
        for (Parser parser : parsers) {
            if (parser.hasNextLine()) {
                currentLines.add(Arrays.asList(parser.readLine()));
            }
        }

        while (true) {
            List<Integer> linesToMerge = Utils.findLinesToMerge(currentLines, indexOfId);

            if (linesToMerge.isEmpty()) {
                break;
            }

            List<String> newLineToWrite = mergeLines(currentLines, linesToMerge, headerIndex, newHeader.size());

            writer.writeLine(newLineToWrite.toArray(new String[newLineToWrite.size()]));

            //if file has next line, load it. if not replace with null;
            for (int i = 0; i < linesToMerge.size(); i++) {
                if (parsers.get(linesToMerge.get(i)).hasNextLine()) {
                    List<String> lineToReplace = Arrays.asList(parsers.get(linesToMerge.get(i)).readLine());
                    currentLines.set(linesToMerge.get(i), lineToReplace);
                } else {
                    currentLines.set(linesToMerge.get(i), null);
                }
            }
        }
        LOGGER.info("Finished merging");
        LOGGER.info("Cleaning up");
        writer.closeWriter();

    }

    /**
     * Accepts list of sorted files and merges them line by line
     * Mutates headerIndex argument
     * @param headers list of headers of sorted files
     * @param headerIndex 2d array mapping the headers of files into new common header
     * @return header for the combined file
     */
    public static List<String> mergeHeaders(ArrayList<List<String>> headers,
                                            ArrayList<ArrayList<Integer>> headerIndex) {
        ArrayList<String> newHeader = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < headers.size(); i++) {
            for (int j = 0; j < headers.get(i).size(); j++) {
                String column = headers.get(i).get(j);
                if (!newHeader.contains(column)) {
                    newHeader.add(column);
                    headerIndex.get(i).set(j, count++);
                }
                if (newHeader.contains(column)) {
                    headerIndex.get(i).set(j, newHeader.indexOf(column));
                }

            }
        }
        return newHeader;
    }

    /**
     * Accepts list of lines from opened csv files
     * merges lines with the same id accordingly to the header map
     * @param currentLines preread lines of csv files
     * @param linesToMerge index of lines to merge
     * @param headerIndex map of old headers to new common header
     * @return csv line ready to be written
     */
    public static List<String> mergeLines(ArrayList<List<String>> currentLines,
                                          List<Integer> linesToMerge,
                                          ArrayList<ArrayList<Integer>> headerIndex,
                                          int size) {
        List<String> lineToWrite = new ArrayList<>(Collections.nCopies(size, PLACE_HOLDER_EMPTY_FIELD));
        for (Integer lineNumber : linesToMerge) {
            for (int i = 0; i < headerIndex.get(lineNumber).size(); i++) {
                if (headerIndex.get(lineNumber).get(i) != -1) {
                    lineToWrite.set(headerIndex.get(lineNumber).get(i), currentLines.get(lineNumber).get(i));
                }
            }
        }

        return lineToWrite;
    }

    /**
     * read data from file (skips header)
     * @param parser initalized file parser
     * @return all of the data in the file (skips header)
     * @throws Exception bad things had happened.
     */
    public static ArrayList<ArrayList<String>> readAll(Parser parser) throws Exception {
        ArrayList<ArrayList<String>> dataToSort = new ArrayList<>();
        String[] header = parser.readHeader();
        int indexOfId = Utils.indexOfString(header, COLUMN_TO_MERGE_BY);
        if (indexOfId < 0) {
            return null;
        }
        while (parser.hasNextLine()) {
            String[] arrayLine = parser.readLine();
            if (arrayLine == null) {
                break;
            }
            ArrayList<String> listLine = new ArrayList<>();
            for (String str : arrayLine) {
                listLine.add(str);
            }

            if (listLine.size() > 1) {
                if (!listLine.get(indexOfId).isEmpty()) {
                    dataToSort.add(listLine);
                }
            }
        }
        parser.close();
        return dataToSort;
    }

    /**
     * write passed data (skips header)
     * @param header header for the file
     * @param dataToWrite data to be written
     * @param fileName name for a file to be written
     * @throws IOException bad things had happened.
     */
    public static void writeAll(String[] header, ArrayList<ArrayList<String>> dataToWrite, String fileName) throws IOException {
        WriterCSV writer = new WriterCSV(fileName);
        writer.writeLine(header);
        for (ArrayList<String> list : dataToWrite) {
            String[] line = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                line[i] = list.get(i);

            }
            writer.writeLine(line);
        }
        writer.closeWriter();
    }

    /**
     * deletes temp files
     * @param files passed file names
     */
    public static void cleanUp(String[] files) {
        //Deleting all the sorted data
        for (String fileName:sortedFileArray) {
            File fileToDelete = new File(fileName);
            if (fileToDelete.delete()) {
                LOGGER.info("Deleted the file: " + fileToDelete.getName());
            } else {
                LOGGER.warning("Failed to delete the file." + fileToDelete.getName());
            }
        }
        //Deleting other created csv files
        for (String fileName:fileArray){
            if (Utils.indexOfString(files, fileName) < 0){
                File fileToDelete = new File(fileName);
                if (fileToDelete.delete()) {
                    LOGGER.info("Deleted the file: " + fileToDelete.getName());
                } else {
                    LOGGER.warning("Failed to delete the file." + fileToDelete.getName());
                }
            }
        }
    }

}
