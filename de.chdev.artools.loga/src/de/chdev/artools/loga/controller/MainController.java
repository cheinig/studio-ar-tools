/*
   Copyright 2011 Christoph Heinig

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package de.chdev.artools.loga.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.model.LogElement;

public class MainController {

	// TODO Remove
//	private static MainController instance = null;
	
	// internal control vars
	private LinkedList<LogElement> logElementList = new LinkedList<LogElement>();
	private LinkedList<LogElement> callHierarchy = new LinkedList<LogElement>();
	
	private HashMap<String, ILogController> controllerMap = new HashMap<String, ILogController>();

	// TODO Remove
	public MainController() {
	}

	// TODO Remove
//	public static MainController getInstance() {
//		if (instance == null) {
//			instance = new MainController();
//		}
//		return instance;
//	}
		
	public void openNewLogElement(LogElement logElement){
		getLogElementList().add(logElement);
		callHierarchy.push(logElement);
	}
	
	public void closeLastLogElement(){
		callHierarchy.pop();
	}
	
	public LogElement getLastOpenLogElement(){
		LogElement result = null;
		
		result = callHierarchy.peek();
		
		return result;
	}

	public void setLogElementList(LinkedList<LogElement> logElementList) {
		this.logElementList = logElementList;
	}

	public LinkedList<LogElement> getLogElementList() {
		return logElementList;
	}
	
	public Long convertToTimestamp(String dateValue, String prefix){
		Long result = 0L;
		Date date;
		String formatString;
		String dateValueLocal = dateValue;
//		String formatString = KeywordLoader.getConfiguration(prefix).getString("format.date."+prefix.substring(1,prefix.length()-1).trim());
		String locale = KeywordLoader.getConfiguration(prefix).getString("language.locale");
//		if (formatString==null)formatString = "EE MMM dd yyyy HH:mm:ss";
		if (prefix.equalsIgnoreCase("<actl>")){
			formatString = "EE MMM dd yyyy HH:mm:ss";
		} else {
			formatString = "EE MMM dd yyyy HH:mm:ss.SSS";
			// Convert date String to milliseconds
			int ms = Integer.parseInt(dateValue.substring(dateValue.length()-4,dateValue.length()-1));
//			int mod = Integer.parseInt(dateValue.substring(dateValue.length()-1,dateValue.length()));
//			if (mod>=5){
//				ms++;
//			}
			dateValueLocal = dateValue.substring(0, dateValue.length()-4)+""+ms;
		}
		SimpleDateFormat df = new SimpleDateFormat(formatString, new Locale(locale));
		/* Do Jan 06 2011 14:40:39 */
		try {
 			date = df.parse(dateValueLocal);
			result = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Extend with last number
		int lastNumber = Integer.parseInt(dateValue.substring(dateValue.length()-1));
		result *= 10;
		result += lastNumber;
		
		return result;
	}
	
	public void clear(){
		logElementList.clear();
		callHierarchy.clear();
	}

	public void setControllerMap(HashMap<String, ILogController> controllerMap) {
		this.controllerMap = controllerMap;
	}

	public HashMap<String, ILogController> getControllerMap() {
		return controllerMap;
	}
	
	public void runPostProcessing(){
		PostProcessing postProcessing = new PostProcessing();
		postProcessing.correctHierarchy(logElementList, callHierarchy);
	}
}
