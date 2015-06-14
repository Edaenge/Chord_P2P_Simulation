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
public class FindSuccessorEventRE extends ReturnEvent
{
    public final ChordNode NODE_REPLIER;
    public final ChordNode NODE_TARGET_REQUESTED;

    /**
     * @param targetNode As a return result of this specific query operation.
     * @param replier The node who created this return event.
     */
    public FindSuccessorEventRE(ChordNode targetNode, ChordNode replier)
    {
        super();
        NODE_TARGET_REQUESTED = targetNode;
        NODE_REPLIER = replier;
    }
}