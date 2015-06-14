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

import Process.ProcessEvent;

/**
 * An abstract class that extends ProcessEvent.
 * This class is used to determine if the event comes as a return from
 * another node in the system.
 * 
 * A node sends a ProcessEvent query and expect a return event from that node. 
 * 
 * @see ProcessEvent
 * @author Simon Edänge
 */
public abstract class ReturnEvent extends ProcessEvent
{
    
}
