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

import de.chdev.artools.loga.model.ActiveLinkLogElement;
import de.chdev.artools.loga.model.CheckRunType;
import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.EscalationLogElement;
import de.chdev.artools.loga.model.FilterLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;
import de.chdev.artools.loga.model.OperationLogElement;

public class EsclController implements ILogController {

	// internal control vars
	private boolean prefixAvailable = false;
	public static final String PREFIX = "<ESCL>";

	MainController mainController;
	// private List<ActiveLinkLogElement> activeLinkLogElementList = new
	// LinkedList<ActiveLinkLogElement>();

	private Configuration keywords;
	private Pattern patternEsclStart;
	private Pattern patternEsclCheckWait;
	private Pattern patternEsclCheckRun;
	private Pattern patternEsclCheckFailed;
	private Pattern patternEsclCheckStop;
	private Pattern patternEsclStop;
	private OperationController operationController;
	private Pattern patternEsclCheckDisabled;

	public EsclController(Configuration keywords,
			MainController mainController,
			OperationController operationController) {
		this.keywords = keywords;
		this.mainController = mainController;
		this.operationController = operationController;

		String regexKey;
		// Define Escalations Start Pattern
		regexKey = keywords.getString("escl.start");
		patternEsclStart = Pattern.compile(regexKey);
		// Define Escalation Check Wait Pattern
		regexKey = keywords.getString("escl.check.wait");
		patternEsclCheckWait = Pattern.compile(regexKey);
		// Define Escalation Check Run Pattern
		regexKey = keywords.getString("escl.check.run");
		patternEsclCheckRun = Pattern.compile(regexKey);
		// Define Escalation Check Failed Pattern
		regexKey = keywords.getString("escl.check.failed");
		patternEsclCheckFailed = Pattern.compile(regexKey);
		// Define Escalation Check Stop Pattern
		regexKey = keywords.getString("escl.check.stop");
		patternEsclCheckStop = Pattern.compile(regexKey);
		// Define Escalations Stop Pattern
		regexKey = keywords.getString("escl.stop");
		patternEsclStop = Pattern.compile(regexKey);
		 // Define Escalation Disabled Pattern
		 regexKey = keywords.getString("escl.check.disabled");
		 patternEsclCheckDisabled = Pattern.compile(regexKey);
		// // Define Comment Pattern
		// regexKey = keywords.getString("actl.comment");
		// patternComment = Pattern.compile(regexKey);
		// // Define AL Failed Pattern
		// regexKey = keywords.getString("actl.check.fail");
		// patternActlFailed = Pattern.compile(regexKey);
		// // Define AL Start Pattern
		// regexKey = keywords.getString("actl.start");
		// patternActlStart = Pattern.compile(regexKey);

	}

	@Override
	public int setLogLine(String logLine, int lineNumber) {
		String textWoPrefix = removePrefix(logLine);

		boolean result = false;

		if (!result)
			result = checkEsclStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkEsclCheckWait(textWoPrefix, lineNumber);
		if (!result)
			result = checkEsclCheckRun(textWoPrefix, lineNumber);
		if (!result)
			result = checkEsclCheckFailed(textWoPrefix, lineNumber);
		if (!result)
			result = checkEsclCheckStop(textWoPrefix, lineNumber);
		if (!result)
			result = checkEsclStop(textWoPrefix, lineNumber);
		 if (!result) result = checkEsclCheckDisabled(textWoPrefix, lineNumber);
		// if (!result) result = checkGuideStop(textWoPrefix, lineNumber);
		// if (!result) result = checkActlOk(textWoPrefix, lineNumber);
		// if (!result) result = checkActlNok(textWoPrefix, lineNumber);
		// if (!result) result = checkActlProceed(textWoPrefix, lineNumber);
		// Else
		if (!result)
			result = checkAttribute(textWoPrefix, lineNumber);

		// System.out.println(lineNumber+" \t"+textWoPrefix);
		return 0;
	}

	private String removePrefix(String text) {
		if (text.startsWith(PREFIX)) {
			prefixAvailable = true;
			return text.substring(6);
		} else {
			prefixAvailable = false;
			return text;
		}
	}

	private boolean checkEsclStart(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			// Create new Escalation Event
			ControlLogElement logElement = new ControlLogElement();
			logElement.setElementAlias("ESCL");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setName(esclText);
			logElement.setParentLogElement(null);
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(esclText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			mainController.openNewLogElement(logElement);

			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEsclCheckWait(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclCheckWait.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			
			LogElement parent = mainController.getLastOpenLogElement();

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			if (parent instanceof EscalationLogElement){
				parent.setEndLineNumber(lineNumber);
				((EscalationLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			
			// Create new Escalation Event
			EscalationLogElement logElement = new EscalationLogElement();
			logElement.setElementAlias("ESCL");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setName(esclText);
			logElement.setParentLogElement(parent);
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(esclText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			logElement.setEndLineNumber(lineNumber);
			logElement.setEndTimestamp(timestamp);
			mainController.openNewLogElement(logElement);

			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEsclCheckRun(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclCheckRun.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			
			LogElement parent = mainController.getLastOpenLogElement();

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			if (parent instanceof EscalationLogElement){
				parent.setEndLineNumber(lineNumber);
				((EscalationLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			
			// Create new Escalation Event
			EscalationLogElement logElement = new EscalationLogElement();
			logElement.setElementAlias("ESCL");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setName(esclText);
			logElement.setParentLogElement(parent);
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(esclText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			mainController.openNewLogElement(logElement);

			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEsclCheckFailed(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclCheckFailed.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			
			LogElement parent = mainController.getLastOpenLogElement();

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
//			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
//			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

//			Long timestamp = mainController.convertToTimestamp(timestampString
//					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			// Create new Escalation Event
			if (parent instanceof EscalationLogElement){
				// TODO
			}

			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEsclCheckStop(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclCheckStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			
			LogElement parent = mainController.getLastOpenLogElement();

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			// Create new Escalation Event
			if (parent instanceof EscalationLogElement){
				parent.setEndLineNumber(lineNumber);
				((EscalationLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkEsclStop(String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			// Close all Elements until Escalations start
			LogElement parent = mainController.getLastOpenLogElement();
			while (parent != null && !(parent instanceof ControlLogElement)) {
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			if (parent != null) {
				if (parent instanceof ControlLogElement) {
					parent.setEndLineNumber(lineNumber);
					((ControlLogElement) parent).setEndTimestamp(timestamp);
					mainController.closeLastLogElement();
				}
			}

			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEsclCheckDisabled (String textWoPrefix, int lineNumber) {
		Matcher m = patternEsclCheckDisabled.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			
			LogElement parent = mainController.getLastOpenLogElement();

			// Extract Filter information
			String temp = textWoPrefix;
			String threadId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(threadId.length());
			String rpcId = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(rpcId.length());
			String queue = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(queue.length());
			String clientRpc = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(clientRpc.length());
			String user = temp.substring(0, temp.indexOf(">") + 1);
			temp = temp.substring(user.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String esclText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(timestampString
					.substring(3, timestampString.length() - 3).trim(), PREFIX);

			if (parent instanceof EscalationLogElement){
				parent.setEndLineNumber(lineNumber);
				((EscalationLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			
			// Create new Escalation Event
			EscalationLogElement logElement = new EscalationLogElement();
			logElement.setElementAlias("ESCL");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setName(esclText);
			logElement.setParentLogElement(parent);
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(esclText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			logElement.setCheckRun(CheckRunType.DISABLED);
			mainController.openNewLogElement(logElement);

			return true;
		} else {
			return false;
		}
	}

	private boolean checkAttribute(String textWoPrefix, int lineNumber) {

		boolean result = false;

		LogElement logElement = mainController.getLastOpenLogElement();
		if (logElement instanceof OperationLogElement) {
			operationController.setAttributeLine(textWoPrefix, lineNumber);
			result = true;
		} else if (logElement instanceof ControlLogElement) {
			assignControlAttribute(textWoPrefix, lineNumber);
			result = true;
		} else {
			System.out.println("Attribute can not be assigned (Line: "
					+ lineNumber + ")");
		}

		return result;
	}

	private boolean assignControlAttribute(String textWoPrefix, int lineNumber) {

		return true;
	}
}
