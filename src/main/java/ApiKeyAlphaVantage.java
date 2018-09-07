public class ApiKeyAlphaVantage {
    private static String apiKey;
    public static void setApiKey (String newKey){
        apiKey =newKey;
    }
    public static String getApiKey(){
        return apiKey;
    }
}
