package de.chdev.artools.loga.model;

public class ApiLogElement extends ServerLogElement {

	protected String apiCall;

	public void setApiCall(String apiCall) {
		this.apiCall = apiCall;
	}

	public String getApiCall() {
		return apiCall;
	}

}
