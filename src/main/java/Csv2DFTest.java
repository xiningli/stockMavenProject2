import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;



public class Csv2DFTest {





    public static void main (String [] args){


        Csv2DF convertor = new Csv2DF();


        Dataset<Row> ds = convertor.toDf("MMM.csv");

        Row[] dataRows = (Row [])  ds.collect();

        System.out.println("------------------------------------");


        System.out.println( dataRows[1].get(1).getClass());






        System.out.println("------------------------------------");






        ds.select("timestamp", "adjusted_close",  "companyName").show();



    }
}
