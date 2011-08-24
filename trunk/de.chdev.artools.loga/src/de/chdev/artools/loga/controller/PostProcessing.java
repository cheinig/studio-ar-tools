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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.chdev.artools.loga.model.LogElement;
import de.chdev.artools.loga.model.LogElementType;

/**
 * This class can be used to do modifications to the hierarchy after it is built.
 * It will try to correct some information or fill missing values.
 * Currently following optimizations are implemented:\n
 * - Try to fill missing timestamps by child elements 
 * 
 */
public class PostProcessing {

	/**
	 * The method should be called only ONCE after the log hierarchy is build.
	 * It will try to correct some information or fill missing values.
	 * Currently following optimizations are implemented:\n
	 * Try to fill missing timestamps by child elements 
	 * 
	 * @param logElementList The complete list of all log elements which should be corrected
	 */
	public void correctHierarchy(LinkedList<LogElement> logElementList){
		// List all post processing actions
		fillMissingTimestamps(logElementList);
	}

	/**
	 * This method tries to fill missing timestamp values by the child elements
	 * 
	 * @param logElementList complete list of all log elements
	 */
	private void fillMissingTimestamps(LinkedList<LogElement> logElementList){
		// Loop all log elements and calculate the timestamps
		for (LogElement element : logElementList){
			if (element.getParentLogElement()==null){
				fillMissingTimestampsLoop(element);
			}
		}
	}
	
	/**
	 * This method does the recursive loop to calculate the missing timestamps.
	 * It will also calculate the timestamps for all child elements. 
	 * If a timestamp is already set, the child elements will be calculated, but the original timestamps not replaced
	 * 
	 * @param element The element to calculate timestamps for
	 * @return original LogElement which calculated timestamps 
	 */
	private LogElement fillMissingTimestampsLoop(LogElement element){

		// If already timestamps are available, they should not be overwritten.
		boolean modifyStartTimestamp = true;
		boolean modifyEndTimestamp = true;
		if (element.getStartTimestamp()!=null){
			modifyStartTimestamp=false;
		}
		if (element.getEndTimestamp()!=null){
			modifyEndTimestamp=false;
		}
		
		// Loop all Child elements and start fill method recursive
		ArrayList<LogElement> childLogElementList = element.getChildLogElementList();
		if (childLogElementList!=null){
			for (LogElement childElement : childLogElementList){
				fillMissingTimestampsLoop(childElement);
				
				// If modify is allowed and start timestamp is empty or greater than the child start timestamp, than replace it
				if (modifyStartTimestamp==true && (element.getStartTimestamp()==null || (childElement.getStartTimestamp()!=null && element.getStartTimestamp()>childElement.getStartTimestamp()))){
					element.setStartTimestamp(childElement.getStartTimestamp());
				}
				// If modify is allowed and end timestamp is empty or smaller than the child end timestamp, than replace it
				if (modifyEndTimestamp == true && (element.getEndTimestamp()==null || (childElement.getEndTimestamp()!=null && element.getEndTimestamp()<childElement.getEndTimestamp()))){
					element.setEndTimestamp(childElement.getEndTimestamp());
				}
			}
		}
		
		return element;
	}
}
