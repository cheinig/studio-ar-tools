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

package de.chdev.artools.loga.handler;

import org.eclipse.jface.action.Action;

import de.chdev.artools.loga.editor.LogaEditor;
import de.chdev.artools.loga.model.LogElement;

/**
 * This action class will switch to the text editor and go to the provided line.
 * 
 * @author cheinig
 *
 */
public class TreeContextToTextAction extends Action {

	private final LogElement logElement;
	private final LogaEditor editor;
	
	/**
	 * The action needs the log element and the multipage editor.
	 * 
	 * @param logElement the log element to get the linenumber information
	 * @param editor the multipage editor to switch the current page
	 */
	public TreeContextToTextAction(LogElement logElement, LogaEditor editor) {
		this.logElement = logElement;
		this.editor = editor;
		setText("Goto line "+logElement.getStartLineNumber());
	}

	@Override
	public void run() {
		editor.gotoLine(logElement.getStartLineNumber());
		super.run();
	}
	
}
