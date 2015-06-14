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
import Chord.ChordKey;
import Chord.ChordNode;

/**
 * This class is used to send specific query events to other nodes.
 * The node who receive such event, will perform the task as it represent, and
 * return a ReturnEvent with the results back to the issuer.
 * 
 * @author Simon Edänge
 */
public class RemoveReplicasEvent extends Event
{
    final public ChordNode NODE_CALLER;
    final public ChordId ID_RANGE;
    final public ChordKey KEYS_TO_REMOVE[];

    /**
     * 
     * @param caller The node who created this event (issuer).
     * @param range From which range (ID) to remove replicas from. If specified,
     * keys parameter will be ignored during the method operation.
     * @param keys Keys to remove.
     */
    public RemoveReplicasEvent(ChordNode caller, ChordId range, ChordKey keys[])
    {
        this.NODE_CALLER = caller;
        this.KEYS_TO_REMOVE = keys;
        this.ID_RANGE = range;
    }
    
    
}
