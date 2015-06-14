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

import Statistics.HopData;

/**
 *  An interface containing important Chord (Remote) methods.
 * Remote means these methods can be called by other nodes in the network.
 * @author Simon Edänge
 */
public interface ChordRemote
{
    /**
     * Ask node n to find the closest preceding node of id.
     * 
     * @param id
     * @return If found, return found node. If not found, return self.
     */
    ChordNode closestPrecedingNode(ChordId id);
    /**
     * Ask node n to find the successor of id.
     * 
     * @param id
     * @return If found, return found node. If not found, return self.
     */
    ChordNode findSuccessor(ChordId id);
    /**
     * Node n notifies node n' to be its predecessor.
     * 
     * @param n
     * @return The successor-list of the notified node, if accepted, else null.
     */
    ChordNode[] Notify(ChordNode n);
    /**
     * Ask node n to return it's predecessor.
     * 
     * @return Current predecessor of n.
     */
    ChordNode predecessor();
    /**
     * Ask node n to return it's immediate successor.
     * 
     * @return Current immediate successor of n.
     */
    ChordNode successor();
    /**
     * Used for the lookup simulation.
     * 
     * 
     * @param key Key to find in the chord ring.
     * @param originCaller The issuer who started this recursive lookup search.
     * @param hop The current hopData. 
     * @param originID The origin ProcessEvent UUID.

     * 
     * @return The node that has the key either as a replica or key.
     */
    public ChordNode lookup(ChordId key, ChordNode originCaller, HopData hop, 
            String originID);
    
    /**
     *  Put a key on node n.
     * 
     * @param k
     * @return True if successful.
     */
    boolean PutKey(ChordKey k);
    
    /**
     * Put replicas on node n.
     * 
     * @param replicas
     * @return True on success.
     */
    boolean PutReplicas(ChordKey[] replicas);
    
    /**
     * Remove replicas from node n.
     * 
     * @param caller If caller is provided, node n will remove all replicas id
     * is responsible for.
     * 
     * @param replicasToRemove If caller is provided, this parameter is ignored.
     * 
     * @return True if successful.
     */
    boolean RemoveReplicas(ChordId caller, ChordKey replicasToRemove[]);
    
    /**
     * Retrieve keys and replicas from node n.
     * If toId is not specified, node n will return all replicas and keys to the
     * issuer.
     * 
     * @param toId Get all replicas and keys this id is responsible for.
     * @return Keys and Replicas 
     */
    public KeyContainer RetrieveKeys(ChordId toId);
    
    /**
     * Safely transfer keys between nodes.
     * Note: Only one ChordKey[] parameter is provided. The operation method
     * will only preform either an Add transfer or a Remove transfer.
     * 
     * @param key_add Keys to add.
     * @param key_remove Keys to remove.
     * @return Successfully removed or added nodes (depends on the parameters).
     */
    public ChordKey[] TransferKeys(
        final ChordKey[] key_add,
        final ChordKey[] key_remove);
}
