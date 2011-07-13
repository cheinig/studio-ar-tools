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
package de.chdev.artools.sql.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.commands.contexts.Context;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.osgi.service.prefs.Preferences;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.studio.model.store.IStore;

import de.chdev.artools.reporter.interfaces.IExporter;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;
import de.chdev.artools.reporter.report.ExportRegistry;
import de.chdev.artools.reporter.utils.ARHelpTools;
import de.chdev.artools.sql.Activator;
import de.chdev.artools.sql.editor.provider.SQLResultContentProvider;
import de.chdev.artools.sql.editor.provider.SQLResultLabelProvider;
import de.chdev.artools.sql.ui.ExporterDialog;
import de.chdev.artools.sql.utils.SQLHelpTools;
import de.chdev.artools.sql.utils.Statement;
import de.chdev.artools.sql.utils.StatementSplitter;

/**
 * @author Christoph Heinig
 * 
 */
public class SQLEditor extends TextEditor {

	public static final String ID = "de.chdev.artools.sql.editor.sql"; //$NON-NLS-1$

	private Preferences globalPrefs = new InstanceScope()
			.getNode(Activator.PLUGIN_ID);

	private Table resultTable;

	private TableViewer resultViewer;

	private ColorManager colorManager;

	private Text executeTimeText;

	private ToolItem stopExecutionButton;

	private QueryJob queryJob;

	private ToolItem btnExecute;

	private SQLEditor editor;

	private TextCellEditor textCellEditor;

	private IContextActivation resultContext;

	private org.eclipse.swt.widgets.List formList;

	private Map<String, String> formMap;

	private boolean dirty = false;

	private Label filenameLabel;

	private Combo serverCombo;

	private Text fileNameText;

	private ToolItem serverComboItem;

	private ToolItem fileNameItem;

	private ToolBar editorToolBar;

	private Combo exportComboBox;

	private ToolItem btnExportTable;

	private FocusListener sqlFocusListener;

	public SQLEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new SQLConfiguration(colorManager));
		setDocumentProvider(new SQLDocumentProvider());
		editor = this;
	}

	public boolean isDirty() {
		if (dirty) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Wrap the original parent composite
		Composite main = new Composite(parent, SWT.None);
		GridLayout layoutMain = new GridLayout(1, true);
		layoutMain.marginWidth = 0;
		layoutMain.marginHeight = 2;
		layoutMain.verticalSpacing = 2;
		layoutMain.horizontalSpacing = 0;
		main.setLayout(layoutMain);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		// Create an editor toolbar
		Composite toolbar = createToolbar(main);

		// Create a sash composite for editor and result table
		SashForm sqlArea = new SashForm(main, SWT.VERTICAL);
		sqlArea.setLayoutData(gridData);

		// Create sql editor area
		createSqlEditorArea(sqlArea);

		// resets focus back to the sql editor area
		sqlFocusListener = new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (resultContext != null) {
					IContextService conServ = ((IContextService) editor
							.getEditorSite().getService(IContextService.class));
					conServ.deactivateContext(resultContext);
					resultContext = null;
				}
				((SQLDocumentProvider) getDocumentProvider())
						.setModifyAllowed(true);
				validateEditorInputState();
//				updateState(getEditorInput());
			}

			@Override
			public void focusLost(FocusEvent e) {

			}

		};
		getSourceViewer().getTextWidget().addFocusListener(sqlFocusListener);
		// super.createPartControl(parent);

		// Create a result area
		// Label label = new Label(main,SWT.NONE);
		// label.setText("Test");
		Composite resultArea = createResultArea(sqlArea);
		resultArea.setLayoutData(gridData);
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	private Composite createSqlEditorArea(Composite sqlArea) {
		// Split sql editor into editor and form selector
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		SashForm editorArea = new SashForm(sqlArea, SWT.HORIZONTAL);
		GridLayout layoutEditorArea = new GridLayout(2, false);
		layoutEditorArea.marginHeight = 0;
		layoutEditorArea.marginWidth = 0;
		editorArea.setLayout(layoutEditorArea);
		editorArea.setLayoutData(gridData);
		// Wrap the original text area
		Composite textArea = new Composite(editorArea, SWT.BORDER);
		FillLayout layoutText = new FillLayout(SWT.HORIZONTAL);
		layoutText.marginHeight = 0;
		layoutText.marginWidth = 0;
		textArea.setLayout(layoutText);
		textArea.setLayoutData(gridData);
		super.createPartControl(textArea);
		getSourceViewer().getDocument().addDocumentListener(
				new IDocumentListener() {

					@Override
					public void documentAboutToBeChanged(DocumentEvent event) {
						// TODO Auto-generated method stub

					}

					@Override
					public void documentChanged(DocumentEvent event) {
						dirty = true;
						firePropertyChange(PROP_DIRTY);
					}

				});
		// ---------- Testcode ------------
		Action action = new Action() {
			public void run() {
				System.out.println("Load action"); //$NON-NLS-1$
			}
		};
		action.setText("Load action"); //$NON-NLS-1$
		action.setId("loadaction"); //$NON-NLS-1$
		MenuManager mm = new MenuManager();
		mm.add(action);
		editorContextMenuAboutToShow(mm);
		String editorMID = getEditorContextMenuId();
		// ---------- Testcode ende ---------
		// Build formlist
		GridData formListAreaGd = new GridData();
		formListAreaGd.grabExcessVerticalSpace = true;
		formListAreaGd.verticalAlignment = SWT.FILL;
		formListAreaGd.widthHint = 200;
		Composite formListArea = new Composite(editorArea, SWT.BORDER);
		GridLayout formListAreaLayout = new GridLayout(1, true);
		formListArea.setLayout(formListAreaLayout);
		formListArea.setLayoutData(formListAreaGd);
		Label formSelectLabel = new Label(formListArea, SWT.NONE);
		formSelectLabel.setText(Messages.SQLEditor_formlistLabel);
		formList = new org.eclipse.swt.widgets.List(formListArea, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		GridData formListGd = new GridData();
		formListGd.grabExcessVerticalSpace = true;
		formListGd.grabExcessHorizontalSpace = true;
		formListGd.horizontalAlignment = SWT.FILL;
		formListGd.verticalAlignment = SWT.FILL;
		formList.setLayoutData(formListGd);
		formList.add(Messages.SQLEditor_formlistLoading);
		Job formListJob = new FormListJob(Messages.SQLEditor_formlistLoadJobName);
		formListJob.schedule();
		// selectList = new Combo(toolbar, SWT.NONE);
		// formList.pack();
		formList.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse
			 * .swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ISourceViewer viewer = getSourceViewer();
				SQLEditorInput input = (SQLEditorInput) getEditorInput();
				Point selectedRange = viewer.getSelectedRange();
				String formName = formList.getItem(formList.getSelectionIndex());
				String viewName = null;
				if (formMap != null && formMap.containsKey(formName)) {
					viewName = formMap.get(formName);
				}

				// Avoid null replacement
				if (viewName != null) {
					try {
						viewer.getDocument().replace(selectedRange.x,
								selectedRange.y, viewName);
						viewer.setSelectedRange(
								selectedRange.x + viewName.length(), 0);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				// Set focus back to text editor
				getSourceViewer().getTextWidget().setFocus();

				// super.mouseDoubleClick(e);
			}
		});

		editorArea.setWeights(new int[] { 70, 30 });
		return editorArea;
	}

	private Composite createResultArea(Composite main) {
		Composite resultArea = new Composite(main, SWT.BORDER);
		GridLayout layout = new GridLayout(1, true);
		resultArea.setLayout(layout);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;

		// resultArea.setLayoutData(layoutData);

		Composite resultToolbar = createResultToolbar(resultArea);
		GridData toolbarLayoutData = new GridData(SWT.FILL, SWT.NONE, true,
				false);
		resultToolbar.setLayoutData(toolbarLayoutData);

		resultTable = new Table(resultArea, SWT.FULL_SELECTION | SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		resultTable.setLayoutData(layoutData);
		resultTable.setHeaderVisible(true);
		resultTable.setLinesVisible(true);

		resultViewer = new TableViewer(resultTable);
		resultViewer.setContentProvider(new SQLResultContentProvider());
		resultViewer.setLabelProvider(new SQLResultLabelProvider());

		return resultArea;
	}

	private Composite createResultToolbar(Composite parent) {
		// Composite toolbar = new Composite(parent, SWT.BORDER);
		ToolBar resultToolBar = new ToolBar(parent, SWT.NONE);
		// RowLayout layout = new RowLayout();
		// layout.center=true;
		// toolbar.setLayout(layout);
		stopExecutionButton = new ToolItem(resultToolBar, SWT.NONE);
		stopExecutionButton.setToolTipText(Messages.SQLEditor_resultToolbarStopSQLCallTooltip);
		stopExecutionButton.setEnabled(false);
		stopExecutionButton.setImage(Activator.getImageDescriptor(
				"icons/error-icon16x16.png").createImage()); //$NON-NLS-1$
		stopExecutionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (queryJob != null) {
					queryJob.cancel();
					btnExecute.setEnabled(true);
					stopExecutionButton.setEnabled(false);
				}
			}

		});

		executeTimeText = new Text(resultToolBar, SWT.READ_ONLY | SWT.SINGLE
				| SWT.BORDER);
		executeTimeText.setText(""); //$NON-NLS-1$
		executeTimeText.setSize(200, 20);
		executeTimeText.setToolTipText(Messages.SQLEditor_resultToolbarResultInfoTooltip);
		ToolItem executeTimeItem = new ToolItem(resultToolBar, SWT.SEPARATOR);
		executeTimeItem.setWidth(executeTimeText.getBounds().width);
		executeTimeItem.setControl(executeTimeText);
		resultToolBar.pack();

		btnExportTable = new ToolItem(resultToolBar, SWT.NONE);
		btnExportTable.setToolTipText(Messages.SQLEditor_resultToolbarExportTooltip);
		btnExportTable.setImage(Activator.getImageDescriptor(
				"icons/download-manager-icon-16x16.png").createImage()); //$NON-NLS-1$
		btnExportTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExporterDialog dialog = new ExporterDialog(editor.getSite()
						.getShell());
				int result = dialog.open();
				if (result == Dialog.OK) {
					// Export data
					IExporter exporter = dialog.getExporter();
					TableItem[] tableItems = resultViewer.getTable().getItems();
					int columnCount = resultViewer.getTable().getColumnCount();
					int rowCount = tableItems.length;

					IReportObject[][] reportMatrix;

					// if there are values to export, add space for the table
					// header
					if (rowCount > 0) {
						reportMatrix = new IReportObject[rowCount + 1][columnCount];

						// Add table header
						IReportObject[] headerVector = new IReportObject[columnCount];
						TableColumn[] columns = resultViewer.getTable()
								.getColumns();
						for (int headerIndex = 0; headerIndex < columnCount; headerIndex++) {
							ReportObjectImpl headerObject = new ReportObjectImpl();
							headerObject.setData(columns[headerIndex].getText());
							headerVector[headerIndex] = headerObject;
						}
						reportMatrix[0] = headerVector;
					} else {
						reportMatrix = new IReportObject[rowCount][columnCount];
					}

					for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
						TableItem row = tableItems[rowIndex];
						IReportObject[] reportVector = new IReportObject[columnCount];
						for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
							IReportObject reportObject = new ReportObjectImpl();
							reportObject.setData(row.getText(columnIndex));
							reportVector[columnIndex] = reportObject;
						}
						reportMatrix[rowIndex + 1] = reportVector;
					}
					exporter.exportData(reportMatrix);
				}
			}
		});

		return resultToolBar;
	}

	private Composite createToolbar(Composite main) {
		editorToolBar = new ToolBar(main, SWT.NONE);

		btnExecute = new ToolItem(editorToolBar, SWT.None);
		btnExecute.setToolTipText(Messages.SQLEditor_sqlToolbarExecuteTooltip);
		btnExecute.setImage(Activator.getImageDescriptor(
				"icons/play-icon-16x16.png").createImage()); //$NON-NLS-1$
		btnExecute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISourceViewer viewer = getSourceViewer();
				SQLEditorInput input = (SQLEditorInput) getEditorInput();

				try {
					// Check if statement is selected, else use complete text
					int offset = 0;
					String statement = ""; //$NON-NLS-1$

					// Get sql text which should be processed (simple or
					// advanced)
					boolean scriptingEnabled = globalPrefs.getBoolean(
							"sql.scripting", true); //$NON-NLS-1$
					boolean selectionEnabled = globalPrefs.getBoolean(
							"sql.selection", true); //$NON-NLS-1$

					if (viewer.getSelectedRange().y > 0
							&& selectionEnabled == true) {
						offset = viewer.getSelectedRange().x;
						statement = viewer.getDocument().get(
								viewer.getSelectedRange().x,
								viewer.getSelectedRange().y);
						// Disable scripting support if sql selection is used
						scriptingEnabled = false;
					} else {
						statement = viewer.getDocument().get();
					}

					// *********** Advanced Scripting Code **************
					if (scriptingEnabled == true) {
						StatementSplitter splitter = new StatementSplitter();
						Point cursorPosition = viewer.getSelectedRange();
						cursorPosition.x -= offset;
						Statement stmt = null;
						try {
							stmt = splitter.getCurrentStatement(statement,
									cursorPosition);
							if (stmt != null) {
								viewer.setSelectedRange(
										stmt.getStartStatementOffset() + offset,
										stmt.getEndStatementOffset()
												- stmt.getStartStatementOffset());
							}
						} catch (Exception splitterException) {
							IStatus status = new Status(Status.ERROR,
									Activator.PLUGIN_ID, splitterException
											.toString(), splitterException);
							ErrorDialog.openError(getEditorSite().getShell(),
									Messages.SQLEditor_errorSqlScriptShort,
									Messages.SQLEditor_errorSqlScriptLong,
									status);
							splitterException.printStackTrace();
						}
						if (stmt != null) {
							statement = stmt.getStatement();
						}
					}
					// *********** Advanced Scripting Code End**************

					queryJob = new QueryJob(Messages.SQLEditor_queryJobName);
					queryJob.setInput(input);
					queryJob.setStatement(statement);
					queryJob.setUser(true);
					queryJob.schedule();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		// Server selection combo box with toolbar item
		serverCombo = new Combo(editorToolBar, SWT.READ_ONLY);
		String serverName = ((SQLEditorInput) getEditorInput()).getStoreName();
		serverCombo.setItems(ARHelpTools.getInstance()
				.getAllActiveServerNames());

		serverCombo.select(serverCombo.indexOf(serverName));
		serverCombo.setToolTipText(Messages.SQLEditor_sqlToolbarServerTooltip);
		serverCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Save document and restore it in new editor input
				IDocument document = editor.getSourceViewer().getDocument();

				// Create and set new sql editor input
				try {
					File editorInputFile = ((SQLEditorInput)editor.getEditorInput()).getFile();
					editor.doSetInput(new SQLEditorInput(ARHelpTools.getInstance().getStoreByName(serverCombo.getText())));
					((SQLEditorInput)editor.getEditorInput()).setFile(editorInputFile);
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				
				// init new document with the old text and the change listener
				getSourceViewer().getDocument().set(document.get());
				getSourceViewer().getDocument().addDocumentListener(
						new IDocumentListener() {

							@Override
							public void documentAboutToBeChanged(DocumentEvent event) {
								// TODO Auto-generated method stub

							}

							@Override
							public void documentChanged(DocumentEvent event) {
								dirty = true;
								firePropertyChange(PROP_DIRTY);
							}

						});
				
				// refresh form list
				formList.removeAll();
				formList.add(Messages.SQLEditor_formlistLoading);
				Job formListJob = new FormListJob(Messages.SQLEditor_formlistLoadJobName);
				formListJob.schedule();
			}

		});
		serverComboItem = new ToolItem(editorToolBar, SWT.SEPARATOR);
		serverComboItem.setWidth(150);
		serverComboItem.setControl(serverCombo);
		serverComboItem.setToolTipText(Messages.SQLEditor_sqlToolbarServerTooltip);
		editorToolBar.pack();

		// Load Button
		ToolItem loadButton = new ToolItem(editorToolBar, SWT.None);
		loadButton.setToolTipText(Messages.SQLEditor_sqlToolbarLoadTooltip);
		loadButton.setImage(Activator.getImageDescriptor(
				"icons/folder-blue-open-icon-16x16.png").createImage()); //$NON-NLS-1$
		loadButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				doLoad();
			}
		});

		// Filename text with toolbar item
		fileNameText = new Text(editorToolBar, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		fileNameText.setText(Messages.SQLEditor_sqlToolbarFileDefault);
		fileNameText.setToolTipText(Messages.SQLEditor_sqlToolbarFileTooltip);
		fileNameText.setSize(300, 20);
		// fileNameText.pack();
		fileNameItem = new ToolItem(editorToolBar, SWT.SEPARATOR);
		fileNameItem.setWidth(fileNameText.getBounds().width);
		fileNameItem.setControl(fileNameText);
		editorToolBar.pack();

		// ToolItem testItem = new ToolItem(editorToolBar, SWT.SEPARATOR);
		// editorToolBar.pack();

		return editorToolBar;
	}

	private int showSQLResult(SQLResult result, String[] header) {
		int columnCount = 0;

		// Avoid table redraw during table rebuild
		resultTable.setRedraw(false);

		// Handle null value
		if (result != null) {
			// Search number of result columns
			List<List<Value>> contents = result.getContents();

			if (contents.size() > 0) {
				columnCount = contents.get(0).size();
			}

			// long timestamp = System.currentTimeMillis();

			TableColumn[] oldColumns = resultTable.getColumns();
			int oldColumnCount = oldColumns.length;
			// Remove columns
			for (; oldColumnCount > columnCount; oldColumnCount--) {
				oldColumns[oldColumnCount - 1].dispose();
			}

			// timestamp = System.currentTimeMillis() - timestamp;
			// System.out.println("Columns removed in " + timestamp + " ms");
			// timestamp = System.currentTimeMillis();

			// Add columns
			oldColumns = resultTable.getColumns();
			oldColumnCount = oldColumns.length;
			for (; oldColumnCount < columnCount; oldColumnCount++) {
				new TableColumn(resultTable, SWT.NONE);
			}

			// Set column metadata
			oldColumns = resultTable.getColumns();
			oldColumnCount = oldColumns.length;
			for (int i = 0; i < columnCount; i++) {
				oldColumns[i].setWidth(100);
				// set column label
				if (header != null && header.length > i) {
					oldColumns[i].setText(header[i]);
				}
			}

			// timestamp = System.currentTimeMillis() - timestamp;
			// System.out.println("Columns added in " + timestamp + " ms");
			// timestamp = System.currentTimeMillis();

			resultViewer.setInput(result);
			resultViewer.refresh();

			resultViewer.setColumnProperties(header);
			CellEditor[] editors = new CellEditor[resultTable.getColumnCount()];
			textCellEditor = new TextCellEditor(resultViewer.getTable(),
					SWT.READ_ONLY);

			for (int i = 0; i < editors.length; i++) {
				editors[i] = textCellEditor;
			}
			resultViewer.setCellEditors(editors);

			// Change the context to the result context.
			// Needed to specify own keybindings
			resultTable.addFocusListener(new FocusListener() {

				public void focusLost(FocusEvent e) {

				}

				public void focusGained(FocusEvent e) {
					if (resultContext == null) {
						IContextService conServ = ((IContextService) editor
								.getEditorSite().getService(
										IContextService.class));
						resultContext = conServ
								.activateContext("de.chdev.artools.sql.resultContext"); //$NON-NLS-1$
						((SQLDocumentProvider) getDocumentProvider())
								.setModifyAllowed(false);
						validateEditorInputState();
						// updateState(getEditorInput());
					}
				}
			});

			resultViewer.setCellModifier(new ICellModifier() {

				@Override
				public boolean canModify(Object element, String property) {
					// Must be set to true so the cell can be selected
					return true;
				}

				@Override
				public Object getValue(Object element, String property) {
					// Find the index of the column
					int columnIndex = -1;
					TableColumn[] columns = resultTable.getColumns();
					for (int i = 0; i < columns.length; i++) {
						if (columns[i].getText().equals(property)) {
							columnIndex = i;
							break;
						}
					}

					ArrayList<Value> row = (ArrayList<Value>) element;

					Object result;
					if (columnIndex >= 0) {
						Value value = row.get(columnIndex);
						if (value != null) {
							result = row.get(columnIndex).toString();
						} else {
							result = ""; //$NON-NLS-1$
						}
					} else {
						result = ""; //$NON-NLS-1$
					}

					return result;
				}

				@Override
				public void modify(Object element, String property, Object value) {
					// No modification allowed so this method is left empty
				}

			});

		} else {
			// Remove all columns
			TableColumn[] oldColumns = resultTable.getColumns();
			// Remove columns
			for (TableColumn oldColumn : oldColumns) {
				oldColumn.dispose();
			}
			resultViewer.setInput(null);
			resultViewer.refresh();
		}

		// Redraw modified result table
		resultTable.setRedraw(true);

		return columnCount;
	}

	private int handleARException(ARException are) {
		are.printStackTrace();
		final IStatus status = new Status(Status.ERROR, Activator.PLUGIN_ID,
				are.getLocalizedMessage(), are);
		// Make an async GUI call so that this method can be run from external
		// threads
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(getEditorSite().getShell(), Messages.SQLEditor_errorSqlShort,
						Messages.SQLEditor_errorSqlLong, status);
			}
		});
		return 0;
	}

	public TextCellEditor getTextCellEditor() {
		return textCellEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.
	 * runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		SQLDocumentProvider provider = (SQLDocumentProvider) editor
				.getDocumentProvider();
		super.doSave(progressMonitor);
		if (provider.isSaved()) {
			dirty = false;
			firePropertyChange(PROP_DIRTY);
			SQLEditorInput editorInput = (SQLEditorInput) getEditorInput();
			File file = editorInput.getFile();
			if (file != null) {
				String filename = file.getAbsolutePath();
				if (filename != null) {
					// filenameLabel.setText(filename);
					// filenameLabel.pack();
					fileNameText.setText(file.getPath());
					fileNameText.setToolTipText(file.getPath());
					// fileNameText.pack();
					// fileNameItem.setWidth(fileNameText.getBounds().width);
					// editorToolBar.pack();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		File file = null;
		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		String filename = fileDialog.open();
		if (filename != null) {
			file = new File(filename);
			((SQLEditorInput) getEditorInput()).setFile(file);
			// filenameLabel.setText(filename);
			// filenameLabel.pack();
			fileNameText.setText(file.getPath());
			fileNameText.setToolTipText(file.getPath());
			// fileNameText.pack();
			// fileNameItem.setWidth(fileNameText.getBounds().width);
			// editorToolBar.pack();
		}
		doSave(null);
	}

	public void doLoad() {
		File file = null;
		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.OPEN);
		String filename = fileDialog.open();
		if (filename != null) {
			file = new File(filename);
			((SQLEditorInput) getEditorInput()).setFile(file);
		}
		// Read file content if available
		if (file != null) {
			try {
				StringBuffer textBuffer = new StringBuffer();
				char[] readArray = new char[100];
				// CharBuffer readBuffer = CharBuffer.allocate(100);
				FileReader fileReader = new FileReader(file);
				int read = fileReader.read(readArray);
				while (read >= 0) {
					textBuffer.append(String.valueOf(readArray).substring(0,
							read));
					read = fileReader.read(readArray);
				}
				getSourceViewer().getDocument().set(textBuffer.toString());
				dirty = false;
				firePropertyChange(PROP_DIRTY);
				// filenameLabel.setText(file.getPath());
				// filenameLabel.pack();
				fileNameText.setText(file.getPath());
				fileNameText.setToolTipText(file.getPath());
				// fileNameText.pack();
				// fileNameItem.setWidth(fileNameText.getBounds().width);
				// editorToolBar.pack();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class FormListJob extends Job {

		/**
		 * @param name
		 */
		public FormListJob(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {

			// Get formnames
			String[][] tempTranslation;
			try {
				tempTranslation = ((SQLEditorInput) getEditorInput())
						.getFormnames();
			} catch (Exception e) {
				tempTranslation = new String[][] { {
						Messages.SQLEditor_formlistLoadError, null } };
			}

			// Convert formnames into a map and a formname array
			Map<String, String> nameTranslationMap = new HashMap<String, String>();
			String[] tempFormnames = new String[tempTranslation.length];
			int n = 0;
			for (String[] nameTranslation : tempTranslation) {
				nameTranslationMap.put(nameTranslation[0], nameTranslation[1]);
				tempFormnames[n] = nameTranslation[0];
				n++;
			}

			// Set global search map
			formMap = nameTranslationMap;

			// Set final variables for further process
			final String[] formnames = tempFormnames;

			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					formList.removeAll();
					formList.setItems(formnames);
				}
			});
			return Status.OK_STATUS;
		}

	}

	class QueryJob extends Job {

		private SQLEditorInput input;

		private String statement;

		private SQLResult result = null;

		private String[] header = null;

		private long timestamp;

		// private Display display;

		public QueryJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			// // --------- DEBUG PART ----------
			// List<List<Value>> valueList = new ArrayList<List<Value>>();
			// List<Value> values = new ArrayList<Value>();
			// values.add(new Value("test"));
			// valueList.add(values);
			// result = new DebugResult(valueList);
			// header = new String[]{"debug"};
			// PlatformUI.getWorkbench().getDisplay().asyncExec(
			// new Runnable() {
			// public void run() {
			// showSQLResult(result, header);
			// }
			// });
			// // --------- DEBUG PART END ----------

			// ---------- PROD PART ------------
			try {
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							public void run() {
								stopExecutionButton.setEnabled(true);
								btnExecute.setEnabled(false);
							}
						});

				// Check for cancel
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				timestamp = System.currentTimeMillis();
				monitor.beginTask(statement, 1);
				result = getInput().executeSQLStatement(getStatement());
				timestamp = System.currentTimeMillis() - timestamp;
				// System.out.println("Executed in " + timestamp + " ms");
				// Set execution timestamp in result area
				// Check for cancel
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							public void run() {
								executeTimeText.setText(Messages.SQLEditor_resultToolbarResultInfoFetched
										+ result.getTotalNumberOfMatches()
										+ Messages.SQLEditor_resultToolbarResultInfoRows + timestamp + Messages.SQLEditor_resultToolbarResultInfoMilliseconds);
								executeTimeText.pack();
							}
						});
				// Check for cancel
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				// timestamp = System.currentTimeMillis();
				header = getInput().getSQLStatementHeader(getStatement());
				// timestamp = System.currentTimeMillis() - timestamp;
				// System.out
				// .println("Fetched header in " + timestamp + " ms");

				// Check for cancel
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							public void run() {
								showSQLResult(result, header);
							}
						});
			} catch (ARException are) {
				handleARException(are);
			} finally {
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							public void run() {
								stopExecutionButton.setEnabled(false);
								btnExecute.setEnabled(true);
							}
						});
			}
			// ----------- PROD PART END -----------

			return Status.OK_STATUS;

		}

		/**
		 * @param input
		 *            the input to set
		 */
		public void setInput(SQLEditorInput input) {
			this.input = input;
		}

		/**
		 * @return the input
		 */
		public SQLEditorInput getInput() {
			return input;
		}

		/**
		 * @param statement
		 *            the statement to set
		 */
		public void setStatement(String statement) {
			this.statement = statement;
		}

		/**
		 * @return the statement
		 */
		public String getStatement() {
			return statement;
		}
	};

	/**
	 * This internal class is only needed for debugging purpose.
	 * 
	 * @author Christoph Heinig
	 * 
	 */
	class DebugResult extends SQLResult {

		/**
		 * @param arg0
		 */
		protected DebugResult(List<List<Value>> arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

	}
}
