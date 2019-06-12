package app.connectors;

import org.springframework.scheduling.annotation.Async;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;


public class HttpsConnectorImpl implements HttpsConnector{

    @Override
    public String doGet(String url) {
        String rtn = "No data";

        try {
            URL urlIn = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) urlIn.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("INFO: Response Code from "+ url + " is " + responseCode);
            if(responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                try {
                    logCookies(con);
                }catch(NullPointerException ex){
                    System.out.println("WARN: No Cookies in response");
                }
            }

            StringBuffer content = new StringBuffer();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            rtn = content.toString();


        }catch(IOException ex){
            System.out.println("################### IOException Caught: "+ex);
        }
        return rtn;
    }

    private void logCookies(HttpsURLConnection con){
        String cookiesHeader = con.getHeaderField("Set-Cookie");
        List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);

        CookieManager cookieManager = new CookieManager();
        cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
        Optional<HttpCookie> sessionCookie = cookies.stream()
                .findAny().filter(cookie -> cookie.getName().equals("xAuth_SESSION_ID"));


        System.out.println("Cookies... ");
        for (HttpCookie cookie : cookies) {
            System.out.println("path: " + cookie.getPath());
            System.out.println("name: " + cookie.getName());
            System.out.println("value: " + cookie.getValue());
            System.out.println("domain: " + cookie.getDomain());
            System.out.println("maxAge: " + cookie.getMaxAge());
            System.out.println();

            if(cookie.getName().equals("xAuth_SESSION_ID")) {
                System.out.println("The session cookie: " + cookie.getValue());
            }
        }

    }
}
