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
package Process;
import java.util.UUID;

/**
 * This is an abstract class.
 * Events used to pass to other ProcessX threads. Events will end up in the
 * process event queue, and will be handled by each process.
 * @see ProcessX
 * @author Simon Edänge
 */
public abstract class ProcessEvent 
{
    private final long mTimeCreated;
    private String mID;

    /**
     * Constructs an event.
     */
    public ProcessEvent()
    {
        mTimeCreated = System.currentTimeMillis();
        mID = UUID.randomUUID().toString();
    }
    
    /**
     * Sets the unique ID for this event.
     * @param ID The id to be set 
     */
    public void SetUUID(String ID)
    {
        mID = ID;
    }
    
    /**
     * 
     * @return When the event was created.
     */
    public long GetTimeCreated()
    {
        return mTimeCreated;
    }
    
    /**
     * 
     * @return The unique event ID
     */
    public String GetUUID()
    {
        return mID;
    }
}