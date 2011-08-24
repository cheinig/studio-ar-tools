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

import java.util.ArrayList;
import java.util.List;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Escalation;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Value;

/**
 * @author Christoph Heinig
 *
 */
public class EscalationDataAR {
    
    private ARServerUser connection;
    
//    private Configuration conf = ConfigurationLoader.load();

    public EscalationDataAR (ARServerUser connection) {
	this.connection = connection;
    }
    
    public List<AdvEscalation> getEscalation(){
	return this.getEscalation(1, "asc");
    }
    
    public List<AdvEscalation> getEscalation(int sortColumn, String sortDirection){
	List<AdvEscalation> escalList = new ArrayList<AdvEscalation>();
	String statement = "SELECT name, hourmask, minute, tminterval, enable FROM Escalation order by "+sortColumn+" "+sortDirection;
	try {
	    SQLResult result = connection.getListSQL(statement,0,false);
	    
		for (List<Value> valueList : result.getContents()) {
		    AdvEscalation newEscal = new AdvEscalation();
		    newEscal.setName(valueList.get(0).toString());
		    newEscal.setHourmask(valueList.get(1).toString());
		    newEscal.setMinute(valueList.get(2).toString());
		    newEscal.setTminterval(valueList.get(3).toString());
		    newEscal.setEnabled(valueList.get(4).toString());
		    escalList.add(newEscal);
		}
	} catch (ARException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	return escalList;
    }
}
