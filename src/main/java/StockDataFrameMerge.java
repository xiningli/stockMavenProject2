import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.util.*;

import org.apache.spark.sql.functions;


//import org.apache.spark.sql.catalyst.expressions.Expression;
public class StockDataFrameMerge {

    public Dataset<Row> merge(ArrayList<String> companyNames){

        /*
        This method will download stock data with specified name
        convert that to the spark dataset with the Csv2DF class
        then merge the files
         */

        StockOhlcav stockGetter = new StockOhlcav();//this is the downloader

        for (String companyName: companyNames){
            stockGetter.get(companyName);
        }

        Csv2DF convertor = new Csv2DF();

        Dataset<Row> bigOdf;

        bigOdf = convertor
                .toDf(companyNames.get(0)+".csv")
                .select("timestamp", "adjusted_close")
                .withColumnRenamed("adjusted_close", companyNames.get(0));

        companyNames.remove(0);



        Dataset<Row> innerJoinData;



        for (String companyName : companyNames){

            Dataset<Row> tmpStock = convertor
                    .toDf(companyName+".csv")
                    .select("timestamp", "adjusted_close")
                    .withColumnRenamed("timestamp", "tmptime")
                    .withColumnRenamed("adjusted_close", companyName);

//            tmpStock = tmpStock.withColumnRenamed("timestamp", "tmptime");
//            tmpStock.show();
            innerJoinData = bigOdf.join(tmpStock,  bigOdf.col("timestamp").equalTo(tmpStock.col("tmptime")) );
            bigOdf = innerJoinData.drop("tmptime");
        }



       // Dataset<Row>  reshapedDF;


        //reshapedDF = bigOdf.groupBy("timestamp").pivot("companyName").sum("adjusted_close");//agg(functions.expr("adjusted_close"))
        return bigOdf.orderBy("timestamp");

    }

}
