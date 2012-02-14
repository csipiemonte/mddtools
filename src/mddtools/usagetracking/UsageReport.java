package mddtools.usagetracking;

import mddtools.preferences.MDDToolsPrefsAlertDialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.prefs.BackingStoreException;

public class UsageReport {

	
	public UsageReport() throws InvalidSyntaxException {
//		eclipseEnvironment = SubclipseToolsUsageActivator.getDefault().getSubclipseEclipseEnvironment();
//		focusPoint = new SuffixFocusPoint("tools", eclipseEnvironment.getSubclipseVersion()) //$NON-NLS-1$ 
//				.setChild(new FocusPoint("usage") //$NON-NLS-1$ 
//						.setChild(new FocusPoint("action") //$NON-NLS-1$ 
//								.setChild(new FocusPoint("wsstartup")))); //$NON-NLS-1$
//
//		globalSettings = new GlobalUsageSettings(SubclipseToolsUsageActivator
//				.getDefault());
	}

	public void report() {
		new ReportingJob().schedule();
	}

	private void askUser() {
		MDDToolsPrefsAlertDialog dialog = new MDDToolsPrefsAlertDialog(
				true,
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if (dialog.open() == Window.OK) {
	//		UsageReportPreferences.setEnabled(dialog.isReportEnabled());
	//		UsageReportPreferences.setAskUser(false);
			flushPreferences();
		}
	}

	private void flushPreferences() {
//		try {
//		//	UsageReportPreferences.flush();
//		} catch (BackingStoreException e) {
////			IStatus status = StatusUtils.getErrorStatus(SubclipseToolsUsageActivator.PLUGIN_ID,
////					ReportingMessages.UsageReport_Error_SavePreferences, e, null);
////			SubclipseToolsUsageActivator.getDefault().getLog().log(status);
//		}
	}

	/**
	 * Reports the usage of the current JBoss Tools / JBoss Developer Studio
	 * installation.
	 */
	private void doReport() {
//		if (UsageReportPreferences.isEnabled()) {
//			IURLBuildingStrategy urlBuildingStrategy = new GoogleAnalyticsUrlStrategy(eclipseEnvironment);
//			ILoggingAdapter loggingAdapter = new PluginLogger(SubclipseToolsUsageActivator.getDefault());
//			ITracker tracker = new Tracker(
//					urlBuildingStrategy
//					, new HttpGetRequest(eclipseEnvironment.getUserAgent(), loggingAdapter)
//					, loggingAdapter);
//			tracker.trackAsynchronously(focusPoint);
//		}
	}

	private class ReportingJob extends Job {
		private ReportingJob() {
			// super(ReportingMessages.UsageReport_Reporting_Usage);
			super("MDDTools_reporting_job");
		}

		protected IStatus run(IProgressMonitor monitor) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.beginTask("ReportingMessages.UsageReport_Querying_Enablement", 2);
			if (TrackingSender.isTrackingActive()) {
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				monitor.worked(1);
				if (/* UsageReportPreferences.isAskUser()*/ true) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					askUserAndReport();
				} else {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					doReport();
				}
				monitor.worked(2);
				monitor.done();
			}
			return Status.OK_STATUS;
		}

		private void askUserAndReport() {
			Job askUserJob = new AskUserJob();
			askUserJob.addJobChangeListener(new IJobChangeListener() {

				public void sleeping(IJobChangeEvent event) {
					// ignore
				}

				public void scheduled(IJobChangeEvent event) {
					// ignore
				}

				public void running(IJobChangeEvent event) {
					// ignore
				}

				public void done(IJobChangeEvent event) {
					doReport();
				}

				public void awake(IJobChangeEvent event) {
					// ignore
				}

				public void aboutToRun(IJobChangeEvent event) {
					// ignore
				}
			});
			askUserJob.setUser(true);
			askUserJob.setPriority(Job.SHORT);
			askUserJob.schedule();
		}
	}

	private class AskUserJob extends UIJob {
		private AskUserJob() {
			super("Asking_user");
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			askUser();
			return Status.OK_STATUS;
		}
	}
}