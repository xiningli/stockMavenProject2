import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.linear.RealMatrix;


import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.stat.Statistics;
import org.apache.spark.api.java.JavaSparkContext;


public class StockDataFrameTest3 {




    public static void main (String [] args){
        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM"));
        StockDataFrame stockDataFrame = new StockDataFrame(companyNames);

        Dataset<Row> dailyLogReturn  = stockDataFrame.getDailyLogReturn();

        double twoDm[][]= GeneralstockDFHelper.corrMatrix(stockDataFrame);


        for (int k=0;k<twoDm.length;k++){
            for (int h=0; h<twoDm[k].length; h++){
                System.out.println(twoDm[k][h]);
            }

        }

    }


}
