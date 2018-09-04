import org.apache.spark.sql.*;
import org.apache.commons.lang3.StringUtils;

import org.apache.spark.sql.functions;

public class Csv2DF {

    public Dataset<Row> toDf(String fileName) {

        String companyName = StringUtils.substringBefore(fileName, "."); // returns "abc"


        Dataset<Row> tmp;
        Dataset<Row> tmp2;
        Dataset<Row> result;

        tmp= ApacheSparkSessionCreator.getSession().read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", true)
                .load(fileName);

        tmp2 = tmp.withColumn("companyName", functions.lit(companyName)  );
        result = tmp2;
     //   result.printSchema();

        /*

        //The following can be used to select a row or a value from
        // a dataset.


        Row[] dataRows = (Row [])  result.collect();
        for (Row row : dataRows) {
            System.out.println("Row : "+row);
            for (int i = 0; i < row.length(); i++) {
                System.out.println("Row Data : "+row.get(i));
            }
        }

        */

        System.out.println();
        return result;

    }


}
