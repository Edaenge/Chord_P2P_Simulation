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

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is for creating lookup statistics.
 * It collects information about different lookups and stored them.
 * It also keep a log for every HopData added.
 * @see HopData
 * @author Simon Edänge
 */
public class HopLookupMeasure
{
    private final ArrayList<HopData> mLookups;
    private final HopChordLog mLogg;

    /**
     * Constructs a HopLookupMeasure object.
     * @param totalNodes Total nodes in the current system.
     */
    public HopLookupMeasure(final int totalNodes)
    {
        mLookups = new ArrayList<>();
        mLogg = new HopChordLog(totalNodes);
    }
    
    /**
     * Adds a lookup result from a lookup and stored it in a list.
     * It will also print the contents in a log file.
     * @see HopChordLog
     * @param data Containing hop information.
     * @return Returns true if it got successfully added.
     */
    public boolean AddLookupResult(final HopData data)
    {
        if(data == null)
            return false;
        
        mLookups.add(data);

        try {
            mLogg.PrintData(data, mLookups.size());
        } catch (IOException ex) {
            System.err.println("In HopLookupMeasure, In LookupResult, "
            + "File cannot be open, no result written.");
        }
        
        return true;
    }
    
    /**
     * 
     * 
     * @return Number of tests performed 
     */
    public int GetNumberOfTests()
    {
        return mLookups.size();
    }
    
    /**
     * Closes the log. Done after the measuring is completed.
     */
    public void CloseLog()
    {
        mLogg.Close();
    }
}