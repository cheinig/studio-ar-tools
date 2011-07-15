package de.chdev.artools.loga.controller;

public class WflgController implements ILogController {

//	private static WflgController instance = null;
	public static final String PREFIX = "<WFLG>";

	public WflgController() {
	}

//	public static WflgController getInstance() {
//		if (instance == null) {
//			instance = new WflgController();
//		}
//		return instance;
//	}
	
	public int setLogLine(String text, int lineNumber){
		// Ignore all
		return 0;
	}
}
