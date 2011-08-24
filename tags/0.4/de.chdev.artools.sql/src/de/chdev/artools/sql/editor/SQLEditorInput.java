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
package de.chdev.artools.sql.editor;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.store.ARServerStore;
import com.bmc.arsys.studio.model.store.IStore;

import de.chdev.artools.sql.utils.DBUtils;
import de.chdev.artools.sql.utils.SQLHelpTools;

/**
 * @author Christoph Heinig
 * 
 */
public class SQLEditorInput implements IEditorInput {

	private IStore store = null;

	private File file = null;

	public SQLEditorInput(IStore store) {
		this.store = store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		String storeName = Messages.SQLEditorInput_serverNameNoConnection;
		if (store != null) {
			storeName = store.getName();
		}
		return "SQL Editor - " + storeName; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		String storeName = Messages.SQLEditorInput_serverNameNoConnection;
		if (store != null) {
			storeName = store.getName();
		}
		return "SQL Editor - " + storeName; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public SQLResult executeSQLStatement(String statement) throws ARException {
		SQLResult result = null;

		if (store != null) {
			try {
				result = ((ARServerStore) store).getContext().getListSQL(
						statement, 0, true);
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public String[] getSQLStatementHeader(String statement) throws ARException {
		return SQLHelpTools.getInstance().getHeaderData(store, statement);
	}

	public String getStoreName() {
		String storeName = ""; //$NON-NLS-1$
		if (store != null) {
			storeName = store.getName();
		}
		return storeName;
	}

	public String[][] getFormnames() throws ARException {
		String[][] resultNames = new String[][] { {} };
		List<String> formnameList = new ArrayList<String>();
		List<String> viewnameList = new ArrayList<String>();
		String statement = "select name, viewname from arschema where viewname is not null order by name"; //$NON-NLS-1$
		SQLResult result = executeSQLStatement(statement);
		List<List<Value>> valueList = result.getContents();
		for (List<Value> row : valueList) {
			formnameList.add(row.get(0).toString());
			viewnameList.add(row.get(1).toString());
		}

		resultNames = new String[formnameList.size()][2];
		for (int i = 0; i < formnameList.size(); i++) {
			resultNames[i][0] = formnameList.get(i);
			resultNames[i][1] = viewnameList.get(i);
		}

		return resultNames;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setStore(IStore store) {
		this.store = store;
	}
}
