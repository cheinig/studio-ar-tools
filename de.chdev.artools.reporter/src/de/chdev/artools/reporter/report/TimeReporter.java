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
package de.chdev.artools.reporter.report;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Escalation;
import com.bmc.arsys.api.EscalationInterval;
import com.bmc.arsys.api.EscalationTime;
import com.bmc.arsys.api.EscalationTimeCriteria;
import com.bmc.arsys.api.Form;
import com.bmc.arsys.api.FormCriteria;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.store.ARServerStore;
import com.bmc.arsys.studio.model.store.IStore;

import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;

/**
 * @author Christoph Heinig
 * 
 */
public class TimeReporter implements IReporter {

	private IProgressMonitor monitor;

	private List<IStore> stores;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.chdev.artools.reporter.interfaces.IReporter#getDataForObject(java
	 * .lang.Object[])
	 */
	@Override
	public IReportObject[][] getDataForObject(Object[] object) {

		IReportObject[][] reportObject = null;

		// Calculate data
		if (object != null && object.length > 0
				&& object[0].toString().equalsIgnoreCase("Escalation")) { //$NON-NLS-1$
			reportObject = getEscalationData();
		} else if (object != null && object.length > 0
				&& object[0].toString().equalsIgnoreCase("Archive")) { //$NON-NLS-1$
			reportObject = getArchiveData();
		}
		return reportObject;
	}

	/**
	 * Returns the name of the reporter
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Time Reporter"; //$NON-NLS-1$
	}

	/**
	 * This method returns a list ob configuration sets. Currently archive and
	 * escalation config sets are present.
	 */
	@Override
	public List<Object> getObjectList() {
		List<Object> typeList = new ArrayList<Object>();
		typeList.add("Archive");
		typeList.add("Escalation");
		return typeList;
	}

	/**
	 * Indicates if multiselect is allowed or not. Default is false, because in
	 * the current implementation it is not possible to calculate escalation and
	 * archive data at once.
	 */
	@Override
	public Boolean isMultiselectAllowed() {
		return false;
	}

	/**
	 * This method is used to set a progress monitor. If a progress monitor is
	 * set, the current work position will be written to the monitor
	 */
	@Override
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * This method is used to set all store objects which should be looped on
	 * get data operation.
	 */
	@Override
	public void setStores(List<IStore> stores) {
		this.stores = stores;
	}

	/**
	 * This method loads all escalations from the provided stores and calculate
	 * their time values. As result a reportObject matrix will be returned.
	 * 
	 * @return A matrix with escalation information like name and time values
	 */
	private IReportObject[][] getEscalationData() {

		if (monitor != null) {
			monitor.beginTask(Messages.TimeReporter_monitorEscalation, stores.size());
		}

		NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$

		List<IReportObject[][]> reportList = new ArrayList<IReportObject[][]>();

		// init map to hold all escalation names
		Map<String, List<String>> escalNameMap = new HashMap<String, List<String>>();
		int escalCounter = 0;

		// Load all escalation names from all stores
		for (IStore store : stores) {
			setMonitorTask(Messages.TimeReporter_monitorInitEscalation + store.getName() + ")"); //$NON-NLS-2$ //$NON-NLS-1$
			try {
				ARServerUser serverUser = ((ARServerStore) store).getContext();
				List<String> escalStringList = serverUser.getListEscalation();
				escalNameMap.put(store.getName(), escalStringList);
				escalCounter += escalStringList.size();
			} catch (ARException are) {
				are.printStackTrace();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// set new monitor counter
		if (monitor != null) {
			monitor.beginTask(Messages.TimeReporter_monitorEscalation, escalCounter + 1);
		}

		// Loop all stores and start calculation
		for (IStore store : stores) {
			List<Escalation> escalList = new ArrayList<Escalation>();

			// Load all escalations from all stores
			try {
				// init working objects
				ARServerUser serverUser = ((ARServerStore) store).getContext();
				List<String> escalNameList = escalNameMap.get(store.getName());
				IReportObject[][] reportObject = new IReportObject[escalNameList
						.size()][5];
				int i = 0; // needed for reportObject array index

				// Loop all escalations in one store and create the time report
				for (String escalName : escalNameList) {
					// Set monitor info
					setMonitorTask(Messages.TimeReporter_monitorLoadEscalation + escalName + " (" //$NON-NLS-2$ //$NON-NLS-1$
							+ store.getName() + ")"); //$NON-NLS-1$

					// Load escalation
					Escalation escal = serverUser.getEscalation(escalName);

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
								+ format.format(hourI) + "/" //$NON-NLS-1$
								+ format.format(minI);
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
						List<String> weekList = getWeekDays(new Double(
								weekDayMask));
						if (weekList.size() > 0) {
							days = " / " + days; //$NON-NLS-1$
						}
						for (String str : weekList) {
							days = str + " " + days; //$NON-NLS-1$
						}
					} else {
						System.out.println("Unknown"); //$NON-NLS-1$
					}

					// Build output array
					IReportObject tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(escal.getName());
					reportObject[i][0] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(hour);
					reportObject[i][1] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(intervall);
					reportObject[i][2] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(escal.isEnable() ? Messages.TimeReporter_dataYes : Messages.TimeReporter_dataNo);
					reportObject[i][3] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(days);
					reportObject[i][4] = tempRepObj;

					i++;
				}
				reportList.add(reportObject);
			} catch (ARException are) {
				are.printStackTrace();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		setMonitorTask(Messages.TimeReporter_monitorGenerateOutput);
		// Calculate report size
		int rows = 0;
		for (IReportObject[][] report : reportList) {
			rows += report.length;
		}
		// int rows = reportList.size();

		// Copy all created report objects into one matrix
		IReportObject[][] reportObjectFinal = null;
		if (rows > 0) {
			reportObjectFinal = new IReportObject[rows + 1][5];
		} else {
			reportObjectFinal = new IReportObject[1][5];
		}
		// Write header
		IReportObject tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerName);
		reportObjectFinal[0][0] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerTime);
		reportObjectFinal[0][1] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerInterval);
		reportObjectFinal[0][2] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerEnabled);
		reportObjectFinal[0][3] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerDays);
		reportObjectFinal[0][4] = tempReport;

		// Copy row data
		int n = 1;
		for (IReportObject[][] report : reportList) {
			for (IReportObject[] reportRow : report) {
				reportObjectFinal[n][0] = reportRow[0];
				reportObjectFinal[n][1] = reportRow[1];
				reportObjectFinal[n][2] = reportRow[2];
				reportObjectFinal[n][3] = reportRow[3];
				reportObjectFinal[n][4] = reportRow[4];
				n++;
			}
		}

		return reportObjectFinal;
	}

	/**
	 * This method loads all form archives from the provided stores and
	 * calculate their time values. As result a reportObject matrix will be
	 * returned.
	 * 
	 * @return A matrix with form archive information like name and time values
	 */
	private IReportObject[][] getArchiveData() {
		NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$

		if (monitor != null) {
			monitor.beginTask(Messages.TimeReporter_monitorArchive, stores.size());
		}

		FormCriteria criteria = new FormCriteria();
		criteria.setRetrieveAll(true);

		List<IReportObject[][]> reportList = new ArrayList<IReportObject[][]>();

		// init map to hold all form names
		Map<String, List<String>> formNameMap = new HashMap<String, List<String>>();
		int formCounter = 0;

		// Load all form names from all stores
		for (IStore store : stores) {
			setMonitorTask(Messages.TimeReporter_monitorInitArchive + store.getName() + ")"); //$NON-NLS-2$ //$NON-NLS-1$
			try {
				ARServerUser serverUser = ((ARServerStore) store).getContext();
				List<String> formNameList = serverUser.getListForm();
				formNameMap.put(store.getName(), formNameList);
				formCounter += formNameList.size();
			} catch (ARException are) {
				are.printStackTrace();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// set new monitor counter
		if (monitor != null) {
			monitor.beginTask(Messages.TimeReporter_monitorArchive, formCounter + 1);
		}

		// Define formCriteria with only archive info
		FormCriteria formCriteria = new FormCriteria();
		formCriteria.setPropertiesToRetrieve(FormCriteria.ARCHIVE_INFO);
		
		// loop all stores and load form data
		for (IStore store : stores) {

			List<Form> formList = new ArrayList<Form>();

			try {
				// init working objects
				ARServerUser serverUser = ((ARServerStore) store).getContext();
				List<String> formNameList = formNameMap.get(store.getName());
				IReportObject[][] reportObject = new IReportObject[formNameList
						.size()][5];
				int i = 0;
				
				// loop all forms and load the time data 
				for (String formName : formNameList) {
					setMonitorTask(Messages.TimeReporter_monitorLoadingArchive+formName+" (" + store.getName() + ")");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					Form form = serverUser.getForm(formName, formCriteria);
					String hours = ""; //$NON-NLS-1$
					String days = ""; //$NON-NLS-1$
					Double minutes = new Double(form.getArchiveInfo()
							.getArchiveTmInfo().getMinute());
					String enabled = form.getArchiveInfo().isEnable() ? Messages.TimeReporter_dataYes
							: Messages.TimeReporter_dataNo;
					List<String> hoursList = getHours(new Double(form
							.getArchiveInfo().getArchiveTmInfo().getHourMask()));
					for (String hour : hoursList) {
						hours = hour + ":" + format.format(minutes.intValue()) //$NON-NLS-1$
								+ " " + hours; //$NON-NLS-1$
					}
					List<String> monthList = getMonthDays(new Double(form
							.getArchiveInfo().getArchiveTmInfo()
							.getMonthDayMask()));
					for (String month : monthList) {
						days = month + " " + days; //$NON-NLS-1$
					}
					List<String> weekList = getWeekDays(new Double(form
							.getArchiveInfo().getArchiveTmInfo()
							.getWeekDayMask()));
					if (weekList.size() > 0) {
						days = " / " + days; //$NON-NLS-1$
					}
					for (String week : weekList) {
						days = week + " " + days; //$NON-NLS-1$
					}

					// Build output array
					IReportObject tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(form.getName());
					reportObject[i][0] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(hours);
					reportObject[i][1] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(""); //$NON-NLS-1$
					reportObject[i][2] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(enabled);
					reportObject[i][3] = tempRepObj;
					tempRepObj = new ReportObjectImpl();
					tempRepObj.setData(days);
					reportObject[i][4] = tempRepObj;

					i++;
				}
				reportList.add(reportObject);
			} catch (ARException are) {
				are.printStackTrace();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		setMonitorTask(Messages.TimeReporter_monitorCreateOutput);

		// Calculate report size
		int rows = 0;
		for (IReportObject[][] report : reportList) {
			rows += report.length;
		}

		IReportObject[][] reportObjectFinal;
		if (rows > 0) {
			// 1st row for header data
			reportObjectFinal = new IReportObject[rows + 1][5];
		} else {
			reportObjectFinal = new IReportObject[1][5];
		}
		// Write header
		IReportObject tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerName);
		reportObjectFinal[0][0] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerTime);
		reportObjectFinal[0][1] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerInterval);
		reportObjectFinal[0][2] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerEnabled);
		reportObjectFinal[0][3] = tempReport;
		tempReport = new ReportObjectImpl();
		tempReport.setData(Messages.TimeReporter_headerDays);
		reportObjectFinal[0][4] = tempReport;

		int n = 1;
		for (IReportObject[][] report : reportList) {
			for (IReportObject[] reportRow : report) {
				reportObjectFinal[n][0] = reportRow[0];
				reportObjectFinal[n][1] = reportRow[1];
				reportObjectFinal[n][2] = reportRow[2];
				reportObjectFinal[n][3] = reportRow[3];
				reportObjectFinal[n][4] = reportRow[4];
				n++;
			}
		}

		return reportObjectFinal;
	}

	/**
	 * This method extracts from a double value a bit coded representation of options.
	 * The numbers of bits which are set will be returned as string coded number.
	 * If the first bit is set, the result will be 01. 
	 * If the first 2 bits are set, the result will be 01 and 02.
	 * Bits which are not set, will not be translated and added to the result list.
	 * @param value The double coded bit representation
	 * @return A list of string coded numbers for the bits which are set
	 */
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

	/**
	 * This method extracts the days of the month from a bit coded double value.
	 * All days will be returned as a string coded number.
	 * @param value The bit coded double value
	 * @return A list of string coded number values which represents the days of a month
	 */
	private List<String> getMonthDays(Double value) {
		List<String> monthDays = new ArrayList<String>();
		NumberFormat format = new DecimalFormat("00"); //$NON-NLS-1$

		if (value == 2147483647) {
			monthDays.add(Messages.TimeReporter_dataAllDays);
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

	/**
	 * This helper class takes a number coded value and translate it 
	 * to human readable Weekdays
	 * @param value Number coded weekday value
	 * @return Returns the short String representation of the coded value: 01=Mo, 02=Th, 03=We, 04=Th, 05=Fr, 06=Sa, 07=Su
	 */
	private List<String> getWeekDays(Double value) {
		List<String> weekDays = new ArrayList<String>();

		if (value == 127) {
			weekDays.add(Messages.TimeReporter_dataFullWeek);
		} else {
			for (String str : getHours(value)) {
				if (str.equalsIgnoreCase("01")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataMondayShort);
				if (str.equalsIgnoreCase("02")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataTuesdayShort);
				if (str.equalsIgnoreCase("03")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataWednesdayShort);
				if (str.equalsIgnoreCase("04")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataThursdayShort);
				if (str.equalsIgnoreCase("05")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataFridayShort);
				if (str.equalsIgnoreCase("06")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataSaturdayShort);
				if (str.equalsIgnoreCase("07")) //$NON-NLS-1$
					weekDays.add(Messages.TimeReporter_dataSundayShort);
			}
		}

		return weekDays;
	}

	/**
	 * This helper method wraps the sub task settings for the monitor.
	 */
	public void setMonitorTask(String taskName) {
		if (monitor != null) {
			monitor.subTask(taskName);
			monitor.worked(1);
		}
	}

}
