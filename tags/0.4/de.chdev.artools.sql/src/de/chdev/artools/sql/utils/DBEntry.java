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

import java.util.Map.Entry;

/**
 * @author Christoph Heinig
 *
 */
public class DBEntry<K,V> implements Entry{
    
    private K key = null;
    private V value = null;
    
    public DBEntry(K key){
	this.key = key;
    }
    
    public DBEntry(K key, V value){
	this.key = key;
	this.value = value;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#getKey()
     */
    @Override
    public K getKey() {
	return key;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#getValue()
     */
    @Override
    public V getValue() {
	return value;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    @Override
    public V setValue(Object value) {
	V oldValue = this.value;
	this.value = (V)value;
	return oldValue;
    }

}
