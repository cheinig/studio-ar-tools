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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cheinig
 * 
 */
public class DBUtils {

    private static DBUtils instance = null;
    
    private List<String> structureStartList = new ArrayList<String>();
    
    private List<String> plsqlList = new ArrayList<String>();
    
    private int col=0;
    private int line=0;
    private String currentStatement="";

    private DBUtils() {
	// This list will be filled with all identifiers which begin an own
	// scope
	// and must be ended with the keyword end
	structureStartList.add("begin");
	structureStartList.add("if");
	structureStartList.add("loop");
	structureStartList.add("case");
	structureStartList.add("for");
	structureStartList.add("while");

	// This list will be filled with all identifiers which starts a
	// plsql structure
	plsqlList.add("package");
	plsqlList.add("function");
	plsqlList.add("procedure");
    }
    
    public static DBUtils getInstance(){
	if (instance==null){
	    instance= new DBUtils();
	}
	return instance;
    }

    public ArrayList<String> getStatementFromScript(Reader reader) {
	List<Token> sqltoken = new ArrayList<Token>();
	ArrayList<String> sqlStatements = new ArrayList<String>();
	// this variable indicates if we are in a string
	boolean inString = false;
	// this variable indicates if we are in a create structure
	boolean structure = false;
	// this variable indicates if we are in a plsql structure
	boolean pack = false;
	// this variable indicates the current scope level
	int level = 0;
	// this variable indicates the current statement
	boolean current=false;
	currentStatement="";
	try {
	    StringBuffer str = new StringBuffer();
	    // FileReader fr = new FileReader(file);
	    SQLScanner sqls = new SQLScanner(reader);
	    sqltoken = sqls.scan();
	    for (Object obj : sqltoken) {
		Token token = (Token) obj;
		System.out.println("Token position Col:"+token.getCol()+"/Line:"+token.getLine());
		// Check if the position is in current statement
		if (current==false && token.getLine()>line || (token.getLine()==line && token.getCol()>=col)){
		    current=true;
		}
		// The ' sign toggle between string and command mode
		if (token.getStr().equals("'")) {
		    if (inString) {
			inString = false;
		    } else {
			inString = true;
		    }
		    str.append(token.getStr());
		}
		// The keyword "create" will start a structure
		else if (token.getStr().equalsIgnoreCase("create") && !inString) {
		    str.append(token.getStr());
		    structure = true;
		}
		// If one of the special predifined keywords is used within a
		// structure
		// the state will be switched to plsql mode
		else if (plsqlList.contains(token.getStr().toLowerCase())
			&& structure) {
		    str.append(token.getStr());
		    pack = true;
		    structure = true;
		}
		// If one of the predefined keywords for a new scope is used,
		// the
		// current structure level will be increased
		else if (structureStartList.contains(token.getStr()
			.toLowerCase())) {
		    level++;
		    str.append(token.getStr());
		}
		// If the "end" keyword is used, the structure level will be
		// decreased
		// and if the level is decreased below 1 the structure will be
		// left
		else if (token.getStr().equalsIgnoreCase("end")) {
		    level--;
		    str.append(token.getStr());
		    if (level <= 0) {
			structure = false;
		    }
		}
		// If we are not in a package or we already left the package
		// structure
		// and we are not in a string structure, then the semicolon will
		// be
		// interpreted as an end sign. The current statement will be
		// saved and
		// a new statement begin
		else if (token.getStr().equals(";") && !inString
			&& (!pack || (pack && !structure))) {
		    // If a pack was processed, the semicolon sign must be
		    // written to the statement
		    if (pack) {
			str.append(token.getStr());
		    }
		    sqlStatements.add(str.toString().trim());
		    str = new StringBuffer();
		    level = 0;
		    pack = false;
		    structure = false;
		}
		// If a carriage return sign is found, it will be deleted because
		// the plsql code could not be compiled with them
		else if ("\r".indexOf(token.getStr()) > -1 && !inString) {
		    str.append("");
		} 
		// If nothing special is found, the current token will be written to the sql statement
		else {
		    str.append(token.getStr());
		}
	    }
	    

	    // If a slash sign is found as a single statement, it will be removed
	    if (str.toString().trim().length() > 0 && !str.toString().trim().equals("/")) {
		    // Remove last / if it is a normal sql statement
		    if (str.toString().trim().endsWith("/")){
			str.deleteCharAt(str.lastIndexOf("/"));
		    }
		    sqlStatements.add(str.toString().trim());
		    // Add current statement
		    if (current==true && currentStatement.equals("")){
			currentStatement=str.toString().trim();
		    }
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	
	

	return sqlStatements;
    }
    
    public String getSingleStatement(Reader reader, int line, int col){
	this.col=col;
	this.line=line;
	getStatementFromScript(reader);
	return currentStatement;
    }
    
    public List<Token> getTokens(Reader reader){
	List<Token> sqltoken = new ArrayList<Token>();
	    SQLScanner sqls = new SQLScanner(reader);
	    try {
		sqltoken = sqls.scan();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
//	    for (Token token : sqltoken){
//		System.out.println(token.getStr());
//	    }
	    
	    return sqltoken;
    }
}
