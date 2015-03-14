# Timetable View #
The timetable plugin provides a view for escalation and archiving data. These data will be displayed in a table, which is sortable by each column. The sortable time/interval columns can be used to sort the elements by their execution time.

To open a timetable view you can use the "clock" symbol within the StART toolbar. Every time the "clock" item is used, a new view will be opened, so you can start several searches and compare them.
Two different search methods are available: escalation and archiv<br />
They can be started within the context menu of the table or with the toolbar items of the view. As target server the currently selected server within the AR navigator will be used. The server will also be used if one of its subelements is selected.

At the top of the view there will be displayed some information about the last search like:
  * Server
> > The AR server name used by the last search
  * Type
> > The search type of the last search (escalation or archive). If a search process is currently running, the search type will be displayed with the prefix "loading"
  * Time
> > The date/time value when the last search processed finished.


**Example:**<br />
You are logged in to "Server 1" and "Server 2", which are displayed in the AR Navigator. If you want to show all escalation times from "Server 2", you must select the servername "Server 2" or one of its sub elements from the AR Navigator. After this you use the "Reload Escalation Table" function from the toolbar (or context menu). After loading is finished, the search results will be displayed within the table.

## Escalation Times ##
To show escalation times you can use the "Timetable View" with the "Reload Escalation Table" function. This function use the reporting modul from the reporting plugin to load the time data. For a complete description of the output you can see [ReportingPlugin](ReportingPlugin.md).

![http://studio-ar-tools.googlecode.com/svn/wiki/images/timetableplugin_escalation.gif](http://studio-ar-tools.googlecode.com/svn/wiki/images/timetableplugin_escalation.gif)

A double click on a table entry will open the corresponding escalation in the escalation editor.

## Archiving Times ##
To show archiving times you can use the "Timetable View" with the "Reload Archive Table" function. This function use the reporting modul from the reporting plugin to load all forms with their archiving time data (if available). For a complete description of the output you can see [ReportingPlugin](ReportingPlugin.md).

![http://studio-ar-tools.googlecode.com/svn/wiki/images/timetableplugin_archive.gif](http://studio-ar-tools.googlecode.com/svn/wiki/images/timetableplugin_archive.gif)

A double click on a table entry will open the corresponding form in the form editor.