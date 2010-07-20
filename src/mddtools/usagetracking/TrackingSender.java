package mddtools.usagetracking;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

public class TrackingSender {

	public static void sendTrackingInfo(Properties info){
		String url = getTrackingServiceURL();
		final HttpClient client = new HttpClient();
		final PostMethod method = new PostMethod(url);
		Enumeration keys= info.keys();
		while(keys.hasMoreElements()){
			String currKey = (String)keys.nextElement();
			String currVal = (String)info.getProperty(currKey);
			method.addParameter(currKey, currVal);
		}
		method.getParams().setSoTimeout(150000);
		Thread t = new Thread("TrackingSenderThread") {
		  int code = -1;
		  public void run() {
			  while (code==-1) {
				try {
		        	code = client.executeMethod(method);	
		        	System.out.println("Tracking response code: "+code);
				} catch (HttpException e) {
					System.out.println("Errore invio info tracking: "+e);
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Errore invio info tracking: "+e);
					e.printStackTrace();
				}						
	         }			  
	      }			      
		};
		t.start();		
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
