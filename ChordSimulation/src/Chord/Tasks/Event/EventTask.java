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
import Process.ProcessEvent;

/**
 * This class is abstract and is extended for classes that will preform Event
 * tasks on a separate thread.
 * 
 * It is used when the node thread do not have time to wait for the task
 * to be executed, so it will let another thread handle it.
 * 
 * @author Simon Edänge
 */
public abstract class EventTask implements Runnable
{
    protected ChordNode mLocal;
    protected ProcessEvent mEvent;

    /**
     * Constructs an EventTask.
     * Takes a ChordNode and a ProcessEvent in the method parameter.
     * 
     * @param self Local node of this EventTask
     * @param e The ProcessEvent to send to other nodes.
     */
    public EventTask(ChordNode self, ProcessEvent e)
    {
        mLocal = self;
        mEvent = e;
    }
    
    
}
