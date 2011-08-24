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

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;
import de.chdev.artools.reporter.report.ExportRegistry;

/**
 * @author Christoph Heinig
 * 
 */
public class ScreenExporterDialog extends Dialog {

    private ScreenExporterDialogVE dialogArea = null;

    private IReportObject[][] tableData = null;

    /**
     * @param parentShell
     */
    public ScreenExporterDialog(Shell parentShell) {
	super(parentShell);
	// TODO Auto-generated constructor stub
    }

    @Override
    protected boolean isResizable() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
	return super.createContents(parent);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	dialogArea = new ScreenExporterDialogVE(parent, SWT.NONE);
	GridData gdDialog = new GridData(SWT.FILL, SWT.FILL, true, true);
	dialogArea.setLayoutData(gdDialog);

	buildTableData(tableData);

	return dialogArea;
    }

    @Override
    protected void okPressed() {
	IExporter exporter = ExportRegistry.getInstance().getExporter(
		dialogArea.exporterCombo.getText());
	exporter.setFilename(dialogArea.fileText.getText());
	exporter.exportData(tableData);
	super.okPressed();
    }

    private void buildTableData(IReportObject[][] data) {
	int maxColumn = 0;

	if (data == null) {
	    return;
	}

	// Search for max column value
	for (IReportObject[] array : data) {
	    if (maxColumn < array.length) {
		maxColumn = array.length;
	    }
	}

	// Check header array
	IReportObject[] header = data[0];
	if (data.length > 0) {
	    if (header.length < maxColumn) {
		IReportObject[] tempArray = new IReportObject[maxColumn];
		for (int i = 0; i < tempArray.length; i++) {
		    if (i < header.length) {
			tempArray[i] = header[i];
		    } else {
			IReportObject repObj = new ReportObjectImpl();
			repObj.setData(""); //$NON-NLS-1$
			tempArray[i] = repObj;
		    }
		}
		header = tempArray;
	    }
	}

	// This listener initialize the sorting functionality of the table
	SelectionListener sortListener = new SelectionListener() {

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		// table.setSortColumn(column);

		String columnName = e.widget.toString();
		Integer columnNumber = new Integer(0);
		String sortDirection = "asc"; //$NON-NLS-1$

		int columnCount = dialogArea.reportTable.getColumnCount();

		for (int i = 0; i < columnCount; i++) {
		    if (dialogArea.reportTable.getColumn(i).toString().equals(
			    columnName)) {
			columnNumber = i;
		    }
		}

		if (dialogArea.reportTable.getSortColumn() != null
			&& dialogArea.reportTable.getSortColumn().toString()
				.equals(columnName)) {
		    if (dialogArea.reportTable.getSortDirection() == SWT.UP) {
			dialogArea.reportTable.setSortDirection(SWT.DOWN);
			sortDirection = "desc"; //$NON-NLS-1$
		    } else {
			dialogArea.reportTable.setSortDirection(SWT.UP);
		    }
		} else {
		    dialogArea.reportTable.setSortColumn(dialogArea.reportTable
			    .getColumn(columnNumber));
		    dialogArea.reportTable.setSortDirection(SWT.UP);
		}

		dialogArea.tableViewer.setSorter(new DataTableSorter(
			columnNumber));
	    }
	};

	// Build table columns
	for (int i = 0; i < maxColumn; i++) {
	    TableColumn tableColumn = new TableColumn(dialogArea.reportTable,
		    SWT.NONE);
	    tableColumn.setWidth(100);
	    tableColumn.setText(header[i].toString());
	    tableColumn.addSelectionListener(sortListener);
	    // tableColumn.addSelectionListener(new SelectionAdapter() {
	    //	       	
	    // public void widgetSelected(SelectionEvent e) {
	    // dialogArea.tableViewer.setSorter(
	    // new DataTableSorter(1));
	    // }
	    // });
	}

	// Fill table
	// dialogArea.tableViewer.setSorter(new DataTableSorter(0));
	dialogArea.tableViewer.setInput(tableData);

	dialogArea.tableViewer.refresh();
    }

    public void setTableData(IReportObject[][] data) {
	this.tableData = data;
    }

    // private void columnSort(int columnNumber) {
    // int columnCount = dialogArea.reportTable.getColumnCount();
    // TableItem[] items = dialogArea.reportTable.getItems();
    // Collator collator = Collator.getInstance(Locale.getDefault());
    //
    // for (int i = 1; i < items.length; i++) {
    // String value1 = items[i].getText(columnNumber);
    // for (int j = 0; j < i; j++) {
    // String value2 = items[j].getText(columnNumber);
    // if ((collator.compare(value1, value2) < 0 && dialogArea.reportTable
    // .getSortDirection() == SWT.UP)
    // || (collator.compare(value1, value2) > 0 && dialogArea.reportTable
    // .getSortDirection() == SWT.DOWN)) {
    // List<String> valueList = new ArrayList<String>();
    // for (int n = 0; n < columnCount; n++) {
    // valueList.add(items[i].getText(n));
    // }
    // String[] values = valueList.toArray(new String[] {});
    // items[i].dispose();
    // TableItem item = new TableItem(dialogArea.reportTable, SWT.NONE, j);
    // item.setText(values);
    // items = dialogArea.reportTable.getItems();
    // break;
    // }
    // }
    // }
    //
    // }

    class DataTableSorter extends ViewerSorter {

	private int index = 0;

	public DataTableSorter(int index) {
	    this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

	    Collator collator = Collator.getInstance(Locale.getDefault());

	    IReportObject[] row1 = (IReportObject[]) e1;
	    IReportObject[] row2 = (IReportObject[]) e2;

	    if (row1[index] != null && row2[index] != null) {
		// Sort if both values are not null
		if (dialogArea.reportTable.getSortDirection() == SWT.DOWN) {
		    return collator.compare(row2[index].toString(), row1[index].toString());
		} else {
		    return collator.compare(row1[index].toString(), row2[index].toString());
		}
	    } else if (row1[index] == null && row2[index] == null) {
		// Set to equal if both values are null
		return 0;
	    } else if (row1[index] == null) {
		// Sort if first value is null
		if (dialogArea.reportTable.getSortDirection() == SWT.DOWN) {
		    return 1;
		} else {
		    return -1;
		}
	    } else {
		if (dialogArea.reportTable.getSortDirection() == SWT.DOWN) {
		    return -1;
		} else {
		    return 1;
		}
	    }

	}
    }

}
