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
package de.chdev.artools.reporter.interfaces;

/**
 * @author Christoph Heinig
 *
 */
public interface IReportObject {
    
    public static final int CLASSIFICATION_INFO = 1;    
    public static final int CLASSIFICATION_WARN = 2;
    public static final int CLASSIFICATION_ERROR = 4;
    public static final int CLASSIFICATION_FATAL = 8;

    public void setData(Object data);
    
    public Object getData();
    
    public void setClassification(int classification);
    
    public int getClassification();
    
    public void setMetaInfo(String key, Object object);
    
    public Object getMetaInfo(String key);
    
    public void removeMetaInfo(String key);
}
