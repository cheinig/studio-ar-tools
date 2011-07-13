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

package de.chdev.artools.reporter.views;

import java.net.URL;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Escalation;
import com.bmc.arsys.api.EscalationCriteria;
import com.bmc.arsys.api.EscalationInterval;
import com.bmc.arsys.api.EscalationTime;
import com.bmc.arsys.api.EscalationTimeCriteria;
import com.bmc.arsys.api.Form;
import com.bmc.arsys.api.FormCriteria;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.item.IModelItem;
import com.bmc.arsys.studio.model.store.IModelObject;
import com.bmc.arsys.studio.model.store.IStore;
import com.bmc.arsys.studio.model.type.IARSystemTypes;
import com.bmc.arsys.studio.commonui.common.EditorInput;
//import com.bmc.arsys.studio.ui.views.navigator.model.INode;
import de.chdev.artools.reporter.Activator;
import de.chdev.artools.reporter.AdvEscalation;
import de.chdev.artools.reporter.EscalationDataAR;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.report.ReportRegistry;
import de.chdev.artools.reporter.report.TimeReporter;
import de.chdev.artools.reporter.ui.ScreenExporterDialog;
import de.chdev.artools.reporter.utils.ARHelpTools;

public class EscalationView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.chdev.artools.reporter.views.EscalationView"; //$NON-NLS-1$

	private Date lastLoad;

	// current type is indexed by an integer
	// 0 = Escalation
	// 1 = Forms
	private int currentType;

	private IStore globStore;

	private Label headerLabel;

	private TableViewer viewer;

	private Action actionEscal;

	private Action actionArchive;

	private Action doubleClickAction;

	private Table table;

	TableColumn tableColumn;

	TableColumn tableColumn2;

	TableColumn tableColumn1;

	TableColumn tableColumn3;

	TableColumn tableColumn4;

	/**
	 * The constructor.
	 */
	public EscalationView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		System.out.println("Create Part Control from View"); //$NON-NLS-1$

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		composite.setLayoutData(gridData);

		headerLabel = new Label(composite, SWT.NONE);
		setLabelData("", ""); //$NON-NLS-1$ //$NON-NLS-2$

		GridData labelData = new GridData(SWT.FILL, SWT.NONE, true, false);
		headerLabel.setLayoutData(labelData);

		viewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create the help context id for the viewer's control
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(),
						"de.chdev.artools.reporter.view.viewer"); //$NON-NLS-1$
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// table = new Table(parent.getShell(), SWT.FULL_SELECTION);
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(tableData);

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(180);
		tableColumn.setText(Messages.EscalationView_tableColumName);
		tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(60);
		tableColumn1.setText(Messages.EscalationView_tableColumnTime);
		tableColumn2 = new TableColumn(table, SWT.NONE);
		tableColumn2.setWidth(60);
		tableColumn2.setText(Messages.EscalationView_tableColumnInterval);
		tableColumn3 = new TableColumn(table, SWT.NONE);
		tableColumn3.setWidth(60);
		tableColumn3.setText(Messages.EscalationView_tableColumnEnabled);
		tableColumn4 = new TableColumn(table, SWT.NONE);
		tableColumn4.setWidth(60);
		tableColumn4.setText(Messages.EscalationView_tableColumnDays);

		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				// table.setSortColumn(column);

				String columnName = e.widget.toString();
				Integer columnNumber = new Integer(0);
				String sortDirection = "asc"; //$NON-NLS-1$

				int columnCount = table.getColumnCount();

				for (int i = 0; i < columnCount; i++) {
					if (table.getColumn(i).toString().equals(columnName)) {
						columnNumber = i;
					}
				}

				if (table.getSortColumn().toString().equals(columnName)) {
					if (table.getSortDirection() == SWT.UP) {
						table.setSortDirection(SWT.DOWN);
						sortDirection = "desc"; //$NON-NLS-1$
					} else {
						table.setSortDirection(SWT.UP);
					}
				} else {
					table.setSortColumn(table.getColumn(columnNumber));
					table.setSortDirection(SWT.UP);
				}

				columnSort(columnNumber);
			}
		};

		tableColumn.addListener(SWT.Selection, sortListener);
		tableColumn1.addListener(SWT.Selection, sortListener);
		tableColumn2.addListener(SWT.Selection, sortListener);
		tableColumn3.addListener(SWT.Selection, sortListener);
		tableColumn4.addListener(SWT.Selection, sortListener);

		table.setSortColumn(tableColumn);
		table.setSortDirection(SWT.UP);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				EscalationView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionEscal);
		manager.add(actionArchive);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionEscal);
		manager.add(actionArchive);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionEscal);
		manager.add(actionArchive);
	}

	private void makeActions() {
		actionEscal = new Action() {
			public void run() {
				IStore store = ARHelpTools.getInstance().getCurrentStore();
				if (store != null && store.isConnected()) {
					globStore = store;
					FillTableJob escalJob = new FillTableJob(Messages.EscalationView_runJobEscalationName);
					escalJob.store = store;
					escalJob.type = 0;
					// fillTableEscalation(store.getContext());
					currentType = 0;
					setLabelData(store != null ? store.getName() : "", //$NON-NLS-1$
							Messages.EscalationView_runJobEscalationLoading);
					escalJob.schedule();
				} else {
					String message = Messages.EscalationView_runJobErrorNoServerLong;
					MessageDialog.openError(null, Messages.EscalationView_runJobErrorNoServerShort, message);
				}
			}
		};
		actionEscal.setText(Messages.EscalationView_actionLoadEscalationName);
		actionEscal.setToolTipText(Messages.EscalationView_actionLoadEscalationTooltip);
		ImageDescriptor escalImage = Activator
				.getImageDescriptor("icons/k-alarm-icon16x16.png"); //$NON-NLS-1$
		actionEscal.setImageDescriptor(escalImage);

		actionArchive = new Action() {
			public void run() {
				IStore store = ARHelpTools.getInstance().getCurrentStore();
				if (store != null && store.isConnected()) {
					globStore = store;
					FillTableJob archiveJob = new FillTableJob(Messages.EscalationView_runJobArchiveName);
					archiveJob.store = store;
					archiveJob.type = 1;
					// fillTableEscalation(store.getContext());
					currentType = 1;
					setLabelData(store != null ? store.getName() : "", //$NON-NLS-1$
							Messages.EscalationView_runJobArchiveLoading);
					archiveJob.schedule();
				} else {
					String message = Messages.EscalationView_runJobErrorNoServerLong_Archive;
					MessageDialog.openError(null, Messages.EscalationView_runJobErrorNoServerShort_Archive, message);
				}
			}
		};
		actionArchive.setText(Messages.EscalationView_actionLoadArchiveName);
		actionArchive.setToolTipText(Messages.EscalationView_actionLoadArchiveTooltip);
		ImageDescriptor archiveImage = Activator
				.getImageDescriptor("icons/karm-icon16x16.png"); //$NON-NLS-1$
		actionArchive.setImageDescriptor(archiveImage);

		doubleClickAction = new Action() {
			public void run() {
				String element = ""; //$NON-NLS-1$
				TableItem[] selection = viewer.getTable().getSelection();
				if (selection != null && selection.length > 0) {
					TableItem ti = selection[0];
					element = ti.getText();
				}
				// showMessage("Double-click detected on " + element);

				// New Test Code
				IEditorPart editorPart = null;
				String editor = null;
				IModelItem item = null;
				if (currentType == 0) {
					editor = "com.bmc.arsys.studio.ui.editors.workflow.EscalationEditor"; //$NON-NLS-1$
					item = globStore
							.getItem(IARSystemTypes.ESCALATION, element);
				} else if (currentType == 1) {
					editor = "com.bmc.arsys.studio.ui.editors.form.FormEditor"; //$NON-NLS-1$
					item = globStore.getItem(IARSystemTypes.FORM, element);
				}
				if (item != null && editor != null) {
					try {
						IModelObject modelObject = item.getStore().getObject(
								item);
						EditorInput editorInput = new EditorInput(modelObject,
								null);
						editorInput.setModelItem(item);
						editorPart = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.openEditor(editorInput, editor);
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (ModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// End new Test Code
			}
		};
	}

	private void fillTableEscalation(ARServerUser serverUser) {
		try {
			NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$
			table.removeAll();
			table.setSortColumn(tableColumn);
			table.setSortDirection(SWT.UP);

			List<Escalation> escalList = new ArrayList<Escalation>();
			List<String> escalStringList = serverUser.getListEscalation();
			for (String str : escalStringList) {
				escalList.add(serverUser.getEscalation(str));
			}

			for (Escalation escal : escalList) {
				System.out.println(escal.getName());
				EscalationTimeCriteria escalTC = escal.getEscalationTm();
				String hour = ""; //$NON-NLS-1$
				String intervall = ""; //$NON-NLS-1$
				String days = ""; //$NON-NLS-1$
				if (escalTC instanceof EscalationInterval) {
					EscalationInterval escalIV = (EscalationInterval) escalTC;
					int hourI = escalIV.getHours();
					int minI = escalIV.getMinutes();
					int daysI = escalIV.getDays();
					intervall = format.format(daysI) + "/" //$NON-NLS-1$
							+ format.format(hourI) + "/" + format.format(minI); //$NON-NLS-1$
				} else if (escalTC instanceof EscalationTime) {
					EscalationTime escalT = (EscalationTime) escalTC;
					int hourMask = escalT.getHourMask();
					int min = escalT.getMinute();
					int monthDayMask = escalT.getMonthDayMask();
					int weekDayMask = escalT.getWeekDayMask();
					// days = monthDayMask + " / " + weekDayMask;
					List<String> hourList = getHours(new Double(hourMask));
					for (String str : hourList) {
						hour = str + ":" + format.format(min) + " " + hour; //$NON-NLS-1$ //$NON-NLS-2$
					}
					List<String> monthList = getMonthDays(new Double(
							monthDayMask));
					for (String str : monthList) {
						days = str + " " + days; //$NON-NLS-1$
					}
					List<String> weekList = getWeekDays(new Double(weekDayMask));
					if (weekList.size() > 0) {
						days = " / " + days; //$NON-NLS-1$
					}
					for (String str : weekList) {
						days = str + " " + days; //$NON-NLS-1$
					}
				} else {
					System.out.println("Unknown"); //$NON-NLS-1$
				}

				TableItem tempItem = new TableItem(table, SWT.NONE);
				tempItem.setText(0, escal.getName());
				tempItem.setText(1, hour);
				tempItem.setText(2, intervall);
				tempItem.setText(3, escal.isEnable() ? Messages.EscalationView_escalationValueEnabledYes : Messages.EscalationView_escalationValueEnabledNo);
				tempItem.setText(4, days);
			}
			columnSort(0);

		} catch (Exception e) {
			System.out.println("Fehler"); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

	private void fillTableArchiving(ARServerUser serverUser) {
		try {
			NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$
			table.removeAll();
			table.setSortColumn(tableColumn);
			table.setSortDirection(SWT.UP);

			FormCriteria criteria = new FormCriteria();
			criteria.setRetrieveAll(true);
			List<Form> formList = serverUser.getListFormObjects(0l,
					Constants.AR_LIST_SCHEMA_ALL
							| Constants.AR_HIDDEN_INCREMENT, null,
					new int[] {}, criteria);
			for (Form form : formList) {
				String hours = ""; //$NON-NLS-1$
				String days = ""; //$NON-NLS-1$
				Double minutes = new Double(form.getArchiveInfo()
						.getArchiveTmInfo().getMinute());
				String enabled = form.getArchiveInfo().isEnable() ? Messages.EscalationView_archiveValueEnabledYes
						: Messages.EscalationView_archiveValueEnabledNo;
				List<String> hoursList = getHours(new Double(form
						.getArchiveInfo().getArchiveTmInfo().getHourMask()));
				for (String hour : hoursList) {
					hours = hour + ":" + format.format(minutes.intValue()) //$NON-NLS-1$
							+ " " + hours; //$NON-NLS-1$
				}
				List<String> monthList = getMonthDays(new Double(form
						.getArchiveInfo().getArchiveTmInfo().getMonthDayMask()));
				for (String month : monthList) {
					days = month + " " + days; //$NON-NLS-1$
				}
				List<String> weekList = getWeekDays(new Double(form
						.getArchiveInfo().getArchiveTmInfo().getWeekDayMask()));
				if (weekList.size() > 0) {
					days = " / " + days; //$NON-NLS-1$
				}
				for (String week : weekList) {
					days = week + " " + days; //$NON-NLS-1$
				}
				TableItem tempItem = new TableItem(table, SWT.NONE);
				tempItem.setText(0, form.getName());
				tempItem.setText(1, hours);
				tempItem.setText(2, ""); //$NON-NLS-1$
				tempItem.setText(3, enabled);
				tempItem.setText(4, days);
			}

			columnSort(0);

		} catch (Exception e) {
			System.out.println("Fehler"); //$NON-NLS-1$
		}
	}

	private void columnSort(int columnNumber) {
		int columnCount = table.getColumnCount();
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());

		table.setRedraw(false);
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(columnNumber);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(columnNumber);
				if ((collator.compare(value1, value2) < 0 && table
						.getSortDirection() == SWT.UP)
						|| (collator.compare(value1, value2) > 0 && table
								.getSortDirection() == SWT.DOWN)) {
					List<String> valueList = new ArrayList<String>();
					for (int n = 0; n < columnCount; n++) {
						valueList.add(items[i].getText(n));
					}
					String[] values = valueList.toArray(new String[] {});
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setRedraw(true);
	}

	private List<String> getHours(Double value) {
		List<String> stunden = new ArrayList<String>();
		Double tempValue = value, restValue = 0.0;
		NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$

		while (tempValue > 0) {
			restValue = new Double(Math.log(tempValue) / Math.log(2));
			restValue = new Double(restValue.intValue());
			stunden.add(format.format(restValue.intValue()));
			tempValue = tempValue - Math.pow(2, restValue);
		}

		return stunden;
	}

	private List<String> getMonthDays(Double value) {
		List<String> monthDays = new ArrayList<String>();
		NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$

		if (value == 2147483647) {
			monthDays.add(Messages.EscalationView_allDays);
		} else {
			Double tempMonthDay = null;
			List<String> tempList = null;
			tempList = getHours(value);
			for (String str : tempList) {
				tempMonthDay = Double.parseDouble(str);
				tempMonthDay++;
				monthDays.add(format.format(tempMonthDay.intValue()));
			}
		}

		return monthDays;
	}

	private List<String> getWeekDays(Double value) {
		List<String> weekDays = new ArrayList<String>();

		if (value == 127) {
			weekDays.add(Messages.EscalationView_fullWeek);
		} else {
			for (String str : getHours(value)) {
				if (str.equalsIgnoreCase("00")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_dayMondayShort);
				if (str.equalsIgnoreCase("01")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_dayTuesdayShort);
				if (str.equalsIgnoreCase("02")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_dayWednesdayShort);
				if (str.equalsIgnoreCase("03")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_dayThursdayShort);
				if (str.equalsIgnoreCase("04")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_dayFridayShort);
				if (str.equalsIgnoreCase("05")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_daySaturdayShort);
				if (str.equalsIgnoreCase("06")) //$NON-NLS-1$
					weekDays.add(Messages.EscalationView_daySundayShort);
			}
		}

		return weekDays;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				Messages.EscalationView_messageDialogTitle, message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void setLabelData(String server, String type) {
		headerLabel.setText(Messages.EscalationView_reloadDataServer + server + Messages.EscalationView_reloadDataType + type
				+ Messages.EscalationView_reloadDataTime + new Date().toString());
		headerLabel.redraw();
	}

	class FillTableJob extends Job {

		int type = 0; // Data type which should be load 0=escal, 1=archive

		// EscalationView view; // View instance

		IStore store;

		/**
		 * @param name
		 */
		public FillTableJob(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IReporter reporter = new TimeReporter();
			String typeString = ""; //$NON-NLS-1$
			reporter.setMonitor(monitor);
			if (type == 0) {
				List<IStore> stores = new ArrayList<IStore>();
				stores.add(store);
				reporter.setStores(stores);
				typeString = "escalation"; //$NON-NLS-1$
			} else if (type == 1) {
				List<IStore> stores = new ArrayList<IStore>();
				stores.add(store);
				reporter.setStores(stores);
				typeString = "archive"; //$NON-NLS-1$
			}
			if (monitor.isCanceled() == false) {
				Object[] objects = new Object[] { typeString };
				final IReportObject[][] data = reporter
						.getDataForObject(objects);

				if (data != null) {
					// Refresh table data
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {
								public void run() {
									table.setRedraw(false);
									table.removeAll();
									table.setSortColumn(tableColumn);
									table.setSortDirection(SWT.UP);
									// Add all report elements as table items
									// but ignore header
									for (int n = 1; n < data.length; n++) {
										IReportObject[] reports = data[n];
										TableItem tempItem = new TableItem(
												table, SWT.NONE);
										tempItem.setText(0,
												reports[0].toString());
										tempItem.setText(1,
												reports[1].toString());
										tempItem.setText(2,
												reports[2].toString());
										tempItem.setText(3,
												reports[3].toString());
										tempItem.setText(4,
												reports[4].toString());
									}
									if (type == 0) {
										setLabelData(
												store != null ? store.getName()
														: "", Messages.EscalationView_reloadDataTypeEscalation); //$NON-NLS-1$
									} else {
										setLabelData(
												store != null ? store.getName()
														: "", Messages.EscalationView_reloadDataTypeArchive); //$NON-NLS-1$
									}
									columnSort(0);
									table.setRedraw(true);
								}
							});
				}
			} else {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}

	}

}