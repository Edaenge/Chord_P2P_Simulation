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
import Chord.Entries;
import Chord.FakeRMIEvents.ReturnRMIevents.LookupEventRE;
import Process.ProcessEvent;

/**
 * Performs a lookup on a target key. Forwards the query to other nodes or 
 * returns the results to the issuer, if key found.
 * 
 * Used in simulations.
 * 
 * @see EventTask
 * @author Simon Edänge
 */
public class LookupTask extends EventTask
{

    public LookupTask(ChordNode self, ProcessEvent e)
    {
        super(self, e);
    }

    @Override
    public void run()
    {
        Chord.FakeRMIEvents.LookupEvent le = 
                (Chord.FakeRMIEvents.LookupEvent)mEvent;
        
        String ID;
        Entries entries = mLocal.GetSuccessorList().GetEntries();
        
        System.out.println(mLocal.PeerID().GetIdentifierCH() + ", Lookup rec from " 
                + le.NODE_CALLER.PeerID().GetIdentifierCH());

        if(le.NODE_CALLER == null)
            return;
        
        le.HOP_DATA.AddHop(mLocal);

        if(le.NODE_CALLER != le.NODE_CALLER_ORIGIN)
        {
            mLocal.GetCOM().GetReturnCOM().LookupEventRETURN(
                    le.GetUUID(), null, 
                    LookupEventRE.Status.STATUS_FOWARED, 
                    le.HOP_DATA, 
                    le.NODE_CALLER);
            
            ID = le.NODE_CALLER_ORIGIN_UUID;
        }
        else
        {
            ID = le.GetUUID();
        }

        ChordNode result = mLocal.lookup(le.TARGET, 
                le.NODE_CALLER_ORIGIN, le.HOP_DATA, ID);

        if( result == mLocal )
        {
            if( entries.HasKey(le.TARGET.GetKeyCH()) 
                    ||  entries.HasReplica(le.TARGET.GetKeyCH()) )
            {
                
                System.out.println(mLocal.PeerID().GetIdentifierCH() 
                        + " Target found, returning to caller");

                mLocal.GetCOM().GetReturnCOM().LookupEventRETURN(
                        ID, result, 
                        LookupEventRE.Status.STATUS_FOUND, 
                        le.HOP_DATA, 
                        le.NODE_CALLER_ORIGIN);
            }
            
            else
            {
                System.out.println(mLocal.PeerID().GetIdentifierCH() 
                        + " Target NOT found, returning to caller");
                
                mLocal.GetCOM().GetReturnCOM().LookupEventRETURN(
                        ID, result, 
                        LookupEventRE.Status.STATUS_FAILED, 
                        le.HOP_DATA, 
                        le.NODE_CALLER_ORIGIN);
            }
        }
    }
    
}
