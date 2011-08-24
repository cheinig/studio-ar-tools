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

package de.chdev.artools.sql.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

/**
 * A simple Scanner implementation that scans an sql file into usable tokens.
 * 
 */
public class SQLScanner {
	/** white spaces */
	private static final String WHITE = "\f\r\t\n ";

	/** alphabetic characters */
	private static final String ALFA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** numbers */
	private static final String NUMER = "0123456789";

	/** alphanumeric */
	private static final String ALFANUM = ALFA + NUMER;

	/** special characters */
	private static final String SPECIAL = ";(),'|+";

	/** comment */
	private static final char COMMENT_POUND = '#';

	/** comment */
	private static final char COMMENT_SLASH = '/';

	/** comment */
	private static final char COMMENT_STAR = '*';

	/** comment */
	private static final char COMMENT_DASH = '-';

	/** the input reader */
	private Reader in;

	/** character */
	private int chr;

	/** token */
	private String token;

	/** list of tokens */
	private List<Token> tokens;

	/** line */
	private int line;

	/** column */
	private int col;

	/** string */
	private boolean inString;

	/**
	 * Creates a new scanner with no Reader
	 */
	public SQLScanner() {
		this(null);
	}

	/**
	 * Creates a new scanner with an Input Reader
	 * 
	 * @param input
	 *            the input reader
	 */
	public SQLScanner(Reader input) {
		setInput(input);
	}

	/**
	 * Set the Input
	 * 
	 * @param input
	 *            the input reader
	 */
	public void setInput(Reader input) {
		in = input;
	}

	/**
	 * Reads the next character and increments the line and column counters.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private void readChar() throws IOException {
		boolean wasLine = (char) chr == '\r';
		chr = in.read();
		if ((char) chr == '\n' || (char) chr == '\r' || (char) chr == '\f') {
			col = 0;
			if (!wasLine || (char) chr != '\n') {
				line++;
			}
		} else {
			col++;
		}
	}

	/**
	 * Scans an identifier.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private void scanIdentifier() throws IOException {
		token = "";
		char c = (char) chr;
		while (chr != -1 && (inString == true || WHITE.indexOf(c) == -1)
				&& (inString == true || SPECIAL.indexOf(c) == -1)) {
			token = token + (char) chr;
			readChar();
			c = (char) chr;
			if (c == '\'')
				break;
		}
		int start = col - token.length();
		tokens.add(new Token(token, line, start));
	}

	/**
	 * Scans an identifier which had started with the negative sign.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private void scanNegativeIdentifier() throws IOException {
		token = "-";
		char c = (char) chr;
		while (chr != -1 && (inString == true || WHITE.indexOf(c) == -1)
				&& (inString == true || SPECIAL.indexOf(c) == -1)) {
			token = token + (char) chr;
			readChar();
			c = (char) chr;
			if (c == '\'') {
				break;
			}
		}
		int start = col - token.length();
		tokens.add(new Token(token, line, start));
	}

	/**
	 * Scan the input Reader and returns a list of tokens.
	 * 
	 * @return a list of tokens
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public List<Token> scan() throws IOException {
		line = 1;
		col = 0;
		boolean inComment = false;
		boolean inCommentSlashStar = false;
		boolean inCommentDash = false;

		boolean inNegative;

		tokens = new ArrayList<Token>();
		readChar();
		while (chr != -1) {
			char c = (char) chr;
			inNegative = false;

			if (c == COMMENT_DASH && inString == false) {
				readChar();
				if ((char) chr == COMMENT_DASH) {
					inCommentDash = true;
				} else {
					inNegative = true;
					c = (char) chr;
				}
			}

			if (inCommentDash && inString == false) {
				if (c == '\n' || c == '\r') {
					inCommentDash = false;
				}
				readChar();
			} else if (c == COMMENT_POUND && inString == false) {
				inComment = true;
				readChar();
			} else if (c == COMMENT_SLASH && inString == false) {
				char tmpChar = c;
				readChar();
				if ((char) chr == COMMENT_STAR) {
					inCommentSlashStar = true;
				} else if (inComment == false && inCommentSlashStar == false
						&& inCommentDash == false) {
					tokens.add(new Token("" + tmpChar, line, col));
				}
			} else if (inComment || inCommentSlashStar && inString == false) {
				if (c == '*') {
					readChar();
					if ((char) chr == COMMENT_SLASH) {
						inCommentSlashStar = false;
					}
				} else if (c == '\n' || c == '\r') {
					inComment = false;
				}
				readChar();
			} else if (ALFANUM.indexOf(c) >= 0 && inString == false) {
				if (inNegative) {
					scanNegativeIdentifier();
				} else {
					scanIdentifier();
				}
			} else if (SPECIAL.indexOf(c) >= 0
					&& (inString == false || c == '\'')) {
				if (inString == true && c == '\'') {
					inString = false;
				} else if (inString == false && c == '\'') {
					inString = true;
				}
				tokens.add(new Token("" + c, line, col));
				readChar();
			} else if (inString == true) {
				if (inNegative) {
					scanNegativeIdentifier();
				} else {
					scanIdentifier();
				}
			} else if (c == COMMENT_STAR && inString == false) {
				tokens.add(new Token("" + c, line, col));
				readChar();
			} else {
				if (inNegative) {
					// tokens.add(new Token("-" + c, line, col));
				} else {
					// tokens.add(new Token("" + c, line, col));
				}

				readChar();
			}
		}
		return tokens;
	}
}
