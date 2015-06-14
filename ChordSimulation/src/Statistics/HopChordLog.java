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
package Statistics;

import Chord.ChordNode;
import java.io.IOException;
import java.util.ArrayList;

/**
 * An class extended from ChordLog class, 
 * used to create logs and print HopData objects.
 * @see ChordLog
 * @see HopData
 *
 * @author Simon Edänge
 * @version 1.0
 */
public class HopChordLog extends ChordLog
{
    private final String HOP_NUBMER = "HOP_NUMBER";
    private final String HOP_CALLER = "HOP_CALLER";
    private final String HOP_TARGET = "HOP_TARGET";
    
    private final String TEST_TARGET_NODE = "TEST_TARGET_NODE";
    private final String TEST_TARGET_KEY = "TEST_TARGET_KEY";
    private final String TEST_RESULT_NODE = "TEST_RESULT_NODE";
    private final String TEST_STATUS = "TEST_STATUS";
    private final String str_status_success = "SUCCESS";
    private final String str_status_failed = "FAILED";
    
    private final String HOP_TOTAL_TIME = "HOP_TOTAL_TIME";
    
    private final String TEST_TOTAL_NODES = "TEST_TOTAL_NODES";
    
    private final int mTotalNodes;
    private boolean mFirstCall;
    
    /**
     * Construct a hop log to store HopData information.
     * @param totalNodes Total nodes in the current systems Chord Ring.
     */
    public HopChordLog(final int totalNodes)
    {
        super("HoppLog", true);
        mTotalNodes = totalNodes;
        mFirstCall = true;
    }
    
   /**
   * Prints a HopData object to the log file.
   * @param data
   * @param measureNumber 
   * @exception IOException On input error.
   * @see IOException
   */
    public void PrintData(final HopData data, final int measureNumber) throws IOException
    {
        if(mFirstCall)
        {
            mFirstCall = false;
            Println(TEST_TOTAL_NODES + "=" + mTotalNodes); 
            Println("*********************");
        }
        
        Println(HOP_NUBMER+"="+measureNumber);
        Println(HOP_CALLER+"="+data.GetCaller().PeerID().GetIdentifierCH());
        Println(TEST_TARGET_NODE+"="+data.GetTargetNode()
                .PeerID().GetIdentifierCH());
        Println(TEST_TARGET_KEY+"="+data.GetTargetNode()
                .PeerID().GetKeyCH());
        
        
        if( !data.IsFailed() )
            Println(TEST_STATUS+"="+str_status_success);
        else
            Println(TEST_STATUS+"="+str_status_failed);
        
        ArrayList<ChordNode> list = data.GetTargets();
        
        for(ChordNode n : list)
        {
            Println(HOP_TARGET+"="+n.PeerID().GetIdentifierCH().toString());
        }
        
        long totalTime = data.GetEndTime() - data.GetStartTime();
        
        Println(HOP_TOTAL_TIME+"="+totalTime);
        Println(TEST_RESULT_NODE+"="+data.GetResultNode().PeerID()
                .GetIdentifierCH());
        Println("*********************");
    }
}