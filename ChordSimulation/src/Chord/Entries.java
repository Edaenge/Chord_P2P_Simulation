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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class stores ChordKeys (replicas and real keys).
 * It is used by nodes to store key's they are currently maintaining.
 * 
 * @author Simon Edänge
 */
public class Entries
{
    private final List<ChordKey> mReplicas;
    private final List<ChordKey> mKeys;
    private final ChordNode mLocal;
    
    /**
     * Construct Entries.
     * 
     * The lists used to store Keys and Replicas are separated. The lists are
     * synchronized so they cannot be corrupted.
     * 
     * @param self The local node.
     */
    public Entries(ChordNode self)
    {
        mLocal = self;
        mReplicas = Collections.synchronizedList(
                new ArrayList<ChordKey>());
        
        mKeys = Collections.synchronizedList(
                new ArrayList<ChordKey>());
    }
    
    /**
     * Checks if it is possible to insert the provided replica.
     * 
     * @param k
     * @return False if that replica already exist or if this node is 
     * maintaining that key.
     * 
     */
    private boolean CanInsertReplica(ChordKey k)
    {
        if(k == null)
            return false;
        
        if(HasReplica(k))
            return false;
        
        return !HasKey(k);
    }
    
    /**
     * Checks if it is possible to insert the provided key.
     * 
     * @param k
     * @return False if that key already exist or if this node has a replica of
     * that key.
     */
    private boolean CanInsertKey(ChordKey k)
    {
        if(k == null)
            return false;
        
        if(HasKey(k))
            return false;
        
        return !HasReplica(k);
    }
    
    /**
     * Search for a key or replica inside a list.
     * @param k Consisted Hashed Key to find.
     * @param list In which list to search.
     * @return Null if not found.
     */
    private ChordKey SearchKey(final BigInteger k, final List<ChordKey> list)
    {
        //Linear search, not optimal
        
        if(k == null)
            return null;
        
        synchronized(list)
        {
            for(ChordKey key : list)
            {
                if(key.KEY.compareTo(k) == 0)
                {
                    return key;
                }
            }
        }
        
        return null;  
    }
    
    /**
     * Check if a replica exists.
     * 
     * @param k
     * @return True if found.
     */
    public boolean HasReplica(BigInteger k)
    {
        return (SearchKey(k, mReplicas) != null);
    }
    
    /**
     * Check if a replica exists.
     * 
     * @param k
     * @return True if found.
     */
    public boolean HasReplica(ChordKey k)
    {
        return this.HasReplica(k.KEY);
    }
    
    /**
     * Check if a key exists.
     * 
     * @param k
     * @return True if found.
     */
    public boolean HasKey(BigInteger k)
    {
        return (SearchKey(k, mKeys) != null);
    }      
    
    /**
     * Check if a key exists.
     * 
     * @param k
     * @return True if found.
     */
    public boolean HasKey(ChordKey k)
    {
        return this.HasKey(k.KEY);
    }
    
    /**
     * Inserts a replica into the list.
     * Returns false if that replica already exist or if that replica is being
     * maintained as a key in this node.
     * 
     * @param k
     * @return True on success.
     */
    public boolean InsertReplica(ChordKey k)
    {
        if(CanInsertReplica(k))
        {
            mReplicas.add(k);
            return true;
        }
        
        return false;
    }
    
    /**
     * Inserts replicas into the list. 
     * Doesn't check if the replicas was added.
     * Always returns true, unless the parameter is null.

     * @param k
     * @return True on success.
     */
    public boolean InsertReplica(ChordKey k[])
    {
        if(k == null)
            return false;
        
        for(ChordKey key: k)
        {
            this.InsertReplica(key);
        }
        
        return true;
    }
    
    /**
     * Inserts a Key into the list.
     * Returns false if that key already exist or if it has a replica of that
     * key.
     * 
     * @param k
     * @return True on success.
     */
    public boolean InsertKey(ChordKey k)
    {
        if(CanInsertKey(k))
        {
            mKeys.add(k);
            return true;
        }
        
        return false;
    }
    
    /**
    * Insert a group of keys to the list.
    * 
    * @param k Keys to be added.
    * @return An array of successfully added keys.
    */
    public ChordKey[] InsertKey(ChordKey k[])
    {
        Set<ChordKey> added = new HashSet<>();
        ChordKey[] ret;
        
        if(k == null)
            return new ChordKey[0];
        
        for(ChordKey key: k)
        {
            if(this.InsertKey(key) == true)
                added.add(key);
        }
        
        ret = new ChordKey[added.size()];
        return added.toArray(ret);
    }
    
    /**
     * Removes replica from the list.
     * @param k
     * @return True if success.
     */
    public boolean RemoveReplica(ChordKey k)
    {
        if(k == null)
            return false;
        
        ChordKey s = SearchKey(k.KEY, mReplicas);
        return mReplicas.remove(s);
    }
    
    /**
     * Remove replicas from the list. 
     * Doesn't check if the replicas was removed.
     * Always returns true, unless the parameter is null.

     * @param k
     * @return True on success.
     */
    public boolean RemoveReplica(ChordKey[] k)
    {
        if(k == null)
            return false;
        else if(k.length == 0)
            return false;
            
        for(ChordKey key : k)
        {
            this.RemoveReplica(key);
        }
        
        return true;
    }
    
    /**
     * Remove key from the list.
     * @param k
     * @return true on success.
     */
    public boolean RemoveKey(ChordKey k)
    {
        ChordKey s = SearchKey(k.KEY, mKeys);
        return mKeys.remove(s);
    }   
    
    /**
    * Remove keys from the list.
    * 
    * @param k
    * @return Successfully removed keys.
    */
    public ChordKey[] RemoveKey(ChordKey k[])
    {
        if(k == null)
            return null;
        
        else if(k.length == 0)
            return k;
        
        Set<ChordKey> temp = new HashSet<>();
        ChordKey[] removed;
        
        for(ChordKey key : k)
        {
            if( RemoveKey(key) )
                temp.add(key);
        }
        
        removed = new ChordKey[temp.size()];
        
        return temp.toArray(removed); 
    }
    
    /**
     * Get all replicas that this ChordId is responsible for.
     * 
     * @param fromID
     * @return Responsible replicas.
     */
    public Set<ChordKey> GetReplicasRespnosibleFor(final ChordId fromID) 
    {
        final Set<ChordKey> result = 
                GetEntiesInInterval(fromID, mLocal.PeerID(), mReplicas);
        
        if(result == null)
            return null;

        return result;
        
    }
    
    /**
     * Get all keys this ChorId is responsible for.
     * @param fromID
     * @return Responsible keys.
     */
    public Set<ChordKey> GetKeysRespnosibleFor(final ChordId fromID)
    {
        final Set<ChordKey> result = 
                GetEntiesInInterval(fromID, mLocal.PeerID(), mKeys);
        
        if(result == null)
            return null;
        
        return result;
    }
    
    /**
     * 
     * @param fromID
     * @param toID
     * @param list
     * @return Get all Entries in the interval from the list.
     */
    private Set<ChordKey> GetEntiesInInterval(final ChordId fromID, 
            final ChordId toID, 
            final List<ChordKey> list)
    {     
        if(fromID == null || toID == null)
            return null;
        
        Set<ChordKey> result = new HashSet<>();
        
        synchronized(list)
        {
            for(ChordKey k : list)
            {
                BigInteger b = k.WhichIsClosestToKey(fromID.GetIdentifierCH(), 
                        toID.GetIdentifierCH());
                if(b != null && b.compareTo(fromID.GetIdentifierCH()) == 0)
                {
                    result.add(k);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param k
     * @return The replica if found, else null.
     */
    public final ChordKey GetReplica(ChordId k)
    {
       if( k == null )
           return null;
       
        ChordKey ret = SearchKey(k.GetKeyCH(), mReplicas);
        
        return ret;
    }
    
    /**
     * 
     * @param k
     * @return The key if found, else null.
     */
    public final ChordKey GetKey(ChordId k)
    {
        if( k == null )
            return null;
       
        ChordKey ret = SearchKey(k.GetKeyCH(), mKeys);
        
        return ret; 
    }
    
    /**
     * 
     * @return All keys in the list.
     */
    public Set<ChordKey> GetAllKeys()
    {
        Set<ChordKey> set = null;
        
        synchronized(mKeys)
        {
            set = new HashSet<>(mKeys);
        }
        return set;
    }
    
    /**
     * 
     * @return All replicas in the list.
     */
    public Set<ChordKey> GetAllReplicas()
    {
        Set<ChordKey> set = null;
        
        synchronized(mReplicas)
        {
            set = new HashSet<>(mReplicas);
        }
        
        return set;
    }
}