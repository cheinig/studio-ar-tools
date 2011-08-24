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

/**
 * @author Christoph Heinig
 * 
 */
public class Statement {

    // This value holds the sql statement
    private String statement = null;

    // This variable holds the complete text within the specified range from
    // startOffset to endOffset
    private String text = null;

    // The value startOffset specifies the start index of the area which belongs
    // to the statement
    private int startOffset;

    // The value endOffset specifies the end index of the area which belongs to
    // the statement
    private int endOffset;

    // The value startStatementOffset specifies the index where the sql
    // statement begins
    private int startStatementOffset;

    // The value endStatementOffset speicifies the index where the sql statement
    // ends
    private int endStatementOffset;

    @Override
    public String toString() {
	return getStatement();
    }

    public String getStatement() {
	String stmt = statement.trim();
	return statement.trim();
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
	if (statement == null) {
	    statement = text;
	}
    }

    public void setStatement(String statement) {
	this.statement = statement;

	// Set text if not set yet
	if (text == null) {
	    text = statement;
	}

    }

    /**
     * @param startOffset
     *            the startOffset to set
     */
    public void setStartOffset(int startOffset) {
	this.startOffset = startOffset;
    }

    /**
     * @return the startOffset
     */
    public int getStartOffset() {
	return startOffset;
    }

    /**
     * @param endOffset
     *            the endOffset to set
     */
    public void setEndOffset(int endOffset) {
	this.endOffset = endOffset;
    }

    /**
     * @return the endOffset
     */
    public int getEndOffset() {
	return endOffset;
    }

    public int getStartStatementOffset() {
	return startStatementOffset;
    }

    public int getEndStatementOffset() {
	return endStatementOffset;
    }

    /**
     * @param startStatementOffset
     *            the startStatementOffset to set
     */
    public void setStartStatementOffset(int startStatementOffset) {
	this.startStatementOffset = startStatementOffset;
    }

    /**
     * @param endStatementOffset
     *            the endStatementOffset to set
     */
    public void setEndStatementOffset(int endStatementOffset) {
	this.endStatementOffset = endStatementOffset;
    }
}
