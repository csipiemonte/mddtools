package mddtools.usagetracking;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class TrackingSender {

	public static void sendTrackingInfo(Properties info){
		String url = getTrackingServiceURL();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		Enumeration keys= info.keys();
		while(keys.hasMoreElements()){
			String currKey = (String)keys.nextElement();
			String currVal = (String)info.getProperty(currKey);
			method.addParameter(currKey, currVal);
		}
		method.getParams().setSoTimeout(2000);
		try {
			int code = client.executeMethod(method);
			System.out.println("tracking response code:"+code);
		} catch (HttpException e) {
			System.out.println("Errore invio info tracking"+e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Errore invio info tracking:"+e);
			e.printStackTrace();
		}
		
	}
	
	private static String getTrackingServiceURL(){
		Properties trackerProps = new Properties();
		InputStream is = TrackingSender.class.getResourceAsStream("/mddtools/usagetracking/tracking.properties");
		if (is != null){
			try {
				trackerProps.load(is);
				return trackerProps.getProperty("url");
			} catch (IOException e) {
				System.out.println("Errore lettura info tracking service");
				return null;
			}
		}
		else{
			System.out.println("Errore reperimento info tracking service");
			return null;
		}
	}
}
