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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.model.ActiveLinkLogElement;
import de.chdev.artools.loga.model.ApiLogElement;
import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;
import de.chdev.artools.loga.model.OperationLogElement;
import de.chdev.artools.loga.model.SqlLogElement;

public class ApiController implements ILogController{
	
	// TODO Remove
//	private static ApiController instance = null;
	
	private boolean prefixAvailable;
	public static final String PREFIX = "<API >";
	private MainController mainController;
	private Configuration keywords;

	private Pattern patternApiStart;

	private Pattern patternApiStop;

	// TODO Remove
//	private ApiController() {
//	}

	// TODO Remove
//	public static ApiController getInstance() {
//		if (instance == null) {
//			instance = new ApiController();
//		}
//		return instance;
//	}
	
	public ApiController(Configuration keywords, MainController mainController) {
		this.keywords = keywords;
		this.mainController=mainController;
		
		String regexKey;
		// Define API Start Pattern
		regexKey = keywords.getString("api.start");
		patternApiStart = Pattern.compile(regexKey);
		// Define API Stop Pattern
		regexKey = keywords.getString("api.stop");
		patternApiStop = Pattern.compile(regexKey);		
	}
	
	public int setLogLine(String text, int lineNumber){
		String textWoPrefix = removePrefix(text);
		
		boolean result = false;
		
		if (!result) result = checkApiStart(textWoPrefix, lineNumber);
		if (!result) result = checkApiStop(textWoPrefix, lineNumber);
		// Else
//		if (!result) result = checkAttribute(textWoPrefix, lineNumber);
		return 0;
	}
	
	private String removePrefix(String text){
		if (text.startsWith(PREFIX)){
			prefixAvailable = true;
			return text.substring(6);
		} else {
			prefixAvailable = false;
			return text;
		}
	}
	
	private boolean checkApiStart(String textWoPrefix, int lineNumber){
		Matcher m = patternApiStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement parent = mainController.getLastOpenLogElement();
				
			// Extract API information
			String temp = textWoPrefix;
			String threadId = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(user.length());
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0,temp.indexOf("*/")+2);
			temp = temp.substring(timestampString.length());
			String apiText = temp;
			String apiCall = temp.substring(0, temp.indexOf(" "));
			
			Long timestamp = mainController.convertToTimestamp(timestampString.substring(3, timestampString.length()-3).trim(), PREFIX);
			
			// Create new Active Link Element
			ApiLogElement logElement = new ApiLogElement();
			logElement.setApiCall(apiCall);
			logElement.setElementAlias("API");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setName(apiText);
			logElement.setParentLogElement(parent);
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(textWoPrefix);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			mainController.openNewLogElement(logElement);
			
			return true;
		} else {
			return false;
		}
	}

	private boolean checkApiStop(String textWoPrefix, int lineNumber){
		Matcher m = patternApiStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement logElement = mainController.getLastOpenLogElement();

			// Extract API information
			String temp = textWoPrefix;
			String threadId = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(user.length());
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0,temp.indexOf("*/")+2);
			temp = temp.substring(timestampString.length());
			String apiText = temp;
			String apiCall = temp.substring(0, temp.indexOf(" "));
			String result = temp.substring(apiCall.length());
			
			Long timestamp = mainController.convertToTimestamp(timestampString.substring(3, timestampString.length()-3).trim(), PREFIX);
			
			if (logElement instanceof ApiLogElement){
				logElement.setEndLineNumber(lineNumber);
				((ApiLogElement) logElement).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
			} else {
				System.out.println("Error during api close in line "+lineNumber);
			}
									
			return true;
		} else {
			return false;
		}
	}
}
