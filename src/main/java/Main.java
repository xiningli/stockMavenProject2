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
    private static boolean isCorrectSetting = false;
    private static StockCorrelation corrAnalysis;

    public static final String CLASSNAME="stockProject";

    public static final Logger log = Logger.getLogger(CLASSNAME);


    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4569; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main(String[] args) {

        port(getHerokuAssignedPort());
        Map<String, String> map = new HashMap<>();

        map.put("apikey", "");
        map.put("stockkeys", "");

        get("/", (req, res) -> {
            map.put("apikey", req.queryParams("apikey"));
            map.put("stockkeys", req.queryParams("stockkeys"));
            return new ModelAndView(map, "stockcorr.mustache");
        }, new MustacheTemplateEngine());


        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();


        spark.Spark.get("/result", (req, res) -> {
            try {
                ApiKeyAlphaVantage.setApiKey(req.queryParams("apikey"));
                map.put("apikey", ApiKeyAlphaVantage.getApiKey());
                StockCorrelation corrAnalysis = new StockCorrelation(req.queryParams("stockkeys"));
                map.put("DataString", corrAnalysis.getDataString());
                map.put("HTMLTable",corrAnalysis.getHTMLTable().trim());
                return new ModelAndView(map, "result.mustache");
//                return "<p>The current relative path is: " + s + " with stockkeys:" + req.queryParams("stockkeys") + " and API key: " + req.queryParams("apikey") + "</p>" +
 //                       corrAnalysis.getHTMLplot()
  //                      ;
            } catch (Exception e) {
                e.printStackTrace();
                return new ModelAndView(map, "resultFailed.mustache");
 //               return "<p>The current relative path is: " + s + " with stockkeys:" + req.queryParams("stockkeys") + "and API key: " + req.queryParams("apikey") + "</p>" +
  //                      "<p>Illegal Entry! Please use the following format</p>" +
   //                     "<p>MMM AAPL GS GM IBM MSFT GOOG</p>"
   //                     ;
            }
        }, new MustacheTemplateEngine());

    }




}