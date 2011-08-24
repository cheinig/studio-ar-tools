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

package de.chdev.artools.sql.editor;

import org.eclipse.swt.graphics.RGB;

public interface ISQLColorConstants {
	RGB SQL_COMMENT = new RGB(128, 128, 128);
	RGB SQL_KEYWORD = new RGB(0, 0, 255);
	RGB SQL_RESERVED = new RGB(200, 0, 128);
	RGB SQL_STRING = new RGB(0, 200, 0);
	RGB DEFAULT = new RGB(0, 0, 0);
}
