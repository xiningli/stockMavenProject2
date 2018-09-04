import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Arrays;


public class StockDataFrameMergeTest {
    public static void main (String [] args){


        StockDataFrameMerge Merger = new StockDataFrameMerge();


        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM"));



        Dataset<Row> ds = Merger.merge(companyNames);


        //Spark definitive guide chapter22

        System.out.println("------------------------------------");
        ds.printSchema();
        ds.show();

        

    }
}




