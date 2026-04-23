/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vnp.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranthanh
 */
public class HttpUtils {
    public static Logger logger = Logger.getLogger("HttpUtils");
    public static String checkStatus(List<String> lstMsisdn) {
//        HashMap<String, BigInteger> rtn = new HashMap<String, BigInteger>();
        String respStr = "";
        long startTime = System.currentTimeMillis();
        logger.info("====checkStatus: " + lstMsisdn.size());

        CloseableHttpClient client = null;

        try {
            client = HttpClients.custom().build();

            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            for(String msisdn : lstMsisdn) {
                sb.append(msisdn).append(";");
            }

            String params = sb.toString().substring(0, sb.length()-1);
            sb.setLength(0);

            URI uri = null;
            try {
                uri = new URIBuilder().setScheme("http")
                        .setHost("192.168.41.211")
                        .setPort(8900)
                        .setPath("/vplus/checkSubscriberStatus")
                        .setParameter("msisdns", URLEncoder.encode(params, "UTF-8")).build();
            } catch (URISyntaxException ex) {
                logger.error("URISyntaxException getPoint", ex);
            }

            HttpGet get = new HttpGet(uri);

//            get.setHeader("Content-Type", "application/json");
            get.setHeader("Content-Encoding", "gzip");

            CloseableHttpResponse resp = client.execute(get);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity respContent = resp.getEntity();
                respStr = EntityUtils.toString(respContent);
                logger.info("====resp: " + respStr);
            }
        } catch (IOException ex) {
            logger.error("IOException getPoint: ", ex);
        } finally {
            if (client != null) {
                try {
                    client.close();
                    client = null;
                } catch (IOException ex) {
                }
            }
        }
        logger.info("Time to getPoint: " + (System.currentTimeMillis() - startTime) + " ms");
        return respStr;
    }
}
