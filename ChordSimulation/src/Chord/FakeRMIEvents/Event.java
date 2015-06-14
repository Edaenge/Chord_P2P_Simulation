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

import Process.ProcessEvent;

/**
 * This class is extended by ProcessEvent classes that has an option to 
 * choose if it needs a return or not.
 * 
 * @see ProcessEvent
 * @author Simon Edänge
 */
public abstract class Event extends ProcessEvent
{
    private boolean mRequiresReturn = false;
    
    /**
     * Sets if the local node needs a return of this ProcessEvent.
     * @param reqRet 
     */
    public void SetRequiresReturn(boolean reqRet)
    {
        mRequiresReturn = reqRet;
    }
    
    /**
     * 
     * @return If this ProcessEvent has to be replied.
     */
    public boolean RequiresReturn()
    {
        return mRequiresReturn;
    }
}
