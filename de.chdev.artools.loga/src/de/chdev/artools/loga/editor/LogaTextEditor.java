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
	
	/**
	 * Set the first line of the document to the provided line number. The cursor will be set to the first position in this line.
	 * @param lineNumber the linenumber to jump to
	 */
	public void gotoLine(int lineNumber){
		// Set the top line
		getSourceViewer().setTopIndex(lineNumber-1);
		// Get offset index from the beginning of the document to the current line
		int topIndexStartOffset = getSourceViewer().getTopIndexStartOffset();
		// Set the cursor to the new  top line
		getSourceViewer().setSelectedRange(topIndexStartOffset, 0);
	}
	
}
