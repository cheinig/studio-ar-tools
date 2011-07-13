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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.chdev.artools.reporter.interfaces.IReporter;

/**
 * @author Christoph Heinig
 * 
 */
public class ReportRegistry {

    private static ReportRegistry instance = null;

    private Map<String, IReporter> reporterMap;

    private ReportRegistry() {
	reporterMap = new HashMap<String, IReporter>();
    }

    private void loadAllReporter() {
	try {
	    IConfigurationElement[] config = Platform.getExtensionRegistry()
		    .getConfigurationElementsFor(
			    "de.chdev.artools.reporter.reporter"); //$NON-NLS-1$
	    for (IConfigurationElement e : config) {
		final Object o = e.createExecutableExtension("class"); //$NON-NLS-1$
		if (o instanceof IReporter) {
		    registerReporter((IReporter) o);
		}
	    }
	} catch (Exception ex) {
	    System.out.println(ex.getMessage());
	}
    }

    public static ReportRegistry getInstance() {
	if (instance == null) {
	    instance = new ReportRegistry();
	    instance.loadAllReporter();
	}
	return instance;
    }

    public void registerReporter(IReporter reporter) {
	reporterMap.put(reporter.getName(), reporter);
    }

    public String[] getReporterNames() {
	String[] reporterNames = new String[reporterMap.size()];
	reporterMap.keySet().toArray(reporterNames);
	return reporterNames;
    }

    public IReporter getReporter(String name) {
	return reporterMap.get(name);
    }
}
