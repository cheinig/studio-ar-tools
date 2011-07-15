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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

import de.chdev.artools.loga.lang.KeywordLoader;
import de.chdev.artools.loga.model.ActiveLinkLogElement;
import de.chdev.artools.loga.model.CheckRunType;
import de.chdev.artools.loga.model.ControlLogElement;
import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;
import de.chdev.artools.loga.model.OperationLogElement;

public class ActlController implements ILogController{

//	private static ActlController instance = null;
	
	// internal control vars
	private boolean prefixAvailable = false;
	public static final String PREFIX = "<ACTL>";
	private String tempBuffer = null;
	private LogElement lastElement = null;
	
	MainController mainController;

	private OperationController operationController;
//	private List<ActiveLinkLogElement> activeLinkLogElementList = new LinkedList<ActiveLinkLogElement>();

	private Configuration keywords;
private Pattern patternEventStart;
private Pattern patternEventStop;
private Pattern patternGuideStart;
private Pattern patternGuideStop;
private Pattern patternActlOk;
private Pattern patternActlNotOk;
private Pattern patternActlProceed;
private Pattern patternComment;
private Pattern patternActlFailed;
private Pattern patternActlStart;

	public ActlController(Configuration keywords, MainController mainController, OperationController operationController) {
		this.keywords = keywords;
		this.mainController = mainController;
		this.operationController = operationController;
		
		String regexKey;
		// Define Event Start Pattern
		regexKey = keywords.getString("actl.event.start");
		patternEventStart = Pattern.compile(regexKey);
		// Define Event Stop Pattern
		regexKey = keywords.getString("actl.event.stop");
		patternEventStop = Pattern.compile(regexKey);
		// Define AL Guide Start Pattern
		regexKey = keywords.getString("actl.guide.start");
		patternGuideStart = Pattern.compile(regexKey);		
		// Define AL Guide Stop Pattern
		regexKey = keywords.getString("actl.guide.stop");
		patternGuideStop = Pattern.compile(regexKey);		
		// Define AL OK Pattern
		regexKey = keywords.getString("actl.check.ok");
		patternActlOk = Pattern.compile(regexKey);	
		// Define AL Not OK Pattern
		regexKey = keywords.getString("actl.check.nok");
		patternActlNotOk = Pattern.compile(regexKey);			
		// Define AL Proceed Pattern
		regexKey = keywords.getString("actl.check.proceed");
		patternActlProceed = Pattern.compile(regexKey);			
		// Define Comment Pattern
		regexKey = keywords.getString("actl.comment");
		patternComment = Pattern.compile(regexKey);				
		// Define AL Failed Pattern
		regexKey = keywords.getString("actl.check.fail");
		patternActlFailed = Pattern.compile(regexKey);				
		// Define AL Start Pattern
		regexKey = keywords.getString("actl.start");
		patternActlStart = Pattern.compile(regexKey);	
		
	}

//	public static ActlController getInstance() {
//		if (instance == null) {
//			instance = new ActlController();
//		}
//		return instance;
//	}
	
	public int setLogLine(String text, int lineNumber){
		String textWoPrefix = removePrefix(text);
		
		boolean result = false;
		
		if (!result) result = checkActlStart(textWoPrefix, lineNumber);
		if (!result) result = checkActlFailed(textWoPrefix, lineNumber);
		if (!result) result = checkComment(textWoPrefix, lineNumber);
		if (!result) result = checkEventStart(textWoPrefix, lineNumber);
		if (!result) result = checkEventStop(textWoPrefix, lineNumber);
		if (!result) result = checkOperationStart(textWoPrefix, lineNumber);
		if (!result) result = checkGuideStart(textWoPrefix, lineNumber);
		if (!result) result = checkGuideStop(textWoPrefix, lineNumber);
		if (!result) result = checkActlOk(textWoPrefix, lineNumber);
		if (!result) result = checkActlNok(textWoPrefix, lineNumber);
		if (!result) result = checkActlProceed(textWoPrefix, lineNumber);
		// Else
		if (!result) result = checkAttribute(textWoPrefix, lineNumber);
		
//		System.out.println(lineNumber+" \t"+textWoPrefix);
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
	
	private boolean checkActlStart(String textWoPrefix, int lineNumber){
		Matcher m = patternActlStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
//		if (textWoPrefix.trim().startsWith(keywords.getString("actl.start"))){
			
			// Check if previous open operations exists and close it
			LogElement logElement = mainController.getLastOpenLogElement();
			if (logElement instanceof OperationLogElement){
				logElement.setEndLineNumber(lineNumber-1);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			// Check if previous open Active Link exists and close it
			if (logElement instanceof ActiveLinkLogElement){
				logElement.setEndLineNumber(lineNumber-1);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			
			// Extract AL name and execution order
			String nameWithEo = textWoPrefix.trim().substring(keywords.getString("actl.start").length()-2);
			String executionOrderText = nameWithEo.substring(nameWithEo.lastIndexOf("("));
			String name = nameWithEo.substring(0, nameWithEo.length()-executionOrderText.length()-1).trim();
			int executionOrder=Integer.parseInt(executionOrderText.substring(1, executionOrderText.length()-1));
			
			// Create new Active Link Element
			ActiveLinkLogElement alLogElement = new ActiveLinkLogElement();
			alLogElement.setElementAlias("ACTL");
			alLogElement.setElementType(LogElementType.ELEMENT);
			alLogElement.setExecutionOrder(executionOrder);
			alLogElement.setName(name);
			alLogElement.setParentLogElement(mainController.getLastOpenLogElement());
			alLogElement.setStartLineNumber(lineNumber);
			alLogElement.setValid(true);
			alLogElement.setText(textWoPrefix.trim());
			mainController.openNewLogElement(alLogElement);
			
			lastElement = alLogElement;
			
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkActlFailed(String textWoPrefix, int lineNumber){
		Matcher m = patternActlFailed.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
//		if (textWoPrefix.trim().equals(keywords.getString("actl.check.fail"))){
			
			LogElement logElement = mainController.getLastOpenLogElement();
			
			// Update last Active Link Element
			if (logElement instanceof ActiveLinkLogElement){
				logElement.setEndLineNumber(lineNumber);
				((ActiveLinkLogElement) logElement).setCheckRun(CheckRunType.FAILED);
				mainController.closeLastLogElement();
				lastElement = logElement;
			} else {
				System.out.println("Error during AL close");
			}
						
			return true;
		} else {
			return false;
		}
	}
		
	private boolean checkComment(String textWoPrefix, int lineNumber){
		Matcher m = patternComment.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
//		if (textWoPrefix.trim().startsWith("/*") && textWoPrefix.trim().endsWith("*/")){
			
			tempBuffer = textWoPrefix.trim();
			
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEventStart(String textWoPrefix, int lineNumber){
		Matcher m = patternEventStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement parent = mainController.getLastOpenLogElement();
			
			// Get start timestamp
			String dateValue = (tempBuffer!=null)?tempBuffer.substring(3,tempBuffer.length()-3):null;
			Long timestamp = 0L;
			if (dateValue!=null){
				timestamp = mainController.convertToTimestamp(dateValue, PREFIX);
			}
			
			// Create event name
			String name = "Event - "+textWoPrefix.substring(textWoPrefix.lastIndexOf("-")+1);
			
			
			// Create new ControlLogElement
			ControlLogElement clElement = new ControlLogElement();
			clElement.setElementAlias("ACTL");
			clElement.setElementType(LogElementType.EVENT);
			clElement.setName(name);
			clElement.setParentLogElement(parent);
			clElement.setStartLineNumber(lineNumber);
			clElement.setStartTimestamp(timestamp);
			clElement.setValid(true);
			clElement.setText(textWoPrefix.trim());

			// Close parent Active Link Element if exist
			if (parent instanceof ActiveLinkLogElement){
				parent.setEndLineNumber(lineNumber-2);
				mainController.closeLastLogElement();
//				parent = MainController.getInstance().getLastOpenLogElement();
			}

			mainController.openNewLogElement(clElement);
			lastElement = clElement;
									
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkEventStop(String textWoPrefix, int lineNumber){
		Matcher m = patternEventStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){

			LogElement logElement = mainController.getLastOpenLogElement();
			
			// Close active operation element
			if (logElement instanceof OperationLogElement){
				logElement.setEndLineNumber(lineNumber-2);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			// Close active Active Link elements
			if (logElement instanceof ActiveLinkLogElement){
				logElement.setEndLineNumber(lineNumber-2);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			
			// Get end timestamp
			String dateValue = (tempBuffer!=null)?tempBuffer.substring(3,tempBuffer.length()-3):null;
			Long timestamp = 0L;
			if (dateValue!=null){
				timestamp = mainController.convertToTimestamp(dateValue, PREFIX);
			}
			
			// If this is a close window event, all parent log elements will be closed
			// until an "Open Window" operation occurs or until root
			if (textWoPrefix.endsWith("On Window Close")){
				boolean openWindowReached = false;
				while (openWindowReached == false && logElement!=null){
					if (logElement instanceof OperationLogElement){
						if (logElement.getText().startsWith("Open")){
							openWindowReached = true;
						} else {
							logElement.setEndLineNumber(lineNumber); 
							mainController.closeLastLogElement();
							logElement = mainController.getLastOpenLogElement();	
						}
					} else {
						if (logElement instanceof ControlLogElement){
							((ControlLogElement) logElement).setEndTimestamp(timestamp);
						}
						logElement.setEndLineNumber(lineNumber);
						mainController.closeLastLogElement();
						logElement = mainController.getLastOpenLogElement();
					}
				}
			}
						
			
			if (logElement instanceof ControlLogElement){
				ControlLogElement clElement = (ControlLogElement)logElement;
				clElement.setEndLineNumber(lineNumber);
				clElement.setEndTimestamp(timestamp);
				mainController.closeLastLogElement();
				lastElement = clElement;
			} else {
				System.out.println("Error during event close in line "+lineNumber);
			}
									
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkOperationStart(String textWoPrefix, int lineNumber){
		boolean result = false;
		// Operation start need always a prefix
		if (prefixAvailable){
			// Try to extract the operation number
			String numberCode;
			int operationNumber;
			if (textWoPrefix.trim().length()>3 && textWoPrefix.contains(":")){
				numberCode = textWoPrefix.substring(0,textWoPrefix.indexOf(":")).trim();
				try{
					operationNumber = Integer.parseInt(numberCode);

					LogElement logElement = mainController.getLastOpenLogElement();
					// Close active operation element
					if (logElement instanceof OperationLogElement){
						logElement.setEndLineNumber(lineNumber-1);
						mainController.closeLastLogElement();
						logElement = mainController.getLastOpenLogElement();
					}
					
					logElement = operationController.setNewOperation(operationNumber, textWoPrefix.substring(textWoPrefix.indexOf(":")+1).trim(), lineNumber);
					lastElement = logElement;
					
					result = true;
				} catch (NumberFormatException nfe){
//					nfe.printStackTrace();
				}
			}
		}
		return result;
	}
	
	private boolean checkGuideStart(String textWoPrefix, int lineNumber){
		Matcher m = patternGuideStart.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement parent = mainController.getLastOpenLogElement();					
			
			// Create event name
			String name = "Guide - "+textWoPrefix.substring(textWoPrefix.indexOf(":")+1);
			
			
			// Create new ControlLogElement
			ControlLogElement clElement = new ControlLogElement();
			clElement.setElementAlias("ACTL");
			clElement.setElementType(LogElementType.EVENT);
			clElement.setName(name);
			clElement.setParentLogElement(parent);
			clElement.setStartLineNumber(lineNumber);
			clElement.setValid(true);
			clElement.setText(textWoPrefix);
			mainController.openNewLogElement(clElement);	
			lastElement=clElement;
									
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkGuideStop(String textWoPrefix, int lineNumber){
		Matcher m = patternGuideStop.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement logElement = mainController.getLastOpenLogElement();
			// Close active operation element
			if (logElement instanceof OperationLogElement){
				logElement.setEndLineNumber(lineNumber-2);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			// Close active Active Link elements
			if (logElement instanceof ActiveLinkLogElement){
				logElement.setEndLineNumber(lineNumber-2);
				mainController.closeLastLogElement();
				logElement = mainController.getLastOpenLogElement();
			}
			
			if (logElement instanceof ControlLogElement){
				ControlLogElement clElement = (ControlLogElement)logElement;
				clElement.setEndLineNumber(lineNumber);
				mainController.closeLastLogElement();
				lastElement = clElement;
			} else {
				System.out.println("Error during guide event close in line "+lineNumber);
			}				
									
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkActlOk(String textWoPrefix, int lineNumber){
		Matcher m = patternActlOk.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement parent = mainController.getLastOpenLogElement();					
			
			if (parent instanceof ActiveLinkLogElement){
				((ActiveLinkLogElement) parent).setCheckRun(CheckRunType.RUNIF);
			} else {
				System.out.println("AL Qualification passed could not be set");
			}
			
			lastElement = parent;
			
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkActlNok(String textWoPrefix, int lineNumber){
		Matcher m = patternActlNotOk.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			
			LogElement parent = mainController.getLastOpenLogElement();					
			
			if (parent instanceof ActiveLinkLogElement){
				((ActiveLinkLogElement) parent).setCheckRun(CheckRunType.RUNELSE);
			} else {
				System.out.println("AL run ELSE could not be set");
			}
			
			lastElement = parent;
									
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkAttribute(String textWoPrefix, int lineNumber){
		
		boolean result = false;
		
		LogElement logElement = mainController.getLastOpenLogElement();
		if (logElement instanceof OperationLogElement){
			operationController.setAttributeLine(textWoPrefix, lineNumber);
			result = true;
		} else if (logElement instanceof ControlLogElement){
			assignControlAttribute(textWoPrefix, lineNumber);
			result = true;
		} else {
			System.out.println("Attribute can not be assigned (Line: "+lineNumber+")");
		}
		
		return result;
	}
	
	private boolean assignControlAttribute(String textWoPrefix, int lineNumber){
		
		return true;
	}
	
	private boolean checkActlProceed(String textWoPrefix, int lineNumber) {
		Matcher m = patternActlProceed.matcher(textWoPrefix.trim());
		boolean valid = m.matches();
		if (valid){
			// Ignore proceed line
									
			return true;
		} else {
			return false;
		}
	}
	
//	public void printHierarchie(){
//		List<LogElement> logList = MainController.getInstance().getLogElementList();
//		for (LogElement le : logList){
//			LogElement parent = le.getParentLogElement();
//			System.out.println("Parent: "+((parent!=null)?parent.getName():"n/a")+" ("+le.getName()+" / "+le.getStartLineNumber()+")");;
//		}
//	}
}
