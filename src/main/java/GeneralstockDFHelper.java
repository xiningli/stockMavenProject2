import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

class GeneralstockDFHelper{
    int numOfrows;
    int numOfcols;

    Dataset<Row> GeneralstockDF;
    List<Row> GDFrowcollections;
    GeneralstockDFHelper (Dataset<Row> GeneralstockDF){
        this.GeneralstockDF = GeneralstockDF;

        this.GDFrowcollections = this.GeneralstockDF.collectAsList();
    }


    double getIndividualCell(int rowIndex, int colIntex){

        return Double.parseDouble(GDFrowcollections.get(rowIndex).get(colIntex+1).toString());
    }


    public static double [] [] corrMatrix(StockDataFrame stockDataFrame){

        GeneralstockDFHelper thehelper = new GeneralstockDFHelper(stockDataFrame.getDailyLogReturn());
        int numOfrows = stockDataFrame.getNumberOfDays(true);
        int numOfcols = stockDataFrame.getCompanyNames().size();

        double [] [] resultMatrix = new double[numOfrows][numOfcols];


        //Just create the DailyLogReturn to matrix stuff


        for (int i =0; i<numOfrows; i++){
            for (int j=0; j<numOfcols; j++){
                resultMatrix[i][j] = thehelper.getIndividualCell(i,j);

            }
        }


        PearsonsCorrelation correlationAnalysis = new PearsonsCorrelation(resultMatrix);

        RealMatrix correlatinMatrix = correlationAnalysis.getCorrelationMatrix();
        return correlatinMatrix.getData();
    }

}