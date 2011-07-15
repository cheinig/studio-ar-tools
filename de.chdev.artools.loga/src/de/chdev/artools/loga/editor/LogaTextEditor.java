package de.chdev.artools.loga.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class LogaTextEditor extends TextEditor{
	
	private LogaTextEditor editor;

	public LogaTextEditor(){
		super();
//		colorManager = new ColorManager();
//		setSourceViewerConfiguration(new SQLConfiguration(colorManager));
		setDocumentProvider(new LogaDocumentProvider());
		editor = this;
	}
	
}
