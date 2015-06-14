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
package Chord.Manager;

import Chord.ChordNode;
import java.util.ArrayList;

/**
 * A wrapper class that is used by the GUI, to call methods
 * from the Chord ring and start simulations.
 * 
 * @author Simon Edänge
 */
public class ChordManager implements InChordManager
{
    private final CircleManager mCircleManager;
    private ChordLookupSimulation mSimulation;
    
    public ChordManager(final int numberOfBits, final int maxfingers)
    {
        mCircleManager = new CircleManager(numberOfBits, maxfingers);
    }
    
    @Override
    public String CreateNode()
    {
        return mCircleManager.CreateNode();
    }

    @Override
    public void PrintSuccessorAndPredecessor()
    {
        mCircleManager.PrintSuccessorAndPredecessor();
    }

    @Override
    public void PrintSuccessorList()
    {
        mCircleManager.PrintSuccessorList();
    }

    @Override
    public void PrintKeys()
    {
        mCircleManager.PrintKeys();
    }

    @Override
    public void KillAll()
    {
        mCircleManager.KillAll();
    }

    @Override
    public boolean RemoveNode(int id)
    {
        return mCircleManager.RemoveNode(id);
    }

    @Override
    public boolean RemoveNode(ChordNode n)
    {
        return mCircleManager.RemoveNode(n);
    }

    @Override
    public int GetSize()
    {
        return mCircleManager.GetSize();
    }

    @Override
    public ChordNode GetNode(int id)
    {
        return mCircleManager.GetNode(id);
    }
    
    public boolean StartLookupSimulation(final long tests)
    {
        if(IsSimulated())
            return false;
        
        mSimulation = new ChordLookupSimulation(mCircleManager, tests);
        mSimulation.start();
        
        return true;
    }

    public void EndLookupSimulation()
    {
        if(IsSimulated())
        {
            mSimulation.Stop();
        }
        
        mSimulation = null;
    }
    
    public boolean IsSimulated()
    {
        if(mSimulation == null || !mSimulation.isAlive())
        {
            return false;
        }
        else return mSimulation.isAlive();
    }
    
    public int CountKeys()
    {
        ArrayList<ChordNode> list = mCircleManager.GetCircleList();
        
        int size = 0;
        for(ChordNode n : list)
        {
            size += n.GetSuccessorList().GetEntries().GetAllKeys().size();
        }
        
        return size;
    }
    
    @Override
    public int GetBits()
    {
        return mCircleManager.GetBits();
    }
    
    public CircleManager GetCircleManager()
    {
        return mCircleManager;
    }
    
}
