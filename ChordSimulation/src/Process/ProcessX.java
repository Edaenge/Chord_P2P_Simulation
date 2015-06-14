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
package Process;

import Chord.FakeRMIEvents.ReturnRMIevents.ReturnEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class is an abstract process thread class that has a event queue.
 * How it handles messages is up to the developer.
 * 
 * @author Simon Edänge
 */
public abstract class ProcessX implements Runnable
{
    private final List<ProcessEvent> mEventList;
    private final List<ReturnEvent> mRetEventList;
    
    private final Map<String, NotifyOnWaitObject> mNotifyBypassReg;
    private boolean mEnableWaitObject;
    protected boolean mKeepAlive;
    
    /**
     * This object is stored in ProcessX event list.
     * The object is notified using a lash when the ProcessEvent, it is waiting
     * for, has been discovered in the EventQueue of ProcessX.
     * It is used when an owner to this object is waiting for a specific
     * ProcessEvent to to be put in the EventQueue.
     * @see CountDownLatch
     * @see ProcessEvent
     * @param <T> Template, used for the ClassType, of the ProcessEvent.
     */
    protected class NotifyOnWaitObject<T>
    {
        
        private final Class<T> mTheClass;
        private final long    mTimeStamp;
        private final String mUUID;
        private final CountDownLatch mMutex;
        private ProcessEvent mEvent;
        
        /**
         * Constructs a NotifyOnWaitObject
         * @param myClass Class type of the ProcessEvent object.
         * @param timeStamp Timestamp on when the object is created.
         * @param ID ID of the ProcessEvent it is waiting for.
         */
        public NotifyOnWaitObject(Class<T> myClass, long timeStamp, String ID)
        {
            mTheClass = myClass;
            mTimeStamp = timeStamp;
            mMutex = new CountDownLatch(1);
            mEvent = null;
            mUUID = ID;
        }
        
        /**
         * This latch will be notified when the event
         * this object is waiting for, has been discovered.
         * @return A CountDownLatch. 
         */
        public CountDownLatch GetMutex()
        {
            return mMutex;
        }
        /**
         * 
         * @return The class type, of the event this class is waiting for. 
         */
        public Class<T> GetNotifyClass()
        {
            return mTheClass;
        }
        
        /**
         * 
         * @return Time stamp, time of when this object was issued.
         */
        public long GetTimeStamp()
        {
            return mTimeStamp;
        }
        
        /**
         * 
         * @return The event ID this class is waiting for. 
         */
        public String GetUUID()
        {
            return mUUID;
        }
        
        /**
         * Compares this ID with target ID.
         * @param target Target ID to compare.
         * @return True if equal
         */
        public boolean IsUUIDEqualTo(String target)
        {
            return mUUID.compareTo(target) == 0;
        }
        
        /**
         * If the target event has been identified, it will be set here, and
         * the mutex latch will be notified.
         * @see CountDownLatch
         * @param e Target event to be set.
         */
        public void SetEvent(ProcessEvent e)
        {
            if(e != null)
            {
                mEvent = e;
                mMutex.countDown();
            }
            
        }
        /**
         * 
         * @return The target event that has been set, returns null if not set.
         */
        public ProcessEvent PollEvent()
        {
            return mEvent;
        }
        
    }

    /**
     * Constructs a ProcessX object.
     */
    public ProcessX()
    {
        mNotifyBypassReg = Collections.synchronizedMap(new HashMap<String, NotifyOnWaitObject>());
        mEventList = Collections.synchronizedList(new ArrayList<ProcessEvent>());
        mRetEventList = Collections.synchronizedList(new ArrayList<ReturnEvent>());
        
        mKeepAlive = true;
        mEnableWaitObject = false;
    }
    
    @Override
    /**
     * This function is where the the process will run.
     * Here is where the developer reads and handle events from the Event Queue.
     */
    public abstract void run();
    
    /**
     * This function puts an event in
     * this threads Event Queue.
     * @param e The event to be put.
     */
    public void putEvent(ProcessEvent e)
    {
        if(e == null)
          return;
    
        if( e instanceof ReturnEvent)
        {
            if(mEnableWaitObject)
            {
                mRetEventList.add((ReturnEvent) e);
            }
        }
        
        else
        {
            mEventList.add(e);
        }
        
        
        if( mEventList.size() > 400 )
        {
            System.out.println("Too many events: " + mEventList.size());
        }

    }
    
    /**
     * This function returns the first (FIFO) event in the event queue. When
     * the event has been returned, it will also be deleted from the event queue.
     * This function when called, also checks if there are NotifyOnWaitObject's
     * waiting for any Events. If the event has been found, 
     * the NotifyOnWaitObject's Latch will be notified.
     * Events that NotifyOnWaitObject are waiting for, will not be returned.
     * @return The first event in the Queue.
     */
    protected ProcessEvent peekEvent()
    {
        ProcessEvent e = null;
        
        checkWaitObjects();
        
        if(mEventList.isEmpty())
        {
            return null;
        }

        try{

            e = mEventList.get(0);
            mEventList.remove(0);

        }catch(ArrayIndexOutOfBoundsException ex){
            mEventList.remove(e);
            return null;
        }  
        
        return e;
    }
    /**
     * Returns the Event Queue size.
     * @return Number of events.
     */
    protected int queueSize()
    {
        return mEventList.size();
    }
    
    /**
     * Kills this thread. Stopping it from executing.
     */
    public synchronized void kill()
    {
        mKeepAlive = false;
    }
    
    /**
     * Goes through the list of NotifyOnWaitObject's, for each event in the
     * event return queue.
     */
    protected void checkWaitObjects()
    {
        if(!mEnableWaitObject)
            return;
        
        if(mRetEventList.isEmpty())
            return;
        
        if(mRetEventList.size() > 100)
        {
            System.out.println("Too many Ret events: " + 
                    mRetEventList.size()); 
        }
        
        long timeNow = System.currentTimeMillis();
        long timeOut = 3000;
        
        synchronized(mRetEventList)
        {
            Iterator<ReturnEvent> i = mRetEventList.iterator();
            
            while( i.hasNext() )
            {
                ReturnEvent e = i.next();
                
                if(e == null){}
                
                else
                {
                    NotifyOnWaitObject obj =  mNotifyBypassReg.get(e.GetUUID());
                    
                    if( obj != null && obj.GetNotifyClass().isInstance(e))
                    {
                        obj.SetEvent(e);
                        mNotifyBypassReg.remove(obj.GetUUID());
                        i.remove();
                    }
                    else if( (timeNow - e.GetTimeCreated()) > timeOut )
                    {
                        i.remove();
                    }
                    
                }
            }
        }
    }
    /**
     * Register a NotifyOnWaitObject to be checked for a specific event.
     * @param <T> A class type to be checked.
     * @param object A NotifyOnWaitObject to be registered.
     * @return True if added.
     */   
    public <T> boolean registerOnWaitObject(NotifyOnWaitObject<T> object)
    {
        if(!mEnableWaitObject)
            return false;
        
        if( !mNotifyBypassReg.containsValue(object) )
        {
           NotifyOnWaitObject k =  mNotifyBypassReg.put(object.GetUUID(), object);
            return true;
        }
        
        return false;
    }
    
    /**
     * Removes A NotifyOnWaitObject to be unregistered.
     * @param <T> Class type
     * @param object A NotifyOnWaitObject to be deleted.
     */
    public <T> void unregisterOnWaitObject(NotifyOnWaitObject<T> object)
    {
        mNotifyBypassReg.remove(object.GetUUID());
    }
    
    /**
     * Enable the NotifyOnWaitObject function.
     * If off, no NotifyOnWaitObject will be registered.
     * @param option 
     */
    public void enableWaitObjectRegister(boolean option)
    {
        mEnableWaitObject = option;
        
        if(!option)
        {
            mRetEventList.clear();
            mNotifyBypassReg.clear();
        }
        
    }
}