import static spark.Spark.port;

import org.apache.log4j.Logger;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Simple example of using Mustache Templates
 *
 */

public class Main {

    public static final String CLASSNAME="stockProject";

    public static final Logger log = Logger.getLogger(CLASSNAME);

    public static void main(String[] args) {

        port(getHerokuAssignedPort());


        String html =
                "<p>This web app is powered by \n" +
                        "<a href='hhttps://github.com/xiningli/stockMavenProject'>this github repo</a></p>\n"+
                        "<p>Please input the keys of companies separated by space \n" +
                        "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<body>\n" +
                        "\n" +
                        "<form action=\"/result\">\n" +
                        "Company keys:<br>\n" +
                        "<input type=\"text\" name=\"keys\" value=\"\">\n" +
                        "<br>\n" +"<br>\n" +
                        "<input type=\"submit\" value=\"Run\">\n"+
                        "</form> \n" +
                        "\n" +
                        "<p>If you click run , you will get the stock correlation heatmaps of companies selected </p>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>\n";


        Map map = new HashMap();



        map.put("key", "");


        get("/", (req, res) -> new ModelAndView(map, "stockcorr.mustache"), new MustacheTemplateEngine());

//        spark.Spark.get("/", (req, res) -> html);
//        spark.Spark.get("/result", (req, res) -> "<p><b>Hello, result!</b>  You just clicked the first link on my web app.</p>");



        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();



        spark.Spark.get("/result", (req,res)->{
            try{
                ApiKeyAlphaVantage.setApiKey(req.queryParams("apikey"));

                StockCorrelation corrAnalysis = new StockCorrelation(req.queryParams("stockkeys"));
                return "<p>The current relative path is: "+s+" with stockkeys:" + req.queryParams("stockkeys") +" and API key: " + req.queryParams("apikey") + "</p>"+
                        corrAnalysis.getHTMLplot()
                        ;
            }catch (Exception e){
                e.printStackTrace();
                return "<p>The current relative path is: "+s+" with stockkeys:" + req.queryParams("stockkeys")+"and API key: " + req.queryParams("apikey") + "</p>"+
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
        return 4569; //return default port if heroku-port isn't set (i.e. on localhost)
    }


}