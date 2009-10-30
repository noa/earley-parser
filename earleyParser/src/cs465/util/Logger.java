package cs465.util;

import java.io.PrintStream;

public class Logger {
	private static boolean debugMode = false;
	
	private static PrintStream out = System.out;
	
	public static void println(String str) {
		if (debugMode) {
			out.println(str);
		}
	}
	
	public static void print(String str) {
		if (debugMode) {
			out.print(str);
		}
	}
	
	public static void printf(String format, Object ... args) {
		if (debugMode) {
			out.printf(format, args);
		}
	}

	public static void setDebugMode(boolean b) {
		debugMode = b;
	}
	
	public static boolean isDebugMode() {
		return debugMode;
	}
}
