import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class StockCorrelation {

    private double corrmatrix[][];
    private ArrayList<String> companyNames;
    public StockCorrelation(ArrayList<String> companyNames){
        this.companyNames = companyNames;
        StockDataFrame stockDataFrame = new StockDataFrame(companyNames);

        Dataset<Row> dailyLogReturn  = stockDataFrame.getDailyLogReturn();

        corrmatrix= GeneralstockDFHelper.corrMatrix(stockDataFrame);



    }

    public StockCorrelation(String companyNamesToBeSplit){
        String[] items = companyNamesToBeSplit.trim().toUpperCase().split(" ");
        ArrayList<String> tmp = new ArrayList<String>();
        for (int j=0;j<items.length;j++){
            tmp.add(items[j]);
        }

        companyNames = tmp;
        StockDataFrame stockDataFrame = new StockDataFrame(companyNames);

        Dataset<Row> dailyLogReturn  = stockDataFrame.getDailyLogReturn();

        corrmatrix= GeneralstockDFHelper.corrMatrix(stockDataFrame);

    }





    public double[][] getCorrmatrix(){
        return corrmatrix;
    }


    public void generateHeatMap(){
        String [] companys = new String[companyNames.size()];
        for (int j = 0; j<companyNames.size(); j++){
            companys[j] = companyNames.get(j);
        }

        HeatChart map = new HeatChart(this.corrmatrix);

        map.setXValues(companys);
        map.setYValues(companys);
        System.out.println("Trying to save the image");
        try {
            map.saveToFile(new File("java-heat-chart.png"));
            System.out.println("Heatmap output created");

        }catch (IOException e){

            System.out.println("Unable to create a heatmap output");
            e.printStackTrace();
        }

    }


    public String getHTMLplot() throws Exception{
        String [] companys = new String[companyNames.size()];
        for (int j = 0; j<companyNames.size(); j++){
            companys[j] = companyNames.get(j);
        }

        HeatChart map = new HeatChart(this.corrmatrix);

        map.setXValues(companys);
        map.setYValues(companys);
        BufferedImage theHeatMap = (BufferedImage)  map.getChartImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(theHeatMap, "png", baos);
        String data = DatatypeConverter.printBase64Binary(baos.toByteArray());
        String imageString = "data:image/png;base64," + data;
        String html = "<img src='" + imageString + "'>";
        return html;
    }

}
