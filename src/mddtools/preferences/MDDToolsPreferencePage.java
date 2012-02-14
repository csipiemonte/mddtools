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
package mddtools.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import mddtools.Activator;
import mddtools.usagetracking.ProfilingPacketBuilder;
import mddtools.usagetracking.TrackingSender;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class MDDToolsPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public MDDToolsPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(
				"Preferenze MDDTools. \n"+
				"In questa pagina è necessario inserire alcune informazioni utilizzate dal meccanismo di tracking dell'utilizzo degli MDDTools. \n"+
				"Il tracking ha scopi statistici e può essere utilizzato in modalità anonima o essere totalmente disabilitato.\n"+
				"Maggiori spiegazioni relativamente al meccanismo di tracking sono disponibili nell' help-on-line, nella sezione MDDTools -> usage tracking.\n\n\n\n"
				);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
//		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH, 
//				"&Directory preference:", getFieldEditorParent()));
////		addField(
//			new BooleanFieldEditor(
//				PreferenceConstants.P_BOOLEAN,
//				"&An example of a boolean preference",
//				getFieldEditorParent()));
//
//		addField(new RadioGroupFieldEditor(
//				PreferenceConstants.P_CHOICE,
//			"An example of a multiple-choice preference",
//			1,
//			new String[][] { { "&Choice 1", "choice1" }, {
//				"C&hoice 2", "choice2" }
//		}, getFieldEditorParent()));
//		addField(
//			new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_NOME, 
						"&Nome:\n"+
						"(in caso di utilizzo anonimo inserire \"anonymous\"):", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_COGNOME, 
						"&Cognome:\n"+
						"(in caso di utilizzo anonimo inserire un identificativo\n"+
						"fittizio univoco all'interno dell'azienda ma non riconducibile\n" +
						"all' utente effettivo al di fuori del contesto dell'azienda di \n"+
						"appartenenza - es. \"user_1\"):", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_COMPANY, "Co&mpany:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_EMAIL, 
						"&Email_\n"+
						"(in caso di utilizzo anonimo non è necessario indicare \n"+
						"un indirizzo associato all'utilizzatore, ma è sufficiente una casella\n"+
						"di gruppo sulla quale sia possibile ricevere eventuali notifiche dal\n" +
						"gruppo di sviluppo degli strumenti MDDTools)"+
						":", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_TRACKING_URL, "Tracking &Url:", getFieldEditorParent()));
		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_DISABLE_TRACKING,
					"&Disabilita invio informazioni di utilizzo:",
					getFieldEditorParent()));
		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_ENABLE_PROXY,
					"Abilita utilizzo del pro&xy:",
					getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_PROXY_HOST, "Proxy &Host:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_PROXY_PORT, "Proxy &Port:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		restorePreferencesFromUserDir();
	}

	@Override
	public boolean performOk() {
		boolean esito = super.performOk();
		if (esito){
			System.out.println("TESTTEST");
			
			// store info in user home dir
			persistPreferencesInUserDir();
			testConnection();
		}
		return esito;
	}
	
	public boolean testConnection() {
		String pingMsg = TrackingSender.pingTracking(new Properties());
		if (pingMsg==null || "HTTP:200".equals(pingMsg)||"N.A".equals(pingMsg))
			return true;
		else {
			// pre-elaborazione messaggio di errore
			if (pingMsg.startsWith("HTTP:")){
				pingMsg = "Il server ha resituito il seguente codice di errore "+pingMsg;
			}
			MessageDialog dlg = new MessageDialog((Shell) null,
					"Errore nel tentativo di connessione al server di tracking", null,
					pingMsg, 0, new String[] { "OK" }, 0);
			dlg.open();
			return false;
		}
	}
	
	/**
	 * Carica le preferenze dal file contenuto nella directory utente
	 */
	public static void restorePreferencesFromUserDir(){
		Activator mddtoolsActivator = mddtools.Activator.getDefault();
		IPreferenceStore mddtoolsInfo = mddtoolsActivator.getPreferenceStore();
		FileInputStream fis = null;
		String homeDir = System.getProperty("user.home");
		try {
			fis = new FileInputStream(homeDir+"/"+ProfilingPacketBuilder.REGISTRATION_FILENAME);
			Properties props = new Properties();
			props.load(fis);
			String whoName = props.getProperty(ProfilingPacketBuilder.P_WHO_NAME);
			String whoSurname = props.getProperty(ProfilingPacketBuilder.P_WHO_SURNAME);
			String whoCompany = props.getProperty(ProfilingPacketBuilder.P_WHO_COMPANY);
			String whoEmail = props.getProperty(ProfilingPacketBuilder.P_WHO_EMAIL);
			String disableTracking = props.getProperty(ProfilingPacketBuilder.P_DISABLE_TRACKING);
			String enableProxy = props.getProperty(ProfilingPacketBuilder.P_ENABLE_PROXY);
			String proxyHost = props.getProperty(ProfilingPacketBuilder.P_PROXY_HOST);
			String proxyPort = props.getProperty(ProfilingPacketBuilder.P_PROXY_PORT);
			String trackingURL = props.getProperty(ProfilingPacketBuilder.P_TRACKING_URL);
			
			
			
			if (whoName != null)  mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_NAME, whoName);
			if (whoSurname != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_SURNAME, whoSurname);
			if (whoCompany != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_COMPANY, whoCompany);
			if (whoEmail != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_EMAIL, whoEmail);
			if (disableTracking != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_DISABLE_TRACKING, disableTracking);
			if (enableProxy != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_ENABLE_PROXY, enableProxy);
			if (proxyHost != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_PROXY_HOST, proxyHost);
			if (proxyPort != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_PROXY_PORT, proxyPort);
			
			// le impostazioni del tracking url sono prese dal file mddtracking.proeprties se non presenti.
			if (trackingURL == null)
				trackingURL = TrackingSender.getDefaultTrackingServiceURL();
			if (trackingURL != null) mddtoolsInfo.setValue(ProfilingPacketBuilder.P_TRACKING_URL, trackingURL);
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}
	
	public static void persistPreferencesInUserDir(){
		Activator mddtoolsActivator = mddtools.Activator.getDefault();
		IPreferenceStore mddtoolsInfo = mddtoolsActivator.getPreferenceStore();
		String whoName = mddtoolsInfo.getString(ProfilingPacketBuilder.P_WHO_NAME);
		String whoSurname = mddtoolsInfo.getString(ProfilingPacketBuilder.P_WHO_SURNAME);
		String whoCompany = mddtoolsInfo.getString(ProfilingPacketBuilder.P_WHO_COMPANY);
		String whoEmail = mddtoolsInfo.getString(ProfilingPacketBuilder.P_WHO_EMAIL);
		String disableTracking = mddtoolsInfo.getString(ProfilingPacketBuilder.P_DISABLE_TRACKING);
		String enableProxy = mddtoolsInfo.getString(ProfilingPacketBuilder.P_ENABLE_PROXY);
		String proxyHost = mddtoolsInfo.getString(ProfilingPacketBuilder.P_PROXY_HOST);
		String proxyPort = mddtoolsInfo.getString(ProfilingPacketBuilder.P_PROXY_PORT);
		String trackingURL = mddtoolsInfo.getString(ProfilingPacketBuilder.P_TRACKING_URL);
		
		Properties props = new Properties();
		props.put(ProfilingPacketBuilder.P_WHO_NAME, whoName);
		props.put(ProfilingPacketBuilder.P_WHO_SURNAME, whoSurname);
		props.put(ProfilingPacketBuilder.P_WHO_COMPANY, whoCompany);
		props.put(ProfilingPacketBuilder.P_WHO_EMAIL, whoEmail);
		props.put(ProfilingPacketBuilder.P_DISABLE_TRACKING, disableTracking);
		props.put(ProfilingPacketBuilder.P_ENABLE_PROXY, enableProxy);
		props.put(ProfilingPacketBuilder.P_PROXY_HOST, proxyHost);
		props.put(ProfilingPacketBuilder.P_PROXY_PORT, proxyPort);
		props.put(ProfilingPacketBuilder.P_TRACKING_URL, trackingURL);
		String homeDir = System.getProperty("user.home");
		FileOutputStream fos = null;
		try {
			 fos = new FileOutputStream(homeDir+"/"+ProfilingPacketBuilder.REGISTRATION_FILENAME);
			 try {
				props.store(fos, "BACKUP of MDDTOOLS preferences: needed by OAW execution context");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}