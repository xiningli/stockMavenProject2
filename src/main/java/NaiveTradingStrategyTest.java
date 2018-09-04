import java.util.ArrayList;
import java.util.Arrays;

public class NaiveTradingStrategyTest {
    public static void main(String [] args){


        ArrayList<String> companyNames = new ArrayList<>(Arrays.asList("MMM", "AAPL", "GS", "GM"));
        NaiveTradingStrategy myStrategy = new NaiveTradingStrategy(companyNames);
        myStrategy.getStockCorrelationMatrix().show();
    }
}
