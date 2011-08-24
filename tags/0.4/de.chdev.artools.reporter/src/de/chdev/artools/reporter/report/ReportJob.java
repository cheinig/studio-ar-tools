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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;

/**
 * @author Christoph Heinig
 *
 */
public class ReportJob extends Job{
    
    private String reporterName;
    private String exporterName;
    private String fileName;
    private IReporter reporter;
    private IExporter exporter;
    private Object[] dataArray;

    /**
     * @param name
     */
    public ReportJob(String name) {
	super(name);
	// TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
	reporter = ReportRegistry.getInstance().getReporter(reporterName);
	reporter.setMonitor(monitor);
	exporter = ExportRegistry.getInstance().getExporter(exporterName);
	exporter.setMonitor(monitor);
	
	if (monitor.isCanceled()==false){
	    IReportObject[][] data = reporter.getDataForObject(getDataArray());
		if (monitor.isCanceled()==false){
			exporter.setFilename(fileName);
		    boolean result = exporter.exportData(data);
		    if (monitor.isCanceled()==false){
			return Status.OK_STATUS;			
		    }
		}
	}
	return Status.CANCEL_STATUS;
    }

    /**
     * @param reporter the reporter to set
     */
    public void setReporter(IReporter reporter) {
	this.reporter = reporter;
    }

    /**
     * @return the reporter
     */
    public IReporter getReporter() {
	return reporter;
    }

    /**
     * @param exporter the exporter to set
     */
    public void setExporter(IExporter exporter) {
	this.exporter = exporter;
    }

    /**
     * @return the exporter
     */
    public IExporter getExporter() {
	return exporter;
    }

    /**
     * @param dataArray the dataArray to set
     */
    public void setDataArray(Object[] dataArray) {
	this.dataArray = dataArray;
    }

    /**
     * @return the dataArray
     */
    public Object[] getDataArray() {
	return dataArray;
    }

    /**
     * @param reporterName the reporterName to set
     */
    public void setReporterName(String reporterName) {
	this.reporterName = reporterName;
    }

    /**
     * @return the reporterName
     */
    public String getReporterName() {
	return reporterName;
    }

    /**
     * @param exporterName the exporterName to set
     */
    public void setExporterName(String exporterName) {
	this.exporterName = exporterName;
    }

    /**
     * @return the exporterName
     */
    public String getExporterName() {
	return exporterName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
	return fileName;
    }

}
