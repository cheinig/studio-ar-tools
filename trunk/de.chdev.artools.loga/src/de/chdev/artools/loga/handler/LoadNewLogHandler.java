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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.chdev.artools.loga.editor.LogaEditor;
import de.chdev.artools.loga.editor.LogaEditorInput;
import de.chdev.artools.loga.editor.LogaTextEditor;

public class LoadNewLogHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Load file");
		FileDialog fileDialog = new FileDialog(HandlerUtil.getActiveShell(event));
//		String filename = fileDialog.open();
		
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		if (editorPart instanceof LogaEditor){
			System.out.println("true");
			LogaEditor logaEditor = (LogaEditor) editorPart;
			LogaEditorInput editorInput = (LogaEditorInput)logaEditor.getEditorInput();
//			editorInput.updateTestString();
//			logaEditor.reloadPages();
		} 
		
		return null;
	}

}
