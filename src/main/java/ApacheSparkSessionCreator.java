import org.apache.spark.sql.*;

public class ApacheSparkSessionCreator {
    private static SparkSession ApacheSparkSession;
    private static boolean started = false;

    public static SparkSession getSession(){
        if ( ApacheSparkSessionCreator.started==false  ){
            ApacheSparkSession = SparkSession
                    .builder()
                    .appName("Apache Spark Portfolio Calculation")
                    .master("local")
                    .getOrCreate();

            ApacheSparkSessionCreator.started = true;

        }
        return ApacheSparkSession;
    }


    public static void main (String [] args){
        ApacheSparkSessionCreator.getSession();
    }

}
