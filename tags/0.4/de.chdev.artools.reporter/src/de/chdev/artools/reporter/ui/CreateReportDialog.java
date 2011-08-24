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
package de.chdev.artools.reporter.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import de.chdev.artools.reporter.Activator;
import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.report.ExportRegistry;
import de.chdev.artools.reporter.report.ReportJob;
import de.chdev.artools.reporter.report.ReportRegistry;
import de.chdev.artools.reporter.utils.ARHelpTools;

/**
 * @author Christoph Heinig
 * 
 */
public class CreateReportDialog extends TitleAreaDialog {

    private CreateReportDialogVE reportData;

    /**
     * @param parentShell
     */
    public CreateReportDialog(Shell parentShell) {
	super(parentShell);
	// TODO Auto-generated constructor stub
    }

    @Override
    protected Control createContents(Composite parent) {
	Control contents = super.createContents(parent);

	setTitle(Messages.CreateReportDialog_dialogTitle);
	ImageDescriptor imageDesc = Activator
		.getImageDescriptor("icons/k-chart-icon-48x48.png"); //$NON-NLS-1$
	setTitleImage(imageDesc.createImage());
	setMessage(
		Messages.CreateReportDialog_dialogInformation,
		IMessageProvider.INFORMATION);

	return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

	reportData = new CreateReportDialogVE(parent, SWT.NONE);
	GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
	reportData.setLayoutData(gridData);

	// Preset value outside generated code
	reportData.serverCombo.setItems(ARHelpTools.getInstance()
		.getAllActiveServerNames());
	// If more than 1 element is available, <All> can be selected
	if (reportData.serverCombo.getItemCount() > 1) {
	    reportData.serverCombo.add("<All>"); //$NON-NLS-1$
	}
	String tempName = ARHelpTools.getInstance().getCurrentStoreName();
	if (tempName != null) {
	    reportData.serverCombo.select(reportData.serverCombo
		    .indexOf(tempName));
	    // Select first item as backup
	    if (reportData.serverCombo.getText().isEmpty()) {
		if (reportData.serverCombo.getItemCount() > 0) {
		    reportData.serverCombo.select(0);
		}
	    }
	} else {
	    if (reportData.serverCombo.getItemCount() > 0) {
		reportData.serverCombo.select(0);
	    }
	}
	return parent;
    }

    @Override
    protected void okPressed() {

	IReporter reporter = ReportRegistry.getInstance().getReporter(
		reportData.reportTypeCombo.getText());
	IExporter exporter = ExportRegistry.getInstance().getExporter(reportData.exportCombo.getText());

	// Validate settings
	// Check row selection
	if (reportData.objectTable.getSelectionCount() > 1
		&& reporter.isMultiselectAllowed() == false) {
//	    MessageDialog.openError(this.getShell(), "No multiselect", "The selected reporter does not support multiselect within the object table. Please select only one entry or choose another reporter.");
	    MessageDialog.openError(this.getShell(), Messages.CreateReportDialog_errorNoMultiselectShort, Messages.CreateReportDialog_errorNoMultiselectLong);
	    return;
	}
	// Check filename
	if (exporter.isNeedFilename()==true && (reportData.fileText.getText()==null || reportData.fileText.getText().length()==0)){
//	    MessageDialog.openError(this.getShell(), "Filename is empty", "The filename must not be empty for this export module. Fill in a filename as export target, or choose another exporter.");
	    MessageDialog.openError(this.getShell(), Messages.CreateReportDialog_errorMissingFilenameShort, Messages.CreateReportDialog_errorMissingFilenameLong);
	    return;
	}

	System.out.println("Report will be generated"); //$NON-NLS-1$
	// TODO Should the store really reset after data loading
	// reporter.setStore(ARHelpTools.getInstance().getStoreByName(reportData.serverCombo.getText()));

	// Build data array
	Object[] dataArray = new Object[reportData.objectTable
		.getSelectionCount()];
	int n = 0;
	for (int itemIndex : reportData.objectTable.getSelectionIndices()) {
	    dataArray[n] = reportData.tableViewer.getElementAt(itemIndex);
	    n++;
	}

	ReportJob reportJob = new ReportJob(reportData.reportTypeCombo
		.getText());
	reportJob.setFileName(reportData.fileText.getText());
	reportJob.setExporterName(reportData.exportCombo.getText());
	reportJob.setReporterName(reportData.reportTypeCombo.getText());
	reportJob.setDataArray(dataArray);
	reportJob.schedule();

	// IReportObject[][] data = reporter.getDataForObject(dataArray);
	// IExporter exporter =
	// ExportRegistry.getInstance().getExporter(reportData.exportCombo.getText());
	// boolean result = exporter.exportData(reportData.fileText.getText(),
	// data);
	// if (result) super.okPressed();
    }
    
    @Override
    protected boolean isResizable() {
	return true;
    }

}
