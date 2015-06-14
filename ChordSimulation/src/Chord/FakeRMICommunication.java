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
import Chord.FakeRMIEvents.*;
import Chord.FakeRMIEvents.ReturnRMIevents.*;
import Statistics.HopData;
/**
 * This class simulates the RMI network communication functionality the Java
 * API offers. These methods will call the methods in the other nodes.
 * 
 * These methods will send a specific event to the target node.
 * Every method expect a return event.
 * 
 * @see ReturnRMICommunication
 * @author Simon Edänge
 */
public class FakeRMICommunication
{
    /**
     * This class is used to return Fake RMI Events back to the issuer.
     * 
     * @see FakeRMICommunication For more information.
     * @author Simon Edänge
     */
    public class ReturnRMICommunication
    {
        final ChordNode mLocal;
        
        /**
         * Constructs a return com.
         * 
         * @param self Local node. 
         */
        
        public ReturnRMICommunication(ChordNode self)
        {
            mLocal = self;
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param targetResult The result from this operation.
         * @param target Target to send this reply.
         */
        public void ClosestPrecedingFingerEventRETURN(final String ID, 
                final ChordNode targetResult, 
                final ChordNode target )
        {
           ClosestPrecedingFingerEventRE re;
           re = new ClosestPrecedingFingerEventRE(targetResult, mLocal);
           re.SetUUID(ID);
           target.putEvent(re);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void RemoveReplicasEventRETURN(String ID, 
                final boolean result, 
                final ChordNode target)
        {
            RemoveReplicasEventRE re;
            re = new RemoveReplicasEventRE(mLocal, result);
            re.SetUUID(ID);
            target.putEvent(re);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void PutReplicasEventRETURN(String ID,
                final boolean result,
                final ChordNode target)
        {
            PutReplicasEventRE ret;
            ret = new PutReplicasEventRE(mLocal, result);
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void PutKeyEventRETURN(String ID, 
                final boolean result, 
                final ChordNode target)
        {
            PutKeyEventRE ret = new PutKeyEventRE(mLocal, result);
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param keys Added Keys.
         * @param replicas Added Replicas.
         * @param target Target to send this reply.
         */
        public void RetrieveKeysEventRETURN(String ID, 
                final ChordKey [] keys,
                final ChordKey [] replicas,
                final ChordNode target)
        {
            RetrieveKeysEventRE ret = new RetrieveKeysEventRE(
                    mLocal, 
                    keys, 
                    replicas);
            
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void PingEventRETUTN(String ID,
                final boolean result, final ChordNode target)
        {
            PingEventRE ret = new PingEventRE(result, mLocal);
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void GetPredecessorEventRETURN(String ID,
                final ChordNode result, final ChordNode target)
        {
            GetPredecessorEventRE re;
            re = new GetPredecessorEventRE(result , mLocal);
            re.SetUUID(ID);
            target.putEvent(re);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void GetSuccessorEventRETURN(String ID, 
                final ChordNode result, 
                final ChordNode target)
        {
            GetSuccessorEventRE re;
            re = new GetSuccessorEventRE(result, mLocal);
            re.SetUUID(ID);
            target.putEvent(re);
        }
       
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param target Target to send this reply.
         */
        public void FindSuccessorEventRETURN(String ID, 
                final ChordNode result, 
                final ChordNode target)
        {
            FindSuccessorEventRE ret;
            ret = new FindSuccessorEventRE(result, mLocal);
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param oldPred The old predecessor.
         * @param successors Successor-list from the notified node.
         * @param target Target to send this reply.
         */
        public void NotifyEventRETURN(String ID,
                final ChordNode oldPred,
                final ChordNode[] successors,
                final ChordNode target)
        {
            NotifyEventRE ret;
            ret = new NotifyEventRE(oldPred, successors, mLocal);
            ret.SetUUID(ID);
            target.putEvent(ret);        
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param result The result from this operation.
         * @param status STATUS_FORWARED, STATUS_FAILED, STATUS_SUCCESS.
         * @param hop The current HopData
         * @param target Target to send this reply.
         */
        public void LookupEventRETURN(String ID, final ChordNode result, 
                final LookupEventRE.Status status, final HopData hop, 
                final ChordNode target)
        {
            LookupEventRE ret = new LookupEventRE(
                    result, 
                    mLocal, 
                    status,
                    hop);
            
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
        
        /**
         * Called when returning back to the issuer with the same event type.
         * 
         * @param ID ProcessEvent ID from the issuer. The ID that the issuer
         * generated with it's ProcessEvent.
         * @param keys The keys result, either a Add transfer or Remove transfer.
         * @param target Target to send this reply.
         */
        public void TransferKeysRETURN(
                String ID, 
                final ChordKey[] keys, 
                final ChordNode target)
        {
            TransferKeysEventRE ret = new TransferKeysEventRE(target, keys);
            ret.SetUUID(ID);
            target.putEvent(ret);
        }
    }
    
    private final ChordNode mLocal;
    private final ReturnRMICommunication mRetCOM;
    
    /**
     * Constructs a Fake RMI Com.
     * @param self Local node.
     */
    public FakeRMICommunication(ChordNode self)
    {
        mLocal = self;
        mRetCOM = new ReturnRMICommunication(self);
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param id ID To find.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public ClosestPrecedingFingerEventRE closestPrecedingFinger( 
            final ChordId id, final ChordNode target)
    {
        if(id == null || target == null)
            return null;

        else if( target == mLocal )
        {
            ChordNode result = mLocal.closestPrecedingNode(id);
            return new ClosestPrecedingFingerEventRE(result, target);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        
        ClosestPrecedingFingerEvent cpfe;
        ClosestPrecedingFingerEventRE re;
        
        cpfe = new ClosestPrecedingFingerEvent(mLocal, id);
        target.putEvent(cpfe);
        re = mLocal.WaitForSingleObject(ClosestPrecedingFingerEventRE.class, 
               cpfe.GetUUID());
        
        return re;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param id ID To find.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public FindSuccessorEventRE findSuccessor(final ChordId id, 
            ChordNode target)
    {
        if(id == null || target == null)
            return null;
        
        else if( target == mLocal )
        {
            ChordNode result = mLocal.findSuccessor(id);
            return new FindSuccessorEventRE(result, target);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        FindSuccessorEvent fse;
        FindSuccessorEventRE ret;
        
        fse = new FindSuccessorEvent(id, mLocal);
        target.putEvent(fse);
        ret = mLocal.WaitForSingleObject(FindSuccessorEventRE.class, 
                fse.GetUUID());

        return ret;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param n The local node (this).
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public NotifyEventRE Notify(final ChordNode n,
            final ChordNode target)
    {
        if(n == null || target == null)
            return null;
                
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        NotifyEvent ne;
        NotifyEventRE ret;
        
        ne = new NotifyEvent(n);
        target.putEvent(ne);
        ret = mLocal.WaitForSingleObject(NotifyEventRE.class, 
                ne.GetUUID());
        
        return ret;
    }

    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public GetPredecessorEventRE predecessor(final ChordNode target)
    {
        if( target == null )
            return null;
        
        else if( target == mLocal )
        {
            return new GetPredecessorEventRE(mLocal.predecessor(), target);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        GetPredecessorEvent gpe;
        GetPredecessorEventRE ret;
        
        gpe = new GetPredecessorEvent(mLocal);
        target.putEvent(gpe);
        
        ret = mLocal.WaitForSingleObject(GetPredecessorEventRE.class, 
                gpe.GetUUID());
        
                
        return ret;
    }

     /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public GetSuccessorEventRE successor(final ChordNode target)
    {
        if( target == null )
            return null;
        else if( target == mLocal )
        {
            return new GetSuccessorEventRE(mLocal.successor(), target);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        GetSuccessorEvent gse;
        GetSuccessorEventRE ret;
        
        gse = new GetSuccessorEvent(mLocal);
        target.putEvent(gse);
        ret = mLocal.WaitForSingleObject(GetSuccessorEventRE.class, 
                gse.GetUUID());
        
             
        return ret;
    }

    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param key Key to find.
     * @param originCaller The node that issued this lookup.
     * @param originID Origin ID of the first lookup message. Used when returning.
     * @param hop Current Hop data.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public LookupEventRE lookup(final ChordId key, final ChordNode originCaller, 
            String originID, final HopData hop, 
            final ChordNode target)
    {
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        LookupEvent le;
        LookupEventRE ret;
        
        le = new LookupEvent(mLocal, originCaller, originID, hop, key);
        target.putEvent(le);
        
        ret = mLocal.WaitForSingleObject(LookupEventRE.class, 
               le.GetUUID());
        
        return ret;
    }

     /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param k Key to put.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public PutKeyEventRE PutKey(final ChordKey k, final ChordNode target)
    {
        if(k == null && target == null)
            return null;
        
        else if( target == mLocal )
        {
            boolean a = mLocal.PutKey(k);
            return new PutKeyEventRE(target, a);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        PutKeyEvent pke;
        PutKeyEventRE ret;
        
        pke = new PutKeyEvent(mLocal, k);
        target.putEvent(pke);
        
        ret = mLocal.WaitForSingleObject(PutKeyEventRE.class, 
                pke.GetUUID());
        
        return ret;
    }

    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param registerReply If a return event is required. If set to false,
     * this node will not wait for a return event from the target node.
     * @param replicas To replicate.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public PutReplicasEventRE PutReplicas(
            final boolean registerReply,
            final ChordKey[] replicas, 
            final ChordNode target)
    {
        if( target == null )
            return null;
        else if( target == mLocal )
        {
            boolean a = mLocal.PutReplicas(replicas);
            return new PutReplicasEventRE(target, a);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        PutReplicasEvent pre;
        PutReplicasEventRE ret = null;
        
        pre = new PutReplicasEvent(mLocal, replicas);
        pre.SetRequiresReturn(registerReply);
        
        target.putEvent(pre);
        
        if(registerReply == true)
        {
            ret = mLocal.WaitForSingleObject(PutReplicasEventRE.class, 
                    pre.GetUUID());
            
        }
        
        return ret;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param registerReply If a return event is required. If set to false,
     * this node will not wait for a return event from the target node.
     * @param caller If set, all replicas this caller is responsible for will be
     * removed.
     * @param replicasToRemove Replicas to remove. Ignored if caller is set.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public RemoveReplicasEventRE RemoveReplicas(
            final boolean registerReply,
            final ChordId caller, 
            final ChordKey[] replicasToRemove, 
            final ChordNode target)
    {
        if( target == null )
            return null;
        
        else if(target == mLocal)
        {
            boolean a = mLocal.RemoveReplicas(caller, replicasToRemove);
            return new RemoveReplicasEventRE(target, a);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        RemoveReplicasEvent rre;
        RemoveReplicasEventRE ret = null;
        
        rre = new RemoveReplicasEvent(mLocal, caller, replicasToRemove);
        rre.SetRequiresReturn(registerReply);
        
        target.putEvent(rre);
        
        if(registerReply == true)
        {
            ret = mLocal.WaitForSingleObject(RemoveReplicasEventRE.class, 
                    rre.GetUUID());
            
        }
               
        return ret;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public PingEventRE Ping(ChordNode target)
    {
        if( target == null )
            return null;
        
        else if( target == mLocal )
        {
            return  new PingEventRE(mLocal.isActive(), target);
        }
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        PingEvent e = new PingEvent(mLocal);
        PingEventRE re;
        
        target.putEvent(e);
        re = mLocal.WaitForSingleObject(PingEventRE.class, e.GetUUID());
        
        
        return re;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * @param toID Retrieve all keys this id is responsible for.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public RetrieveKeysEventRE RetrieveKeys(final ChordId toID,
            final ChordNode target)
    {
        if( target == null )
            return null;

        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        RetrieveKeysEvent e = new RetrieveKeysEvent(toID, mLocal);
        RetrieveKeysEventRE re;
        
        target.putEvent(e);
        re = mLocal.WaitForSingleObject(RetrieveKeysEventRE.class, 
                e.GetUUID());
        
        return re;
    }
    
    /**
     * Sends this event to a node the method represent. 
     * Target node will execute this event, if it is ALIVE.
     * Every method waits for a return event, until time out. If time out,
     * the method returns null.
     * 
     * Note: Only one ChordKey should be provided. Either an Add transfer or
     * a Remove transfer.
     * 
     * @param keys Keys to add.
     * @param remove Keys to remove.
     * @param target Target to send this event to.
     * @return Return event if success, else null.
     */
    public TransferKeysEventRE TransferKeys(final ChordKey[] keys,
            final ChordKey[] remove,
            final ChordNode target)
    {
        if(target == null)
            return null;
        
        //Check if target is dead.
        //See method description
        if ( !IsTargetActive(target) )
            return null;
        
        TransferKeysEvent e = new TransferKeysEvent(mLocal, keys, remove);
        TransferKeysEventRE re;
        
        target.putEvent(e);
        re = mLocal.WaitForSingleObject(TransferKeysEventRE.class, 
                e.GetUUID());
        
        return re;
        
    }
    
    /**
     * Check if the target is active.
     * This is done because this is a simulation.
     * In a real RMI network, we would know if the peer has lost connection.
     * 
     * @param target Node to check if active.
     * @return True if active, else false.
     */
    private boolean IsTargetActive(ChordNode target)
    {
        if(target == null)
        {
            System.out.println("COM: Target null, no packet sent."
                    + mLocal.PeerID().GetIdentifierCH());
            
            return false;
        }
        
        if( !target.isActive() )
        {
            System.out.println("COM: Dead node at "
                    + mLocal.PeerID().GetIdentifierCH());
            System.out.println("Node Will be removed and replaced: "
                    + target.PeerID().GetIdentifierCH());
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Return the return Com.
     * Used to reply events.
     * 
     * @return Return com.
     */
    public final ReturnRMICommunication GetReturnCOM()
    {
        return mRetCOM;
    }
    
}