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
package Chord;

import Chord.FingerTable.SuccessorList;
import Chord.FakeRMIEvents.ReturnRMIevents.PutKeyEventRE;
import Chord.FakeRMIEvents.*;
import Chord.FakeRMIEvents.ReturnRMIevents.*;
import Chord.FingerTable.Finger;
import Chord.FingerTable.FingerTable;
import Chord.Tasks.CheckPredecessorTask;
import Chord.Tasks.Event.ClosestPrecedingFingerTask;
import Chord.Tasks.Event.FindSucessorTask;
import Chord.Tasks.Event.LookupTask;
import Chord.Tasks.Event.NotifyTask;
import Chord.Tasks.Event.PutKeyTask;
import Chord.Tasks.FixFingersTask;
import Chord.Tasks.StabilizeTask;
import Process.ProcessEvent;
import Process.ProcessX;
import Statistics.HopData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main Node class. Here is everything that represent a Chord Node.
 * All important functions is in here. Some e.g. Stabilize and FixFingers
 * Methods can be found in Chord.Tasks Package.
 * 
 * @author Simon Edänge
 */
public class ChordNode extends ProcessX implements ChordRemote
{
    private final ChordId mId;
    private final int mBits;
    private final ChordNode mBootStrap;
    private boolean mActive;
    private final FakeRMICommunication mCOM;

    protected FingerTable mFingerTable;
    
    private final ScheduledExecutorService mMaintenanceTasks;
    private final ExecutorService mExecutor;
    
    private static class ChordThreadFactory implements
                    java.util.concurrent.ThreadFactory {

            private final String executorName;

            ChordThreadFactory(String executorName) {
                    this.executorName = executorName;
            }

            @Override
            public Thread newThread(Runnable r) {
                    Thread newThread = new Thread(r);
                    newThread.setName(this.executorName);
                    return newThread;
            }

    }
    
    /**
     * Constructs a ChordNode
     * 
     * @param bits Bits the chord ring utilizes.
     * @param maxFingers The maximum fingers to use.
     * @param bootStrap The bootstrap node to join the network with.
     */
    public ChordNode(int bits, int maxFingers, ChordNode bootStrap)
    {
        mId = new ChordId(Integer.toString(IDGenerator.GenerateID()), IDGenerator.GenerateIP(), bits);
        mBits = bits;
        mBootStrap = bootStrap;
        mCOM = new FakeRMICommunication(this);
        
        //Init fingers values
        mFingerTable = new FingerTable(maxFingers, 3, bits, this);
        
        this.mMaintenanceTasks = new ScheduledThreadPoolExecutor(1,
				new ChordThreadFactory("MaintenanceTaskExecution - "
                                        +mId.GetIdentifierCH()));
        
        mExecutor = Executors.newCachedThreadPool(
                new ChordThreadFactory("AsynchronousExecution - " 
                        + mId.GetIdentifierCH()));

        this.enableWaitObjectRegister(true);

        mActive = false;
        mKeepAlive = true;
    }
    
    @Override
    public ChordNode closestPrecedingNode(ChordId id) //Remote
    {
        if(id == null)
            return null;
        
        Finger f;
        for(int i = mFingerTable.GetSize()-1; i >= 0; i--)
        {
            f = mFingerTable.Get(i);
            
            if( f == null || f.node == null )
                continue;
            
            if( ChordId.isBetween(f.node.PeerID(), mId, id) )
            {
                return f.node;
            }
        }
        
        return this;
    }
    
    /**
     * Unlike closestPrecedingNode method, this method searches all available
     * sources including successor-list, predecessor etc. to find the absolute
     * closest preceding node this node has.
     * 
     * @param key
     * @return Closest preceding node.
     */
    protected ChordNode getClosestPrecedingNode(ChordId key)
    {
        if (key == null) 
        {
            return null;
        }

        Map<ChordId, ChordNode> foundNodes = new HashMap<>();
        // determine closest preceding reference of finger table
        ChordNode closestNodeFT = closestPrecedingNode(key);
        if (closestNodeFT != null && closestNodeFT != this) {
            foundNodes.put(closestNodeFT.PeerID(), closestNodeFT);
        }

        // determine closest preceding reference of successor list
        ChordNode closestNodeSL = GetSuccessorList()
                .getClosestPrecedingSuccessor(key);
        
        if (closestNodeSL != null) {
                foundNodes.put(closestNodeSL.PeerID(), closestNodeSL);
        }

        // predecessor is appropriate only if it precedes the given id
        ChordNode predecessorIfAppropriate = null;
        ChordNode predecessor = predecessor();
        if (predecessor != null
                        && ChordId.isBetween(key, predecessor.PeerID(), mId))
        {
                predecessorIfAppropriate = predecessor;
                foundNodes.put(predecessor.PeerID(), predecessor);
        }

        // with three references which may be null, there are eight (8) cases we
        // have to enumerate...
        ChordNode closestNode;
        List<ChordId> orderedIDList = new ArrayList<>(foundNodes.keySet());
        orderedIDList.add(key);
        int sizeOfList = orderedIDList.size();
        // size of list must be greater than one to not only contain the key.
        // if (sizeOfList > 1) {

        /*
         * Sort list in ascending order
         */
        Collections.sort(orderedIDList);
        /*
         * The list item with one index lower than that of the key must be the
         * id of the closest predecessor or the key.
         */
        int keyIndex = orderedIDList.indexOf(key);
        /*
         * As all ids are located on a ring if the key is the first item in the
         * list we have to select the last item as predecessor with help of this
         * calculation.
         */
        int index = (sizeOfList + (keyIndex - 1)) % sizeOfList;
        /*
         * Get the references to the node from the map of collected nodes.
         */
        ChordId idOfclosestNode = orderedIDList.get(index);
        closestNode = foundNodes.get(idOfclosestNode);
        if (closestNode == null) 
        {
            return null;
        }
        
        return closestNode;
    }
    
    @Override
    public ChordNode findSuccessor(ChordId id) // Remote
    {
        ChordNode successor = successor();
        
        if( successor == null )
            return this;
        
        else if( ChordId.isBetweenSuccessor(id, mId, successor.PeerID()) )
        {
            return successor;
        }
        else
        {
            FindSuccessorEventRE re;
            ChordNode closestPreceeding = closestPrecedingNode(id);
            
            if(closestPreceeding == this)
                return this;
            
            re = mCOM.findSuccessor(id, closestPreceeding);
            
            if(re == null)
            {
                mFingerTable.RemoveNode(closestPreceeding);
                return findSuccessor(id);
            }
            
            return re.NODE_TARGET_REQUESTED;
        }
    }
    
    @Override
    public ChordNode[] Notify(ChordNode n) //Remote
    {
        ChordNode[] mySuccessors;
        ChordNode oldpred = predecessor();
        mySuccessors = mFingerTable.GetSuccessorList().GetArray();
        
        if(oldpred == n)
        {
            return mySuccessors;
        }
        else if( oldpred == null || ChordId.isBetweenNotify(n.PeerID(), 
                oldpred.PeerID(), mId) )
        {
            SetPredecessor(n);

            return mySuccessors;
        }
        
        return null;
    }
    
    @Override
    public ChordNode predecessor() //Remote
    {
        return mFingerTable.GetPredecessor();
    }
    
    @Override
    public ChordNode successor() //Remote
    {
        return mFingerTable.GetImmediateSuccessor();
    }
    
    /**
     * This is the nodes ChordId. It contains its hashed id and key as well as
     * the original values. This one is stored locally when getting a node.
     * Calling this function is not done remote.
     * 
     * @return ChordId of this node.
     */
    public ChordId PeerID() // NOT - Remote, Saved locally when getting a node
    {
        return mId;
    }
    
    @Override
    public ChordNode lookup(final ChordId key, final ChordNode originCaller, 
            final HopData hop, 
            String originID) //Remote
    {
        //Check if this node had either the replica or the real key.
        if(GetSuccessorList().GetEntries().HasKey(key.GetKeyCH()))
            return this;
        if(GetSuccessorList().GetEntries().HasReplica(key.GetKeyCH()))
            return this;
        
        //Look for closest finger
        ChordId temp = new ChordId(mBits);
        temp.SetIdentifier(key.GetKey());
        temp.SetIdentifierCH(key.GetKeyCH());
        //ChordNode n = closestPrecedingFinger(temp);
        ChordNode n = getClosestPrecedingNode(temp);
        
        //I am the closest preceding node
        if(n == null || n == this)
        {
            //Target has been overshot
            //check if it between me and successor
            
            if( successor() == null )
                return this;
            else if(ChordId.isBetweenSuccessor(temp, mId, successor().PeerID()))
            {
                if( n != successor() )
                    n = successor();
                else
                    return this;
            }
            else
                return this;
        }
        
        if( n == originCaller || hop.GetTargets().contains(n) )
            return this;
        
        //Foward the message
        System.out.println(PeerID().GetIdentifierCH() + " Forwards the message"
                + " To: " + n.PeerID().GetIdentifierCH());
        
        LookupEventRE re = mCOM.lookup(key, originCaller, originID, hop, n);
        
        if(re == null)
        {
            mFingerTable.RemoveNode(n);
            return lookup(key, originCaller, hop, originID);
        }
        else if(re.HOP_STATUS == LookupEventRE.Status.STATUS_FOWARED)
            return null;
        
        else if(re.HOP_STATUS == LookupEventRE.Status.STATUS_FAILED)
        {
            hop.AddHop(this);
            return null;
        }
        
        hop.AddHop(this);
        
        return re.NODE_TARGET_REQUESTED;
    }
    
    /**
     * Joins a node in the network. This application uses a bootstrap node.
     * 
     * @param n Node To Join.
     */
    public void Join(ChordNode n)
    {
        if( n != null && n != this)
        {
            mFingerTable.ClearAll();
            mFingerTable.InitFingers();
            
            //**Find Sucessor of this node**//
            ChordNode mySucessor;
            FindSuccessorEventRE ret;

            //Fake Send   n.findSuccessor(mId)
            ret = mCOM.findSuccessor(mFingerTable.Get(0).start, n);
            
            if( ret == null)
            {
                System.out.println("Cannot connect to bootstrap, kill myself. " 
                        + mId.GetIdentifierCH());
                this.kill();
                return;
            }
            //Extract data
            mySucessor = ret.NODE_TARGET_REQUESTED;

            if( mySucessor == null)
            {
                //BootStrap is probably my Successor
                mySucessor = mBootStrap;
            }
            
            System.out.println("Node: "+ mId.GetIdentifierCH() +" got a new Successor: " + mySucessor.PeerID().GetIdentifierCH());
            
            SetSuccessor(mySucessor);
            SetPredecessor(null);
                
            //Stabilize
            //maintenanceTasks.execute(new StabilizeTask(this));
            StabilizeTask t = new StabilizeTask(this);
            t.run();
            
            //Find node and put key
            ChordKey k = new ChordKey(mId.GetKeyCH(), mId.GetKey(), 
            mId.GetIdentifierCH(), mId.GetIdentifier());
            
            boolean a = Insert(k);
            if( a == false )
            {
                System.out.println("Cannot put key, "+mId.GetIdentifierCH()+
                        " kill myself!");
                kill();
            }

        }
        else // Alone
        {
            mFingerTable.ClearAll();
            mFingerTable.InitFingers();
            SetPredecessor(null);
            SetSuccessor(null);
        }

    }
    
    /**
     * Creates background tasks that executes every time t.
     * Such as: Stabilize, FixFingers and CheckPredecessor.
     */
    private void CreateTasks()
    {
        // start thread which periodically stabilizes with successor
        this.mMaintenanceTasks.scheduleWithFixedDelay(new StabilizeTask(
                        this), 0, 6, TimeUnit.SECONDS);

        // start thread which periodically attempts to fix finger table
        this.mMaintenanceTasks.scheduleWithFixedDelay(new FixFingersTask(
                        this), 6, 6, TimeUnit.SECONDS);
        
        // start thread which periodically attempts to check predecessor
        this.mMaintenanceTasks.scheduleWithFixedDelay(new CheckPredecessorTask(
                this), 6, 6, TimeUnit.SECONDS);
        
    }
    
    /**
     * Finds the successor of key k and put it on that node.
     * Called when joining the Chord ring.
     * 
     * @param k Key to insert.
     * @return True on success.
     */
    public final boolean Insert(ChordKey k)
    {
        if(k == null)
            return false;
        
        //Make tmepID of key
        ChordId id = new ChordId(mBits);
        id.SetIdentifier(mId.GetKey());
        id.SetIdentifierCH(mId.GetKeyCH());
            
        boolean inserted = false;
        PutKeyEventRE ret;
        ChordNode responsibleNode = null;
        
        while (!inserted) 
        {
            // find successor of id
            responsibleNode = this.findSuccessor(id);

            ChordId temp = k.WhichIsClosestToKey(mId, responsibleNode.PeerID());
            
            if( temp.Equals(mId) )
            {
                responsibleNode = this;
            }
            
            // invoke PutKey method
            ret = mCOM.PutKey(k, responsibleNode);

            if(ret != null)
                inserted = true;
        }
        
        if(responsibleNode != null)
        {
            System.out.println("Key: " + k.KEY.toString() +" was put in ID: " + 
                    responsibleNode.PeerID().GetIdentifierCH().toString());
        }
        
        return inserted;
    }
    
    @Override
    public boolean PutKey(ChordKey k) // Remote
    {
        Entries entries = GetSuccessorList().GetEntries();
        // Possible, but rare situation: a new node has joined which now is
        // responsible for the id!
       // if ((predecessor() != null)
              //          && !k.WhichIsClosestToKey(
                        //            predecessor().PeerID().GetIdentifierCH(),
                       //             mId.GetIdentifierCH())) 
        {
             //   mCOM.PutKey(k, predecessor());
               // return true;
        }
        
        entries.RemoveReplica(k);
        boolean result = entries.InsertKey(k);
        
        if( !result )
            return false;
        
        //Invoke replication on all successors 
        ChordKey[] key = new ChordKey[1];
        key[0] = k;
        GetSuccessorList().InvokeReplicateAll(key);

        return true;
    }
    
    @Override
    public boolean PutReplicas(ChordKey[] replicas) //Remote
    {
        if(replicas == null)
            return false;
        
        return GetSuccessorList().GetEntries().InsertReplica(replicas);
    }
    
    @Override
    public KeyContainer RetrieveKeys(ChordId toId) //Remote
    {
        Entries entries = GetSuccessorList().GetEntries();
        ChordKey[] keys;
        ChordKey[] replicas;
        
        if(toId == null)
        {
            Set<ChordKey> k = entries.GetAllKeys();
            Set<ChordKey> r = entries.GetAllReplicas();
            
            keys = new ChordKey[k.size()];
            replicas = new ChordKey[r.size()];
            
            keys = k.toArray(keys);
            replicas = r.toArray(replicas);
        }
        else
        {
            Set<ChordKey> k = entries.GetKeysRespnosibleFor(toId);
            Set<ChordKey> r = entries.GetReplicasRespnosibleFor(toId);
            
            keys = new ChordKey[k.size()];
            replicas = new ChordKey[r.size()];
            
            keys = k.toArray(keys);
            replicas = r.toArray(replicas);
        }
        
        return new KeyContainer(keys, replicas);
    }

    @Override
    public boolean RemoveReplicas(ChordId caller, ChordKey replicasToRemove[])//Remote
    {
        Entries entries = GetSuccessorList().GetEntries();
        
        if(replicasToRemove != null)
        {
            // remove only replicas of given entry
            return entries.RemoveReplica(replicasToRemove);
        }
        
        else
        {
            // remove all replicas in interval
            /*
             * Determine entries to remove. These entries are located between
             * the id of the local peer and the argument sendingNodeID
             */
            Set<ChordKey> allReplicasToRemove = entries
                    .GetReplicasRespnosibleFor(caller);

            /*
             * Remove entries
             */
            ChordKey temp[] = new ChordKey[allReplicasToRemove.size()];
            return entries.RemoveReplica(allReplicasToRemove.toArray(temp));

        } 

    }
    
    @Override
    public synchronized ChordKey[] TransferKeys(
            final ChordKey[] key_add,
            final ChordKey[] key_remove) //Remote
    {
        Entries entries = GetSuccessorList().GetEntries();
        ChordKey[] returnKeys = null;
        
        if(key_remove != null)
        {
            returnKeys = entries.RemoveKey(key_remove);
            
            if(returnKeys != null && returnKeys.length >0)
            {
                entries.InsertReplica(returnKeys);
                GetSuccessorList().InvokeRemoveReplicateAll(returnKeys);
            }
        }
        
        if(key_add != null)
        {
            entries.RemoveReplica(key_add);
            returnKeys = entries.InsertKey(key_add);
            GetSuccessorList().InvokeReplicateAll(returnKeys);
        }
        
        return returnKeys;
    }
    
    /**
     * 
     * @return The successor-list.
     */
    public SuccessorList GetSuccessorList()
    {
        return mFingerTable.GetSuccessorList();
    }
    
    /**
     * @return If this node is Active.
     */
    public synchronized boolean isActive()
    {
        return mActive;
    }
    
    /**
     * Tells the thread to wait for a specific object.
     * Used for waiting for return events.
     * 
     * @param <T> Class template.
     * @param theClass The return class to wait for, e.g. GetSuccessorEventRE.
     * @param ID The expected ID of the return Event.
     * @return The return Event, null if failed after time out.
     */
    public <T> T WaitForSingleObject(Class<T> theClass, String ID)
    {
        long timeOut = 3000;
        
        NotifyOnWaitObject<T> object = new NotifyOnWaitObject<>(
                theClass, 
                System.currentTimeMillis(),
                ID);
        
        registerOnWaitObject(object);
        CountDownLatch mutex = object.GetMutex();
        ProcessEvent e;
   
        try {
            mutex.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        e = object.PollEvent();
        
        if(e == null)
        {
            unregisterOnWaitObject(object);
            
            System.out.println("COM: TimeOut packet at " 
                    + this.PeerID().GetIdentifierCH());
            System.out.println("Excpected: " 
                    + object.GetNotifyClass().getSimpleName());
        }
        
        return theClass.cast(e);
    }
    
    /**
     * 
     * @return The finger table.
     */
    public final FingerTable GetFingerTable()
    {
        return this.mFingerTable;
    }
    
    /**
     * 
     * @param n Sets an immediate successor.
     */
    public void SetSuccessor(ChordNode n)
    {
        mFingerTable.SetImmediateSuccessor(n);
    }
    
    /**
     * 
     * @param n Sets an immediate predecessor.
     */
    public void SetPredecessor(ChordNode n)
    {
        mFingerTable.SetPredecessor(n);
    }
    
    @Override
    public void run()
    {
        if(!mKeepAlive)
            return;
            
        if( mActive == false )
        {
            mFingerTable.InitFingers();
            mActive = true;
            CreateTasks();
        }
        
        ProcessEvent e = peekEvent();

        while(mKeepAlive && e != null)
        {
            HandleEvent(e);

            e = peekEvent();
        }
    }
    
    /**
     * Handles incoming ProcessEvents from other nodes.
     * 
     * @param e Event to handle.
     */
    private void HandleEvent(ProcessEvent e)
    {
        if( e instanceof GetSuccessorEvent )
        {
            GetSuccessorEvent gse = (GetSuccessorEvent)e;
            
            mCOM.GetReturnCOM().GetSuccessorEventRETURN(e.GetUUID(), 
                    successor(), gse.NODE_CALLER);
            
        }
        else if ( e instanceof GetPredecessorEvent )
        {
            GetPredecessorEvent gpe = (GetPredecessorEvent)e;
            
            mCOM.GetReturnCOM().GetPredecessorEventRETURN(e.GetUUID(), 
                    predecessor(), gpe.NODE_CALLER);
            
        }
        else if( e instanceof PingEvent )
        {
            PingEvent pe = (PingEvent)e;
            mCOM.GetReturnCOM().PingEventRETUTN(e.GetUUID(), mActive, 
                    pe.NODE_CALLER);
        }
        
        else if( e instanceof LookupEvent )
        {
            mExecutor.execute(new LookupTask(this, e));
            
        }
        else if( e instanceof NotifyEvent )
        {
            mExecutor.execute(new NotifyTask(this, e));
            
        }
        else if( e instanceof PutKeyEvent )
        {
            mExecutor.execute(new PutKeyTask(this, e));
            
        }
        else if( e instanceof PutReplicasEvent )
        {
            PutReplicasEvent pre = (PutReplicasEvent)e;
            
            if( pre.RequiresReturn() )
            {
                mCOM.GetReturnCOM().PutReplicasEventRETURN(pre.GetUUID(), 
                        true, pre.NODE_CALLER);
            }
            
            PutReplicas(pre.REPLICAS);
            
        }
        else if( e instanceof RemoveReplicasEvent)
        {
            RemoveReplicasEvent rre = (RemoveReplicasEvent)e;
            
            if( rre.RequiresReturn() )
            {
                mCOM.GetReturnCOM().RemoveReplicasEventRETURN(rre.GetUUID(), 
                        true, rre.NODE_CALLER);
            }
            
            RemoveReplicas(rre.ID_RANGE, rre.KEYS_TO_REMOVE);
            
        }
        else if( e instanceof TransferKeysEvent )
        {
            TransferKeysEvent ev = (TransferKeysEvent)e;
            
            ChordKey[] removedKeys = TransferKeys(
                    ev.KEYS_ADD, 
                    ev.KEYS_REMOVE);
            
            
            mCOM.GetReturnCOM().TransferKeysRETURN(
                    ev.GetUUID(),
                    removedKeys,
                    ev.NODE_CALLER);
            
        }
        else if( e instanceof RetrieveKeysEvent)
        {
            
            RetrieveKeysEvent ev = (RetrieveKeysEvent)e;
            KeyContainer con;
            
            if(ev.NODE_CALLER == null)
                return;
            
            con = RetrieveKeys(ev.TO_ID);

            mCOM.GetReturnCOM().RetrieveKeysEventRETURN(ev.GetUUID(), 
                con.GetKeys(), con.GetReplicas(), ev.NODE_CALLER);
            
            
        }
        else if( e instanceof FindSuccessorEvent )
        {
            mExecutor.execute(new FindSucessorTask(this, (FindSuccessorEvent)e));
        }
        else if( e instanceof ClosestPrecedingFingerEvent )
        {
            mExecutor.execute(new ClosestPrecedingFingerTask(this, e));
        }
    }
    
    /**
     * 
     * @return This nodes bootstrap node.
     */
    public final ChordNode GetBootstrap()
    {
        return mBootStrap;
    }
    
    /**
     * 
     * @return The com object to communicate with other nodes.
     */
    public final FakeRMICommunication GetCOM()
    {
        return mCOM;
    }
    
    @Override
    public synchronized void kill()
    {
        mKeepAlive = false;
        mActive = false;
        mMaintenanceTasks.shutdownNow();
        mExecutor.shutdownNow();
    }
}