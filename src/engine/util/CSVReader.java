package engine.util;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    private static String cvsSplitBy = ",";

    public static List<List<String>> read(String csvFile) {
        try {
            return read(new FileReader(csvFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<List<String>> read(URL url) {
        try {
            return read(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<List<String>> read(Reader reader) {
        String line = "";

        List<List<String>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                List<String> r = Arrays.asList(line.split(cvsSplitBy));
                result.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}