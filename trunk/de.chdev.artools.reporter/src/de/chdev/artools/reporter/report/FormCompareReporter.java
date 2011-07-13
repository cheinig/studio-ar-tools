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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldLimit;
import com.bmc.arsys.api.Form;
import com.bmc.arsys.api.PermissionInfo;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.store.ARServerStore;
import com.bmc.arsys.studio.model.store.IStore;

import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;
import de.chdev.artools.reporter.utils.ARHelpTools;

/**
 * @author Christoph Heinig
 * 
 */
public class FormCompareReporter implements IReporter {

    private String name = null;

    private IStore store = null;

    private IProgressMonitor monitor;

    public static final int FIELD_DBNAME = 1;

    public static final int FIELD_DATATYPE = 2;

    public static final int FIELD_OPTION = 4;

    public static final int FIELD_LIMIT = 8;

    public static final int FIELD_PERMISSION = 16;

    private List<IStore> stores = new ArrayList<IStore>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#getDataForObject(java
     * .lang. String)
     */
    @Override
    public IReportObject[][] getDataForObject(Object[] object) {
	IReportObject[][] resultMatrix = null;
	ArrayList<Form> formList = new ArrayList<Form>();
	ArrayList<SelectObject> selectList = new ArrayList<SelectObject>();
	Map<Integer, ArrayList<IReportObject>> idMap = new TreeMap<Integer, ArrayList<IReportObject>>();
	List<Integer> ids = new ArrayList<Integer>();

	// Start monitor output
	if (monitor != null) {
	    monitor.beginTask(Messages.FormCompareReporter_monitorTaskInit, object.length);
	}

	// Load forms and ids from AR
	for (Object obj : object) {

	    // Monitor
	    if (obj != null) {
		setMonitorTask(obj.toString());
	    } else {
		setMonitorTask(Messages.FormCompareReporter_monitorTaskError);
	    }

	    // initialize search
	    SelectObject selObj = null;
	    if (obj instanceof SelectObject) {
		selObj = (SelectObject) obj;
	    } else {
		throw new RuntimeException(
			"internal mapping error for selection object"); //$NON-NLS-1$
	    }
	    String formName = selObj.getName();
	    this.store = selObj.getStore();

	    try {
		Form tempForm = ((ARServerStore)store).getContext().getForm(formName);
		formList.add(tempForm);
		selObj.setForm(tempForm);
		selectList.add(selObj);
		List<Integer> idList = ((ARServerStore)store).getContext().getListField(
			formName, Constants.AR_FIELD_TYPE_ALL, 0);
		for (Integer id : idList) {
		    if (!idMap.containsKey(id)) {
			idMap.put(id, null);
			ids.add(new Integer(id));
		    }
		}
	    } catch (ARException e) {
		// Field does not exist and will be ignored
	    } catch (ModelException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	// Start monitor output
	if (monitor != null) {
	    monitor.beginTask(Messages.FormCompareReporter_monitorTaskFieldCompare, idMap.size());
	}

	// Load and compare fields
	boolean equal = true;
	int fieldDiff = 0;
	ArrayList<Field> fieldList = new ArrayList<Field>();
	for (Integer id : ids) {
	    // initialize
	    fieldList.clear();
	    equal = true;
	    fieldDiff = 0;

	    // Monitor
	    setMonitorTask(Messages.FormCompareReporter_monitorTaskFieldID + id);

	    for (SelectObject selectObj : selectList) {
		Form form = selectObj.getForm();
		Field field = null;
		try {
		    this.store = selectObj.getStore();
		    field = ((ARServerStore)store).getContext().getField(form.getName(), id);
		} catch (ARException e) {
		    // e.printStackTrace();
		} catch (ModelException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		fieldList.add(field);

		// If the field is not available, no compare is needed
		// This should avoid check to save time
		if (field == null) {
		    fieldDiff = FIELD_DBNAME | FIELD_DATATYPE | FIELD_OPTION
			    | FIELD_LIMIT | FIELD_PERMISSION;
		    equal = false;
		}
	    }

	    // Compare the loaded fields
	    if (equal == true) {
		fieldDiff = compareFields(fieldList);
		if (fieldDiff > 0) {
		    equal = false;
		}
	    }

	    // If the fields are different, they will be put into the result
	    // map.
	    // If the fields are equal, the field id will be removed from the
	    // result map.
	    // If only one form is available, all fields will be put into the
	    // result map.
	    if (equal == false || formList.size() == 1) {
		idMap.put(id, generateOutput(fieldList, fieldDiff));
	    } else {
		idMap.remove(id);
	    }

	}

	// Start monitor output
	if (monitor != null) {
	    monitor.beginTask(Messages.FormCompareReporter_monitorTaskGenerateOutput, 2);
	}

	// Convert idMap into resultMatrix
	resultMatrix = new IReportObject[idMap.size() + 1][];
	// Monitor
	setMonitorTask(Messages.FormCompareReporter_monitorTaskGenerateHeader);
	ArrayList<IReportObject> header = generateHeader(selectList);
	resultMatrix[0] = header.toArray(new ReportObjectImpl[header.size()]);
	int i = 1;
	// Monitor
	setMonitorTask(Messages.FormCompareReporter_monitorTaskGenerateData);
	for (Integer id : idMap.keySet()) {
	    // IReportObject repObj = new ReportObjectImpl();
	    resultMatrix[i] = idMap.get(id).toArray(
		    new IReportObject[idMap.get(id).size()]);
	    i++;
	}

	return resultMatrix;
    }

    private ArrayList<IReportObject> generateOutput(ArrayList<Field> fieldList,
	    int fieldDiff) {
	ArrayList<IReportObject> resultList = new ArrayList<IReportObject>();
	ARHelpTools helper = ARHelpTools.getInstance();

	// Set field id
	for (Field field : fieldList) {
	    if (field != null) {
		IReportObject repObj = new ReportObjectImpl();
		repObj.setData(String.valueOf(field.getFieldID()));
		resultList.add(repObj);
		break; // Will be set only once
	    }
	}

	// Set field db name
	for (Field field : fieldList) {
	    IReportObject repObj = new ReportObjectImpl();
	    if (field != null) {
		repObj.setData(field.getName());
	    } else {
		repObj.setData(Messages.FormCompareReporter_dataNotAvailable);
	    }
	    if ((fieldDiff & FIELD_DBNAME) > 0) {
		repObj.setClassification(IReportObject.CLASSIFICATION_ERROR);
	    }
	    resultList.add(repObj);
	}

	// Set field type
	for (Field field : fieldList) {
	    IReportObject repObj = new ReportObjectImpl();
	    if (field != null) {
		repObj.setData(helper.getFieldTypeString(field.getDataType()));
	    } else {
		repObj.setData(Messages.FormCompareReporter_dataNotAvailable);
	    }
	    if ((fieldDiff & FIELD_DATATYPE) > 0) {
		repObj.setClassification(IReportObject.CLASSIFICATION_ERROR);
	    }
	    resultList.add(repObj);
	}

	// Set field option
	for (Field field : fieldList) {
	    IReportObject repObj = new ReportObjectImpl();
	    if (field != null) {
		repObj.setData(helper.getFieldOptionString(field
			.getFieldOption()));
	    } else {
		repObj.setData(Messages.FormCompareReporter_dataNotAvailable);
	    }
	    if ((fieldDiff & FIELD_OPTION) > 0) {
		repObj.setClassification(IReportObject.CLASSIFICATION_ERROR);
	    }
	    resultList.add(repObj);
	}

	// Set field permission
	for (Field field : fieldList) {
	    IReportObject repObj = new ReportObjectImpl();
	    if (field != null && field.getPermissions() != null) {
		List<PermissionInfo> permissions = field.getPermissions();
		String dataString = null;
		// Set string value for empty lists
		if (permissions == null || permissions.size() == 0) {
		    dataString = Messages.FormCompareReporter_dataNone;
		} else {
		    // Build string value for all permissions
		    for (PermissionInfo permInfo : permissions) {
			if (dataString == null) {
			    dataString = ""; //$NON-NLS-1$
			} else {
			    dataString = dataString + ";"; //$NON-NLS-1$
			}
			dataString = dataString + permInfo.toString();
		    }
		}
		repObj.setData(dataString);
	    } else {
		repObj.setData(Messages.FormCompareReporter_dataNotAvailable);
	    }
	    if ((fieldDiff & FIELD_PERMISSION) > 0) {
		repObj.setClassification(IReportObject.CLASSIFICATION_ERROR);
	    }
	    resultList.add(repObj);
	}

	// Set field limits
	for (Field field : fieldList) {
	    IReportObject repObj = new ReportObjectImpl();
	    if (field != null && field.getFieldLimit() != null) {
		repObj.setData(field.getFieldLimit().toString());
	    } else {
		repObj.setData(Messages.FormCompareReporter_dataNotAvailable);
	    }
	    if ((fieldDiff & FIELD_LIMIT) > 0) {
		repObj.setClassification(IReportObject.CLASSIFICATION_WARN);
	    }
	    resultList.add(repObj);
	}

	return resultList;
    }

    // Build table header
    private ArrayList<IReportObject> generateHeader(
	    ArrayList<SelectObject> selectList) {
	ArrayList<IReportObject> resultList = new ArrayList<IReportObject>();

	IReportObject repObj = new ReportObjectImpl();
	repObj.setData(Messages.FormCompareReporter_headerID);
	resultList.add(repObj);
	for (SelectObject selObj : selectList) {
	    repObj = new ReportObjectImpl();
	    repObj.setData(Messages.FormCompareReporter_headerName + selObj.getForm().getName() + " (" //$NON-NLS-2$ //$NON-NLS-1$
		    + selObj.getStoreName() + ")"); //$NON-NLS-1$
	    resultList.add(repObj);
	}
	for (SelectObject selObj : selectList) {
	    repObj = new ReportObjectImpl();
	    repObj.setData(Messages.FormCompareReporter_headerType + selObj.getForm().getName() + " (" //$NON-NLS-2$ //$NON-NLS-1$
		    + selObj.getStoreName() + ")"); //$NON-NLS-1$
	    resultList.add(repObj);
	}
	for (SelectObject selObj : selectList) {
	    repObj = new ReportObjectImpl();
	    repObj.setData(Messages.FormCompareReporter_headerOption + selObj.getForm().getName() + " (" //$NON-NLS-2$ //$NON-NLS-1$
		    + selObj.getStoreName() + ")"); //$NON-NLS-1$
	    resultList.add(repObj);
	}
	for (SelectObject selObj : selectList) {
	    repObj = new ReportObjectImpl();
	    repObj.setData(Messages.FormCompareReporter_headerPermission + selObj.getForm().getName() + " (" //$NON-NLS-2$ //$NON-NLS-1$
		    + selObj.getStoreName() + ")"); //$NON-NLS-1$
	    resultList.add(repObj);
	}
	for (SelectObject selObj : selectList) {
	    repObj = new ReportObjectImpl();
	    repObj.setData(Messages.FormCompareReporter_headerLimits + selObj.getForm().getName() + " (" //$NON-NLS-2$ //$NON-NLS-1$
		    + selObj.getStoreName() + ")"); //$NON-NLS-1$
	    resultList.add(repObj);
	}

	return resultList;
    }

    /**
     * This method will return a list of all difference indicator. The result
     * will be a bitwise combination of the FIELD Constants.
     * 
     * @param fieldList
     * @return
     */
    private int compareFields(ArrayList<Field> fieldList) {
	// boolean equal = true;
	int result = 0;

	Field baseField = null;
	boolean setFirst = true;
	for (Field field : fieldList) {
	    // Set compare base on first loop
	    if (setFirst == true) {
		baseField = field;
		setFirst = false;
	    } else {
		// Start compare
		// Start null compare
		if ((baseField == null && field != null)
			|| (field == null && baseField != null)) {
		    result = FIELD_DBNAME | FIELD_DATATYPE | FIELD_OPTION
			    | FIELD_LIMIT | FIELD_PERMISSION;
		    // equal = false;
		    break; // Stop further process
		} else if (baseField == null && field == null) {
		    break; // Stop further process
		}

		// if (!baseField.equals(field)){
		// equal = false;
		// break;
		// }

		// Start content compare
		// Compare field db name
		if (!baseField.getName().equals(field.getName())) {
		    result = result | FIELD_DBNAME;
		    // equal = false;
		    // break;
		}
		// Compare data type
		if (baseField.getDataType() != field.getDataType()) {
		    result = result | FIELD_DATATYPE;
		    // equal = false;
		    // break;
		}
		// Compare field option
		if (baseField.getFieldOption() != field.getFieldOption()) {
		    result = result | FIELD_OPTION;
		    // equal = false;
		    // break;
		}
		// Compare field permissions
		if ((baseField.getPermissions() != null && field
			.getPermissions() == null)
			|| (baseField.getPermissions() == null && field
				.getPermissions() != null)) {
		    result = result | FIELD_PERMISSION;
		} else if (baseField.getPermissions() != null
			&& field.getPermissions() != null) {
		    List<PermissionInfo> basePermissions = baseField
			    .getPermissions();
		    List<PermissionInfo> fieldPermissions = field
			    .getPermissions();
		    // Check if permission count is equal
		    if (basePermissions.size() != fieldPermissions.size()) {
			result = result | FIELD_PERMISSION;
		    } else {
			Map<Integer, Integer> fieldIdMap = new HashMap<Integer, Integer>();
			// Fill compare map with base field data
			for (PermissionInfo permInfo : basePermissions) {
			    fieldIdMap.put(permInfo.getGroupID(), permInfo
				    .getPermissionValue());
			}
			// Check if permissions are equal
			for (PermissionInfo permInfo : fieldPermissions) {
			    if (fieldIdMap.containsKey(permInfo.getGroupID())) {
				if (fieldIdMap.get(permInfo.getGroupID())
					.compareTo(
						permInfo.getPermissionValue()) != 0) {
				    result = result | FIELD_PERMISSION;
				}
			    } else {
				result = result | FIELD_PERMISSION;
			    }
			}
		    }
		}
		// Compare field length
		if ((baseField.getFieldLimit() != null && field.getFieldLimit() == null)
			|| (baseField.getFieldLimit() == null && field
				.getFieldLimit() != null)) {
		    result = result | FIELD_LIMIT;
		    // equal = false;
		    // break;
		} else if (baseField.getFieldLimit() != null
			&& field.getFieldLimit() != null) {
		    FieldLimit baseLimit = baseField.getFieldLimit();
		    FieldLimit fieldLimit = field.getFieldLimit();
		    if (!baseLimit.equals(fieldLimit)) {
			result = result | FIELD_LIMIT;
			// equal = false;
			// break;
		    }
		}
		// Check if all values are set and break
		if (result == (FIELD_DBNAME | FIELD_DATATYPE | FIELD_OPTION
			| FIELD_LIMIT | FIELD_PERMISSION)) {
		    break;
		}
	    }
	}

	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.chdev.artools.reporter.interfaces.IReporter#getName()
     */
    @Override
    public String getName() {
	if (name == null) {
	    return "Form Compare Reporter"; //$NON-NLS-1$
	} else {
	    return name;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.chdev.artools.reporter.interfaces.IReporter#getObjectList()
     */
    @Override
    public List<Object> getObjectList() {
	List<Object> resultList = new ArrayList<Object>();

	try {
	    List<SelectObject> tempList = new ArrayList<SelectObject>();
	    for (IStore loopStore : stores) {
		ARServerUser server = ((ARServerStore)loopStore).getContext();
		List<String> formLists = server.getListForm();
		for (String str : formLists) {
		    SelectObject selObj = new SelectObject();
		    selObj.setName(str);
		    selObj.setStore(loopStore);
		    tempList.add(selObj);
		}
	    }
	    Collections.sort(tempList);
	    resultList.addAll(tempList);
	} catch (ARException e) {
	    // Gib leere Liste zurück
	    if (resultList == null) {
		resultList = new ArrayList<Object>();
	    } else {
		resultList.clear();
	    }
	} catch (NullPointerException npe) {
	    // Gib leere Liste zurück
	    if (resultList == null) {
		resultList = new ArrayList<Object>();
	    } else {
		resultList.clear();
	    }
	} catch (ModelException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return resultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#setStore(com.bmc.arsys
     * .studio .model.store.IStore)
     */
    @Override
    public void setStores(List<IStore> stores) {
	this.stores = stores;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#isMultiselectAllowed()
     */
    @Override
    public Boolean isMultiselectAllowed() {
	return true;
    }

    class SelectObject implements Comparable {
	private String name = ""; //$NON-NLS-1$

	private IStore store = null;

	private String storeName = ""; //$NON-NLS-1$

	private Form form = null;

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
	    this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
	    return name;
	}

	/**
	 * @param store
	 *            the store to set
	 */
	public void setStore(IStore store) {
	    this.store = store;
	    this.storeName = store.getName();
	}

	/**
	 * @return the store
	 */
	public IStore getStore() {
	    return store;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(Form form) {
	    this.form = form;
	}

	/**
	 * @return the form
	 */
	public Form getForm() {
	    return form;
	}

	public String toString() {
	    return name + " (" + storeName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getStoreName() {
	    return storeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object arg0) {
	    int result = this.toString().compareTo(arg0.toString());
	    return result;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.chdev.artools.reporter.interfaces.IReporter#setMonitor(org.eclipse
     * .core.runtime.IProgressMonitor)
     */
    @Override
    public void setMonitor(IProgressMonitor monitor) {
	this.monitor = monitor;
    }

    // Handling monitor tasks
    public void setMonitorTask(String taskName) {
	if (monitor != null) {
	    monitor.subTask(taskName);
	    monitor.worked(1);
	}
    }
}
