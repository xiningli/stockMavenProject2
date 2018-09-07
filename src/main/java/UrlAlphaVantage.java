public class UrlAlphaVantage {

    public static String getStockCSV(String compname, String apiKey){
        //R4MH59PCDVHF6SDB


        return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+compname+"&outputsize=full&apikey="+apiKey+"&datatype=csv";

    }
}
