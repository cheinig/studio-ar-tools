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

package de.chdev.artools.reporter.report;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.chdev.artools.reporter.report.messages"; //$NON-NLS-1$
	public static String FormCompareReporter_dataNone;
	public static String FormCompareReporter_dataNotAvailable;
	public static String FormCompareReporter_headerID;
	public static String FormCompareReporter_headerLimits;
	public static String FormCompareReporter_headerName;
	public static String FormCompareReporter_headerOption;
	public static String FormCompareReporter_headerPermission;
	public static String FormCompareReporter_headerType;
	public static String FormCompareReporter_monitorTaskError;
	public static String FormCompareReporter_monitorTaskFieldCompare;
	public static String FormCompareReporter_monitorTaskFieldID;
	public static String FormCompareReporter_monitorTaskGenerateData;
	public static String FormCompareReporter_monitorTaskGenerateHeader;
	public static String FormCompareReporter_monitorTaskGenerateOutput;
	public static String FormCompareReporter_monitorTaskInit;
	public static String PackingListReporter_headerName;
	public static String PackingListReporter_headerType;
	public static String TimeReporter_dataAllDays;
	public static String TimeReporter_dataFridayShort;
	public static String TimeReporter_dataFullWeek;
	public static String TimeReporter_dataMondayShort;
	public static String TimeReporter_dataNo;
	public static String TimeReporter_dataSaturdayShort;
	public static String TimeReporter_dataSundayShort;
	public static String TimeReporter_dataThursdayShort;
	public static String TimeReporter_dataTuesdayShort;
	public static String TimeReporter_dataWednesdayShort;
	public static String TimeReporter_dataYes;
	public static String TimeReporter_headerDays;
	public static String TimeReporter_headerEnabled;
	public static String TimeReporter_headerInterval;
	public static String TimeReporter_headerName;
	public static String TimeReporter_headerTime;
	public static String TimeReporter_monitorArchive;
	public static String TimeReporter_monitorCreateOutput;
	public static String TimeReporter_monitorEscalation;
	public static String TimeReporter_monitorGenerateOutput;
	public static String TimeReporter_monitorInitArchive;
	public static String TimeReporter_monitorInitEscalation;
	public static String TimeReporter_monitorLoadEscalation;
	public static String TimeReporter_monitorLoadingArchive;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
