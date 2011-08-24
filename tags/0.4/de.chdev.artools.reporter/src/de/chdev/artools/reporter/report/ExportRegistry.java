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

import de.chdev.artools.reporter.interfaces.IExporter;

/**
 * @author Christoph Heinig
 * 
 */
public class ExportRegistry {

    private static ExportRegistry instance = null;

    private Map<String, IExporter> exporterMap;

    private ExportRegistry() {
	exporterMap = new HashMap<String, IExporter>();
    }

    private void loadAllExporter() {
	try {
	    IConfigurationElement[] config = Platform.getExtensionRegistry()
		    .getConfigurationElementsFor(
			    "de.chdev.artools.reporter.exporter"); //$NON-NLS-1$
	    for (IConfigurationElement e : config) {
		final Object o = e.createExecutableExtension("class"); //$NON-NLS-1$
		if (o instanceof IExporter) {
		    registerExporter((IExporter) o);
		}
	    }
	} catch (Exception ex) {
	    System.out.println(ex.getMessage());
	}
    }

    public static ExportRegistry getInstance() {
	if (instance == null) {
	    instance = new ExportRegistry();
	    instance.loadAllExporter();
	}
	return instance;
    }

    public void registerExporter(IExporter exporter) {
	exporterMap.put(exporter.getName(), exporter);
    }

    public String[] getExporterNames() {
	String[] exporterNames = new String[exporterMap.size()];
	exporterMap.keySet().toArray(exporterNames);
	return exporterNames;
    }

    public IExporter getExporter(String exporterName) {
	return exporterMap.get(exporterName);
    }
}
