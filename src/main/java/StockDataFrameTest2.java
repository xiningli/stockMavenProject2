import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Arrays;

public class StockDataFrameTest2 {
    public static void main (String [] args){
        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM"));
        StockDataFrame stockDataFrame = new StockDataFrame(companyNames);
        Dataset<Row> dailyReturn = stockDataFrame.getDailyReturn();
        dailyReturn.show();
        stockDataFrame.showWideDataFrame();
        stockDataFrame.getDailyLogReturn().show();
    }
}
