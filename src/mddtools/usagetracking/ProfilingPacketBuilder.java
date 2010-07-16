package mddtools.usagetracking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import mddtools.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * utility che consente di creare le info di profilazione dell'utilizzo
 * da sottomettere al server di tracking
 * @author CSI Piemonte
 *
 */
public class ProfilingPacketBuilder {

	public static final String P_WHO_NAME = "whoName";
	public static final String P_WHO_SURNAME = "whoSurname";
	public static final String P_WHO_COMPANY = "whoCompany";
	public static final String P_WHO_EMAIL = "whoEmail";
	public static final String P_WHO_IPADDRESS = "whoIpaddress";
	public static final String P_WHO_OSNAME = "whoOsname";
	public static final String P_WHO_JAVAVERSION = "whoJavaVersion";
	public static final String P_ACTION = "action";
	public static final String P_MODULE_NAME = "moduleName";
	public static final String P_MODULE_VERSION = "moduleVersion";
	
	public static final String P_GENERATE_MODEL_NAME = "generateModelName";
	public static final String P_GENERATE_GENPROJ_NAME = "generateGeneratorProjectName";
	public static final String P_GENERATE_TRGPROJ_NAME = "generateTargetProjectName";
	
	public static final String V_ACTION_STARTUP = "startup";
	public static final String V_ACTION_GENERATE = "generate";
	
	
	public static final String REGISTRATION_FILENAME = ".mddtools.info";
	
	/**
	 * who.userid=<id dell'utente fornito alla registrazione>
	 * who.email=<email dell'utente fornito alla registrazione o configurato>
	 * who.ipaddress=<ipaddress dell'utente, rilevato a runtime>
	 * who.osname=<nome del sistema operative dell'utente>
	 *
	 * @return
	 */
	private static Properties packCommonInfo(){
		System.out.println("packCommonInfo...");
		Properties p = new Properties();
		String homeDir = System.getProperty("user.home");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(homeDir+"/"+REGISTRATION_FILENAME);
			p.load(fis);
			String whoIpAddress = "";
			java.net.InetAddress i;
			try {
				i = java.net.InetAddress.getLocalHost();
				whoIpAddress = ""+i.getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String whoOsname = System.getProperty("os.name");
			p.put(P_WHO_IPADDRESS, whoIpAddress);
			p.put(P_WHO_OSNAME, whoOsname);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
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
	
	/**
	 * proprieta' comuni piu':
	 * action=startup
	 * module.name=<codice del plugin coinvolto: {guigen|datagen|mddtools|servicegen|blgen|...} >
	 * module.version=<versione del plugin coinvolto>
	 *
	 * @return
	 */
	public static Properties packStartupInfo(String pluginName, String pluginVersion){
		Properties p = packCommonInfo();
		p.put(P_ACTION, V_ACTION_STARTUP);
		p.put(P_MODULE_NAME, pluginName);
		p.put(P_MODULE_VERSION, pluginVersion);
		return p;
	}
	
	/**
	 * proprieta' comuni piu':
	 * action=startup
	 * module.name=<codice del plugin coinvolto: {guigen|datagen|mddtools|servicegen|blgen|...} >
	 * module.version=<versione del plugin coinvolto>
	 * generate.model.name=<uri del modello principale manipolato>
	 * generate.generatorproject.name=<nome del progetto generatore>
	 * generate.targetproject.name=<nome del progetto generato> (*)


	 * @return
	 */
	public static Properties packGenerateInfo(String pluginName, String pluginVersion, String modelName, String genProjName, String trgProjName){
		System.out.println("packGenerateInfo...");
		Properties p = packCommonInfo();
		p.put(P_ACTION, V_ACTION_GENERATE);
		p.put(P_MODULE_NAME, pluginName);
		p.put(P_MODULE_VERSION, pluginVersion);
		
		p.put(P_GENERATE_MODEL_NAME, modelName);
		p.put(P_GENERATE_GENPROJ_NAME, genProjName);
		p.put(P_GENERATE_TRGPROJ_NAME, trgProjName);
		return p;
	}
	
	
}
