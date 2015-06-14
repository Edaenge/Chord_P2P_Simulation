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
import Chord.ChordKey;
import Chord.ChordNode;
import Chord.Entries;
import Chord.FakeRMICommunication;
import Chord.FakeRMIEvents.ReturnRMIevents.PutReplicasEventRE;
import Chord.FakeRMIEvents.ReturnRMIevents.RemoveReplicasEventRE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This is the successor-list. It is used as backup nodes if node 
 * failure occurs and contains the r closest successors from n.
 * Successors is being added through discovery of new fingers in the FixFinger
 * operation and by notifying a new successor in the Stabilize function.
 * 
 * @author Simon Edänge
 */

public class SuccessorList
{
    private final List<ChordNode> mSuccessorList;
    private final int MAX_SUCCESSORS;
    private final ChordNode mLocal;
    private final Entries mEntries;

    /**
     * Construct a successor-list.
     * 
     * @param maxSuccessors Maximum successors to maintain.
     * @param self The local node.
     */
    public SuccessorList(int maxSuccessors, ChordNode self)
    {
        mSuccessorList = Collections.synchronizedList(
                new ArrayList<ChordNode>(maxSuccessors));
        MAX_SUCCESSORS = maxSuccessors;
        mLocal = self;
        mEntries = new Entries(self);
    }
    
    /**
     * Search in the list and retrieve the closest preceding successor to the
     * idToLooup provided in the function.
     * 
     * @param idToLookup
     * @return Successor, null if successor-list is empty.
     */
    public ChordNode getClosestPrecedingSuccessor(ChordId idToLookup)
    {
        if (idToLookup == null)
            return null;

        synchronized(mSuccessorList)
        {
            for (int i = mSuccessorList.size() - 1; i >= 0; i--) 
            {
                ChordNode nextNode = mSuccessorList.get(i);
                if(ChordId.isBetween(nextNode.PeerID(), mLocal.PeerID(), idToLookup)) 
                {
                    return nextNode;
                }
            }
        }

        return null;
    }
    
    /**
     * Check if a node contains in the list.
     * 
     * @param n
     * @return True if it contains.
     */
    public boolean Contains(ChordNode n)
    {
        return mSuccessorList.contains(n);
    }
    
    /**
     * Replicate the provided keys on to all successors in the list.
     * 
     * @param keys Replicas to be added.
     */
    public void InvokeReplicateAll(final ChordKey[] keys)
    {
        final FakeRMICommunication com = mLocal.GetCOM();
        //Invoke replication on all successors 
        synchronized(mSuccessorList)
        {
            for (final ChordNode successor : mSuccessorList) 
            {
                    Thread t = new Thread(new Runnable()
                    {
                            @Override
                            public void run() 
                            {
                                    try {
                                            com.PutReplicas(
                                                    false, 
                                                    keys, 
                                                    successor);

                                    } catch (Exception e) {
                                            // do nothing
                                    }
                            }
                    });
                    t.start();
            }
        }
    }
    
    /**
     * Remove all replicas from all successors in the list.
     * 
     * @param keys Replicas be removed.
     */
    public void InvokeRemoveReplicateAll(final ChordKey[] keys)
    {
        final FakeRMICommunication com = mLocal.GetCOM();
        //Invoke replication on all successors 
        synchronized(mSuccessorList)
        {
            for (final ChordNode successor : mSuccessorList) 
            {
                    Thread t = new Thread(new Runnable()
                    {
                            @Override
                            public void run() 
                            {
                                    try {
                                        com.RemoveReplicas(
                                                false, 
                                                mLocal.PeerID(),
                                                keys, 
                                                successor);

                                    } catch (Exception e) {
                                            // do nothing
                                    }
                            }
                    });
                    t.start();
            }
        }
    }
    
    /**
     * Replicate to a specific node.
     * 
     * @param entriesToReplicate The replicas.
     * @param n Node to replicate on.
     * @return True if success.
     */
    public boolean Replicate(ChordKey[] entriesToReplicate, ChordNode n)
    {
        PutReplicasEventRE ret;
        ret = mLocal.GetCOM().PutReplicas(true, entriesToReplicate, n);

        if(ret == null || ret.MESSAGE_REACHED == false)
        {
            System.out.println(mLocal.PeerID().GetIdentifierCH()
                    + " Could not replicate keys to "
                    + n.PeerID().GetIdentifierCH());
            
            mLocal.GetFingerTable().RemoveNode(n);
            
            return false;
        } 
        
        return true;
    }
    
    /**
     * Remove replicas on a specific node.
     * @param replicasToRemove Replcias to be removed.
     * @param n Node to remove replicas from.
     * @return True on success.
     */
    public boolean RemoveReplicate(ChordKey[] replicasToRemove, ChordNode n)
    {
        RemoveReplicasEventRE ret2;

        ret2 = mLocal.GetCOM().RemoveReplicas(true, mLocal.PeerID(), 
                replicasToRemove, 
                n);

        if(ret2 == null || !ret2.REMOVED)
        {
            System.out.println(mLocal.PeerID().GetIdentifierCH()
            + "Could not delete replicate on "
            + n.PeerID().GetIdentifierCH());
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if it is possible to add this node to the successor-list.
     * Returns false if it already contains inside the list.
     * 
     * @param n Node to add.
     * @return True on success.
     */
    private boolean CanAddNode(ChordNode n)
    {
        if(n == null || Contains(n))
            return false;
        
        return (n != mLocal);
    }
    
    /**
     * Adds a node to the successor-list.
     * The node is added if it does not exist in the list, and if the node is
     * closer to the local node than another node. If it is closer, that other
     * node will be discarded (if the list is full).
     * 
     * @param n Node to add.
     * @return True on success.
     */
    public boolean AddNode(ChordNode n)
    {
        if(!CanAddNode(n))
            return false;
        
        if( IsFull() && ChordId.isBetween(n.PeerID(), GetLast().PeerID(), 
                mLocal.PeerID()))
        {
            return false;
        }
        
        boolean inserted = false;
        
        if(IsEmpty())
        {
            mSuccessorList.add(n);
            inserted = true;
        }
        else
        {
            synchronized(mSuccessorList)
            {
                for(int i = 0; i<mSuccessorList.size() && !inserted; i++)
                {
                    ChordNode it = mSuccessorList.get(i);
                    if(ChordId.isBetween(n.PeerID(), mLocal.PeerID(), 
                            it.PeerID()))
                    {
                        mSuccessorList.add(i, n);
                        inserted = true;
                    }
                }
            }
        }
        
        if( !inserted && IsFull() )
            return false;
        
        // replicate all responsible key entries
        Set<ChordKey> entriesToReplicate = mEntries.GetAllKeys();
        ChordKey temp[] = new ChordKey[entriesToReplicate.size()];
        
        if( !Replicate(entriesToReplicate.toArray(temp), n) )
            return false;

        if(!inserted)
            mSuccessorList.add(n);

        if(mSuccessorList.size() > MAX_SUCCESSORS)
        {
            ChordNode nodeToDelete = GetLast();
            mSuccessorList.remove(nodeToDelete);

            // remove all this key replicas!
            ChordKey[] replicasToRemove 
                    = new ChordKey[mEntries.GetAllKeys().size()];
            
            RemoveReplicate( mEntries.GetAllKeys().toArray(replicasToRemove), 
                    nodeToDelete);
        }
        
        return true;
    }
    
    /**
     * Adds a nodes to the successor-list.
     * The nodes are added if they do not exist in the list, and if the nodes 
     * are closer to the local node than the another nodes. If they are closer, 
     * the other nodes will be discarded.
     * 
    * @param arr Nodes to add to the list.
    * @return Nodes that did not get added.
    */
    public ChordNode[] AddAll(ChordNode[] arr)
    {
        if(arr == null)
            return null;
        
        List l = Arrays.asList(arr);
        ArrayList<ChordNode> b = new ArrayList<>();
        ChordNode[] notAdded = null;
        
        if(l.size() > MAX_SUCCESSORS)
        {
            l = l.subList(0, MAX_SUCCESSORS-1);
        }
        
        for(Object obj : l)
        {
            ChordNode node = (ChordNode)obj; 
            
            if( !AddNode(node) )
                b.add(node);
        }
        
        if(b.size() > 0)
            notAdded = new ChordNode[b.size()];
        
        return notAdded;
        
    }
    
    /**
     * Clear the successor-list, removing all successors.
     */
    public void Clear()
    {
        mSuccessorList.clear();
    }
    
    /**
     * Removes a node from the list.
     * 
     * @param n Node to remove.
     * @return True on success.
     */
    public synchronized boolean RemoveNode(ChordNode n)
    {
        if( n == null)
            return false;
        
        boolean result = this.mSuccessorList.remove(n);
        
        if( !result )
            return false;
        
        //Fill Holes in successor list
        List<Finger> fingers = this.mLocal.GetFingerTable().GetTable();

        for(Finger f : fingers)
        {
            if(f.node != null );
                this.AddNode(f.node);
        }
        
        return true;
    }
    
    /**
     * @return The Key/Replicas Entries that are being 
     * managed by the local node.
     */
    public final Entries GetEntries()
    {
        return mEntries;
    }
    
    /**
     * 
     * @return The first successor from the successor-list, i.e. closest
     * successor from the local node.
     */
    public ChordNode GetFirst()
    {
        if(mSuccessorList.isEmpty())
            return null;
        
        return mSuccessorList.get(0);
    }
    
    /**
     * 
     * @return The last successor from the list, i.e. furthest successor from
     * the local node.
     */
    public ChordNode GetLast()
    {
        if(mSuccessorList.isEmpty())
            return null;
        
       return mSuccessorList.get(mSuccessorList.size()-1);
    }
    
    /**
     * 
     * @return Return a copy of the successor-list.
     */
    public ChordNode[] GetArray()
    {
        List<ChordNode> l = new ArrayList<>(mSuccessorList);
        
        if(mSuccessorList.size() > MAX_SUCCESSORS)
        {
            l = l.subList(0, MAX_SUCCESSORS-1);
        }

        ChordNode n[] = new ChordNode[l.size()];
        return l.toArray(n);
    }
    
    /**
     * 
     * @return Max number of successors being maintained.
     */
    public int GetMaxSuccesors()
    {
        return MAX_SUCCESSORS;
    }
    
    /**
     * 
     * @return Size of successor-list.
     */
    public int GetNumberOfSuccessors()
    {
        return mSuccessorList.size();
    }
    
    /**
     * 
     * @return True if successor-list contains no elements.
     */
    public boolean IsEmpty()
    {
        return mSuccessorList.isEmpty();
    }
    
    /**
     * 
     * @return True if successor-list is full (max successors).
     */
    public boolean IsFull()
    {
        return MAX_SUCCESSORS <= mSuccessorList.size();
    }
    
    /**
     * Get the successor on this index.
     * 
     * @param index index <= 0
     * @return The successor on this location.
     */
    public ChordNode Get(int index)
    {
        if(mSuccessorList.isEmpty())
            return null;
        
        ChordNode ret;
        
        try{
           ret = mSuccessorList.get(index);
        } catch(IndexOutOfBoundsException ex){
            return null;
        }
        
        return ret;
    }
}