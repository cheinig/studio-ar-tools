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
package de.chdev.artools.reporter.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.bmc.arsys.api.ARTypeMgr;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.studio.model.store.IStore;
import com.bmc.arsys.studio.ui.Studio;
import com.bmc.arsys.studio.ui.views.navigator.model.INode;

/**
 * @author Christoph Heinig
 * 
 */
public class ARHelpTools {

    private static ARHelpTools instance = null;

    private ARHelpTools() {

    }

    public static ARHelpTools getInstance() {
	if (instance == null) {
	    instance = new ARHelpTools();
	}
	return instance;
    }

    public IStore getCurrentStore() {
	IStore store = null;
	CommonViewer navView = null;
	IViewReference reference = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().findViewReference(
			"com.bmc.arsys.studio.ui.views.navigator.viewer");
	if (reference != null) {
	    org.eclipse.ui.IViewPart view = reference.getView(false);
	    if (view != null) {
		navView = ((CommonNavigator) view).getCommonViewer();
	    } else {
		navView = null;
	    }
	} else {
	    navView = null;
	}

	if (navView != null) {
	    System.out.println("Viewer gefunden");
	    INode node = null;
	    ISelection selection = navView.getSelection();
	    if (!selection.isEmpty()) {
		IStructuredSelection structured = (IStructuredSelection) selection;
		if (structured.size() == 1
			&& (structured.getFirstElement() instanceof INode))
		    node = (INode) structured.getFirstElement();
	    }
	    if (node != null) {
		System.out.println("Node gefunden");
		store = node.getStore();
	    } else {
		System.out.println("Keine Node gefunden");
	    }
	}

	return store;
    }

    public String getCurrentStoreName() {
	IStore store = getCurrentStore();
	String result = null;

	// If current store is defined, return the name
	if (store != null) {
	    result = store.getName();
	}

	return result;
    }
    
    public IStore getStoreByName(String servername){
	IStore store = Studio.getInstance().getStore(servername);
	return store;
    }

    public String[] getAllActiveServerNames() {
	List<IStore> serverList = null;
	ArrayList<String> serverNameList;
	String[] server;
	serverList = getAllActiveStores();
	if (serverList == null) {
	    serverList = new ArrayList<IStore>();
	}

	// Convert serverlist to String Array
	serverNameList = new ArrayList<String>();
	for (IStore store : serverList) {
		serverNameList.add(store.getName());
	}
	
	server = serverNameList.toArray(new String[serverNameList.size()]);

	return server;
    }
    
    public List<IStore> getAllActiveStores(){
	List<IStore> storeList = new ArrayList<IStore>();
	for (IStore store : Studio.getInstance().getAllStores()){
	    if (store.isConnected()){
		storeList.add(store);
	    }
	}
	return storeList;
    }
    
    public String getFieldOptionString(int option){
	switch (option){
	case Constants.AR_FIELD_OPTION_DISPLAY:
	    return "Display Only";
	case Constants.AR_FIELD_OPTION_OPTIONAL:
	    return "Optional";
	case Constants.AR_FIELD_OPTION_REQUIRED:
	    return "Required";
	case Constants.AR_FIELD_OPTION_SYSTEM:
	    return "System";
	default:
	    return "Unknown";
	}
    }
    
    public String getFieldTypeString(int type){
	switch (type){
	case 0: // DataType.NULL.toInt():
	    return "NULL";
	case 1: // DataType.KEYWORD.toInt():
	    return "Keyword";
	case 2: // DataType.INTEGER.toInt():
	    return "Integer";
	case 3: // DataType.REAL.toInt():
	    return "Real";
	case 4: // DataType.CHAR.toInt():
	    return "Character";
	case 5: // DataType.DIARY.toInt():
	    return "Diary";
	case 6: // DataType.ENUM.toInt():
	    return "Enum";
	case 7: // DataType.TIME.toInt():
	    return "Time";
	case 8: // DataType.BITMASK.toInt():
	    return "Bitmask";
	case 9: // DataType.BYTES.toInt():
	    return "Bytes";
	case 10: // DataType.DECIMAL.toInt():
	    return "Decimal";
	case 11: // DataType.ATTACHMENT.toInt():
	    return ("Attachment");
	case 12: // DataType.CURRENCY.toInt():
	    return "Currency";
	case 13: // DataType.DATE.toInt():
	    return "Date";
	case 14: // DataType.TIME_OF_DAY.toInt():
	    return "Time of Day";
	case 30: // DataType.JOIN.toInt():
	    return "Join";
	case 31: // DataType.TRIM.toInt():
	    return "Trim";
	case 32: // DataType.CONTROL.toInt():
	    return "Control";
	case 33: // DataType.TABLE.toInt():
	    return "Table";
	case 34: // DataType.COLUMN.toInt():
	    return "Column";
	case 35: // DataType.PAGE.toInt():
	    return "Page";
	case 36: // DataType.PAGE_HOLDER.toInt():
	    return "Page Holder";
	case 37: // DataType.ATTACHMENT_POOL.toInt():
	    return "Attachment Pool";
	case 40: // DataType.ULONG.toInt():
	    return "Long";
	case 41: // DataType.COORDS.toInt():
	    return "Coords";
	case 42: // DataType.VIEW.toInt():
	    return "View";
	case 43: // DataType.DISPLAY.toInt():
	    return "Display";
	case 100: // DataType.VALUELIST.toInt():
	    return "Valuelist";
	default:
	    return "Unknown";
	}
    }
    
}
