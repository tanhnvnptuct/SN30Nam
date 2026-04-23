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
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class ResponseUtil {
	// http get
	public String getJson(String url) throws Exception {
		URL sendUrl = new URL(url);
		URLConnection urlCon = sendUrl.openConnection();
		urlCon.setDoOutput(true);
		urlCon.setDoInput(true);
		HttpURLConnection httpConnection = (HttpURLConnection) urlCon;
		httpConnection.setRequestMethod("GET");
		httpConnection.setConnectTimeout(2000);
		httpConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		httpConnection.setRequestProperty("Content-Length", "0");

		InputStream is = httpConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String str;

		StringBuilder sb = new StringBuilder();
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		is.close();

		return new String(sb.toString().getBytes("utf-8"), "utf-8").trim();
	}

	public String HttpGetResp(String url, int TIME_OUT) {
		GetMethod method = new GetMethod(url);
		try {
			HttpClient client = new HttpClient();
			client.getParams().setSoTimeout(TIME_OUT);
			int ret = client.executeMethod(method);
			if (ret == HttpStatus.SC_OK) {
				String res = new String(method.getResponseBody(), "UTF-8").trim();
				return res;
			} else {
				return "";
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			return "";
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return "";
		} finally {
			try {
				method.releaseConnection();
			} catch (Exception ex) {
			}
		}
	}

	// http get
	public String getResponse(String url) throws Exception {
		URL sendUrl = new URL(url);
		URLConnection urlCon = sendUrl.openConnection();
		urlCon.setDoOutput(true);
		urlCon.setDoInput(true);
		HttpURLConnection httpConnection = (HttpURLConnection) urlCon;
		httpConnection.setRequestMethod("GET");
		httpConnection.setConnectTimeout(2000);
		httpConnection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
		httpConnection.setRequestProperty("Content-Length", "0");

		InputStream is = httpConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String str;

		StringBuilder sb = new StringBuilder();
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		is.close();

		return new String(sb.toString().getBytes("utf-8"), "utf-8").trim();
	}

	public String getResponse(String request, String url) throws Exception {
		URL sendUrl = new URL(url);
		URLConnection urlCon = sendUrl.openConnection();
		urlCon.setDoOutput(true);
		urlCon.setDoInput(true);
		HttpURLConnection httpConnection = (HttpURLConnection) urlCon;
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
		httpConnection.setRequestProperty("Content-Length", Integer.toString(request.length()));

		// file.log(DirLogs + "request/","request", url);
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
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}

		is.close();
		// file.log(DirLogs + "response/","response", url + ": " +
		// sb.toString());
		return sb.toString();
	}
	
	public String getHttpsJson(String https_url){

		// https_url =
		// "https://10.149.248.64/CommonLotteryV2/api/GenCodesOff/1636425293588/330/8484979148300/0/0/0";
		URL url;
		try {

			url = new URL(https_url);

			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string,
							Socket socket) throws CertificateException {

					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string,
							Socket socket) throws CertificateException {

					}

					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string,
							SSLEngine ssle) throws CertificateException {

					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string,
							SSLEngine ssle) throws CertificateException {

					}

				} };

				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

				// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				// Install the all-trusting host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			} catch (Exception e) {

			}

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(2000);
			con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			con.setRequestProperty("Content-Length", "0");

			InputStream is = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String str;

			StringBuilder sb = new StringBuilder();
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			is.close();

			return new String(sb.toString().getBytes("utf-8"), "utf-8").trim();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	   }

	public String getHttpPost(String xmlStr, String url, int timeout) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout); // ms
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout); // ms
		PostMethod postMethod = new PostMethod(url);
		try {
			ByteArrayRequestEntity entity = new ByteArrayRequestEntity(xmlStr.getBytes("UTF-8"));
			postMethod.setRequestEntity(entity);
			postMethod.setRequestHeader("Content-type", "text/xml; charset=utf-8");
			HttpClientParams params = new HttpClientParams();
			params.setVersion(HttpVersion.HTTP_1_1);
			httpClient.setParams(params);
			int result = httpClient.executeMethod(postMethod);
			if (result == HttpStatus.SC_OK) {
				return postMethod.getResponseBodyAsString().trim();
			} else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			postMethod.releaseConnection();
		}
	}
}
