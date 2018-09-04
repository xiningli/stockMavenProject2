import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
public class StockDataFrame  {


    private Dataset<Row> wideDataFrame;
    private ArrayList<String> companyNames;
    private List <Row> rowcollections;
    private int numberOfDays;

    public ArrayList<String> getCompanyNames(){
        return this.companyNames;
    }

    public  StockDataFrame (ArrayList<String> companyNames){
        this.companyNames = companyNames;
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

        ArrayList<String> theCompanyNameWithoutFirst = (ArrayList<String>)companyNames.clone();

        theCompanyNameWithoutFirst.remove(0);
        Dataset<Row> innerJoinData;

        for (String companyName : theCompanyNameWithoutFirst){

            Dataset<Row> tmpStock = convertor
                    .toDf(companyName+".csv")
                    .select("timestamp", "adjusted_close")
                    .withColumnRenamed("timestamp", "tmptime")
                    .withColumnRenamed("adjusted_close", companyName);

            innerJoinData = bigOdf.join(tmpStock,  bigOdf.col("timestamp").equalTo(tmpStock.col("tmptime")) );
            bigOdf = innerJoinData.drop("tmptime");
        }
        this.wideDataFrame = bigOdf.orderBy("timestamp");


        this.rowcollections = wideDataFrame.collectAsList();
        this.numberOfDays = this.rowcollections.size();

    }



    public int getNumberOfDays(boolean isTransformed){
        int flag =0;
        if (isTransformed){
            flag=1;
        }
        return this.numberOfDays-flag;
    }


    public Dataset<Row> getWideDataFrame(){
        return this.wideDataFrame ;
    }

    public void showWideDataFrame(){
        this.wideDataFrame.show();
    }


    private static Row TakeDiffereceOfRowsandDivide(Row row2, Row row1){


        ArrayList elementsFromARow = new ArrayList();
//        System.out.println(  row2.get(1).getClass() );
        elementsFromARow.add(row2.get(0));
        double tmpDenom = 0;
        for (int j =1; j< row1.size(); j++){
            tmpDenom = Double.parseDouble(row1.get(j).toString());
            elementsFromARow.add( ( Double.parseDouble(row2.get(j).toString()) -tmpDenom )/tmpDenom   );
        }

        return RowFactory.create(elementsFromARow.toArray());

    }

    private boolean isDailyReturnObtained= false;
    private Dataset<Row> dailyReturn;




    public Dataset<Row> getDailyReturn(){

        if (this.isDailyReturnObtained==false){


            List<Row> rows = new ArrayList<>();


            for (int i=1; i< rowcollections.size(); i++){
                rows.add(TakeDiffereceOfRowsandDivide(rowcollections.get(i) ,rowcollections.get(i-1)));
            }


            StructField[] structFields = new StructField[companyNames.size()+1];

            structFields[0] = new StructField("timestamp", DataTypes.TimestampType
                    , true, Metadata.empty());

            for(int i=1;i<companyNames.size()+1;i++){
                structFields[i] = new StructField(companyNames.get(i-1),DataTypes.DoubleType,true,Metadata.empty());
            }


            StructType structType = new StructType(structFields);
            Dataset<Row> toShow = ApacheSparkSessionCreator.getSession().createDataFrame(rows, structType);
            this.dailyReturn = toShow;
            return this.dailyReturn;


        }else{
            return this.dailyReturn;
        }

    }

    private boolean isDailyLogReturnObtained= false;
    private Dataset<Row> dailyLogReturn;

    private static Row TakeDiffereceOfRowsLogs(Row row2, Row row1){


        ArrayList elementsFromARow = new ArrayList();
//        System.out.println(  row2.get(1).getClass() );
        elementsFromARow.add(row2.get(0));
        for (int j =1; j< row1.size(); j++){
            elementsFromARow.add( Math.log( Double.parseDouble(row2.get(j).toString()) ) -Math.log(Double.parseDouble(row1.get(j).toString())) );
        }

        return RowFactory.create(elementsFromARow.toArray());

    }
    public Dataset<Row> getDailyLogReturn(){

        if (this.isDailyLogReturnObtained==false){

            List<Row> rows = new ArrayList<>();


            for (int i=1; i< rowcollections.size(); i++){
                rows.add(TakeDiffereceOfRowsLogs(rowcollections.get(i) ,rowcollections.get(i-1)));
            }


            StructField[] structFields = new StructField[companyNames.size()+1];

            structFields[0] = new StructField("timestamp", DataTypes.TimestampType
                    , true, Metadata.empty());

            for(int i=1;i<companyNames.size()+1;i++){
                structFields[i] = new StructField(companyNames.get(i-1),DataTypes.DoubleType,true,Metadata.empty());
            }


            StructType structType = new StructType(structFields);
            Dataset<Row> toShow = ApacheSparkSessionCreator.getSession().createDataFrame(rows, structType);
            this.dailyReturn = toShow;
            return this.dailyReturn;


        }else{
            return this.dailyReturn;
        }

    }


}
