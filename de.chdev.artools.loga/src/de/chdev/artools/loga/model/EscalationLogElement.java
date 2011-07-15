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

public class EscalationLogElement extends ServerLogElement {

	private CheckRunType checkRun;

	public void setCheckRun(CheckRunType checkRun) {
		this.checkRun = checkRun;
	}

	public CheckRunType getCheckRun() {
		return checkRun;
	}
}
