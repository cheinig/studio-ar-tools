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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import de.chdev.artools.loga.model.LogElement;

/**
 * This class will open an AR object within its editor. 
 * The object to open will be calculated by the provided log element.
 *  
 * @author cheinig
 *
 */
public class TreeContextOpenAction extends Action {
	
	private final LogElement logElement;

	/**
	 * Create a new action which calculates the AR object which should be opened and set the action name with the name of the object.
	 * 
	 * @param logElement The starting log element to calculate the AR object which should be opened
	 */
	public TreeContextOpenAction(LogElement logElement) {
		this.logElement = logElement;

		setText("Open Element "+logElement.getName());
	}
	
	@Override
	public void run() {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Open AR Element", "This action will open the AR element "+logElement.getName());
		super.run();
	}

}
