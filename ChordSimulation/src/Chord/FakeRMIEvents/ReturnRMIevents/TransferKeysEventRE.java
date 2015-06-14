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
package Chord.FakeRMIEvents.ReturnRMIevents;

import Chord.ChordKey;
import Chord.ChordNode;

/**
 * A specific return event. Used when replying a certain query.
 * 
 * @see ReturnEvent
 * @author Simon Edänge
 */
public class TransferKeysEventRE extends ReturnEvent
{
    public final ChordNode NODE_REPLIER;
    public final ChordKey[] KEYS_REMOVED_OR_ADDED;
    
    /**
     * 
     * @param replier The creator of this return event.
     * @param removedORaddedKeys The keys that was successfully transfered.
     * Depending on the query, if it was an Add transfer or a Remove transfer.
     */
    public TransferKeysEventRE(ChordNode replier, ChordKey[] removedORaddedKeys)
    {
        NODE_REPLIER = replier;
        KEYS_REMOVED_OR_ADDED = removedORaddedKeys;
    }
}
