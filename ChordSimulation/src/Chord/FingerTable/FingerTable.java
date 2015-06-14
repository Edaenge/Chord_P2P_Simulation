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
package Chord.FingerTable;

import Chord.ChordId;
import Chord.ChordNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to store routing information and the successor-list.
 * 
 * @author Simon Edänge
 */
public class FingerTable 
{
    private final List<Finger> mFingerTable;
    private final int mBits;
    private boolean mInitialized;
    private final int mMaxFingers;
    private ChordNode mPredecessor;
    private final SuccessorList mSuccessorList;
    private final ChordNode mLocal;
    
    /**
     * Constructs a finger table.
     * 
     * @param maxFingers Max fingers to create.
     * @param maxSuccessors Max successors in the successor-list.
     * @param bits The bits used in the current Chord ring.
     * @param self The local node of this table.
     */
    public FingerTable(int maxFingers, int maxSuccessors, int bits, ChordNode self) 
    {
        mBits = bits;
        mInitialized = false;
                
        if(maxFingers == 0 || maxFingers > bits)
            maxFingers = bits;
        
        mMaxFingers = maxFingers;
        
        mFingerTable = Collections.synchronizedList(
                new ArrayList<Finger>(mMaxFingers));
        
        mPredecessor = null;
        mLocal = self;
        mSuccessorList = new SuccessorList(maxSuccessors, self);
    }

    /**
     * 
     * @return Local nodes predecessor
     */
    public ChordNode GetPredecessor()
    {
        return mPredecessor;
    }

    /**
     * 
     * @param mPredecessor to set as predecessor 
     */
    public synchronized void SetPredecessor(ChordNode mPredecessor)
    {
        this.mPredecessor = mPredecessor;
    }
    
    /**
     * Create the fingers, define the start, interval etc.
     */
    public void InitFingers()
    {
        //Init the table
        ChordId selfId = mLocal.PeerID();
        BigInteger two = new BigInteger("2");
        mFingerTable.clear();
        
        synchronized(mFingerTable)
        {
            for(int i = 1; i <= mMaxFingers; i++)
            {
                ChordId id = new ChordId(null, null, mBits);
                ChordId id2 = new ChordId(null, null, mBits);
                id.SetIdentifierCH((selfId.GetIdentifierCH().add( two.pow(i-1) )).mod(two.pow(mBits)));
                id2.SetIdentifierCH((selfId.GetIdentifierCH().add( two.pow(i) )).mod(two.pow(mBits)));

                mFingerTable.add(i-1, new Finger());
                mFingerTable.get(i-1).start = id;
                mFingerTable.get(i-1).intervalFrom = mFingerTable.get(i-1).start;
                mFingerTable.get(i-1).intervalTo = id2;
                mFingerTable.get(i-1).node = null;
            }
        }
        
        mInitialized = true;
    }
    
    /**
     * 
     * @param n Node to be set as the immediate successor of local node.
     * @return true on success.
     */
    public synchronized boolean SetImmediateSuccessor(ChordNode n)
    {
        if(mInitialized)
        {
            mFingerTable.get(0).node = n;
            mSuccessorList.AddNode(n);
        }
        
        return mInitialized;
    }
    
    /**
     * 
     * @return Immediate successor.
     */
    public ChordNode GetImmediateSuccessor()
    {
        if(mInitialized)
            return mFingerTable.get(0).node;
        
        return null;
    }
    
    /**
     * Removes a node from the finger table AND the successor-list.
     * 
     * @param n The node to remove.
     * @return True on success.
     */
    public synchronized boolean RemoveNode(ChordNode n)
    {
        boolean found = false;
        
        for(Finger f : mFingerTable)
        {
            if(f.node == n)
            {
                f.node = null;
                found = true;
            }
        }
        
        if(n == mPredecessor)
            mPredecessor = null;
        
        mSuccessorList.RemoveNode(n);
        
        if( found && !mSuccessorList.IsEmpty() )
        {
            ChordNode[] successors = mSuccessorList.GetArray();
            

            for(Finger f : mFingerTable)
            {
                if(f.node == null)
                {
                    for(ChordNode s : successors)
                    { 
                        if(ChordId.isBetweenSuccessor(f.start,
                                mLocal.PeerID(), s.PeerID()))
                        {
                            f.node = s;
                            break;
                        }
                    }
                }
            }

        }
        
        return found;
    }
    
    /**
     * 
     * @return The finger table list.
     */
    public List<Finger> GetTable()
    {
        return mFingerTable;
    }
    
    /**
     * 
     * @param index List index.
     * @return Specific finger, null if not found.
     */
    public Finger Get(int index)
    {
        if(mFingerTable.isEmpty())
            return null;
        
        Finger n;
        
        try{
            n = mFingerTable.get(index);
        }catch(ArrayIndexOutOfBoundsException ex){
            return null;
        }
        
        return n;
    }
    
    /**
     * 
     * @return Size of the finger table list.
     */
    public int GetSize()
    {
        return mFingerTable.size();
    }
    
    /**
     * Clear all finger information in the finger table and the successor list.
     * Both list will become empty.
     */
    public void ClearAll()
    {
        mInitialized = false;
        mFingerTable.clear();
        mSuccessorList.Clear();
    }
    
    /**
     * 
     * @param n The successor to add to the successor-list.
     * @return True on success.
     */
    public boolean AddSuccessorToList(ChordNode n)
    {
        return mSuccessorList.AddNode(n);
    }
    
    /**
     * 
     * @param arr The successors to add to the successor-list.
     * @return True on success.
     */
    public ChordNode[] AddSuccessorToList(ChordNode[] arr)
    {
        return mSuccessorList.AddAll(arr);
    }
    
    /**
     * 
     * @return the successor-list.
     */
    public SuccessorList GetSuccessorList()
    {
        return mSuccessorList;
    }
}