package mddtools.util;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class StatusUtils {

	private StatusUtils() {
		// inhibit instantiation
	}

	/**
	 * Returns an error status for a given plugin id, message and arguments.
	 * 
	 * @param pluginId
	 *            the plugin id
	 * @param message
	 *            the message
	 * @param throwable
	 *            the throwable
	 * @param messageArguments
	 *            the message arguments
	 * @return the error status
	 */
	public static IStatus getErrorStatus(String pluginId, String message, Throwable throwable,
			Object[] messageArguments) {
		String formattedMessage = null;
		if (message != null) {
			formattedMessage = MessageFormat.format(message, messageArguments);
		}
		return new Status(Status.ERROR, pluginId, Status.ERROR, formattedMessage,
				throwable);
	}

	/**
	 * Returns an debug status for a given plugin id, message and arguments.
	 * 
	 * @param pluginId
	 *            the plugin id
	 * @param message
	 *            the message
	 * @param messageArguments
	 *            the message arguments
	 * 
	 * @return the debug status
	 */
	public static IStatus getInfoStatus(String pluginId, String message, Object[] messageArguments) {
		return new Status(Status.INFO, pluginId, Status.INFO, MessageFormat.format(message, messageArguments), null);
	}

	/**
	 * Clones a given status.
	 * 
	 * @param status
	 *            the status
	 * 
	 * @return the i status
	 */
	public static IStatus clone(IStatus status) {
		switch (status.getSeverity()) {
		case IStatus.INFO:
			return getInfoStatus(status.getPlugin(), status.getMessage(), null);
		case IStatus.ERROR:
			return getErrorStatus(status.getPlugin(), status.getMessage(), status.getException(), null);
		default:
			throw new UnsupportedOperationException("noy implemented yet!");
		}
	}
}