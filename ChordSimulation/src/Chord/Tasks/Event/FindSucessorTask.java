/* 
 * Copyright (C) 2015 Simon Edänge <ediz_cracked@hotmail.com>
 * Bachelor Computer Science Degree Project
 * Blekinge Institute of Technology Sweden <http://www.bth.se/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Chord.Tasks.Event;

import Chord.ChordNode;
import Chord.FakeRMIEvents.FindSuccessorEvent;

/**
 * Perform a successor lookup in this local node and return the results to the
 * issuer.
 * 
 * @see EventTask
 * @author Simon Edänge
 */
public class FindSucessorTask extends EventTask
{
    public FindSucessorTask(ChordNode self, FindSuccessorEvent event )
    {
        super(self, event);
    }
        
    @Override
    public void run()
    {
        if(mEvent instanceof FindSuccessorEvent && mLocal != null )
        {   
            FindSuccessorEvent e = (FindSuccessorEvent)mEvent;
            
            if(e.NODE_CALLER == null)
                return;
            
            ChordNode result = mLocal.findSuccessor(e.TARGET);
            mLocal.GetCOM().GetReturnCOM().FindSuccessorEventRETURN(
                    e.GetUUID(), result, e.NODE_CALLER);
        }
    }
    
}
