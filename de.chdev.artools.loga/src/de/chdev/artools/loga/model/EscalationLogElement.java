package de.chdev.artools.loga.model;

public class EscalationLogElement extends ServerLogElement {

	private CheckRunType checkRun;

	public void setCheckRun(CheckRunType checkRun) {
		this.checkRun = checkRun;
	}

	public CheckRunType getCheckRun() {
		return checkRun;
	}
}
