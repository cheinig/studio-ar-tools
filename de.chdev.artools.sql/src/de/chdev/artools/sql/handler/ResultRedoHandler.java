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
package de.chdev.artools.sql.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.chdev.artools.sql.editor.SQLEditor;

/**
 * @author Christoph Heinig
 *
 */
public class ResultRedoHandler extends AbstractHandler {

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	
	IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	if (activeEditor instanceof SQLEditor){
	    SQLEditor sqlEditor = (SQLEditor)activeEditor;
	    TextCellEditor cellEditor = sqlEditor.getTextCellEditor();
	    if (cellEditor!=null && cellEditor.isActivated() && cellEditor.isRedoEnabled()){
		cellEditor.performRedo();
	    }
	}
	
	return null;
    }

}
