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

package de.chdev.artools.sql.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.report.ExportRegistry;

public class ExporterDialog extends Dialog {

	private IExporter exporter=null;
	private Composite dialogArea;
	private Composite commonArea;
	private Composite specialArea;
	private Combo exporterComboBox;
	private Text fileText;

	public ExporterDialog(Shell parentShell) {
		super(parentShell);
		// createDialogArea(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);
		commonArea = new Composite(dialogArea, SWT.NONE);
		commonArea.setLayout(new GridLayout(2, false));
		commonArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		specialArea = new Composite(dialogArea, SWT.NONE);
		specialArea.setLayout(new GridLayout(3, false));
		specialArea
				.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		specialArea.setVisible(false);

		// Create exporter selectioncombo box
		Label exporterLabel = new Label(commonArea, SWT.NONE);
		exporterLabel.setText("Exporter"); //$NON-NLS-1$
		exporterComboBox = new Combo(commonArea, SWT.READ_ONLY);
		exporterComboBox.setItems(ExportRegistry.getInstance()
				.getExporterNames());
		exporterComboBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String exporterName = exporterComboBox.getText();
				if (exporterName != null && exporterName.length() > 0) {
					IExporter exporter = ExportRegistry.getInstance()
							.getExporter(exporterName);
					if (exporter.isNeedFilename() == true) {
						specialArea.setVisible(true);
					} else {
						specialArea.setVisible(false);
					}
				}
			}
		});

		// Create file input
		Label fileLabel = new Label(specialArea, SWT.NONE);
		fileLabel.setText(Messages.ExporterDialog_filenameLabel);
		fileText = new Text(specialArea, SWT.SINGLE | SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		Button fileButton = new Button(specialArea, SWT.NONE);
		fileButton.setText("..."); //$NON-NLS-1$
		fileButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog dialog = new FileDialog(e.widget
								.getDisplay().getActiveShell(), SWT.SAVE);
						dialog.setOverwrite(true);
						dialog.setText(Messages.ExporterDialog_fileDialogTitle);
						dialog.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$
						String filePath = dialog.open();
						fileText.setText(filePath);
					}
				});

		return dialogArea;
	}
	
	@Override
	protected void okPressed() {
		if (exporterComboBox.getText()!=null && exporterComboBox.getText().length()>0){
			exporter = ExportRegistry.getInstance().getExporter(exporterComboBox.getText());
			exporter.setFilename(fileText.getText());
			super.okPressed();
		}
	}
	
	public IExporter getExporter(){
		return exporter;
	}
}
