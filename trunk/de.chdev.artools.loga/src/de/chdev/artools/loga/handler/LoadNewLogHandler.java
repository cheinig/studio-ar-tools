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
