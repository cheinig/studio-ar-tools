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

package de.chdev.artools.loga.model;

import java.util.ArrayList;

public class LogElement{
	
	protected LogElement parentLogElement;
	
	protected ArrayList<LogElement> childLogElements = new ArrayList<LogElement>();

	protected int startLineNumber;

	protected int endLineNumber;

	protected String elementAlias;

	public LogElement getParentLogElement() {
		return parentLogElement;
	}

	public void setParentLogElement(LogElement parentLogElement) {
		// remove this log element from the child list of the old parent log element
		if (this.parentLogElement!=null){
			this.parentLogElement.removeChildLogElement(this);
		}
		this.parentLogElement = parentLogElement;
		if (parentLogElement != null)
			parentLogElement.addChildLogElement(this);
	}
	
	protected void addChildLogElement(LogElement childLogElement){
		childLogElements.add(childLogElement);
	}
	
	protected void removeChildLogElement(LogElement childLogElement){
		if (childLogElements.contains(childLogElement)){
			childLogElements.remove(childLogElement);
		}
	}

	public ArrayList<LogElement> getChildLogElementList(){
		return childLogElements;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

	public String getElementAlias() {
		return elementAlias;
	}

	public void setElementAlias(String elementAlias) {
		this.elementAlias = elementAlias;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public LogElementType getElementType() {
		return elementType;
	}

	public void setElementType(LogElementType elementType) {
		this.elementType = elementType;
	}

	protected String text;

	protected String name;

	protected boolean valid;

	protected LogElementType elementType;

}

