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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * @author Christoph Heinig
 * 
 */
public class StatementSplitter {
    private List<Statement> statements = new ArrayList<Statement>();
    
    /** Whitespace characters */
    private static final String WHITESPACE = "\n\r ";
    
    /** alphabetic characters */
    private static final String ALFA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** numbers */
    private static final String NUMER = "0123456789";

    /** alphanumeric */
    private static final String ALFANUM = ALFA + NUMER;

    /**
     * This method parses the provided sql text and returns a list with statement objects
     * for each statement.
     * @param text The complete sql text to parse
     * @return A list with Statement objects for each parsed statement
     * @throws Exception All exceptions will be thrown to indicate a parsing error
     */
    public List<Statement> splitStatement(String text) throws Exception{
	statements.clear();
	int lastPosition = 0;
	int endPosition = -1;
	int startPosition = -1;
	int currentPosition = 0;
	boolean inString = false;
	boolean inCommentLine = false;
	boolean inCommentArea = false;
	boolean endSign = false;
	boolean startSign = false;
	while (currentPosition < text.length()) {
	    // Check if inString
	    if (inCommentLine == false && inCommentArea == false
		    && text.charAt(currentPosition) == '\'') {
		inString = true;
		// Go to next char to check end of string
		currentPosition++;
		while (text.length() > currentPosition
			&& text.charAt(currentPosition) != '\'') {
		    currentPosition++;
		}
		inString = false;	
		currentPosition++;
		continue;
	    }
	    // Check if inCommentLine
	    if (inString == false && inCommentArea == false
		    && text.charAt(currentPosition) == '-'
		    && text.charAt(currentPosition + 1) == '-') {
		inCommentLine = true;
		currentPosition++;
		while (text.length() > currentPosition
			&& text.charAt(currentPosition) != '\n') {
		    currentPosition++;
		}
		inCommentLine = false;
		continue;
	    }
	    // Check if inCommentArea
	    if (text.length()>currentPosition+1 && inString == false && inCommentLine == false
		    && text.charAt(currentPosition) == '/'
		    && text.charAt(currentPosition + 1) == '*') {
		inCommentArea = true;
		currentPosition++;
		while (text.length() > currentPosition
			&& (text.charAt(currentPosition) != '*'
			|| text.charAt(currentPosition + 1) != '/')) {
		    currentPosition++;
		}
		inCommentArea = false;
		continue;
	    }
	    //Check if statement starts
	    if (startSign == false && inString == false && inCommentLine==false && inCommentArea==false && ALFANUM.indexOf(text.charAt(currentPosition))>=0){
		startPosition=currentPosition;
		startSign = true;
	    }
	    // Statement ends
	    // Check if statement finished
	    if (inString == false && inCommentLine == false
		    && inCommentArea == false
		    && text.charAt(currentPosition) == ';') {
		
		endSign = true;
		endPosition = currentPosition;
	    }
	    // Create statement after line change
	    if (inString == false && inCommentLine == false
		    && inCommentArea == false && endSign == true
		    && text.charAt(currentPosition) == '\n') {
		Statement statement = new Statement();
		statement.setStartOffset(lastPosition);
		statement.setEndOffset(currentPosition);
		statement.setStartStatementOffset(startPosition);
		statement.setEndStatementOffset(endPosition);
		statement.setStatement(text.substring(startPosition,
			endPosition));
		statement.setText(text.substring(lastPosition,currentPosition));
		lastPosition = currentPosition + 1;
		statements.add(statement);	
		endSign=false;
		endPosition=-1;
		startSign=false;
		startPosition=-1;
	    }
	    currentPosition++;
	}

	// Check if final statement exists
	if (text.length() - 1 > lastPosition && startPosition >= 0) {
	    Statement statement = new Statement();
	    statement.setStartOffset(lastPosition);
	    statement.setEndOffset(text.length());
	    statement.setStartStatementOffset(startPosition);
	    if (endPosition<0){
		statement.setEndStatementOffset(text.length());
		statement.setStatement(text.substring(startPosition));
	    } else {
		statement.setEndStatementOffset(endPosition);
		statement.setStatement(text.substring(startPosition,endPosition));
	    }
	    // Only add if statement length is greater 0
	    if (statement.getStatement().length()>0)
		statements.add(statement);
	}
	
	// For Debug purpose
//	for (Statement stmt : statements){
//	    System.out.println(stmt.getStatement());
//	}
	
	return statements;
    }

    /**
     * This method takes the complete sql text and the position within the text.
     * As result one statement will be returned related to the position.
     * @param text The complete sql text to parse
     * @param position The position within the text. Only the x value is relavant at the moment
     * @return A statement object which defines the statement related to the position
     * @throws Exception All exceptions will be thrown to indicate an error during parse
     */
    public Statement getCurrentStatement(String text, Point position) throws Exception {
	Statement statement = null;
	
	// Split complete text into single statements
	splitStatement(text);
	// Search for the statement at the current position
	for (Statement stmt : statements){
	    if (position.x>=stmt.getStartOffset() && position.x<=stmt.getEndOffset()){
		statement = stmt;
		break;
	    }
	}
	
	// For debug purpose
//	System.out.println(statement);
	
	return statement;
    }


}
