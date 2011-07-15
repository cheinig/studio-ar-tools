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

import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;
import de.chdev.artools.loga.model.OperationLogElement;

public class OperationController {
private MainController mainController;

//	private static OperationController instance = null;

	public OperationController(MainController mainController) {
		this.mainController = mainController;
	}

//	public static OperationController getInstance() {
//		if (instance == null) {
//			instance = new OperationController();
//		}
//		return instance;
//	}
	
	public LogElement setNewOperation(int number, String operationText, int lineNumber){
		
		LogElement parent = mainController.getLastOpenLogElement();
		
		OperationLogElement olElement = new OperationLogElement();
		olElement.setElementAlias("ACTL");
		olElement.setElementType(LogElementType.OPERATION);
		olElement.setName(operationText.trim());
		olElement.setNumber(number);
		olElement.setParentLogElement(parent);
		olElement.setStartLineNumber(lineNumber);
		olElement.setText(operationText);
		olElement.setValid(true);
		
		mainController.openNewLogElement(olElement);
		
		return olElement;
	}
	
	public int setNewFltrOperation(int number, String operationText, int lineNumber){
		
		LogElement parent = mainController.getLastOpenLogElement();
		
		OperationLogElement olElement = new OperationLogElement();
		olElement.setElementAlias("FLTR");
		olElement.setElementType(LogElementType.OPERATION);
		olElement.setName(operationText.trim());
		olElement.setNumber(number);
		olElement.setParentLogElement(parent);
		olElement.setStartLineNumber(lineNumber);
		olElement.setText(operationText);
		olElement.setValid(true);
		
		mainController.openNewLogElement(olElement);
		
		return 0;
	}
	
	public int setAttributeLine(String text, int lineNumber) {
		return 0;
	}
}
