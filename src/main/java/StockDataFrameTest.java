import org.apache.spark.sql.*;

import org.apache.spark.sql.types.Metadata;

import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

import org.apache.spark.sql.*;
import org.apache.commons.lang3.StringUtils;

import org.apache.spark.sql.functions;
import scala.collection.Seq;
import java.util.LinkedList;
import org.apache.spark.sql.types.DataTypes;

import org.apache.spark.sql.types.StructType;

public class StockDataFrameTest {


    public static void test_getDataSetResult() {

        StructField[] structFields = new StructField[]{
                new StructField("intColumn", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("stringColumn", DataTypes.StringType, true, Metadata.empty())
        };


        StructType structType = new StructType(structFields);

        List<Row> rows = new ArrayList<>();
        rows.add(RowFactory.create(1, "v1"));
        rows.add(RowFactory.create(2, "v2"));

        SparkSession spark = SparkSession
                .builder()
                .appName("Spark SQL  examples")
                .master("local")
                .getOrCreate();

        Dataset<Row> df = spark.createDataFrame(rows, structType);


        System.out.println(structFields);

        for (int j=0 ; j < structFields.length; j++){
            System.out.println(structFields[j]);
        }
        for (int i =0; i<2; i++){
            System.out.println(rows.get(i));
        }

        df.show();
    }



    private static Row differeceOfRows(Row row2, Row row1){


        ArrayList elementsFromARow = new ArrayList();
//        System.out.println(  row2.get(1).getClass() );
        elementsFromARow.add(row2.get(0));
        for (int j =1; j< row1.size(); j++){
            elementsFromARow.add(Double.parseDouble(row2.get(j).toString()) - Double.parseDouble(row1.get(j).toString()));
        }

        return RowFactory.create(elementsFromARow.toArray());

    }


    public static void main (String[] args){
        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM"));
        StockDataFrame stockDataFrame = new StockDataFrame(companyNames);
        stockDataFrame.showWideDataFrame();


        Dataset<Row> wideDataFrame =  stockDataFrame.getWideDataFrame();

//        wideDataFrame.show();

        SparkSession spark = SparkSession
                .builder()
                .appName("Spark SQL  examples")
                .master("local")
                .getOrCreate();


        List<Row> rows = new ArrayList<>();
        List <Row> rowcollections = wideDataFrame.collectAsList();

//System.out.println(row.get(0))
//
        for (int i=1; i< rowcollections.size(); i++){
            rows.add(differeceOfRows(rowcollections.get(i) ,rowcollections.get(i-1)));
        }
        System.out.println("AAA-------------------------");

        StructField[] structFields = new StructField[companyNames.size()+1];

        System.out.println(companyNames);
        structFields[0] = new StructField("timestamp", DataTypes.TimestampType
                , true, Metadata.empty());


        System.out.println("BBB-------------------------");

        for(int i=1;i<companyNames.size()+1;i++){
            structFields[i] = new StructField(companyNames.get(i-1),DataTypes.DoubleType,true,Metadata.empty());
        }



        StructType structType = new StructType(structFields);




        System.out.println("CCC-------------------------");
        System.out.println(structFields);

        for (int j=0 ; j < structFields.length; j++){
            System.out.println(structFields[j]);
        }

        test_getDataSetResult();

        for (int i =0; i<8; i++){
            System.out.println(rows.get(i));
        }

        Dataset<Row> toShow = spark.createDataFrame(rows, structType);


        System.out.println("DDD-------------------------");


        toShow.show();


    }

}
