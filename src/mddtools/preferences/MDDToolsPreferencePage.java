package mddtools.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import mddtools.Activator;
import mddtools.usagetracking.ProfilingPacketBuilder;

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
		setDescription("Preferenze MDDTools");
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
				new StringFieldEditor(PreferenceConstants.P_NOME, "&Nome:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_COGNOME, "&Cognome:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_COMPANY, "Co&mpany:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.P_EMAIL, "&Email:", getFieldEditorParent()));
		
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
			// store info in user home dir
			persistPreferencesInUserDir();
		}
		return esito;
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
			mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_NAME, whoName);
			mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_SURNAME, whoSurname);
			mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_COMPANY, whoCompany);
			mddtoolsInfo.setValue(ProfilingPacketBuilder.P_WHO_EMAIL, whoEmail);
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
		Properties props = new Properties();
		props.put(ProfilingPacketBuilder.P_WHO_NAME, whoName);
		props.put(ProfilingPacketBuilder.P_WHO_SURNAME, whoSurname);
		props.put(ProfilingPacketBuilder.P_WHO_COMPANY, whoCompany);
		props.put(ProfilingPacketBuilder.P_WHO_EMAIL, whoEmail);
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