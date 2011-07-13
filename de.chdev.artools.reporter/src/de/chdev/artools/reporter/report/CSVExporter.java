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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * @author Christoph Heinig
 * 
 */
public class CSVExporter implements IExporter {

	private String name;

	private String filename;

	private char separator;

	private char stringChar;

	public CSVExporter() {
		name = "CSV Writer"; //$NON-NLS-1$
		separator = ';';
		stringChar = '\"';
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.chdev.artools.reporter.report.IExporter#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.chdev.artools.reporter.report.IExporter#exportData(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public boolean exportData(IReportObject[][] reportMatrix) {
		// List<String[]> exportList = new ArrayList<String[]>();

		boolean result = false;

		// Only execute if filename is provided
		if (filename != null && filename.length() > 0) {
			File file = new File(filename);
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);

				for (int i = 0; i < reportMatrix.length; i++) {
					IReportObject[] row = reportMatrix[i];

					// Convert IReportObject Array to String Array
					String[] stringRow = new String[row.length];
					for (int j = 0; j < row.length; j++) {
						stringRow[j] = row[j].toString();
					}

					// Start writing csv process
					CSVWriter csvWriter = new CSVWriter(writer, separator,
							stringChar);
					csvWriter.writeNext(stringRow);
				}

				writer.flush();
				writer.close();
				result = true;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.chdev.artools.reporter.interfaces.IExporter#setMonitor(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	@Override
	public void setMonitor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.chdev.artools.reporter.interfaces.IExporter#isNeedFilename()
	 */
	@Override
	public boolean isNeedFilename() {
		// A CSV export always need a filename
		return true;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
