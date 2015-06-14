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

/**
 * And interface used by the Chord managers, contains important methods.
 * 
 * @see ChordManager
 * @see CircleManager
 * @author Simon Edänge
 */
public interface InChordManager
{
    /**
     * Creates a node.
     * 
     * @return The id of the created node
     */
    public String CreateNode();
    /**
     * Prints information to the Console TextField.
     */
    public void PrintSuccessorAndPredecessor();
    /**
     * Prints information to the Console TextField.
     */
    public void PrintSuccessorList();
    /**
     * Prints information to the Console TextField.
     */
    public void PrintKeys();
    /**
     * Kill all nodes in the Circle.
     */
    public void KillAll();
    /**
     * Removes a specific node from the circle.
     * @param id Removes the node with this id.
     * @return True on success.
     */
    public boolean RemoveNode(int id);
    /**
     * Removes a specific node from the circle.
     * @param n Removes the provided node.
     * @return True on success.
     */
    public boolean RemoveNode(ChordNode n);
    /**
     * 
     * @return Total nodes in the circle.
     */
    public int GetSize();
    /**
     * @param id The id of the node to return.
     * @return A specific node.
     */
    public ChordNode GetNode(int id);
    /**
     * 
     * @return The bits used in this ring.
     */
    public int GetBits();
}
