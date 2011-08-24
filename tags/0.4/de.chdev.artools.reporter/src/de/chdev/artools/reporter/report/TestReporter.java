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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bmc.arsys.studio.model.store.IStore;

import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;

/**
 * @author Christoph Heinig
 * 
 */
public class TestReporter implements IReporter {

    private IProgressMonitor monitor;

    private List<Object> objectList;

    public TestReporter() {
	objectList = new ArrayList<Object>();
	objectList.add("Test1");
	objectList.add("Test2");
	objectList.add("Test3");
	objectList.add("Test4");
	objectList.add("Test5");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.report.IReporter#getDataForObject(java.lang
     * .String)
     */
    @Override
    public IReportObject[][] getDataForObject(Object[] object) {
	if (monitor!=null){
	    monitor.beginTask("TestReporter", 6);
	}
	IReportObject[][] matrix = new IReportObject[3][3];
	IReportObject tempRepObj;

	try {
	    setMonitorTask("Test Header 1");
//	    Thread.sleep(10000);
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Header 1");
	    setMonitorTask("Test Header 2");
//	    Thread.sleep(1000);
	    matrix[0][0] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Header 2");
	    setMonitorTask("Test Header 3");
//	    Thread.sleep(1000);
	    matrix[0][1] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Header 3");
//	    Thread.sleep(1000);
	    matrix[0][2] = tempRepObj;
	    setMonitorTask("Test Data 1");
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 1");
	    setMonitorTask("Test Data 2");
//	    Thread.sleep(1000);
	    matrix[1][0] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 2");
	    setMonitorTask("Test Data 3");
//	    Thread.sleep(1000);
	    matrix[1][1] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 3");
	    matrix[1][2] = tempRepObj;
	    setMonitorTask("Test Data 4");
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 4 Test");
	    setMonitorTask("Test Data 5");
//	    Thread.sleep(1000);
	    matrix[2][0] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 5");
	    setMonitorTask("Test Data 6");
	    Thread.sleep(1000);
	    matrix[2][1] = tempRepObj;
	    tempRepObj = new ReportObjectImpl();
	    tempRepObj.setData("Data 6");
	    matrix[2][2] = tempRepObj;
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return matrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.chdev.artools.reporter.report.IReporter#getName()
     */
    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return "Testreporter";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.chdev.artools.reporter.report.IReporter#getObjectList()
     */
    @Override
    public List<Object> getObjectList() {
	return objectList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#setStore(com.bmc.arsys
     * .studio.model.store.IStore)
     */
    @Override
    public void setStores(List<IStore> stores) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#isMultiselectAllowed()
     */
    @Override
    public Boolean isMultiselectAllowed() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#setMonitor(org.eclipse
     * .core.runtime.IProgressMonitor)
     */
    @Override
    public void setMonitor(IProgressMonitor monitor) {
	this.monitor = monitor;
    }

    // Handling monitor tasks
    public void setMonitorTask(String taskName) {
	if (monitor != null) {
	    monitor.subTask(taskName);
	    monitor.worked(1);
	}
    }
}
