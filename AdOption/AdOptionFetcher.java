package AdOption;

import android.content.Context;
import android.content.SharedPreferences;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class AdOptionFetcher {

    private static SharedPreferences sharedPreferences;

    public  class AdOptions {
        public String appid = "";
        public boolean splash_en = false;
        public boolean banner_en = false;
        public boolean popup_en = false;
        public String splash_id = "";
        public String banner_id = "";
        public String popup_id = "";
    }
    public  AdOptions options = new AdOptions();

    private  AdOptions tmp_ap = new AdOptions();


    private String url="";

    class  XMLHandler extends DefaultHandler
    {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {

            if("root".equals(localName)&&attributes.getLength()>0)
            {
                tmp_ap.appid = attributes.getValue(0);
            }

            if("splash".equals(localName)&&attributes.getLength()>1)
            {
                tmp_ap.splash_en = "true".equals(attributes.getValue(0));
                tmp_ap.splash_id = attributes.getValue(1);
            }
            if("banner".equals(localName)&&attributes.getLength()>1)
            {
                tmp_ap.banner_en = "true".equals(attributes.getValue(0));
                tmp_ap.banner_id = attributes.getValue(1);
            }

            if("popup".equals(localName)&&attributes.getLength()>1)
            {
                tmp_ap.popup_en = "true".equals(attributes.getValue(0));
                tmp_ap.popup_id = attributes.getValue(1);
            }
        }
    }

    class FetchingThread extends Thread
    {
        @Override
        public void run() {
            boolean isXmlOK = true;
            HttpsURLConnection connection;
            try {

                URL url = new URL(AdOptionFetcher.this.url);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(9000);
                connection.setReadTimeout(9000);
                InputStream inputStream = connection.getInputStream();
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser parser = spf.newSAXParser();
                XMLHandler xmlHandler =new XMLHandler() ;
                parser.parse(inputStream,xmlHandler);

            } catch (MalformedURLException e) {
                isXmlOK = false;
            } catch (ProtocolException e) {
                isXmlOK = false;
                e.printStackTrace();
            } catch (IOException e) {
                isXmlOK = false;
                e.printStackTrace();
            } catch (SAXException e) {
                isXmlOK = false;
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                isXmlOK = false;
                e.printStackTrace();
            }

            if(isXmlOK)
            saveTheseOptions();

        }
    }

    private void saveTheseOptions()
    {
        sharedPreferences.edit().putString("appid",tmp_ap.appid)
                .putBoolean("banner_en",tmp_ap.banner_en)
                .putBoolean("splash_en",tmp_ap.splash_en)
                .putBoolean("popup_en",tmp_ap.popup_en)
                .putString("banner_id",tmp_ap.banner_id)
                .putString("splash_id",tmp_ap.splash_id)
                .putString("popup_id",tmp_ap.popup_id).apply();
    }

    private void loadLastOptions()
    {
        options.appid = sharedPreferences.getString("appid","");
        options.banner_en = sharedPreferences.getBoolean("banner_en",false);
        options.splash_en = sharedPreferences.getBoolean("splash_en",false);
        options.popup_en = sharedPreferences.getBoolean("popup_en",false);

        options.banner_id = sharedPreferences.getString("banner_id","");
        options.splash_id = sharedPreferences.getString("splash_id","");
        options.popup_id = sharedPreferences.getString("popup_id","");
    }
    public AdOptionFetcher(Context context, String url)
    {
        if(sharedPreferences==null)
        sharedPreferences = context.getSharedPreferences("ad_options",Context.MODE_PRIVATE);
        if(sharedPreferences!=null)
        loadLastOptions();
        if("".equals(url)||url==null)
            return;
            this.url = url;
            FetchingThread ft = new FetchingThread();
            ft.start();
    }
    public AdOptionFetcher(Context context)
    {
        if(sharedPreferences==null)
            sharedPreferences = context.getSharedPreferences("ad_options",Context.MODE_PRIVATE);
        if(sharedPreferences!=null)
            loadLastOptions();
    }
}
