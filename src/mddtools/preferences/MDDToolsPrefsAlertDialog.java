package mddtools.preferences;

import mddtools.Activator;
import mddtools.util.BrowserUtils;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.emf.mwe.internal.core.debug.processing.handlers.CommandRuntimeHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;


public class MDDToolsPrefsAlertDialog extends Dialog {

//	private Button checkBox;
	private boolean reportEnabled;

	public MDDToolsPrefsAlertDialog(boolean reportEnabled, Shell parentShell) {
		super(parentShell);
		this.reportEnabled = reportEnabled;
	}

	public MDDToolsPrefsAlertDialog(boolean reportEnabled, IShellProvider parentShell) {
		super(parentShell);
		this.reportEnabled = reportEnabled;
	}

	protected void buttonPressed(int buttonId) {
//		if (buttonId == IDialogConstants.OK_ID) {
//			this.reportEnabled = checkBox.getSelection();
//		} else if (buttonId == IDialogConstants.CANCEL_ID) {
//			this.reportEnabled = false;
//		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(getDialogTitle());
	}

	private String getDialogTitle() {
		return "MDDTools usage tracking";
	}

	public final static String DLG_TITLE = "Configurazione iniziale MDDTools mancante";
	public final static String DLG_MESSAGE = 
			"Attenzione: prima di poter utilizzare gli MDD-Tools è necessario configurare le preferenze dello strumento\n"+
			"(selezionare la voce di menu: window -> preferences -> MDDTools preferences).\n"+
			"Le impostazioni permettono di abilitare/disabilitare l'invio di informazioni statistiche sull'utilizzo dei vari tool.";
	public final static String EXPLANATION_PAGE = "http://mdd.csipiemonte.it/usageTracking.html";
//	public final static String CHECKBOX_TEXT = "Report usage of MDDTools to development team.";
	
	
	protected void createButtonsForButtonBar(Composite parent) {
		Button b_ok = createButton(parent, IDialogConstants.OK_ID, "Apri il dialog delle preferenze", true);
		b_ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				// apre la pagina di properties
				IWorkbench workbench = PlatformUI.getWorkbench();
				IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
				ICommandService commandService = (ICommandService) workbench.getService(ICommandService.class);
				try {
					Command prefCmd = commandService.getCommand("org.eclipse.ui.window.preferences");
					Parameterization params[] = new Parameterization[1];
					params[0] = new Parameterization(prefCmd.getParameters()[0], "mddtools.preferences.MDDToolsPreferencePage");
					ParameterizedCommand mddToolPrefCmd = new ParameterizedCommand(prefCmd, params);
					//handlerService.executeCommand("org.eclipse.ui.window.preferences", null);
					handlerService.executeCommand(mddToolPrefCmd, null);
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotDefinedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotEnabledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotHandledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		b_ok.setFocus();
		//createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		//checkBox.setFocus();
		//checkBox.setSelection(reportEnabled);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// message
		Link link = new Link(composite, SWT.WRAP);
		link.setFont(parent.getFont());
		link.setText(DLG_MESSAGE);
		link.setToolTipText(EXPLANATION_PAGE);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BrowserUtils.checkedCreateExternalBrowser(
						EXPLANATION_PAGE,
						Activator.PLUGIN_ID,
						Activator.getDefault().getLog());
			}
		});

		/* per il momento il link è disabilitato perchè non è ancora disponibile la pagina
		Button b = new Button(composite, SWT.NULL);
		b.setText("MDDTools usage tracking: dettagli");
		b.setToolTipText("Premere per visualizzare una pagina esplicativa \ndel meccanismo di usage tracking degli MDDTools");
		b.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				BrowserUtils.checkedCreateExternalBrowser(
						EXPLANATION_PAGE,
						Activator.PLUGIN_ID,
						Activator.getDefault().getLog());				
			}
		});
		
		*/
		// checkbox
//		checkBox = new Button(composite, SWT.CHECK);
//		checkBox.setText(CHECKBOX_TEXT);

		return composite;
	}

	public boolean isReportEnabled() {
		return reportEnabled;
	}

}