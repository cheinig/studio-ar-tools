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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.chdev.artools.reporter.Activator;
import de.chdev.artools.reporter.interfaces.IReportObject;

/**
 * @author Christoph Heinig
 * 
 */
public class ReportResultLabelProvider extends LabelProvider implements
	ITableLabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
     * .Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
	IReportObject[] row;
	row = (IReportObject[]) element;
	IReportObject repObj = row[columnIndex];
	
	if ((repObj.getClassification() & IReportObject.CLASSIFICATION_ERROR) > 0){
	    ImageDescriptor imageDesc = Activator.getImageDescriptor("icons/delete-icon-16x16.png");
	    return imageDesc.createImage();
	} else if ((repObj.getClassification() & IReportObject.CLASSIFICATION_WARN) > 0){
	    ImageDescriptor imageDesc = Activator.getImageDescriptor("icons/warning-icon-16x16.png");
	    return imageDesc.createImage();
	} else {
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
     * .Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
	IReportObject[] row;
	row = (IReportObject[]) element;
	return row[columnIndex].toString();
    }

}
