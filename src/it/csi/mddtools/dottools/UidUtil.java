package it.csi.mddtools.dottools;

public class UidUtil {

	private static int uid = 0;
	
	public static String newUid( Object o ) {
		return new Integer(uid++).toString();
	}
	
}
