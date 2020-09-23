import java.io.IOException;
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

    public static String extendFileName(String oldName, String extension){
        int indexFileExtension = oldName.lastIndexOf('.');
        String fileExtension = oldName.substring(indexFileExtension);
        String nameWithoutExtension = oldName.substring(0,indexFileExtension);
        return nameWithoutExtension + extension + fileExtension;
    }

    public static int indexOfString(String[] data, String word){
        for (int i = 0; i < data.length; i++){
            if (data[i].equalsIgnoreCase(word)){
                return i;
            }
        }
        return -1;
    }

    /**
     * find lines to merge
     * @param currentLines list of preloaded lines
     * @param indexOfId indexes of column to compare by for each file
     * @return indexs of lines to merge (based on ids)
     */
    public static List<Integer> findLinesToMerge(ArrayList<List<String>> currentLines,
                                                int[] indexOfId){
        List<Integer> nextLines = new ArrayList<>();
        //looping through lines from all files looking for min id
        int i = 0;
        int currentMin = Integer.MAX_VALUE;
        int temp;
        for (List<String> line: currentLines){
            if(line != null){
                temp = Integer.valueOf(line.get(indexOfId[i]));
                if (temp < currentMin){
                    currentMin = temp;
                }
            }
            i++;
        }
        //looping through one more time to find matching id
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
