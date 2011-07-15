package de.chdev.artools.loga.model;
public class SetCharacteristicsOperation extends OperationLogElement{
	protected String fieldName;

	protected int fieldId;

	protected String operationText;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public String getOperationText() {
		return operationText;
	}

	public void setOperationText(String operationText) {
		this.operationText = operationText;
	}



}

