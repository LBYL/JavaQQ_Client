package com.lbyl.Utils;

/**
 * ��־������
 * @author LBYL
 *
 */
public class LogUtil {

	private static boolean doLog = true;

	public static void log(String sth) {
		if (!doLog) {
			return;
		}
		System.out.println(sth);
	}

}
