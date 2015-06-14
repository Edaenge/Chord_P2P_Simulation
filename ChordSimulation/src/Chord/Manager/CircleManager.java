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

import Chord.ChordKey;
import Chord.ChordNode;
import Chord.FingerTable.Finger;
import GUI.Quicksort;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is where the Chord ring is managed. It contains all the nodes and
 * can be used to extract information and perform tests.
 * 
 * @author Simon Edänge
 */
public class CircleManager implements InChordManager
{
    private ArrayList<ChordNode> mCircle = null;
    private final int mBits;
    private final int mFingers;
    private ChordNode mBootStrap;
    private final ArrayList<BigInteger> mKeyPool;
    private final ScheduledThreadPoolExecutor mExecutor;
    
    /**
     * Constructs a ChordRing
     * 
     * @param numberOfBits Bits to be used in the Chord ring
     * @param maxfingers Max fingers to be used per node
     */
    public CircleManager(final int numberOfBits, final int maxfingers)
    {
        mBits = numberOfBits;
        mCircle = new ArrayList<>();
        mKeyPool = new ArrayList<>();
        mBootStrap = null;
        mFingers = maxfingers;
        mExecutor = new ScheduledThreadPoolExecutor(12);

    }
    
    @Override
    public String CreateNode()
    {
        if( Math.pow(2, mBits) <= mCircle.size() )
        {
            return null;
        }
        
        boolean success;
        ChordNode node;
        
        do
        {
            success = true;
            
            node = new ChordNode(mBits, mFingers, mBootStrap);
            //node.setName("Node: " + node.PeerID().GetIdentifierCH());

            if( mKeyPool.contains(node.PeerID().GetKeyCH()) )
                success = false;
            else
                mKeyPool.add(node.PeerID().GetKeyCH());

            if( success && !contains(node.PeerID().GetIdentifierCH()) )
                AddToCircle(node);
            else
                success = false;
        
        } while( !success );
        
        if(mBootStrap == null)
            mBootStrap = node;
        
        mExecutor.scheduleWithFixedDelay(node, 0, 10, TimeUnit.MILLISECONDS);
        
        System.out.println("Node was created, (" + node.PeerID().GetIdentifierCH() + ") With key: " + node.PeerID().GetKeyCH());
        
        return node.PeerID().GetIdentifierCH().toString();
    }
    
    /**
     * Adds a node to the circle.
     * @param n The node to add. 
     */
    private void AddToCircle(ChordNode n)
    {
        mCircle.add(n);
        Quicksort.sort(mCircle);
    }
    
    @Override
    public void PrintSuccessorAndPredecessor()
    {
        System.out.println("***********************");
        for(ChordNode n :  mCircle)
        {
           ChordNode pred = n.predecessor();
           ChordNode suc = n.successor();
           
           System.out.println("\n");
           System.out.println("ID: " + n.PeerID().GetIdentifierCH());
           if( suc != null)
                System.out.println("Successor: " + n.successor().PeerID().GetIdentifierCH());
           if( pred != null )
                System.out.println("Predecessor: " + n.predecessor().PeerID().GetIdentifierCH());
           
           List<Finger> fingers = n.GetFingerTable().GetTable();
           System.out.println("Fingers:");
           
           int counter = 1;
           for(Finger f : fingers)
           {
               if(f != null && f.node != null)
                    System.out.println("F"+counter+": "+f.node.PeerID().GetIdentifierCH());
               
               counter++;
           }
           
        }
    }
    
    @Override
    public void PrintSuccessorList()
    {
        System.out.println("***********************");
        for(ChordNode n : mCircle)
        {
            ChordNode[] nodes = n.GetSuccessorList().GetArray();
            
            System.out.println("Successor List Of: "+n.PeerID().GetIdentifierCH());
            for(ChordNode nn: nodes)
                System.out.println(nn.PeerID().GetIdentifierCH());
        }
    }
    
    @Override
    public void PrintKeys()
    {
        System.out.println("***********************");
        for( ChordNode n : mCircle)
        {
            Set<ChordKey> replica = n.GetSuccessorList().GetEntries().GetAllReplicas();
            Set<ChordKey> keys = n.GetSuccessorList().GetEntries().GetAllKeys();
            
            System.out.println("ID: "+n.PeerID().GetIdentifierCH());
            
            System.out.println("KEYS:");
            for(ChordKey x : keys)
            {
                System.out.println(x.KEY.toString());
            }
            
            System.out.println("REPLICAS:");
            
            for(ChordKey x : replica)
            {
                System.out.println(x.KEY.toString());
            }
            System.out.print("\n");
        }
    }
    
    @Override
    public void KillAll()
    {
        for(ChordNode node : mCircle  )
        {
            if(node == null)
                continue;
            
            node.kill();
            node = null;
        }
    }
    
    private boolean contains(BigInteger k)
    {
        if(k == null)
            return false;
        
        return Quicksort.binarySearch(mCircle, k) != null;
    }
    
    @Override
    public boolean RemoveNode(int id)
    {
        BigInteger temp = new BigInteger(id+"");
        ChordNode n;
        
        n = Quicksort.binarySearch(mCircle, temp);
        return RemoveNode(n);
    }
    
    @Override
    public boolean RemoveNode(ChordNode n)
    {
        if(n == null)
            return false;
        
        n.kill();
        mKeyPool.remove(n.PeerID().GetKeyCH());
        mExecutor.remove(n);
        mCircle.remove(n);
        n = null;
        
        return true;
    }
    
    @Override
    public int GetSize()
    {
        return mCircle.size();
    }
    
    @Override
    public ChordNode GetNode(int id)
    {
        BigInteger temp = new BigInteger(id+"");
        for(ChordNode n : mCircle)
        {
            if(n.PeerID().GetIdentifierCH().compareTo(temp) == 0)
            {
                return n;
            }
        }
        
        return null;
    }
    
    @Override
    public int GetBits()
    {
        return mBits;
    }
    
    public final ArrayList<ChordNode> GetCircleList()
    {
        return mCircle;
    }

    ChordNode GetBootstrap()
    {
        return mBootStrap;
    }
    
    public int GetMaxFingers()
    {
        return mFingers;
    }
}