package cs465.util;

public class Logger {
	private static boolean debugMode = false;
	
	public static void println(String str) {
		if (debugMode) {
			System.err.println(str);
		}
	}
	
	public static void print(String str) {
		if (debugMode) {
			System.err.print(str);
		}
	}
	
	public static void printf(String format, Object ... args) {
		if (debugMode) {
			System.err.printf(format, args);
		}
	}

	public static void setDebugMode(boolean b) {
		debugMode = b;
	}
}
