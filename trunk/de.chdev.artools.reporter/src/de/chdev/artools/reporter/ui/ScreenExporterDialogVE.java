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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

import de.chdev.artools.reporter.report.ExportRegistry;
import de.chdev.artools.reporter.ui.provider.ReportResultContentProvider;
import de.chdev.artools.reporter.ui.provider.ReportResultLabelProvider;

import org.eclipse.jface.viewers.TableViewer;

/**
 * @author Christoph Heinig
 *
 */
public class ScreenExporterDialogVE extends Composite {

    private Group reportGroup = null;
    private Group exportGroup = null;
    protected Table reportTable = null;
    protected Text fileText = null;
    private Button fileButton = null;
    protected Combo exporterCombo = null;
    protected TableViewer tableViewer = null;

    public ScreenExporterDialogVE(Composite parent, int style) {
	super(parent, style);
	initialize();
    }

    private void initialize() {
	createReportGroup();
	createExportGroup();
	setSize(new Point(300, 200));
	setLayout(new GridLayout());
    }

    /**
     * This method initializes reportGroup	
     *
     */
    private void createReportGroup() {
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.grabExcessVerticalSpace = true;
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        reportGroup = new Group(this, SWT.NONE);
        reportGroup.setLayout(new GridLayout());
        reportGroup.setText(Messages.ScreenExporterDialogVE_resultGroupLabel);
        reportGroup.setLayoutData(gridData);
        reportTable = new Table(reportGroup, SWT.FULL_SELECTION | SWT.BORDER);
        reportTable.setHeaderVisible(true);
        reportTable.setLayoutData(gridData1);
        reportTable.setLinesVisible(true);
        tableViewer = new TableViewer(reportTable);
        tableViewer.setContentProvider(new ReportResultContentProvider());
        tableViewer.setLabelProvider(new ReportResultLabelProvider());
    }

    /**
     * This method initializes exportGroup	
     *
     */
    private void createExportGroup() {
        GridData gridData2 = new GridData();
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        GridData gridData5 = new GridData();
        gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData5.grabExcessHorizontalSpace = true;
        exportGroup = new Group(this, SWT.NONE);
        exportGroup.setText(Messages.ScreenExporterDialogVE_exportGroupLabel);
        exportGroup.setLayout(gridLayout);
        exportGroup.setLayoutData(gridData2);
        createExporterCombo();
        Label filler = new Label(exportGroup, SWT.NONE);
        fileText = new Text(exportGroup, SWT.BORDER);
        fileText.setLayoutData(gridData5);
        fileButton = new Button(exportGroup, SWT.NONE);
        fileButton.setText("..."); //$NON-NLS-1$
        fileButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		FileDialog dialog = new FileDialog(e.widget.getDisplay().getActiveShell(),SWT.SAVE);
		dialog.setOverwrite(true);
		dialog.setText(Messages.ScreenExporterDialogVE_fileDialogTitle);
		dialog.setFilterExtensions(new String[]{"*.*"}); //$NON-NLS-1$
		String filePath = dialog.open();
		fileText.setText(filePath);
            }
        });

    }

    /**
     * This method initializes exporterCombo	
     *
     */
    private void createExporterCombo() {
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        exporterCombo = new Combo(exportGroup, SWT.READ_ONLY);
        exporterCombo.setItems(ExportRegistry.getInstance().getExporterNames());
        exporterCombo.setLayoutData(gridData3);
        if (exporterCombo.getItemCount()>0){
            exporterCombo.select(0);
        }

    }

}
