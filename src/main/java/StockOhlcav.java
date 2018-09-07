import java.io.*;
import java.net.*;
public class StockOhlcav
{

    public void get(String CompanyName)
    {
        String tmp = CompanyName.toUpperCase();
        CompanyName=tmp;
        File f = new File(CompanyName+".csv");



            String url = UrlAlphaVantage.getStockCSV(CompanyName, ApiKeyAlphaVantage.getApiKey());
            URL u;
            InputStream is = null;
            DataInputStream dis;
            String s;


            try
            {
                PrintWriter writer = new PrintWriter(CompanyName+".csv", "UTF-8");

                u = new URL(url);
                is = u.openStream();
                dis = new DataInputStream(new BufferedInputStream(is));
                while ((s = dis.readLine()) != null)
                {
                    //System.out.println(s);
                    writer.println(s);
                }
                writer.close();
            }
            catch (MalformedURLException mue)
            {
                System.err.println("Ouch - a MalformedURLException happened.");
                mue.printStackTrace();
                System.exit(2);
            }
            catch (IOException ioe)
            {
                System.err.println("Oops- an IOException happened.");
                ioe.printStackTrace();
                System.exit(3);
            }

            finally
            {
                try
                {
                    is.close();
                }
                catch (IOException ioe)
                {
                }
            }

            try{
                File fileToBeDeleted = new File(CompanyName+".csv");

                fileToBeDeleted.setWritable(true);

                FileInputStream fis = new FileInputStream(fileToBeDeleted);
                String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
                fis.close();
                String theFalseString = "4ebcb905d6bb74ea25607665c032f6a3";
                if( md5.equals(theFalseString)){
                    fileToBeDeleted.delete();
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }










    }

}