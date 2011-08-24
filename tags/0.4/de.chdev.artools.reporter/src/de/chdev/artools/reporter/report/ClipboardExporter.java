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

package de.chdev.artools.reporter.report;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;

public class ClipboardExporter implements IExporter{

	private String name = "Clipboard Exporter (text)"; //$NON-NLS-1$
	private IProgressMonitor monitor = null;
	private String text;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public boolean exportData(IReportObject[][] stringMatrix) {
		text = getTextFromMatrix(stringMatrix);
		
		// Copy to clipboard
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
				clipboard.setContents(new Object[] { text.toString() },
			              new Transfer[] { TextTransfer.getInstance() });
			}
			
		});

		return true;
	}

	private String getTextFromMatrix(IReportObject[][] matrix) {
		StringBuffer clipText = new StringBuffer();
		// Calculate maximum column width
		ArrayList<Integer> columnWidth = new ArrayList<Integer>();
		for (int rowIndex=0; rowIndex<matrix.length;rowIndex++){
			IReportObject[] row = matrix[rowIndex];
			// Make sure enough elements are int the columnWidth list
			for (;row.length>columnWidth.size();){
				columnWidth.add(null);
			}
			for (int columnIndex=0; columnIndex<row.length; columnIndex++){
				if (columnWidth.get(columnIndex)==null || columnWidth.get(columnIndex)<row[columnIndex].toString().length()){
					columnWidth.set(columnIndex, new Integer(row[columnIndex].toString().length()));
				}
			}
		}
		
		// Create formatted text
		for (IReportObject[] row : matrix){
			for (int columnIndex=0; columnIndex<row.length;columnIndex++){
				IReportObject cell = row[columnIndex];
				StringBuffer tempText = new StringBuffer();
				tempText.append(cell.toString());
				for (int fillIndex=tempText.length();fillIndex<columnWidth.get(columnIndex);fillIndex++){
					tempText.append(" "); //$NON-NLS-1$
				}
				tempText.append("\t"); //$NON-NLS-1$
				clipText.append(tempText);
			}
			clipText.append("\n"); //$NON-NLS-1$
		}
		return clipText.toString();
	}

	@Override
	public boolean isNeedFilename() {
		return false;
	}

	public void setFilename(String filename){
		// Not needed
	}
}
