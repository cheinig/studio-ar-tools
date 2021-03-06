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
package de.chdev.artools.reporter.interfaces;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bmc.arsys.studio.model.store.IStore;

/**
 * This interface can be implemented by all reporting classes which want
 * to integrate within the StART reporting framework.
 * 
 * @author Christoph Heinig
 *
 */
public interface IReporter {
    
	/**
	 * This method is used to set all AR server stores which should be
	 * processed by the method calls. 
	 * 
	 * @param stores The ARServerStore objects which should be processed by the reporter
	 */
    public void setStores(List<IStore> stores);

    /**
     * This method returns the name of the reporter. This is used for display options.
     * 
     * @return The reporter name
     */
    public String getName();
    
    /**
     * Calculates and returns a list of supported objects, which can be used
     * as input parameter for the getData method.
     * The object list can be dynamic or static, so it can depend on 
     * all configured ARServerStores. 
     * The returned objects must have a human
     * readable toString method, because it is used at UI.
     * 
     * @return A list of configuration sets
     */
    public List<Object> getObjectList();
    
    /**
     * The getData method takes as input parameter a configuration set which
     * was generated by the getObjectList method and returns an array of IReportObjects.
     * What these IReportObjects contain must be specified within the implementing reporter class.
     * 
     * @param object A configuration object from getObjectList
     * @return A standard IReportObject array with reporter dependent data
     */
    public IReportObject[][] getDataForObject(Object[] object);
    
    /**
     * This method returns a boolean value to configure the possibility of providing more than
     * one configuration data set at once.
     * 
     * @return Is more than one configuration data set allowed.
     */
    public Boolean isMultiselectAllowed();
    
    /**
     * If the reporter is called by a job, a progress monitor can be attached to the reporter.
     * If it is supported, the reporter implementation can write the current status to the monitor.
     * 
     * @param monitor An IProgressMonitor instance which should be updated by the reporter
     */
    public void setMonitor(IProgressMonitor monitor);
}
