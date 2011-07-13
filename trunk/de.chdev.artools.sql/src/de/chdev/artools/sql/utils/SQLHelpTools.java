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
package de.chdev.artools.sql.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Point;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.ServerInfoMap;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.studio.model.ModelException;
import com.bmc.arsys.studio.model.store.ARServerStore;
import com.bmc.arsys.studio.model.store.IStore;

/**
 * @author Christoph Heinig
 * 
 */
public class SQLHelpTools {

	private static SQLHelpTools instance = null;

	public static SQLHelpTools getInstance() {
		if (instance == null) {
			instance = new SQLHelpTools();
		}
		return instance;
	}

	public String[] getHeaderData(IStore store, String statement)
			throws ARException {
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<String> headerInformation = new ArrayList<String>();
		ArrayList<DBEntry<String, String>> fromInformation;

		List<Token> tokenizedStatement = DBUtils.getInstance().getTokens(
				new StringReader(statement));

		// Parse Header labels
		// System.out.println("Headerdata:");
		headerInformation = parseColumnObjects(tokenizedStatement);

		// Parse Table/View names
		// System.out.println("Objectdata:");
		fromInformation = parseFromObjects(tokenizedStatement);

		// Check if header label is wildcard
		for (String header : headerInformation) {
			if (header.equals("*") || header.endsWith(".*")) {
				headerList.addAll(findColumnNames(header, fromInformation,
						store));
			} else {
				headerList.add(header);
			}
		}

		return headerList.toArray(new String[headerList.size()]);
	}

	private ArrayList<String> findColumnNames(String header,
			ArrayList<DBEntry<String, String>> objectList, IStore store)
			throws ARException {
		ArrayList<String> headerList = new ArrayList<String>();
		String prefix = null;
		List<String> objectName = new ArrayList<String>();
		Map<Integer, Value> dbData = getDBData(store);

		// Look for table prefix
		if (header.contains(".")) {
			prefix = header.substring(0, header.indexOf("."));
		}

		// Get object name to look for columns
		// If prefix is given, only columns for one object will be read
		if (prefix != null) {
			for (DBEntry<String, String> entry : objectList) {
				if (entry.getKey().equals(prefix)) {
					objectName.add(entry.getValue());
				}
			}
		} else if (prefix == null && objectList.size() > 0) {
			for (DBEntry<String, String> entry : objectList) {
				objectName.add(entry.getValue());
			}
		} else {
			throw new RuntimeException(
					"Invalid prefix situation for column search.");
		}

		if (dbData.get(Constants.AR_SERVER_INFO_DB_TYPE) != null) {
			String dbType = dbData.get(Constants.AR_SERVER_INFO_DB_TYPE)
					.toString();
			// Check if database is Sybase
			if (dbType.toUpperCase().contains("SYBASE")) {
				for (String object : objectName) {
					headerList.addAll(getColumnsForSybase(object, store));
				}
			} else if (dbType.toUpperCase().contains("ORACLE")) {
				for (String object : objectName) {
					headerList.addAll(getColumnsForOracle(object, store));
				}
			} else if (dbType.toUpperCase().contains("SQL SERVER")) {
				for (String object : objectName) {
					headerList.addAll(getColumnsForMSSQL(object, store));
				}
			} else if (dbType.toUpperCase().contains("DB2")) {
				for (String object : objectName) {
					headerList.addAll(getColumnsForDB2(object, store));
				}
			} else if (dbType.toUpperCase().contains("INFORMIX")) {
				for (String object : objectName) {
					headerList.addAll(getColumnsForInformix(object, store));
				}
			}
		}

		return headerList;
	}

	private ArrayList<String> getColumnsForSybase(String objectName,
			IStore store) throws ARException {
		ArrayList<String> columnList = new ArrayList<String>();
		String statement = "SELECT col.name FROM syscolumns col, sysobjects obj where col.id = obj.id and obj.name='"
				+ objectName.toUpperCase() + "' order by col.colid asc";

		SQLResult result = null;
		try {
			result = ((ARServerStore) store).getContext().getListSQL(statement,
					0, true);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			for (List<Value> colList : result.getContents()) {
				if (colList.size() > 0) {
					columnList.add(colList.get(0).toString());
				} else {
					columnList.add("");
				}
			}
		}

		return columnList;
	}

	private ArrayList<String> getColumnsForMSSQL(String objectName, IStore store)
			throws ARException {
		ArrayList<String> columnList = new ArrayList<String>();
		String statement = "SELECT col.name FROM syscolumns col, sysobjects obj where col.id = obj.id and obj.name='"
				+ objectName.toUpperCase() + "' order by col.colid asc";

		SQLResult result = null;
		try {
			result = ((ARServerStore) store).getContext().getListSQL(statement,
					0, true);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			for (List<Value> colList : result.getContents()) {
				if (colList.size() > 0) {
					columnList.add(colList.get(0).toString());
				} else {
					columnList.add("");
				}
			}
		}

		return columnList;
	}

	private ArrayList<String> getColumnsForDB2(String objectName, IStore store)
			throws ARException {
		ArrayList<String> columnList = new ArrayList<String>();
		String statement = "SELECT COLNAME from SYSCAT.COLUMNS where TABNAME='"
				+ objectName.toUpperCase() + "' order by colno asc";

		SQLResult result = null;
		try {
			result = ((ARServerStore) store).getContext().getListSQL(statement,
					0, true);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			for (List<Value> colList : result.getContents()) {
				if (colList.size() > 0) {
					columnList.add(colList.get(0).toString());
				} else {
					columnList.add("");
				}
			}
		}

		return columnList;
	}

	private ArrayList<String> getColumnsForInformix(String objectName,
			IStore store) throws ARException {
		ArrayList<String> columnList = new ArrayList<String>();
		String statement = "SELECT c.colname FROM systables t, syscolumns c WHERE t.tabid = c.tabid AND t.tabname='"
				+ objectName.toUpperCase() + "' order by colno asc";

		SQLResult result = null;
		try {
			result = ((ARServerStore) store).getContext().getListSQL(statement,
					0, true);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			for (List<Value> colList : result.getContents()) {
				if (colList.size() > 0) {
					columnList.add(colList.get(0).toString());
				} else {
					columnList.add("");
				}
			}
		}

		return columnList;
	}

	private ArrayList<String> getColumnsForOracle(String objectName,
			IStore store) throws ARException {
		ArrayList<String> columnList = new ArrayList<String>();
		String statement = "select column_name from all_tab_columns where table_name = '"
				+ objectName.toUpperCase() + "' order by column_id asc";

		SQLResult result = null;
		try {
			result = ((ARServerStore) store).getContext().getListSQL(statement,
					0, true);
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result != null) {
			for (List<Value> colList : result.getContents()) {
				if (colList.size() > 0) {
					columnList.add(colList.get(0).toString());
				} else {
					columnList.add("");
				}
			}
		}

		return columnList;
	}

	private ArrayList<String> parseColumnObjects(List<Token> tokenList) {
		ArrayList<String> headerInformation = new ArrayList<String>();
		List<Token> tokenSubList = new ArrayList<Token>();
		tokenSubList.addAll(tokenList);

		// Check if select statement
		if (tokenSubList.size() > 0
				&& tokenSubList.get(0).getStr().equalsIgnoreCase("select")) {
			tokenSubList.remove(0);
			// Check further keywords
			if (tokenSubList.size() > 0
					&& tokenSubList.get(0).getStr()
							.equalsIgnoreCase("distinct")) {
				tokenSubList.remove(0);
			}
			// Fill header string list
			String tokenString;
			while (tokenSubList.size() > 0
					&& !tokenSubList.get(0).getStr().equalsIgnoreCase("from")) {
				tokenString = tokenSubList.get(0).getStr();
				tokenSubList.remove(0);

				// Loop until
				// * next column header (,)
				// * table definition (from keyword)
				// * column alias definition (AS keyword)
				while (!tokenSubList.get(0).getStr().equalsIgnoreCase("from")
						&& !tokenSubList.get(0).getStr().equalsIgnoreCase(",")
						&& !tokenSubList.get(0).getStr().equalsIgnoreCase("AS")) {
					// Check column function and add token until function ends
					if (tokenSubList.get(0).getStr().equalsIgnoreCase("(")) {
						int level = 1;
						// Combine all function tokens
						while (level > 0) {
							tokenString = tokenString
									+ tokenSubList.get(0).getStr();
							tokenSubList.remove(0);
							if (tokenSubList.get(0).getStr().equals("(")) {
								level++;
							} else if (tokenSubList.get(0).getStr().equals(")")) {
								level--;
							}
						}
					}

					// Add every token to tokenString and remove it from the
					// stack
					tokenString = tokenString + tokenSubList.get(0).getStr();
					tokenSubList.remove(0);
				}

				// Add header information if no column alias is defined
				if (!tokenSubList.get(0).getStr().equalsIgnoreCase("AS")) {
					headerInformation.add(tokenString);
				} else {
					// If an alias definition is provided, the column name will
					// be discarded and the search process will restart for the
					// alias definition
					tokenSubList.remove(0);
					continue;
				}

				// Remove separator token
				if (tokenSubList.get(0).getStr().equalsIgnoreCase(",")) {
					tokenSubList.remove(0);
				}
			}
		}
		
		/*   DEBUG CODE     */
		for (String header : headerInformation) {
			System.out.println(header);
		}
		/*   DEBUG CODE END */

		return headerInformation;
	}

	private ArrayList<DBEntry<String, String>> parseFromObjects(
			List<Token> tokenList) {
		Map<String, String> resultMap = new HashMap<String, String>();
		ArrayList<DBEntry<String, String>> resultList = new ArrayList<DBEntry<String, String>>();
		ArrayList<String> objectList = new ArrayList<String>();
		ArrayList<String> objectIdentifier = new ArrayList<String>();
		ArrayList<Token> tokenSubList = new ArrayList<Token>();
		tokenSubList.addAll(tokenList);

		// Remove all tokens until keyword "from"
		while (tokenSubList.size() > 0
				&& !tokenSubList.get(0).getStr().equalsIgnoreCase("from")) {
			tokenSubList.remove(0);
		}

		if (tokenSubList.size() > 0) {
			// --- Get all object names
			// Remove keyword "from"
			tokenSubList.remove(0);
			// Loop remaining tokens until end or until defined keyword found
			List<String> keywordList = new LinkedList<String>();
			keywordList.add("WHERE");
			keywordList.add("GROUP");
			keywordList.add("HAVING");
			keywordList.add("ORDER");
			List<String> joinKeywordList = new LinkedList<String>();
			joinKeywordList.add("JOIN");
			joinKeywordList.add("LEFT");
			joinKeywordList.add("RIGHT");
			joinKeywordList.add("FULL");
			joinKeywordList.add("INNER");
			joinKeywordList.add("OUTER");
			joinKeywordList.add("ON");
			boolean inJoin = false;
			while (tokenSubList.size() > 0
					&& !keywordList.contains(tokenSubList.get(0).getStr()
							.toUpperCase())) {
				// First token is always the objectname
				objectList.add(tokenSubList.get(0).getStr());
				tokenSubList.remove(0);

				// Check if next object provided
				if (tokenSubList.size() > 0
						&& tokenSubList.get(0).getStr().equals(",")) {
					objectIdentifier.add(objectList.get(objectList.size() - 1));
					tokenSubList.remove(0);
					continue;
				}

				// Check if join statement
				if (tokenSubList.size() > 0
						&& joinKeywordList.contains(tokenSubList.get(0)
								.getStr().toUpperCase())) {
					if (tokenSubList.get(0).getStr().equalsIgnoreCase("join")) {
						inJoin = true;
					} else if (tokenSubList.size() > 1
							&& tokenSubList.get(0).getStr()
									.equalsIgnoreCase("join")) {
						inJoin = true;
					} else if (tokenSubList.size() > 2
							&& tokenSubList.get(2).getStr()
									.equalsIgnoreCase("join")
							&& tokenSubList.get(1).getStr()
									.equalsIgnoreCase("outer")) {
						inJoin = true;
					} else if (tokenSubList.get(0).getStr()
							.equalsIgnoreCase("on")) {
						inJoin = true;
					}
				}

				// Check if alias is provided
				if (tokenSubList.size() > 0
						&& !tokenSubList.get(0).getStr().equals(",")
						&& !keywordList.contains(tokenSubList.get(0).getStr()
								.toUpperCase()) && inJoin == false) {
					objectIdentifier.add(tokenSubList.get(0).getStr());
					tokenSubList.remove(0);
				} else {
					objectIdentifier.add(objectList.get(objectList.size() - 1));
				}

				while (tokenSubList.size() > 0
						&& !tokenSubList.get(0).getStr()
								.equalsIgnoreCase("join")
						&& !tokenSubList.get(0).getStr().equalsIgnoreCase(",")
						&& !keywordList.contains(tokenSubList.get(0).getStr()
								.toUpperCase())) {
					tokenSubList.remove(0);
				}
				// remove "join" keyword or ","
				if (tokenSubList.size() > 0
						&& (tokenSubList.get(0).getStr()
								.equalsIgnoreCase("join") || tokenSubList
								.get(0).getStr().equals(","))) {
					tokenSubList.remove(0);
				}

				// Reset in join flag
				inJoin = false;

			}
		}

		for (int i = 0; i < objectList.size(); i++) {
			System.out.println("Objectname: " + objectList.get(i)
					+ " / Objectidentifier: " + objectIdentifier.get(i));
		}

		for (int i = 0; i < objectList.size(); i++) {
			resultMap.put(objectIdentifier.get(i), objectList.get(i));
			resultList.add(new DBEntry<String, String>(objectIdentifier.get(i),
					objectList.get(i)));
		}

		return resultList;
	}

	public Map<Integer, Value> getDBData(IStore store) throws ARException {

		Map<Integer, Value> dbData = new HashMap<Integer, Value>();
		try {
			ServerInfoMap infoMap = ((ARServerStore) store).getContext()
					.getServerInfo(
							new int[] { Constants.AR_SERVER_INFO_DB_TYPE,
									Constants.AR_SERVER_INFO_DB_NAME,
									Constants.AR_SERVER_INFO_DB_VERSION,
									Constants.AR_SERVER_INFO_DB_CHAR_SET,
									Constants.AR_SERVER_INFO_DB_USER });

			dbData.put(Constants.AR_SERVER_INFO_DB_TYPE,
					infoMap.get(Constants.AR_SERVER_INFO_DB_TYPE));
			dbData.put(Constants.AR_SERVER_INFO_DB_NAME,
					infoMap.get(Constants.AR_SERVER_INFO_DB_NAME));
			dbData.put(Constants.AR_SERVER_INFO_DB_VERSION,
					infoMap.get(Constants.AR_SERVER_INFO_DB_VERSION));
			dbData.put(Constants.AR_SERVER_INFO_DB_CHAR_SET,
					infoMap.get(Constants.AR_SERVER_INFO_DB_CHAR_SET));
			dbData.put(Constants.AR_SERVER_INFO_DB_USER,
					infoMap.get(Constants.AR_SERVER_INFO_DB_USER));
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dbData;
	}

}
