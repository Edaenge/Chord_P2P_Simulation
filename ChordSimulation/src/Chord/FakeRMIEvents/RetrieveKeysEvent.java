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
package Chord.FakeRMIEvents;

import Chord.ChordId;
import Chord.ChordNode;
import Process.ProcessEvent;

/**
 * This class is used to send specific query events to other nodes.
 * The node who receive such event, will perform the task as it represent, and
 * return a ReturnEvent with the results back to the issuer.
 * 
 * @author Simon Edänge
 */
public class RetrieveKeysEvent extends ProcessEvent
{
    public final ChordNode NODE_CALLER;
    public final ChordId TO_ID;

    /**
     * 
     * @param id Retrieves all keys up to this id range.
     * @param caller The node who created this event (issuer). 
     */
    public RetrieveKeysEvent(final ChordId id, final ChordNode caller)
    {
        super();
        this.NODE_CALLER = caller;
        this.TO_ID = id;
    }
    
}
