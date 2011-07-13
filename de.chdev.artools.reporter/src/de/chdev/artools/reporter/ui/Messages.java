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

package de.chdev.artools.reporter.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.chdev.artools.reporter.ui.messages"; //$NON-NLS-1$
	public static String CreateReportDialog_dialogInformation;
	public static String CreateReportDialog_dialogTitle;
	public static String CreateReportDialog_errorMissingFilenameLong;
	public static String CreateReportDialog_errorMissingFilenameShort;
	public static String CreateReportDialog_errorNoMultiselectLong;
	public static String CreateReportDialog_errorNoMultiselectShort;
	public static String CreateReportDialogVE_exportGroupLabel;
	public static String CreateReportDialogVE_exportLabel;
	public static String CreateReportDialogVE_fileDialogTitle;
	public static String CreateReportDialogVE_filenameLabel;
	public static String CreateReportDialogVE_introLabel;
	public static String CreateReportDialogVE_loadObjectsLabel;
	public static String CreateReportDialogVE_objectTableHeaderName;
	public static String CreateReportDialogVE_reportGroupLabel;
	public static String CreateReportDialogVE_reportLabel;
	public static String CreateReportDialogVE_serverEntryAll;
	public static String CreateReportDialogVE_serverLabel;
	public static String ScreenExporterDialogVE_exportGroupLabel;
	public static String ScreenExporterDialogVE_fileDialogTitle;
	public static String ScreenExporterDialogVE_resultGroupLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
