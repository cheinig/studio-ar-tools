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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.chdev.artools.reporter.views.messages"; //$NON-NLS-1$
	public static String EscalationView_actionLoadArchiveName;
	public static String EscalationView_actionLoadArchiveTooltip;
	public static String EscalationView_actionLoadEscalationName;
	public static String EscalationView_actionLoadEscalationTooltip;
	public static String EscalationView_allDays;
	public static String EscalationView_archiveValueEnabledNo;
	public static String EscalationView_archiveValueEnabledYes;
	public static String EscalationView_dayFridayShort;
	public static String EscalationView_dayMondayShort;
	public static String EscalationView_daySaturdayShort;
	public static String EscalationView_daySundayShort;
	public static String EscalationView_dayThursdayShort;
	public static String EscalationView_dayTuesdayShort;
	public static String EscalationView_dayWednesdayShort;
	public static String EscalationView_escalationValueEnabledNo;
	public static String EscalationView_escalationValueEnabledYes;
	public static String EscalationView_fullWeek;
	public static String EscalationView_messageDialogTitle;
	public static String EscalationView_reloadDataServer;
	public static String EscalationView_reloadDataTime;
	public static String EscalationView_reloadDataType;
	public static String EscalationView_reloadDataTypeArchive;
	public static String EscalationView_reloadDataTypeEscalation;
	public static String EscalationView_runJobArchiveLoading;
	public static String EscalationView_runJobArchiveName;
	public static String EscalationView_runJobErrorNoServerLong;
	public static String EscalationView_runJobErrorNoServerLong_Archive;
	public static String EscalationView_runJobErrorNoServerShort;
	public static String EscalationView_runJobErrorNoServerShort_Archive;
	public static String EscalationView_runJobEscalationLoading;
	public static String EscalationView_runJobEscalationName;
	public static String EscalationView_tableColumName;
	public static String EscalationView_tableColumnDays;
	public static String EscalationView_tableColumnEnabled;
	public static String EscalationView_tableColumnInterval;
	public static String EscalationView_tableColumnTime;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
