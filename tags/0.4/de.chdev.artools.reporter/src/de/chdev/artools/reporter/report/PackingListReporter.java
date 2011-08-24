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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ExternalReference;
import com.bmc.arsys.api.Form;
import com.bmc.arsys.api.PackingList;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.item.IModelItem;
import com.bmc.arsys.studio.model.item.ItemList;
import com.bmc.arsys.studio.model.store.ARServerStore;
import com.bmc.arsys.studio.model.store.IStore;
import com.bmc.arsys.studio.model.type.IARSystemTypes;
//import com.bmc.arsys.studio.ui.Studio;
import de.chdev.artools.reporter.interfaces.IReportObject;
import de.chdev.artools.reporter.interfaces.IReporter;
import de.chdev.artools.reporter.interfaces.ReportObjectImpl;
import de.chdev.artools.reporter.utils.ARHelpTools;

/**
 * @author Christoph Heinig
 * 
 */
public class PackingListReporter implements IReporter {

	private String name;

	private List<IStore> stores = new ArrayList<IStore>();

	// private IStore store = null;

	public PackingListReporter() {
		name = "Packing List Report"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.chdev.artools.reporter.report.IReporter#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.chdev.artools.reporter.report.IReporter#getObjectList()
	 */
	@Override
	public List<Object> getObjectList() {
		List<Object> returnList = new ArrayList<Object>();
		List<SelectObject> resultList = new ArrayList<SelectObject>();

		try {
			for (IStore store : stores) {
				ARServerStore arServerStore = null;
				if (store instanceof ARServerStore) {
					arServerStore = (ARServerStore) store;
				}
				ARServerUser server = arServerStore.getContext();
				List<String> packingLists = server.getListContainer(0,
						new int[] { Constants.ARCON_PACK }, true, null, null);
				// ItemList<IModelItem> packLists =
				// store.getList(IARSystemTypes.PACKING_LIST, false);
				for (String str : packingLists) {
					SelectObject selObj = new SelectObject();
					selObj.setName(str);
					selObj.setStore(store);
					resultList.add(selObj);
				}
			}
			Collections.sort(resultList);
		} catch (ARException e) {
			// Gib leere Liste zurück
			if (resultList == null) {
				resultList = new ArrayList<SelectObject>();
			} else {
				resultList.clear();
			}
		} catch (NullPointerException npe) {
			// Gib leere Liste zurück
			if (resultList == null) {
				resultList = new ArrayList<SelectObject>();
			} else {
				resultList.clear();
			}
		} catch (ModelException e) {
			// Gib leere Liste zurück
			if (resultList == null) {
				resultList = new ArrayList<SelectObject>();
			} else {
				resultList.clear();
			}
		}

		for (SelectObject selObj : resultList) {
			returnList.add(selObj);
		}

		return returnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.chdev.artools.reporter.report.IReporter#getDataForObject(java.lang
	 * .Object)
	 */
	@Override
	public IReportObject[][] getDataForObject(Object[] object) {
		// Prepare select object
		SelectObject selObj = null;
		IStore store = null;

		if (object.length > 0 && object[0] instanceof SelectObject) {
			selObj = (SelectObject) object[0];
		} else {
			throw new RuntimeException(
					"internal mapping error for select object"); //$NON-NLS-1$
		}

		IReportObject[][] resultMatrix = new IReportObject[][] { {} };
		Container container;
		PackingList packingList = null;
		List<Reference> referenceList = null;
		ARServerUser server = null;
		// Load Packinglist data for name
		try {
			// Get store from current selection object
			store = selObj.getStore();
			server = ((ARServerStore) store).getContext();
			container = server.getContainer(selObj.getName());
			if (container instanceof PackingList) {
				packingList = (PackingList) container;
			}
		} catch (ARException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (packingList != null) {
			System.out
					.println("Bearbeite packinglist " + packingList.getName()); //$NON-NLS-1$
			referenceList = packingList.getReferences();
			ArrayList<String[]> rowList = new ArrayList<String[]>();
			ArrayList<String> itemList = new ArrayList<String>();

			// Create first row with header data
			itemList.add(Messages.PackingListReporter_headerType);
			itemList.add(Messages.PackingListReporter_headerName);

			rowList.add(itemList.toArray(new String[itemList.size()]));
			itemList.clear();

			if (referenceList != null) {
				for (Reference ref : referenceList) {
					itemList.clear();

					try {
						itemList = getReferenceData(ref, store);
						// Clean all internal structure lists
						// indicated by missing name
						// if (itemList==null){
						// itemList = new ArrayList<String>();
						// }
						// if (itemList.size()>0 && (itemList.get(0)==null ||
						// itemList.get(0).length()<=0)){
						// itemList.clear();
						// }
					} catch (ARException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Add itemlist to rowlist if it contains objects
					if (itemList.size() > 0) {
						rowList.add(itemList.toArray(new String[itemList.size()]));
					}
				}
			}

			// Convert rowlist to resultmatrix
			resultMatrix = new IReportObject[rowList.size()][2];
			int i = 0;
			int j = 0;
			for (String[] array : rowList) {
				j = 0;
				for (String str : array) {
					IReportObject reportObject = new ReportObjectImpl();
					reportObject.setData(str);
					resultMatrix[i][j] = reportObject;
					j++;
				}
				i++;
			}
		}

		return resultMatrix;
	}

	private ArrayList<String> getReferenceData(Reference ref, IStore store)
			throws ARException {
		ArrayList<String> resultList = new ArrayList<String>();
		ReferenceType refType = ref.getReferenceType();
		Container container;
		ARServerUser server = null;
		// Initialize data loading
		try {
			server = ((ARServerStore) store).getContext();
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Switch to reference type
		if (refType == ReferenceType.ACTIVELINK) {
			resultList.add("Active Link"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.CHAR_MENU) {
			resultList.add("Menu"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.ESCALATION) {
			resultList.add("Escalation"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.FILTER) {
			resultList.add("Filter"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.IMAGE) {
			resultList.add("Image"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.PACKINGLIST_FILTER_GUIDE) {
			// resultList.add(ref.getName());
			// resultList.add("P Filter Guide");
		} else if (refType == ReferenceType.PACKINGLIST_GUIDE) {
			// resultList.add(ref.getName());
			// resultList.add("P Active Link Guide");
		} else if (refType == ReferenceType.PACKINGLIST_WEBSERVICE) {
			// resultList.add(ref.getName());
			// resultList.add("P Webservice");
		} else if (refType == ReferenceType.SCHEMA) {
			resultList.add("Form"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.CONTAINER) {
			container = server.getContainer(ref.getName());
			int type = container.getType();
			switch (type) {
			case 1:
				resultList.add("Active Link Guide"); //$NON-NLS-1$
				break;
			case 2:
				resultList.add("Application"); //$NON-NLS-1$
				break;
			case 3:
				resultList.add("Packing List"); //$NON-NLS-1$
				break;
			case 4:
				resultList.add("Filter Guide"); //$NON-NLS-1$
				break;
			case 5:
				resultList.add("Webservice"); //$NON-NLS-1$
				break;
			default:
				resultList.add("Container"); //$NON-NLS-1$
			}
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.DISTMAPPING_DATA) {
			resultList.add("Distributed Mapping"); //$NON-NLS-1$
			resultList.add(ref.getLabel());
		} else if (refType == ReferenceType.FLASH_BOARD_DEF) {
			resultList.add("Flash Board"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.FLASH_DATA_SOURCE_DEF) {
			resultList.add("Flash Data"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.FLASH_VARIABLE_DEF) {
			resultList.add("Flash Variable"); //$NON-NLS-1$
			resultList.add(ref.getName());
		} else if (refType == ReferenceType.GROUP_DATA) {
			resultList.add("Group"); //$NON-NLS-1$
			resultList.add(ref.getLabel());
		} else if (refType == ReferenceType.PACKINGLIST_APP) {
			// resultList.add(ref.getName());
			// resultList.add("P Application");
		} else if (refType == ReferenceType.PACKINGLIST_DSOPOOL) {
			// resultList.add(ref.getLabel());
			// resultList.add("Distributed Pool");
		} else if (refType == ReferenceType.PACKINGLIST_PACK) {
			// resultList.add(ref.getName());
			// resultList.add("P Packing List");
		} else {
			resultList.add("Unknown (" + refType.toString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			resultList.add(ref.getName());
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
		return false;
	}

	class SelectObject implements Comparable {
		private String name = ""; //$NON-NLS-1$

		private IStore store = null;

		private String storeName = ""; //$NON-NLS-1$

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
		// TODO Auto-generated method stub

	}

}
