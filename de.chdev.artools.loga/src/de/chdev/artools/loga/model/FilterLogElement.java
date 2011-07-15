package de.chdev.artools.loga.model;
public class FilterLogElement extends ServerLogElement{
	protected int phase;

	protected boolean deferred;

	protected int executionOrder;

	protected CheckRunType checkRun;

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public boolean isDeferred() {
		return deferred;
	}

	public void setDeferred(boolean deferred) {
		this.deferred = deferred;
	}

	public int getExecutionOrder() {
		return executionOrder;
	}

	public void setExecutionOrder(int executionOrder) {
		this.executionOrder = executionOrder;
	}

	public CheckRunType getCheckRun() {
		return checkRun;
	}

	public void setCheckRun(CheckRunType checkRun) {
		this.checkRun = checkRun;
	}


}

