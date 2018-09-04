import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;

import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.stat.Statistics;
import org.apache.spark.ml.stat.Correlation;


import java.util.ArrayList;

public class NaiveTradingStrategy extends StockDataFrame {


    public NaiveTradingStrategy(ArrayList<String> companyNames){
        super(companyNames);
    }

    private Dataset<Row> stockCorrelationMatrix;
    public Dataset<Row> getStockCorrelationMatrix(){
        Dataset<Row> result;
        return Correlation.corr(this.getDailyLogReturn(),"MMM");

    }


}
