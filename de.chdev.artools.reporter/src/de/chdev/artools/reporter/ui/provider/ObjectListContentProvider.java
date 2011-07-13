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
package de.chdev.artools.reporter.ui.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.chdev.artools.reporter.interfaces.IReporter;

/**
 * @author Christoph Heinig
 *
 */
public class ObjectListContentProvider implements IStructuredContentProvider{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
	IReporter reporter = null;
//	String [] result = new String[]{};
	List<Object> objectList = new ArrayList<Object>();
	if (inputElement instanceof IReporter){
	    reporter = (IReporter) inputElement;
	    objectList = reporter.getObjectList();
//	    result = new String[objectList.size()];
//	    for (int i=0;i<objectList.size();i++){
//		result[i] = objectList.get(i).toString();
//	    }
	}
	return objectList.toArray();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
	// TODO Auto-generated method stub
	
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	// TODO Auto-generated method stub
	
    }

}
