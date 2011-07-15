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
		this.parentLogElement = parentLogElement;
		if (parentLogElement != null)
			parentLogElement.addChildLogElement(this);
	}
	
	protected void addChildLogElement(LogElement childLogElement){
		childLogElements.add(childLogElement);
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

