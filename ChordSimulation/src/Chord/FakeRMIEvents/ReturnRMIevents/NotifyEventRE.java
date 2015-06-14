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

import Chord.ChordNode;

/**
 * A specific return event. Used when replying a certain query.
 * 
 * @see ReturnEvent
 * @author Simon Edänge
 */
public class NotifyEventRE extends ReturnEvent
{
    public final ChordNode NODE_REPLIER;
    public final ChordNode NODE_CURRENT_PRED;
    public final ChordNode[] NODE_SUCCESSORS;

    /**
     * @param pred The current predecessor of the replier node, before the 
     * node set this local node as predecessor (old predecessor).
     * @param successors The successor-list of the replier node. Null if the 
     * replier node did not accept the local node as its predecessor.
     * @param replier The replier node that created this return event.
     */
    public NotifyEventRE(ChordNode pred, 
            ChordNode[] successors,
            ChordNode replier)
    {
        super();
        NODE_CURRENT_PRED = pred;
        NODE_REPLIER = replier;
        NODE_SUCCESSORS = successors;
    }
}