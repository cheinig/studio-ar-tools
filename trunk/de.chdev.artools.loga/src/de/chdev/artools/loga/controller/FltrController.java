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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.model.ActiveLinkLogElement;
import de.chdev.artools.loga.model.ApiLogElement;
import de.chdev.artools.loga.model.CheckRunType;
import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.FilterLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;
import de.chdev.artools.loga.model.OperationLogElement;
import de.chdev.artools.loga.model.ServerLogElement;

public class FltrController implements ILogController{
//	private static FltrController instance = null;
	private boolean prefixAvailable;
	public static final String PREFIX = "<FLTR>";
	private LinkedList<ControlLogElement> eventCache = new LinkedList<ControlLogElement>();
	private MainController mainController;
	private Configuration keywords;
	private Pattern patternFltrStart;
	private Pattern patternFltrFailed;
	private Pattern patternEventStart;
	private Pattern patternEventReStart;
	private Pattern patternEventStop;
	private Pattern patternOperationStart;
	private OperationController operationController;
	private Pattern patternOperationDeferred;
	private Pattern patternGuideStart;
	private Pattern patternGuideStop;
	private Pattern patternFltrOk;
	private Pattern patternFltrNotOk;
	private Pattern patternFltrSkipped;

	public FltrController(Configuration keywords, MainController mainController, OperationController operationController) {
		this.keywords = keywords;
		this.mainController = mainController;
		this.operationController = operationController;
		
		String regexKey;
		// Define Filter Start Pattern 
		regexKey = keywords.getString("fltr.start");
		patternFltrStart = Pattern.compile(regexKey);
		// Define Filter Failed Pattern 
		regexKey = keywords.getString("fltr.check.fail");
		patternFltrFailed = Pattern.compile(regexKey);
		// Define Filter Event Start Pattern 
		regexKey = 	keywords.getString("fltr.event.start");
		patternEventStart = Pattern.compile(regexKey);
		// Define Filter Event ReStart Pattern 
		regexKey = keywords.getString("fltr.event.restart");
		patternEventReStart = Pattern.compile(regexKey);		
		// Define Filter Event Stop Pattern 
		regexKey = keywords.getString("fltr.event.stop");
		patternEventStop = Pattern.compile(regexKey);			
		// Define Filter Operation Start Pattern 
		regexKey = keywords.getString("fltr.operation.start");
		patternOperationStart = Pattern.compile(regexKey);		
		// Define Filter Operation Deferred Pattern 
		regexKey = keywords.getString("fltr.operation.deferred");
		patternOperationDeferred = Pattern.compile(regexKey);		
		// Define Filter Guide Start Pattern 
		regexKey = keywords.getString("fltr.guide.start");
		patternGuideStart = Pattern.compile(regexKey);		
		// Define Filter Guide Stop Pattern 
		regexKey = keywords.getString("fltr.guide.stop");
		patternGuideStop = Pattern.compile(regexKey);		
		// Define Filter OK Pattern 
		regexKey = keywords.getString("fltr.check.ok");
		patternFltrOk = Pattern.compile(regexKey);			
		// Define Filter Not OK Pattern 
		regexKey = keywords.getString("fltr.check.nok");
		patternFltrNotOk = Pattern.compile(regexKey);			
		// Define Filter Skipped Pattern 
		regexKey = keywords.getString("fltr.check.skipped");
		patternFltrSkipped = Pattern.compile(regexKey);		
		
	}

//	public static FltrController getInstance() {
//		if (instance == null) {
//			instance = new FltrController();
//		}
//		return instance;
//	}

	public int setLogLine(String text, int lineNumber) {
		String textWoPrefix = removePrefix(text);

		boolean result = false;

		if (!result)
			result = checkFltrStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkFltrFailed(textWoPrefix, lineNumber);
		if (!result)
			result = checkEventStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkEventReStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkEventStop(textWoPrefix, lineNumber);
		if (!result)
			result = checkOperationStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkOperationDeferred(textWoPrefix, lineNumber);
		if (!result)
			result = checkGuideStart(textWoPrefix, lineNumber);
		if (!result)
			result = checkGuideStop(textWoPrefix, lineNumber);
		if (!result)
			result = checkFltrOk(textWoPrefix, lineNumber);
		if (!result)
			result = checkFltrNok(textWoPrefix, lineNumber);
		if (!result)
			result = checkFltrSkipped(textWoPrefix, lineNumber);
		// Else
		if (!result)
			result = checkAttribute(textWoPrefix, lineNumber);

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

	private boolean checkFltrStart(String textWoPrefix, int lineNumber) {
		Matcher m = patternFltrStart.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String fltrText = temp.trim();
			String nameWithEo = fltrText.substring(fltrText.indexOf("\""));
			String executionOrderText = "";
			int executionOrder = -1;
			// Only set execution order if available
			if (nameWithEo.endsWith(")")) {
				executionOrderText = nameWithEo.substring(nameWithEo
						.lastIndexOf("("));
				executionOrder = Integer.parseInt(executionOrderText.substring(
						1, executionOrderText.length() - 1));
			}
			String name = nameWithEo.substring(0,
					nameWithEo.length() - executionOrderText.length() - 1)
					.trim();

			Long timestamp = mainController.convertToTimestamp(
					timestampString.substring(3, timestampString.length() - 3)
							.trim(), PREFIX);

			// Check if previous open operations exists and close it
			LogElement parent = mainController.getLastOpenLogElement();
			if (parent instanceof OperationLogElement) {
				parent.setEndLineNumber(lineNumber - 1);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			// Check if previous open Filter exists and close it
			if (parent instanceof FilterLogElement) {
				parent.setEndLineNumber(lineNumber - 1);
				((FilterLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}

			// Create new Active Link Element
			FilterLogElement logElement = new FilterLogElement();
			logElement.setElementAlias("FLTR");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setExecutionOrder(executionOrder);
			logElement.setName(name);
			logElement.setParentLogElement(parent);
			logElement.setPhase(0); // TODO
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(fltrText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			mainController.openNewLogElement(logElement);

			return true;
		} else {
			return false;
		}
	}

	private boolean checkFltrFailed(String textWoPrefix, int lineNumber) {
		Matcher m = patternFltrFailed.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {

			// // Extract Filter information
			// String temp = textWoPrefix;
			// String threadId = temp.substring(0,temp.indexOf(">")+1);
			// temp = temp.substring(threadId.length());
			// String rpcId = temp.substring(0,temp.indexOf(">")+1);
			// temp = temp.substring(rpcId.length());
			// String queue = temp.substring(0,temp.indexOf(">")+1);
			// temp = temp.substring(queue.length());
			// String clientRpc = temp.substring(0,temp.indexOf(">")+1);
			// temp = temp.substring(clientRpc.length());
			// String user = temp.substring(0,temp.indexOf(">")+1);
			// temp = temp.substring(user.length());
			// String timestampString = temp.substring(0,temp.indexOf("*/")+2);
			// temp = temp.substring(timestampString.length());
			// String fltrText = temp;
			//
			// Long timestamp =
			// MainController.getInstance().convertToTimestamp(timestampString.substring(3,
			// timestampString.length()-3).trim(), prefix);

			LogElement parent = mainController
					.getLastOpenLogElement();

			// Update last Active Link Element
			if (parent instanceof FilterLogElement) {
				parent.setEndLineNumber(lineNumber);
				((FilterLogElement) parent).setCheckRun(CheckRunType.FAILED);
				((FilterLogElement) parent)
						.setEndTimestamp(((FilterLogElement) parent)
								.getStartTimestamp());
				mainController.closeLastLogElement();
			} else {
				System.out.println("Error during Filter close");
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkEventStart(String textWoPrefix, int lineNumber) {
		Matcher m = patternEventStart.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String phaseString = temp.substring(temp.indexOf("(") + 1,
					temp.indexOf(")"));
			temp = temp.substring(temp.indexOf(")") + 4);
			String fltrText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(
					timestampString.substring(3, timestampString.length() - 3)
							.trim(), PREFIX);

			LogElement parent = mainController.getLastOpenLogElement();

			// Create event name
			String name = "Event - (" + phaseString + ") - " + fltrText;

			// Create new ControlLogElement
			ControlLogElement clElement = new ControlLogElement();
			clElement.setElementAlias("FLTR");
			clElement.setElementType(LogElementType.EVENT);
			clElement.setName(name);
			clElement.setParentLogElement(parent);
			clElement.setStartLineNumber(lineNumber);
			clElement.setStartTimestamp(timestamp);
			clElement.setValid(true);
			clElement.setText(textWoPrefix);

			// TODO Is this part relevant?
			// // Close parent Filter Operation Element if exist
			// if (parent instanceof OperationLogElement){
			// parent.setEndLineNumber(lineNumber-2);
			// MainController.getInstance().closeLastLogElement();
			// parent = MainController.getInstance().getLastOpenLogElement();
			// }

			mainController.openNewLogElement(clElement);
			eventCache.push(clElement);

			return true;
		} else {
			return false;
		}
	}

	private boolean checkEventReStart(String textWoPrefix, int lineNumber) {
		Matcher m = patternEventReStart.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String phaseString = temp.substring(temp.indexOf("(") + 1,
					temp.indexOf(")"));
			temp = temp.substring(temp.indexOf(")") + 4);
			String fltrText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(
					timestampString.substring(3, timestampString.length() - 3)
							.trim(), PREFIX);

			// LogElement parent =
			// MainController.getInstance().getLastOpenLogElement();
			LogElement parent;
			try {
				if (phaseString.equalsIgnoreCase("phase 2")) {
					parent = eventCache.peek().getParentLogElement();
				} else {
					parent = eventCache.peek().getParentLogElement();
				}
			} catch (NullPointerException npe){
				parent = null;
				System.out.println("Parent element not available");
				npe.printStackTrace();
			}

			// Create event name
			String name = "Event - (" + phaseString + ") - " + fltrText;

			// Create new ControlLogElement
			ControlLogElement clElement = new ControlLogElement();
			clElement.setElementAlias("FLTR");
			clElement.setElementType(LogElementType.EVENT);
			clElement.setName(name);
			clElement.setParentLogElement(parent);
			clElement.setStartLineNumber(lineNumber);
			clElement.setStartTimestamp(timestamp);
			clElement.setValid(true);
			clElement.setText(textWoPrefix);

			// TODO Is this part relevant?
			// // Close parent Filter Operation Element if exist
			// if (parent instanceof OperationLogElement){
			// parent.setEndLineNumber(lineNumber-2);
			// MainController.getInstance().closeLastLogElement();
			// parent = MainController.getInstance().getLastOpenLogElement();
			// }

			mainController.openNewLogElement(clElement);
//			eventCache.push(clElement);

			return true;
		} else {
			return false;
		}
	}

	private boolean checkEventStop(String textWoPrefix, int lineNumber) {
		Matcher m = patternEventStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			// if
			// (textWoPrefix.trim().startsWith(keywords.getString("event.stop"))){

			LogElement parent = mainController.getLastOpenLogElement();
			// Close active operation element
			if (parent instanceof OperationLogElement) {
				parent.setEndLineNumber(lineNumber - 2);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}
			// Close active Filter elements
			if (parent instanceof FilterLogElement) {
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
				String overlay = temp.substring(0,temp.indexOf(">")+1);
				temp = temp.substring(overlay.length());
				String timestampString = temp.substring(0,
						temp.indexOf("*/") + 2);
				temp = temp.substring(timestampString.length());
				String fltrText = temp;

				Long timestamp = mainController
						.convertToTimestamp(
								timestampString.substring(3,
										timestampString.length() - 3).trim(),
								PREFIX);

				parent.setEndLineNumber(lineNumber - 2);
				((FilterLogElement) parent).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				parent = mainController.getLastOpenLogElement();
			}

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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String phaseString = temp.substring(temp.indexOf("(") + 1,
					temp.indexOf(")"));
			temp = temp.substring(temp.indexOf(")") + 4);
			String fltrText = temp.trim();

			Long timestamp = mainController.convertToTimestamp(
					timestampString.substring(3, timestampString.length() - 3)
							.trim(), PREFIX);

			if (parent instanceof ControlLogElement) {
				ControlLogElement clElement = (ControlLogElement) parent;
				clElement.setEndLineNumber(lineNumber);
				clElement.setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				// Load last primary event
				clElement = eventCache.peek();
				try {
					if (clElement.isPhase2Available()==false && clElement.isPhase3Available()==false){
						eventCache.pop();
					} else if (clElement.isPhase3Available()==false && phaseString.equalsIgnoreCase("phase 2")){
						eventCache.pop();
					} else if (phaseString.equalsIgnoreCase("phase 3")){
						eventCache.pop();
					}
				} catch (NullPointerException npe){
					System.out.println("Parent event not available");
					npe.printStackTrace();
				}
			} else {
				System.out.println("Error during filter event close in line "
						+ lineNumber);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkOperationStart(String textWoPrefix, int lineNumber) {
		boolean result = false;
		Matcher m = patternOperationStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {
			// Try to extract the operation number
			String numberCode;
			int operationNumber;

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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String operationText = temp.trim();

			if (operationText.length() > 3 && operationText.contains(":")) {
				numberCode = operationText.substring(0,
						operationText.indexOf(":"));
				try {
					operationNumber = Integer.parseInt(numberCode);

					LogElement logElement = mainController.getLastOpenLogElement();
					// Close active operation element
					if (logElement instanceof OperationLogElement) {
						logElement.setEndLineNumber(lineNumber - 1);
						mainController.closeLastLogElement();
						logElement = mainController.getLastOpenLogElement();
					}

					operationController.setNewFltrOperation(
									operationNumber,
									operationText.substring(operationText
											.indexOf(":") + 1), lineNumber);

					// Because Operation start and guide start is the same log
					// line
					// the check will be forwarded to the check guide method
					checkGuideStart(textWoPrefix, lineNumber);

					result = true;
				} catch (NumberFormatException nfe) {
					System.out.println("Error during execution order parse");
				}
			}
		}
		return result;
	}
	
	private boolean checkOperationDeferred(String textWoPrefix, int lineNumber) {
		Matcher m = patternOperationDeferred.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String fltrText = temp.trim();
			String phaseString = fltrText.substring(fltrText.length()-2,fltrText.length()-1);
			
			// LogElement parent =
			// MainController.getInstance().getLastOpenLogElement();
			ControlLogElement parent = eventCache.peek();
			try {
				if (phaseString.equalsIgnoreCase("2")) {
					parent.setPhase2Available(true);
				} else if (phaseString.equals("3")){
					parent.setPhase3Available(true);
				}
			} catch (NullPointerException npe) {
				System.out.println("Parent event not available");
				npe.printStackTrace();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkGuideStart(String textWoPrefix, int lineNumber) {
		Matcher m = patternGuideStart.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			// String timestampString = temp.substring(0,temp.indexOf("*/")+2);
			// temp = temp.substring(timestampString.length());
			String fltrText = temp;

			// Long timestamp =
			// MainController.getInstance().convertToTimestamp(timestampString.substring(3,
			// timestampString.length()-3).trim(), prefix);

			// Create event name
			String name = "Fltr Guide - " + fltrText;

			// Get start timestamp from parent logElement
			Long startTimestamp = 0L;
			if (parent instanceof OperationLogElement) {
				LogElement parentParent = parent.getParentLogElement();
				if (parentParent instanceof ServerLogElement) {
					startTimestamp = ((ServerLogElement) parentParent)
							.getStartTimestamp();
				}
			}

			// Create new ControlLogElement
			ControlLogElement clElement = new ControlLogElement();
			clElement.setElementAlias("FLTR");
			clElement.setElementType(LogElementType.EVENT);
			clElement.setName(name);
			clElement.setParentLogElement(parent);
			clElement.setStartLineNumber(lineNumber);
			clElement.setValid(true);
			clElement.setText(textWoPrefix);
			clElement.setStartTimestamp(startTimestamp);
			mainController.openNewLogElement(clElement);

			return true;
		} else {
			return false;
		}
	}

	private boolean checkGuideStop(String textWoPrefix, int lineNumber) {
		Matcher m = patternGuideStop.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String timestampString = temp.substring(0, temp.indexOf("*/") + 2);
			temp = temp.substring(timestampString.length());
			String fltrText = temp;

			Long timestamp = mainController.convertToTimestamp(
					timestampString.substring(3, timestampString.length() - 3)
							.trim(), PREFIX);

			LogElement logElement = mainController.getLastOpenLogElement();
			// Close active operation element
			if (logElement instanceof OperationLogElement) {
				logElement.setEndLineNumber(lineNumber - 2);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			// Close active Active Link elements
			if (logElement instanceof FilterLogElement) {
				logElement.setEndLineNumber(lineNumber - 2);
				((FilterLogElement) logElement).setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}

			if (logElement instanceof ControlLogElement) {
				ControlLogElement clElement = (ControlLogElement) logElement;
				clElement.setEndLineNumber(lineNumber);
				clElement.setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
			} else {
				System.out
						.println("Error during filter guide event close in line "
								+ lineNumber);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkFltrOk(String textWoPrefix, int lineNumber) {
		Matcher m = patternFltrOk.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {

			LogElement parent = mainController.getLastOpenLogElement();

			if (parent instanceof FilterLogElement) {
				((FilterLogElement) parent).setCheckRun(CheckRunType.RUNIF);
			} else {
				System.out
						.println("FLTR Qualification passed could not be set");
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkFltrNok(String textWoPrefix, int lineNumber) {
		Matcher m = patternFltrNotOk.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid) {

			LogElement parent = mainController.getLastOpenLogElement();

			if (parent instanceof FilterLogElement) {
				((FilterLogElement) parent).setCheckRun(CheckRunType.RUNELSE);
			} else {
				System.out.println("FLTR run ELSE could not be set");
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean checkFltrSkipped(String textWoPrefix, int lineNumber) {
		Matcher m = patternFltrSkipped.matcher(textWoPrefix.trim());
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
			String overlay = temp.substring(0,temp.indexOf(">")+1);
			temp = temp.substring(overlay.length());
			String fltrText = temp.trim();
			String nameWithEo = fltrText.substring(fltrText.indexOf(": ")+2);
			String executionOrderText = "";
			int executionOrder = -1;
			// Only set execution order if available
			if (nameWithEo.endsWith(")")) {
				executionOrderText = nameWithEo.substring(nameWithEo
						.lastIndexOf("("));
				executionOrder = Integer.parseInt(executionOrderText.substring(
						1, executionOrderText.length() - 1));
			}
			String name = nameWithEo.substring(0,
					nameWithEo.length() - executionOrderText.length() - 1)
					.trim();

			// Check if previous open operations exists and close it
			LogElement parent = mainController.getLastOpenLogElement();
			Long timestamp=0L;
			if (parent instanceof OperationLogElement) {
				LogElement parentParent = parent.getParentLogElement();
				if (parentParent instanceof ServerLogElement){
					timestamp = ((ServerLogElement) parentParent).getStartTimestamp();
				}
//				parent.setEndLineNumber(lineNumber - 1);
//				MainController.getInstance().closeLastLogElement();
//				parent = MainController.getInstance().getLastOpenLogElement();
			}
//			// Check if previous open Filter exists and close it
//			if (parent instanceof FilterLogElement) {
//				parent.setEndLineNumber(lineNumber - 1);
//				((FilterLogElement) parent).setEndTimestamp(timestamp);
//				MainController.getInstance().closeLastLogElement();
//				parent = MainController.getInstance().getLastOpenLogElement();
//			}

			// Create new Active Link Element
			FilterLogElement logElement = new FilterLogElement();
			logElement.setElementAlias("FLTR");
			logElement.setElementType(LogElementType.SERVERACTION);
			logElement.setExecutionOrder(executionOrder);
			logElement.setName(name);
			logElement.setParentLogElement(parent);
			logElement.setPhase(0); // TODO
			logElement.setQueue(queue);
			logElement.setRpcId(rpcId);
			logElement.setStartLineNumber(lineNumber);
			logElement.setStartTimestamp(timestamp);
			logElement.setText(fltrText);
			logElement.setThreadId(threadId);
			logElement.setUser(user);
			logElement.setValid(true);
			logElement.setCheckRun(CheckRunType.SKIPPED);
			logElement.setEndLineNumber(lineNumber);
			logElement.setEndTimestamp(timestamp);
			mainController.openNewLogElement(logElement);
			mainController.closeLastLogElement();

			return true;
		} else {
			return false;
		}
	}

	private boolean checkAttribute(String textWoPrefix, int lineNumber) {

		boolean result = false;

		LogElement logElement = mainController.getLastOpenLogElement();
		if (logElement instanceof OperationLogElement) {
			operationController.setAttributeLine(textWoPrefix,
					lineNumber);
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
