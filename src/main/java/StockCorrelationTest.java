import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;


public class StockCorrelationTest {




    public static void main (String [] args){
//        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM","IBM","MSFT","GOOG"));
        StockCorrelation corrAnalysis = new StockCorrelation("MMM AAPL GS GM IBM MSFT GOOG");


        double[][] corrMatrix= corrAnalysis.getCorrmatrix();
        for (int k=0;k<corrMatrix.length;k++){
            for (int h=0; h<corrMatrix[k].length; h++){
                System.out.print(corrMatrix[k][h] + "\t");
            }
            System.out.println("\n");

        }

     //   corrAnalysis.generateHeatMap();
        try{
            System.out.println(corrAnalysis.getHTMLplot());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
