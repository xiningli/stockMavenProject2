
import static spark.Spark.port;
import java.nio.file.Path;
import  java.nio.file.Paths;
/**
 * Hello world!
 *
 */

public class MainPlain {
    public static void main(String[] args) {

        port(getHerokuAssignedPort());

        System.out.println("");
        System.out.println("(Don't worry about the warnings below about SLF4J... we'll deal with those later)");
        System.out.println("");
        System.out.println("In browser, visit: http://localhost:" + getHerokuAssignedPort() );
        System.out.println("");
        String html =
                "<p>This web app is powered by \n" +
                        "<a href='hhttps://github.com/xiningli/stockMavenProject'>this github repo</a></p>\n"+
                        "<p>Please input the keys of companies separated by space \n" +
                        "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "\n" +
                        "<form action=\"/result\">\n" +
                        "API key:<br>\n" +
                        "<input type=\"text\" name=\"apikey\" value=\"\">\n" +
                        "<br>\n" +"<br>\n" +
                        "Company keys:<br>\n" +
                        "<input type=\"text\" name=\"companykeys\" value=\"\">\n" +
                        "<br>\n" +"<br>\n" +
                        "<input type=\"submit\" value=\"Run\">\n"+
                        "</form> \n" +
                        "\n" +
                        "<p>If you click run , you will get the stock correlation heatmaps of companies selected </p>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>\n";
        spark.Spark.get("/", (req, res) -> html);
//        spark.Spark.get("/result", (req, res) -> "<p><b>Hello, result!</b>  You just clicked the first link on my web app.</p>");



        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();



        spark.Spark.get("/result", (req,res)->{

            try{
                ApiKeyAlphaVantage.setApiKey(req.queryParams("apikey"));
                StockCorrelation corrAnalysis = new StockCorrelation(req.queryParams("companykeys"));
                return "<p>Current relative path is: "+s+"</p>"+
                        corrAnalysis.getHTMLplot()
                        ;
            }catch (Exception e){
                e.printStackTrace();
                return "<p>Current relative path is: "+s+"</p>"+
                        "<p>Illegal Entry! Please use the following format</p>"+
                        "<p>MMM AAPL GS GM IBM MSFT GOOG</p>"
                        ;
            }


        });


    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4568; //return default port if heroku-port isn't set (i.e. on localhost)



    }


}