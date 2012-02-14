/**
 * <copyright>
 * (C) Copyright 2011 CSI-PIEMONTE;

 * Concesso in licenza a norma dell'EUPL, esclusivamente versione 1.1;
 * Non e' possibile utilizzare l'opera salvo nel rispetto della Licenza.
 * E' possibile ottenere una copia della Licenza al seguente indirizzo:
 *
 * http://www.eupl.it/opensource/eupl-1-1
 *
 * Salvo diversamente indicato dalla legge applicabile o concordato per 
 * iscritto, il software distribuito secondo i termini della Licenza e' 
 * distribuito "TAL QUALE", SENZA GARANZIE O CONDIZIONI DI ALCUN TIPO,
 * esplicite o implicite.
 * Si veda la Licenza per la lingua specifica che disciplina le autorizzazioni
 * e le limitazioni secondo i termini della Licenza.
 * </copyright>
 *
 * $Id$
 */
package mddtools.usagetracking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class TrackingSender {

	public static class TrackingProxyInfo {
		public TrackingProxyInfo(String s_host, String s_port, String s_enable) {
			if (s_enable != null && "true".equalsIgnoreCase(s_enable))
				enabled = true;
			else
				enabled = false;
			host = s_host == null ? "" : s_host;
			try {
				if (s_port != null && s_port.length() > 0)
					port = Integer.parseInt(s_port);
				else
					port = 80;
			} catch (NumberFormatException nfe) {
				port = 80;
			}
		}

		private String host;

		public String getHost() {
			return host;
		}

		private int port;

		public int getPort() {
			return port;
		}

		private boolean enabled = false;

		public boolean isEnabled() {
			return enabled;
		}

	}

	public static class TrackingThread extends Thread {
		Properties info = new Properties();
		int code = -1;

		public TrackingThread(Properties info) {
			super("TrackingSenderThread");
			this.info = info;

		}

		public String runOnce() {
			if (isTrackingActive() && !isTrackingUserDisabled()) {
				try {
					String url = getTrackingServiceURL();

					Properties mddtoolsInfo = getMDDToolsInfoProps();
					String enableProxyProp = mddtoolsInfo
							.getProperty(ProfilingPacketBuilder.P_ENABLE_PROXY);

					// java.net.URI uri = new java.net.URI(url);
					// org.eclipse.core.net.proxy.IProxyService proxyService =
					// (IProxyService) proxyTracker.getService();

					final HttpClient client = new HttpClient();
					final PostMethod method = new PostMethod(url);
					Enumeration keys = info.keys();
					while (keys.hasMoreElements()) {
						String currKey = (String) keys.nextElement();
						String currVal = (String) info.getProperty(currKey);
						method.addParameter(currKey, currVal);
					}
					method.getParams().setSoTimeout(150000);
					TrackingProxyInfo proxyDataForHost = new TrackingProxyInfo(
							mddtoolsInfo
									.getProperty(ProfilingPacketBuilder.P_PROXY_HOST),
							mddtoolsInfo
									.getProperty(ProfilingPacketBuilder.P_PROXY_PORT),
							mddtoolsInfo
									.getProperty(ProfilingPacketBuilder.P_ENABLE_PROXY));
					if (proxyDataForHost.isEnabled()) {
						client.getHostConfiguration().setProxy(
								proxyDataForHost.getHost(),
								proxyDataForHost.getPort());
					}
					code = client.executeMethod(method);
					System.out.println("Tracking response code: " + code);
					return "HTTP:" + code;
				} catch (HttpException e) {
					System.out
							.println("Errore (non bloccante) invio info tracking: "
									+ e);
					return "Errore HTTP nella connessione al server:" + e + ","
							+ e.getMessage();
				} catch (UnknownHostException e) {
					System.out.println("Errore rete non bloccante: " + e);
					// e.printStackTrace();
					return "Il server specificato non esiste:" + e + ","
							+ e.getMessage();
				} catch (IOException e) {
					System.out
							.println("Errore (non bloccante) invio info tracking: "
									+ e);
					// e.printStackTrace();
					return "Errore nella connessione al server:" + e + ","
							+ e.getMessage();
				}
			}
			else
				return "N.A"; // esito OK in caso di non abilitazioen del tracking 
		}

		public void run() {
				runOnce();
		}
	}

	public static void sendTrackingInfo(Properties info) {

		Thread t = new TrackingThread(info);
		t.start();
	}

	public static String getTrackingServiceURL() {
		String defaultUrl = getDefaultTrackingServiceURL();
		Properties mddtoolsInfo = getMDDToolsInfoProps();
		String preferenceTrackingUrl = mddtoolsInfo
				.getProperty(ProfilingPacketBuilder.P_TRACKING_URL);
		if (preferenceTrackingUrl != null && preferenceTrackingUrl.length() > 0)
			return preferenceTrackingUrl;
		else
			return defaultUrl;
	}

	public static String getDefaultTrackingServiceURL() {
		Properties trackerProps = new Properties();
		InputStream is = TrackingSender.class
				.getResourceAsStream("/mddtools/usagetracking/tracking.properties");
		if (is != null) {
			try {
				trackerProps.load(is);
				return trackerProps.getProperty("url");
			} catch (IOException e) {
				System.out.println("Errore lettura info tracking service");
				return null;
			}
		} else {
			System.out.println("Errore reperimento info tracking service");
			return null;
		}
	}

	/**
	 * 
	 * @return true se l'utente ha disabilitato il tracking dalla pagina delle
	 *         preferenze.
	 */
	private static boolean isTrackingUserDisabled() {
		Properties props = getMDDToolsInfoProps();
		String disableTracking = (String) props
				.get(ProfilingPacketBuilder.P_DISABLE_TRACKING);
		if ("true".equalsIgnoreCase(disableTracking))
			return true;
		else
			return false;
	}

	private static Properties getMDDToolsInfoProps() {
		Properties p = new Properties();
		String homeDir = System.getProperty("user.home");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(homeDir + "/"
					+ ProfilingPacketBuilder.REGISTRATION_FILENAME);
			p.load(fis);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			throw new IllegalStateException(ProfilingPacketBuilder.ERROR_MSG);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new IllegalStateException(ProfilingPacketBuilder.ERROR_MSG);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return p;
	}

	public static boolean isTrackingActive() {
		Properties trackerProps = new Properties();
		InputStream is = TrackingSender.class
				.getResourceAsStream("/mddtools/usagetracking/tracking.properties");
		if (is != null) {
			try {
				trackerProps.load(is);
				String p = trackerProps.getProperty("tracking.active");
				if (p != null && "true".equals(p)) {

					return true;
				} else
					return false;
			} catch (IOException e) {
				System.out.println("Errore lettura info tracking service");
				return false;
			}
		} else {
			System.out.println("Errore reperimento info tracking service");
			return false;
		}
	}

	public static String pingTracking(Properties info) {
		TrackingThread tr = new TrackingThread(info);
		return tr.runOnce();
	}
}
