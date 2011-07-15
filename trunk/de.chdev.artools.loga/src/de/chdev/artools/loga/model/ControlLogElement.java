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
public class ControlLogElement extends LogElement{
	protected Long startTimestamp;

	protected Long endTimestamp;

	protected String operationType;

	protected String operationSchema;

	protected String operationReference;

	protected String onScreenType;
	
	private boolean phase2Available = false;
	private boolean phase3Available = false;
	private String threadId;

	private String rpcId;

	private String queue;

	private String user;

	public void setStartTimestamp(long startTimestamp){
		this.startTimestamp = startTimestamp;
	}

	public Long getStartTimestamp(){
		return startTimestamp;
	}

	public void setEndTimestamp(long endTimestamp){
		this.endTimestamp = endTimestamp;
	}

	public Long getEndTimestamp(){
		return endTimestamp;
	}

	public void setOperationType(String operationType){
		this.operationType = operationType;
	}

	public String getOperationType(){
		return operationType;
	}

	public void setOperationSchema(String operationSchema){
		this.operationSchema = operationSchema;
	}

	public String getOperationSchema(){
		return operationSchema;
	}

	public void setOperationReference(String operationReference){
		this.operationReference = operationReference;
	}

	public String getOperationReference(){
		return operationReference;
	}

	public void setOnScreenType(String onScreenType){
		this.onScreenType = onScreenType;
	}

	public String getOnScreenType(){
		return onScreenType;
	}

	public void setPhase2Available(boolean phase2Available) {
		this.phase2Available = phase2Available;
	}

	public boolean isPhase2Available() {
		return phase2Available;
	}

	public void setPhase3Available(boolean phase3Available) {
		this.phase3Available = phase3Available;
	}

	public boolean isPhase3Available() {
		return phase3Available;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setRpcId(String rpcId) {
		this.rpcId = rpcId;
	}

	public String getRpcId() {
		return rpcId;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getQueue() {
		return queue;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

}

