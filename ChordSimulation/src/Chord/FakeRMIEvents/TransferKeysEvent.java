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

import Chord.ChordKey;
import Chord.ChordNode;
import Process.ProcessEvent;

/**
 * This class is used to send specific query events to other nodes.
 * The node who receive such event, will perform the task as it represent, and
 * return a ReturnEvent with the results back to the issuer.
 * 
 * @author Simon Edänge
 */
public class TransferKeysEvent extends ProcessEvent
{
    public final ChordKey[] KEYS_ADD;
    public final ChordKey[] KEYS_REMOVE;
    public final ChordNode NODE_CALLER;
    
    /**
     * Note: Only one ChordKey[] parameter is provided. The operation method
     * will only preform either an Add transfer or a Remove transfer.
     * 
     * @param caller The node who created this event (issuer). 
     * @param keys Keys to add to the receiver node's key list. Should be Null 
     * if removing.
     * @param remove Keys to remove from the receiver node's key list. Should be 
     * Null if adding.
     */
    public TransferKeysEvent(ChordNode caller, 
            ChordKey[] keys, 
            ChordKey[] remove)
    {
        KEYS_ADD = keys;
        KEYS_REMOVE = remove;
        NODE_CALLER = caller;
    }
}
