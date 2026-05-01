package vnp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;

public class ResponseUtil {
  public String getJson(String url) throws Exception {
    URL sendUrl = new URL(url);
    URLConnection urlCon = sendUrl.openConnection();
    urlCon.setDoOutput(true);
    urlCon.setDoInput(true);
    HttpURLConnection httpConnection = (HttpURLConnection)urlCon;
    httpConnection.setRequestMethod("GET");
    httpConnection.setConnectTimeout(2000);
    httpConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    httpConnection.setRequestProperty("Content-Length", "0");
    InputStream is = httpConnection.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "utf-8");
    BufferedReader br = new BufferedReader(isr);
    StringBuilder sb = new StringBuilder();
    String str;
    while ((str = br.readLine()) != null)
      sb.append(str); 
    is.close();
    return (new String(sb.toString().getBytes("utf-8"), "utf-8")).trim();
  }
  
  public String HttpGetResp(String url, int TIME_OUT) {
    GetMethod method = new GetMethod(url);
    try {
      HttpClient client = new HttpClient();
      client.getParams().setSoTimeout(TIME_OUT);
      int ret = client.executeMethod((HttpMethod)method);
      if (ret == 200) {
        String res = (new String(method.getResponseBody(), "UTF-8")).trim();
        return res;
      } 
      return "";
    } catch (IOException e) {
      System.out.println(e.toString());
      return "";
    } catch (Exception ex) {
      System.out.println(ex.toString());
      return "";
    } finally {
      try {
        method.releaseConnection();
      } catch (Exception exception) {}
    } 
  }
  
  public String getResponse(String url) throws Exception {
    URL sendUrl = new URL(url);
    URLConnection urlCon = sendUrl.openConnection();
    urlCon.setDoOutput(true);
    urlCon.setDoInput(true);
    HttpURLConnection httpConnection = (HttpURLConnection)urlCon;
    httpConnection.setRequestMethod("GET");
    httpConnection.setConnectTimeout(2000);
    httpConnection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
    httpConnection.setRequestProperty("Content-Length", "0");
    InputStream is = httpConnection.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "utf-8");
    BufferedReader br = new BufferedReader(isr);
    StringBuilder sb = new StringBuilder();
    String str;
    while ((str = br.readLine()) != null)
      sb.append(str); 
    is.close();
    return (new String(sb.toString().getBytes("utf-8"), "utf-8")).trim();
  }
  
  public String getResponse(String request, String url) throws Exception {
    URL sendUrl = new URL(url);
    URLConnection urlCon = sendUrl.openConnection();
    urlCon.setDoOutput(true);
    urlCon.setDoInput(true);
    HttpURLConnection httpConnection = (HttpURLConnection)urlCon;
    httpConnection.setRequestMethod("POST");
    httpConnection.setRequestProperty("Content-Type", "application/json");
    httpConnection.setRequestProperty("Content-Length", Integer.toString(request.length()));
    PrintStream ps = null;
    ps = new PrintStream(httpConnection.getOutputStream(), true, "utf-8");
    ps.write(request.getBytes("utf-8"));
    ps.flush();
    String str = httpConnection.getResponseMessage();
    InputStream is = httpConnection.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "utf-8");
    BufferedReader br = new BufferedReader(isr);
    str = "";
    StringBuffer sb = new StringBuffer();
    while ((str = br.readLine()) != null)
      sb.append(str); 
    is.close();
    return sb.toString();
  }
  
  public String getHttpsJson(String https_url) {
    try {
      URL url = new URL(https_url);
      try {
        TrustManager[] trustAllCerts = { new X509ExtendedTrustManager() {
              public X509Certificate[] getAcceptedIssuers() {
                return null;
              }
              
              public void checkClientTrusted(X509Certificate[] certs, String authType) {}
              
              public void checkServerTrusted(X509Certificate[] certs, String authType) {}
              
              public void checkClientTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {}
              
              public void checkServerTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {}
              
              public void checkClientTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {}
              
              public void checkServerTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {}
            } };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
      } catch (Exception exception) {}
      HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
      con.setRequestMethod("GET");
      con.setConnectTimeout(2000);
      con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      con.setRequestProperty("Content-Length", "0");
      InputStream is = con.getInputStream();
      InputStreamReader isr = new InputStreamReader(is, "utf-8");
      BufferedReader br = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String str;
      while ((str = br.readLine()) != null)
        sb.append(str); 
      is.close();
      return (new String(sb.toString().getBytes("utf-8"), "utf-8")).trim();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return "";
  }
  
  public String getHttpPost(String xmlStr, String url, int timeout) throws Exception {
    HttpClient httpClient = new HttpClient();
    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
    httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
    PostMethod postMethod = new PostMethod(url);
    try {
      ByteArrayRequestEntity entity = new ByteArrayRequestEntity(xmlStr.getBytes("UTF-8"));
      postMethod.setRequestEntity((RequestEntity)entity);
      postMethod.setRequestHeader("Content-type", "text/xml; charset=utf-8");
      HttpClientParams params = new HttpClientParams();
      params.setVersion(HttpVersion.HTTP_1_1);
      httpClient.setParams(params);
      int result = httpClient.executeMethod((HttpMethod)postMethod);
      if (result == 200)
        return postMethod.getResponseBodyAsString().trim(); 
      return "";
    } catch (Exception e) {
      throw e;
    } finally {
      postMethod.releaseConnection();
    } 
  }
}
