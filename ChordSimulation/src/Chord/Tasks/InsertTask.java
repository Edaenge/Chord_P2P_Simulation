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

import Chord.ChordKey;
import Chord.ChordNode;

/**
 * This class is used as a background worker, and called when inserting a key.
 * The node put this work on a separate thread who's objective is to insert 
 * a key to a node.
 * 
 * @author Simon Edänge
 */
public class InsertTask implements Runnable
{
    private final ChordNode mLocalNode;
    private final ChordKey mKEY;

    public InsertTask(final ChordKey k, final ChordNode self)
    {
        this.mLocalNode = self;
        this.mKEY = k;
    }

    @Override
    public void run()
    {
        if(mLocalNode != null && mKEY != null)
            mLocalNode.Insert(mKEY);
    }
    
}
