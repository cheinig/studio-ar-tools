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

/**
 * 
 */
package de.chdev.artools.loga.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.registry.osgi.Activator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.chdev.artools.loga.editor.LogaEditor;
import de.chdev.artools.loga.editor.LogaEditorInput;

/**
 * @author Christoph Heinig
 * 
 */
public class OpenLogaEditorHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Open Editor");

		System.out.println("Load file");
		FileDialog fileDialog = new FileDialog(
				HandlerUtil.getActiveShell(event));
		String filename = fileDialog.open();

		File file = new File(filename);

		if (file.canRead() == true) {

			IEditorInput input = new LogaEditorInput(file);
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getActivePage();
			try {
				page.openEditor(input,
						"de.chdev.artools.loga.editor.logaeditor");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			MessageDialog.openError(null, "File not readable",
					"The file is not readable");
		}
		return null;
	}

}
