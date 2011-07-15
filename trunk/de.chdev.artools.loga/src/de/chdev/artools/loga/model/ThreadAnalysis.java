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
