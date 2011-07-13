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

package de.chdev.artools.sql.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.chdev.artools.sql.editor.messages"; //$NON-NLS-1$
	public static String SQLEditor_errorSqlLong;
	public static String SQLEditor_errorSqlScriptLong;
	public static String SQLEditor_errorSqlScriptShort;
	public static String SQLEditor_errorSqlShort;
	public static String SQLEditor_formlistLabel;
	public static String SQLEditor_formlistLoadError;
	public static String SQLEditor_formlistLoading;
	public static String SQLEditor_formlistLoadJobName;
	public static String SQLEditor_queryJobName;
	public static String SQLEditor_resultToolbarExportTooltip;
	public static String SQLEditor_resultToolbarResultInfoFetched;
	public static String SQLEditor_resultToolbarResultInfoMilliseconds;
	public static String SQLEditor_resultToolbarResultInfoRows;
	public static String SQLEditor_resultToolbarResultInfoTooltip;
	public static String SQLEditor_resultToolbarStopSQLCallTooltip;
	public static String SQLEditor_sqlToolbarExecuteTooltip;
	public static String SQLEditor_sqlToolbarFileDefault;
	public static String SQLEditor_sqlToolbarFileTooltip;
	public static String SQLEditor_sqlToolbarLoadTooltip;
	public static String SQLEditor_sqlToolbarServerTooltip;
	public static String SQLEditorInput_serverNameNoConnection;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
