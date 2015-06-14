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
import Statistics.HopData;

/**
 * A specific return event. Used when replying a certain query.
 * 
 * @see ReturnEvent
 * @author Simon Edänge
 */
public class LookupEventRE extends ReturnEvent
{
    public static enum Status
    {
        STATUS_FAILED,
        STATUS_FOWARED,
        STATUS_FOUND
    }
        
    public final ChordNode NODE_TARGET_REQUESTED;
    public final ChordNode NODE_REPLIER;
    public HopData HOP_DATA;
    public final Status HOP_STATUS;
    

    /**
     * @param target As a return result of this specific query operation.
     * @param replier The node who created this return event.
     * @param status The Status enum of this lookup query: STATUS_FAILED, 
     * STATUS_FOWARED, STATUS_FOUND
     * @param hop Current hop statistics of this lookup.
     */
    public LookupEventRE(ChordNode target, ChordNode replier, 
            Status status, HopData hop)
    {
        super();
        NODE_TARGET_REQUESTED = target;
        NODE_REPLIER = replier;
        HOP_STATUS = status;
        HOP_DATA = hop;
    }
}