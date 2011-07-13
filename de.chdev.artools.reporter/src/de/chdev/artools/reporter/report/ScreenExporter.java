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
package de.chdev.artools.reporter.report;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.ui.ScreenExporterDialog;

/**
 * @author Christoph Heinig
 *
 */
public class ScreenExporter implements IExporter{

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IExporter#exportData(java.lang.String, java.lang.String[][])
     */
    @Override
    public boolean exportData(IReportObject[][] reportMatrix) {
	final IReportObject[][] matrix=reportMatrix;
//	IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//	Shell shell = workbenchWindow.getShell();

	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	    public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();
		Shell shell = display.getActiveShell();
	ScreenExporterDialog dialog = new ScreenExporterDialog(shell);
//	ScreenExporterDialog dialog = new ScreenExporterDialog(null);
	dialog.setTableData(matrix);
	dialog.open();
	    }
	});

//	int result = dialog.open();
//	if (result==Dialog.OK){
//	    return true;
//	} else {
	    return false;
//	}
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IExporter#getName()
     */
    @Override
    public String getName() {
	return "Screen"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IExporter#setMonitor(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setMonitor(IProgressMonitor monitor) {
	// TODO Auto-generated method stub
	
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IExporter#isNeedFilename()
     */
    @Override
    public boolean isNeedFilename() {
	// No filename is needed for screen export
	return false;
    }

	@Override
	public void setFilename(String filename) {
		// TODO Auto-generated method stub
		
	}

}
