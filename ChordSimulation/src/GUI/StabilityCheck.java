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
package GUI;

import Chord.ChordId;
import Chord.ChordNode;
import Chord.Manager.CircleManager;
import Chord.FingerTable.Finger;
import Chord.FingerTable.FingerTable;
import Chord.FingerTable.SuccessorList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ediz
 */
public class StabilityCheck implements Runnable
{
    private final CircleManager mCircle;
    private final javax.swing.JLabel mLabel;

    public StabilityCheck(CircleManager circle, javax.swing.JLabel label)
    {
        mCircle = circle;
        mLabel = label;
    }
    
    @Override
    public void run()
    {
        if(mCircle != null && !mCircle.GetCircleList().isEmpty())
            mLabel.setText((int)(StabCheck() * 100) + "%");
    }
    
    private double StabCheck()
    {
        ChordNode[] circle = new ChordNode[mCircle.GetSize()];
        circle = mCircle.GetCircleList().toArray(circle);
        int total = mCircle.GetMaxFingers() + 3 + 1;
        double result = 0;
        
        for(int i = 0; i<circle.length;i++)
        {
            ChordNode n = circle[i];
            
            FingerTable fingerTable = n.GetFingerTable();
            SuccessorList succesorList = fingerTable.GetSuccessorList();
            
            //check fingers
             List<Finger> fingers 
                     = new ArrayList<>(fingerTable.GetTable());
            
            int countFingers = 0;
            int index;
                             
            for(Finger f : fingers)
            {
                if(f == null) {}
                else if(f.node == null) {}
                else
                {   
                    index = BinarySearch(
                            circle, 
                            f.node.PeerID(), 
                            0, 
                            circle.length - 1);
                    
                    if(index == -1){}
                    else
                    {
                        if( index-1 <0 )
                            index = circle.length-1;
                        
                        if(circle[i] == n)
                            countFingers++;
                        else if( circle[index].PeerID().GetIdentifierCH()
                                .compareTo(f.intervalFrom
                                        .GetIdentifierCH()) <0)
                        {
                            countFingers++;
                        }
                    }
                }
            }
            
            //check successors
            ChordNode[] succesors = succesorList.GetArray();
            int countSuccesors = 0;
            index = i+1;
            
            for(ChordNode s : succesors)
            {
                if(index >= circle.length)
                    index = 0;
                
                if(s == null)
                    break;
                    
                else
                {
                    if(s.PeerID().GetIdentifierCH()
                            .compareTo(circle[index].PeerID()
                                    .GetIdentifierCH()) == 0)
                    {
                        countSuccesors++;
                    }
                }
                
                index++;
            }
            
            int predCount = 0;
            if(n.predecessor() != null)
            {
                predCount++;
            }
            
            result += ((double)countFingers / total) + 
                    ((double)countSuccesors / total) +
                    ((double)predCount / total);
        }
        
        return result / circle.length;
    }
    
    private int BinarySearch(ChordNode[] circle, ChordId ID, int min, int max)
    {
        // test if array is empty
        if (max < min)
          // set is empty, so return value showing not found
          return -1;
        else
        {
            // calculate midpoint to cut set in half
            int mid = Mid(min, max);

            // three-way comparison
            if (circle[mid].PeerID().GetIdentifierCH()
                    .compareTo(ID.GetIdentifierCH()) >0)
              // key is in lower subset
              return BinarySearch(circle, ID, min, mid - 1);
            
            else if (circle[mid].PeerID().GetIdentifierCH()
                    .compareTo(ID.GetIdentifierCH()) <0)
                
              // key is in upper subset
              return BinarySearch(circle, ID, mid + 1, max);
            
            else
              // key has been found
              return mid;
        }
    }
    
    private int Mid(int low, int high)
    {
        return low + ((high - low) / 2);
    }
    
}
