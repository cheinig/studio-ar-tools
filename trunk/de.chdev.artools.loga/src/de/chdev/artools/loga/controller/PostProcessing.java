package de.chdev.artools.loga.controller;

import java.util.LinkedList;
import java.util.List;

import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;

public class PostProcessing {

	public void correctHierarchy(LinkedList<LogElement> logElementList, LinkedList<LogElement> callHierarchy){
		// List all post processing actions
		correctMissingCloseWindowEvents(logElementList, callHierarchy);
	}
	
	private void correctMissingCloseWindowEvents(LinkedList<LogElement> logElementList, LinkedList<LogElement> callHierarchy) {
//		LinkedList<LogElement> openDialogElementList = new LinkedList<LogElement>();
//		LogElement closeElement = null;
		for (LogElement element : logElementList){
//			// If element is an open dialog element, than put it on the stack
//			if (element.getElementType() == LogElementType.OPERATION && element.getName().equalsIgnoreCase("Open Dialog")){
//				openDialogElementList.push(element);
//			}
			// If a dialog is open a close condition will be searched
			if (element.getElementType() == LogElementType.OPERATION && element.getName().equalsIgnoreCase("Close Windows")){
				LogElement closeEvent = element;
				LogElement topEvent = null;
				// Search for the event above the close windows operation
				while (closeEvent!=null && closeEvent.getElementType()!=null && closeEvent.getElementType()!=LogElementType.EVENT){
					closeEvent = closeEvent.getParentLogElement();
				}
				// Search for the event above the close windows event
				if (closeEvent!=null){
					topEvent = closeEvent.getParentLogElement();
				}
				while (topEvent!=null && topEvent.getElementType()!=null && topEvent.getElementType()!=LogElementType.EVENT){
					topEvent = topEvent.getParentLogElement();
				}
				//Search and correct hierarchy
				for (LogElement moveElement : logElementList){
					if (moveElement.getStartLineNumber()>element.getEndLineNumber() && moveElement.getParentLogElement()==closeEvent){
						moveElement.setParentLogElement(topEvent);
					}
				}
			}
//			// If a close element is available, than pop following LogElements the level above the open dialog LogElement
//			if (closeElement!=null && closeElement!=element){
//				if (element.getParentLogElement()==closeElement.getParentLogElement()){
//					
//				}
//				if (element.getParentLogElement()==openDialogElementList.peek()){
//					
//					element.setParentLogElement(openDialogElementList.peek().getParentLogElement());
//				} else {
//					closeElement=null;
//					openDialogElementList.pop();
//				}
//			}
		}
	}
}
