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

/**
 * This class contains ChordKey objects, such as replicas and keys.
 * 
 * @author Simon Edänge
 */
public class KeyContainer
{
    private final ChordKey[] mKeys;
    private final ChordKey[] mReplicas;

    /**
     * Constructs a Key container.
     * 
     * @param keys
     * @param replicas 
     */
    public KeyContainer(ChordKey[] keys, ChordKey[] replicas)
    {
        mKeys = keys;
        mReplicas = replicas;
    }
    
    /**
     * 
     * @return Keys from the container.
     */
    public ChordKey[] GetKeys()
    {
        return mKeys;
    }
    
    /**
     * 
     * @return Replicas from the container.
     */
    public ChordKey[] GetReplicas()
    {
        return mReplicas;
    }
}
