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
package Chord.Tasks;

import Chord.ChordId;
import Chord.ChordKey;
import Chord.ChordNode;
import Chord.Entries;
import Chord.FakeRMICommunication;
import Chord.FakeRMIEvents.ReturnRMIevents.GetPredecessorEventRE;
import Chord.FakeRMIEvents.ReturnRMIevents.NotifyEventRE;
import Chord.FakeRMIEvents.ReturnRMIevents.TransferKeysEventRE;
import Chord.FakeRMIEvents.ReturnRMIevents.RetrieveKeysEventRE;
import Chord.FingerTable.FingerTable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class is used as a background worker, and called every time t.
 * 
 * Stabilize the systems state by correcting the local nodes successor and
 * manage the keys.
 * 
 * @author Simon Edänge
 */
public class StabilizeTask implements Runnable
{
    private final ChordNode mLocalNode;
    private final FakeRMICommunication mCOM;
    private final FingerTable mFingerTable;
    private final ChordId mId;
    
    public StabilizeTask(ChordNode self)
    {
        mLocalNode = self;
        mCOM = self.GetCOM();
        mFingerTable = self.GetFingerTable();
        mId = self.PeerID();
    }
    
    @Override
    public void run()
    {
        if( mLocalNode != null )
            Stabilize();
    }
    
    public void Stabilize()
    {
        ChordNode mySuccessor;
        ChordNode pred;
        GetPredecessorEventRE gpeRE;
        NotifyEventRE notifyRE;
        Entries entries = mLocalNode.GetSuccessorList().GetEntries();
        
        if( mLocalNode.successor() == null && mLocalNode.predecessor() == null )
        {
            mLocalNode.Join( mLocalNode.GetBootstrap() );
            return;
        }
        else if( mLocalNode.successor() == null )
        {
            mLocalNode.SetSuccessor( mLocalNode.predecessor() );
            return;
        }
        
        //Check If i have replicas I should be responsible for
        Set<ChordKey> replicas = entries.GetAllReplicas();
        if( replicas.size() >0 )
        {
            Set<ChordKey> keysToAdd = new HashSet<>();
            ChordId target;
            
            if( mLocalNode.predecessor() == null )
                target = mLocalNode.successor().PeerID();

            else
                target = mLocalNode.predecessor().PeerID();
            
            for(ChordKey k : replicas)
            {
                //Some keys, i should perhaps take care of
                if( k.WhichIsClosestToKey(target,
                        mId) == mId)
                    keysToAdd.add(k);
            }
            
            //Add as keys
            ChordKey[] keys = new ChordKey[keysToAdd.size()];
            keys = keysToAdd.toArray(keys);
            
            entries.RemoveReplica(keys);
            entries.InsertKey(keys);
            mFingerTable.GetSuccessorList().InvokeReplicateAll(keys);
        }
        
        mySuccessor = mLocalNode.successor();
        gpeRE = mCOM.predecessor(mySuccessor);
        
        if(gpeRE == null)
        {
            mFingerTable.RemoveNode(mySuccessor);
            Stabilize();
            return;
        }
        
        pred = gpeRE.NODE_TARGET_REQUESTED;
        
        if( pred != null && ChordId.isBetween(pred.PeerID(), mId, 
                mySuccessor.PeerID()) )
        {
            mLocalNode.SetSuccessor(pred);
        }
        
        mySuccessor = mLocalNode.successor();

        //Notify new successor
        notifyRE = mCOM.Notify(mLocalNode, mySuccessor);
        
        if(notifyRE == null)
        {
            mFingerTable.RemoveNode(mySuccessor);
            Stabilize();
            return;
        }

        //Add successors from successor
        if( notifyRE.NODE_SUCCESSORS != null )
        {
            //Add the new successors
            mFingerTable.AddSuccessorToList(
                    notifyRE.NODE_SUCCESSORS);
        }
        
        //Get successor replicas
        if( notifyRE.NODE_CURRENT_PRED != mLocalNode )
        {
            RetrieveKeysEventRE rke;

            rke = mCOM.RetrieveKeys(null, mySuccessor);

            if( rke == null )
            {
                mFingerTable.RemoveNode(mySuccessor);
                Stabilize();
                return;
            }

        }
        
        //Check Successors from list, if they have keys that this node is
        //Responisble for, and if this node has keys to send
        if( !mLocalNode.GetSuccessorList().IsEmpty() )
        {
            //SaveKeys The keys to be replicated later
            Set<ChordKey[]> replicateThese = new HashSet<>();
            //Get Successors
            ChordNode[] mySuccessorList 
                    = mLocalNode.GetSuccessorList().GetArray();
            
            //Get my keys
            final Set<ChordKey> myKeys = entries.GetAllKeys();
            //Create a Map of my Successors
            Map<ChordNode, Set<ChordKey>> keyOwners = new HashMap<>();
            
            for(ChordNode target : mySuccessorList)
                keyOwners.put(target, new HashSet<ChordKey>());
            
            //Iterator for the map
            Iterator it = keyOwners.entrySet().iterator();
            
            //Check which node should get a specific key
            for(ChordKey k : myKeys)
            {
                ChordNode owner = mLocalNode;
                
                while(it.hasNext())
                {
                    Map.Entry<ChordNode, Set<ChordKey>> pairs 
                            = (Map.Entry<ChordNode, Set<ChordKey>>)it.next();
                    ChordNode target = pairs.getKey();
                    
                    ChordId id = k.WhichIsClosestToKey(
                            target.PeerID(), 
                            owner.PeerID());
                    
                    if(id == null) {}
                    else if( id.compareTo(target.PeerID()) == 0 )
                    {
                        owner = target;
                    }
                }
                
                Set<ChordKey> con = keyOwners.get(owner);
                if( con != null )
                    con.add(k);
            }
            
            //Transfer the keys to the new owners
            it = keyOwners.entrySet().iterator();
            while(it.hasNext())
            {
                TransferKeysEventRE tkeRE;
                Map.Entry<ChordNode, Set<ChordKey>> pairs 
                        = (Map.Entry<ChordNode, Set<ChordKey>>)it.next();
                
                if( !pairs.getValue().isEmpty() )
                {
                    ChordKey[] keysToSend 
                            = new ChordKey[pairs.getValue().size()];
                    
                    keysToSend = pairs.getValue().toArray(keysToSend);
                    tkeRE = mCOM.TransferKeys(keysToSend, null, pairs.getKey());
                    
                    //Cant put, node is dead
                    //Because we have been using these nodes
                    //We will just remove it and ignore it
                    if(tkeRE == null)
                    {
                        mFingerTable.RemoveNode(pairs.getKey());
                        it.remove();
                    }
                    //Remove nodes that was successfully added from successor 
                    else
                    {
                        //Keys transfered, remove them
                        entries.RemoveKey(tkeRE.KEYS_REMOVED_OR_ADDED);
                    }
                    
                }
            }
            //Last Operation, get keys that might belong to this node
            it = keyOwners.keySet().iterator();
            while( it.hasNext() )
            {
                RetrieveKeysEventRE rkeRET;
                TransferKeysEventRE traKeys;
                
                ChordNode node = (ChordNode)it.next();
                rkeRET = mCOM.RetrieveKeys(mId, node);
                
                //Node is dead, ignore
                if( rkeRET == null )
                {
                    mFingerTable.RemoveNode(node);
                    it.remove();
                }
                else if(rkeRET.KEYS != null && rkeRET.KEYS.length >0)
                {
                    traKeys = mCOM.TransferKeys(null, rkeRET.KEYS, node);
                    
                    //Node is dead, ignore
                    if(traKeys == null)
                    {
                       mFingerTable.RemoveNode(node);
                       it.remove();
                    }
                    else if(traKeys.KEYS_REMOVED_OR_ADDED != null 
                            && traKeys.KEYS_REMOVED_OR_ADDED.length >0)
                    {
                        entries.RemoveReplica(traKeys.KEYS_REMOVED_OR_ADDED);
                        entries.InsertKey(traKeys.KEYS_REMOVED_OR_ADDED);
                        replicateThese.add(traKeys.KEYS_REMOVED_OR_ADDED);
                    }
                }
            }
            
            for(ChordKey[] keys : replicateThese)
            {
                mFingerTable.GetSuccessorList().InvokeReplicateAll(keys);
            }

        }
    }
    
}