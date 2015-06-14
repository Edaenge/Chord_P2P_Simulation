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
import Chord.FakeRMIEvents.ReturnRMIevents.PingEventRE;

/**
 * This class is used as a background worker, and called every time t.
 * 
 * It checks if the current predecessor of this local node
 * has died. If so, it will be set to null.
 * 
 * @author Simon Edänge
 */
public class CheckPredecessorTask implements Runnable
{
    private final ChordNode mLocal;
    
    public CheckPredecessorTask(ChordNode self)
    {
        mLocal = self;
    }
    
    @Override
    public void run()
    {
        if(mLocal == null)
            return;
        
        CheckPredecessor();
               
    }
    
    private void CheckPredecessor()
    {
       ChordNode pred = mLocal.predecessor();

       if(pred != null)
       {
           //ping the pred, to check if running
           PingEventRE re = mLocal.GetCOM().Ping(pred);
           
           if(re == null)
           {
               //Node is down, remove
               mLocal.GetFingerTable().RemoveNode(pred);
           } 
       }
    }
}
