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

import Chord.ChordKey;
import Chord.ChordNode;
import Statistics.HopData;
import Statistics.HopLookupMeasure;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to perform lookup simulation tests on the system.
 * 
 * @author Simon Edänge
 */
public class ChordLookupSimulation extends Thread
{
    private final CircleManager mCircle;
    private final ArrayList<ChordNode> mNodes;
    private final long mTests;
    private final Random mRand;
    private final HopLookupMeasure mHopMeasureStatistics;
    private boolean mKeepAlive;
    
    /**
     * @param circle The Chord circle, containing all the nodes.
     * @param tests How many lookup tests to be performed.
     */
    public ChordLookupSimulation(final CircleManager circle, final long tests)
    {
        mCircle = circle;
        mTests = tests;
        mNodes = mCircle.GetCircleList();
        mRand = new Random(System.currentTimeMillis());
        
        mHopMeasureStatistics = new HopLookupMeasure(circle.GetSize());
        mKeepAlive = true;
    }

    @Override
    public void run()
    {
        System.out.println("Simulation Started");
        System.out.println("Simulation will perform " + mTests + " Lookup tests.");
        System.out.println("The Lookup target and the Requester will be chosen "
                + "randomly.");
        System.out.println("********************");
        
        while(mKeepAlive)
        {
            ChordNode req = null;
            ChordNode target = null;
            
            //Get Requester
            do
            {
                req = RandomizeNode(0);
                
            }while(req == null);
            
            //Get target
            do
            {
                target = RandomizeNode(0);
                
                if(target == mCircle.GetBootstrap())
                    target = null;
                
            }while(target == null 
                    || target == req );
            
            HopData data = PerformTest(req, target);
            
            if( !mHopMeasureStatistics.AddLookupResult(data) )
            {
                System.out.println("***Lookup failed***");
                System.out.println("Hop Data was null, nothing written");
                System.out.println("Test number: "
                        + mHopMeasureStatistics.GetNumberOfTests());
                System.out.println("Req: "+req.PeerID().GetIdentifierCH());
                System.out.println("Tar: "+target.PeerID().GetIdentifierCH());

            }
            else if( data.IsFailed() )
            {
                System.out.println("***Lookup failed***");
                System.out.println("Unable to find key");
                System.out.println("Test number: "
                        + mHopMeasureStatistics.GetNumberOfTests());
                System.out.println("Req: "+req.PeerID().GetIdentifierCH());
                System.out.println("Tar: "+target.PeerID().GetIdentifierCH());
            }
            else
            {
                long time = data.GetEndTime() - data.GetStartTime();
                System.out.println("***Test \"" 
                + mHopMeasureStatistics.GetNumberOfTests()
                + "\" Completed [" 
                + time
                +"]***");
            }
            
            if(mHopMeasureStatistics.GetNumberOfTests() == mTests)
            {
                System.out.println("Test Complete! Terminating.");
                this.Stop();
            }
            else
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChordLookupSimulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        
        mHopMeasureStatistics.CloseLog();
    }
    
    /**
     * Performs a lookup test on the given target.
     * @param req The issuer node to perform the lookup request.
     * @param target Target node to be found (uses it's key during the test)
     * @return The results of the lookup, such as hops.
     */
    private HopData PerformTest(final ChordNode req, final ChordNode target)
    {
        HopData hop = new HopData(req, target);
        ChordNode result;
        
        System.out.println(req.PeerID().GetIdentifierCH() + " wants to find "
        + target.PeerID().GetIdentifierCH() 
        + " With key " + target.PeerID().GetKeyCH() );
        
        //Start Time Set
        hop.SetStartTime(System.currentTimeMillis());
        
        result = req.lookup(target.PeerID(), req, hop, null);
        
        //End Time Set
        hop.SetEndTime(System.currentTimeMillis());
        
        if( hop.IsFailed() )
            return hop;
        
        if(result == null)
            return null;
        
        hop.SetResultNode(result);
        
        //Check if the node is a responsible node of the key
        ChordKey r = result.GetSuccessorList().GetEntries()
                .GetReplica(target.PeerID());
        ChordKey k = result.GetSuccessorList().GetEntries()
                .GetKey(target.PeerID());
        
        ChordKey testKey;
        
        if(k != null)
            testKey = k;
        else if(k == null && r != null)
            testKey = r;
        else
            return null;
        
        if( testKey.BASE_TEN_RADIX.compareTo(target.PeerID().GetIdentifierCH())
                == 0)
            return hop;
        else
        {
            hop.SetFailed(true);
            return hop;
        }

    }
    
    private ChordNode RandomizeNode(final int min)
    {
        int max = mNodes.size()-1;
        int value = mRand.nextInt((max - min) + 1) + min;
       
        return mNodes.get(value);
    }
    
    /**
     * Stops the simulation.
     */
    public void Stop()
    {
        mKeepAlive = false;
    }
}
