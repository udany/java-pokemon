package engine.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVBuilder {
    public static <T> List<T> build(String file, Function<Data, T> fn){
        ArrayList<T> r = new ArrayList<>();

        URL url = CSVBuilder.class.getResource(file);
        List<List<String>> dataStr = CSVReader.read(url);

        /// NESTED LAMBDAS YAY! /n
        List<List<Integer>> data = dataStr.stream().map(
                x -> x.stream().map(
                        y -> {
                            try {
                                return (Integer) Integer.parseInt(y.trim());
                            }catch (NumberFormatException e){
                            }

                            return (Integer) null;
                        }
                ).collect(Collectors.toList())
        ).collect(Collectors.toList());

        for (int i = 0; i < data.size(); i++) {
            List<Integer> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                Integer index = row.get(j);
                if (index != null) {
                    r.add(fn.apply(new Data(j, i, index)));
                }
            }
        }

        return r;
    }

    public static class Data {
        public Data(int x, int y, int v) {
            this.x = x;
            this.y = y;
            this.v = v;
        }

        public int x;
        public int y;
        public int v;
    }
}
