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
package de.chdev.artools.reporter;

import com.bmc.arsys.api.Escalation;

/**
 * @author cheinig
 *
 */
public class AdvEscalation extends Escalation{

    private String name;
    private String hourmask;
    private String minute;
    private String tminterval;
    private String enabled;
    
    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getTminterval() {
        return tminterval;
    }

    public void setTminterval(String tminterval) {
        this.tminterval = tminterval;
    }

    public String getHourmask() {
        return hourmask;
    }

    public void setHourmask(String hourmask) {
        this.hourmask = hourmask;
    }

    public AdvEscalation(){
	
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(String enabled) {
	this.enabled = enabled;
    }

    /**
     * @return the enabled
     */
    public String getEnabled() {
	return enabled;
    }
    
    
}
