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
package mddtools;


import java.text.MessageFormat;

import mddtools.usagetracking.ProfilingPacketBuilder;
import mddtools.usagetracking.TrackingSender;
import mddtools.usagetracking.UsageReport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup{

	// The plug-in ID
	public static final String PLUGIN_ID = "mddtools";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("STARTED!!!!!!");
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	
	public static IStatus getErrorStatus(String pluginId, String message, Throwable throwable,
			Object[] messageArguments) {
		String formattedMessage = null;
		if (message != null) {
			formattedMessage = MessageFormat.format(message, messageArguments);
		}
		return new org.eclipse.core.runtime.Status(org.eclipse.core.runtime.Status.ERROR, pluginId, org.eclipse.core.runtime.Status.ERROR, formattedMessage,
				throwable);
	}
	
	
	private void showWarningDialog(){
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				try {
					new UsageReport().report();
				} catch (Exception e) {
					IStatus status = getErrorStatus(Activator.PLUGIN_ID,
							"could not start usage reporting", e, null);
					//LoggingUtils.log(status, SubclipseToolsUsageActivator.getDefault());
					Activator.getDefault().getLog().log(status);
				}
			}
		});
	}
	
	public void earlyStartup() {
		System.out.println("EARLYSTARTUP");
		if (TrackingSender.isTrackingActive()){
			boolean showWarningDlg = false;
	
			try{
				// verifica la presenza del file ".mddtools.info"
				ProfilingPacketBuilder.packCommonInfo();
			}
			catch(Exception e){
				showWarningDlg=true;
			}
			// qui potrebbe essere necessario aggiungere dei controlli
			if (showWarningDlg)
				showWarningDialog();
		}
	}
}
