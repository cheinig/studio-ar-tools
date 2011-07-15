package de.chdev.artools.loga.model;

public class ThreadAnalysis {

	private String threadId;
	private String queue;
	private Long runningTime;
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public String getQueue() {
		return queue;
	}
	public void setRunningTime(Long runningTime) {
		this.runningTime = runningTime;
	}
	public Long getRunningTime() {
		return runningTime;
	}
	
}
