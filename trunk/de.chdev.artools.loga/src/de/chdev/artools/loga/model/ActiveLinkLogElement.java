package de.chdev.artools.loga.model;
public class ActiveLinkLogElement extends LogElement{
	protected int executionOrder;

	protected CheckRunType checkRun;

  public void setExecutionOrder(int executionOrder){
  	this.executionOrder = executionOrder;
  }
	public int getExecutionOrder(){
		return executionOrder;
	}

	public void setCheckRun(CheckRunType checkRun){
		this.checkRun = checkRun;
	}

	public CheckRunType getCheckRun(){
		return checkRun;
	}

}

