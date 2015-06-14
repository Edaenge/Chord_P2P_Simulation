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
import Chord.FakeRMIEvents.NotifyEvent;
import Process.ProcessEvent;

/**
 * Notify this local node and set the issuer as the predecessor if success.
 * Returns the successor-list to the issuer on return.
 * 
 * @see EventTask
 * @author Simon Edänge
 */
public class NotifyTask extends EventTask
{

    public NotifyTask(ChordNode self, ProcessEvent e)
    {
        super(self, e);
    }

    @Override
    public void run()
    {
        if(mEvent instanceof NotifyEvent && mLocal != null)
        {
            ChordNode caller = ((NotifyEvent)mEvent).NODE_CALLER;
            
            if(caller == null)
                return;
            
            ChordNode[] mySuccessors;
            ChordNode oldPred = mLocal.predecessor();
            
            mySuccessors = mLocal.Notify(caller);
            
   
            mLocal.GetCOM().GetReturnCOM().NotifyEventRETURN(
                    mEvent.GetUUID()
                    ,oldPred
                    ,mySuccessors
                    ,caller);

            
        }
    }
    
}