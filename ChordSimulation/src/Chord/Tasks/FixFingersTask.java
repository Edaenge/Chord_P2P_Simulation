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

import Chord.ChordNode;
import Chord.FingerTable.Finger;
import Chord.FingerTable.FingerTable;

/**
 * This class is used as a background worker, and called every time t.
 * 
 * It corrects the finger entries in the local nodes finger table.
 * 
 * @author Simon Edänge
 */
public class FixFingersTask implements Runnable
{
    private final ChordNode mLocal;
    private int mFixFingerIndex;
        
    public FixFingersTask(ChordNode self)
    {
        mLocal = self;
        mFixFingerIndex = 0;
    }
    
    @Override
    public void run()
    {
        if(mLocal != null)
            FixFingers();
    }
    
    private void FixFingers()
    {
        FingerTable fingerTable = mLocal.GetFingerTable();
        if( mFixFingerIndex >= fingerTable.GetSize() )
            mFixFingerIndex = 0;
        
        ChordNode n;
        Finger f = fingerTable.Get(mFixFingerIndex++);
        
        if(f == null)
            return;
                    
        n = mLocal.findSuccessor(f.start);

        if(n != null && n != mLocal)
        {
            f.node = n;
            fingerTable.AddSuccessorToList(n);
        }
    }
    
}
