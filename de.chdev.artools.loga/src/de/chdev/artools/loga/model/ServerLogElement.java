package de.chdev.artools.loga.model;
public class ServerLogElement extends LogElement{
	protected long startTimestamp;

	protected long endTimestamp;

	protected String threadId;

	protected String rpcId;

	protected String queue;

	protected String user;

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId2) {
		this.threadId = threadId2;
	}

	public String getRpcId() {
		return rpcId;
	}

	public void setRpcId(String rpcId2) {
		this.rpcId = rpcId2;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}



}

