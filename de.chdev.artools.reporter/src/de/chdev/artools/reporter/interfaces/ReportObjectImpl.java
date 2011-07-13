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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Heinig
 *
 */
public class ReportObjectImpl implements IReportObject{

    private int classification = 0;
    private Map<String, Object> metaInfoMap;
    private Object data=null;
    
    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#getClassification()
     */
    @Override
    public int getClassification() {
	return classification;
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#getData()
     */
    @Override
    public Object getData() {
	return data;
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#getMetaInfo(java.lang.String)
     */
    @Override
    public Object getMetaInfo(String key) {
	if (metaInfoMap!=null){
	    return metaInfoMap.get(key);
	} else {
	    return null;
	}
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#setClassification(int)
     */
    @Override
    public void setClassification(int classification) {
	this.classification = classification;
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#setData(java.lang.Object)
     */
    @Override
    public void setData(Object data) {
	this.data = data;
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#setMetaInfo(java.lang.String, java.lang.Object)
     */
    @Override
    public void setMetaInfo(String key, Object object) {
	if (metaInfoMap == null){
	    metaInfoMap = new HashMap<String, Object>();
	}
	metaInfoMap.put(key, object);
    }

    /* (non-Javadoc)
     * @see de.chdev.artools.reporter.interfaces.IReportObject#removeMetaInfo(java.lang.String)
     */
    @Override
    public void removeMetaInfo(String key) {
	if (metaInfoMap!=null && metaInfoMap.containsKey(key)){
	    metaInfoMap.remove(key);
	}
    }
    
    public String toString(){
	if (data!=null){
	    return data.toString();
	} else {
	    return "";
	}
    }

}
