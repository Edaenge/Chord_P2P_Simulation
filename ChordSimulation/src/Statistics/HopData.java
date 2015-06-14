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
package Statistics;

import Chord.ChordNode;
import java.util.ArrayList;

/**
 * This class is used for lookups in the Chord Ring.
 * Every lookup generates a HopData object, where information about a 
 * certain lookup is stored.
 * 
 * @author Simon Edänge
 */
public class HopData
{
    private final ChordNode mCaller;
    private final ChordNode mTargetNode;
    private ChordNode mResultNode;
    private final ArrayList<ChordNode> mTargets;
    private long mStart;
    private long mEnd;
    private boolean mFailed;

    /**
     * Checks if the current lookup is a failed lookup or if it is successful.
     * @return 
     */
    public boolean IsFailed()
    {
        return mFailed;
    }

    /**
     * Sets the failed variable.
     * @param failed Option
     */
    public void SetFailed(boolean failed)
    {
        this.mFailed = failed;
    }
    
    /**
     * Constructs a HopData object
     * @param caller The caller who initiated the lookup.
     * @param target Target node to find.
     */
    public HopData(final ChordNode caller, final ChordNode target)
    {
        mCaller = caller;
        mTargetNode = target;
        mTargets = new ArrayList<>();
        mResultNode = null;
        mFailed = false;
    }
    
    /**
     * Adds a hop.
     * @param target 
     */
    public void AddHop(final ChordNode target)
    {
        mTargets.add(target);
    }
    
    /**
     * @return The caller who initiated this lookup.
     */
    public final ChordNode GetCaller()
    {
        return mCaller;
    }
    
    /**
     * 
     * @return An List of hops.
     */
    public final ArrayList<ChordNode> GetTargets()
    {
        return mTargets;
    }
    
    /**
     * 
     * @return Number of hops
     */
    public int GetHops()
    {
        return mTargets.size();
    }
    
    /**
     * 
     * @param start Timestamp of when the lookup was started.  
     */
    public void SetStartTime(long start)
    {
        mStart = start;
    }
    
    /**
     * 
     * @param end Timestamp of when the lookup was ended.
     */
    public void SetEndTime(long end)
    {
        mEnd = end;
    }
    
    /**
     * 
     * @param result Sets the Result not, which is the result of the lookup.
     */
    public void SetResultNode(ChordNode result)
    {
        mResultNode = result;
    }
    
    /**
     * 
     * @return Result node
     */
    public ChordNode GetResultNode()
    {
        return mResultNode;
    }
    
    /**
     * 
     * @return Start time
     */
    public long GetStartTime()
    {
        return mStart;
    }
    
    /**
     * 
     * @return End Time
     */
    public long GetEndTime()
    {
        return mEnd;
    }
    
    /**
     * 
     * @return Target node to find
     */
    public ChordNode GetTargetNode()
    {
        return mTargetNode;
    }
    
}