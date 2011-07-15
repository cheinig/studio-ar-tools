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

package de.chdev.artools.loga.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import de.chdev.artools.loga.controller.ILogController;

public class ParseWorker {

	private Reader reader;
	private Map<String, ILogController> controllerMap;

	public ParseWorker(Reader reader, Map<String, ILogController> controllerMap) {
		this.reader = reader;
		this.controllerMap = controllerMap;
	}

	public void run() {
		try {
			BufferedReader bufReader = new BufferedReader(reader);
			String line = bufReader.readLine();
			int lineNumber = 1;
			String logType = "";
			String logTypeBak = "";
			while (line != null) {
				// Check if a keyword may exist
				if (line.length() > 5) {
					logType = line.substring(0, 6);
				} else if (logTypeBak != null && logTypeBak.length() > 0) {
					logType = logTypeBak;
				} else {
					System.out.println("Error in line " + lineNumber + ": "
							+ line);
				}

				// Run controller action
				int result = chooseController(logType, line, lineNumber);

				// If a result <0 is returned, no valid keyword exists
				// If a backup keyword is present, relate the line to the backup
				// keyword
				// If a result >=0 is returned, the keyword is valid and will be
				// stored
				if (result >= 0) {
					logTypeBak = logType;
				} else if (logTypeBak != null && logTypeBak.length() > 0
						&& !logTypeBak.equals(logType)) {
					chooseController(logTypeBak, line, lineNumber);
				} else {
					System.out.println("Error in line " + lineNumber + ": "
							+ line);
				}

				// Read new line
				line = bufReader.readLine();
				lineNumber++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int chooseController(String logType, String line, int lineNumber) {
		int result = -1;
		if (controllerMap.containsKey(logType)){
			result = controllerMap.get(logType).setLogLine(line, lineNumber);
		}		
		return result;
	}
}
