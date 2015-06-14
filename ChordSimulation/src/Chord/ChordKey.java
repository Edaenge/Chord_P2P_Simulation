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

import java.math.BigInteger;

/**
 * This class represent a Key/Replica element.
 * It is used when storing and maintaining keys.
 * 
 * @author Simon Edänge
 */
public class ChordKey
{
    public final BigInteger KEY;
    public final String CHORD_KEY;
    
    public final BigInteger BASE_TEN_RADIX;
    public final String CHORD_RADIX;
    
    /**
     * Constructs a ChordKey
     * 
     * @param key Consistent Hashed key representation (hashed)
     * @param realK Original key representation (non hashed)
     * @param identifier Consistent Hashed ID representation (hashed)
     * @param realIdentifier Original ID representation (non hashed)
     */
    public ChordKey(BigInteger key, String realK, BigInteger identifier, String realIdentifier)
    {
         KEY = key;
         CHORD_KEY = realK;
         
         BASE_TEN_RADIX = identifier;
         CHORD_RADIX = realIdentifier;
    }
    
    /**
     * Compare this hashed Key with other hashed Key.
     * 
     * @param other
     * @return True if equal.
     */
    public boolean Equal( BigInteger other )
    {
        return KEY.compareTo(other) == 0;
    }
    
    /**
     * Determine which ChordId is closest to this key.
     * 
     * @param from
     * @param to
     * @return The closest ChordId
     */
    public ChordId WhichIsClosestToKey(ChordId from, ChordId to)
    {
       if(from == null || to == null)
           return null;
       
       BigInteger a = WhichIsClosestToKey(
               from.GetIdentifierCH(), 
               to.GetIdentifierCH());
       
       if(a == null)
           return null;
       
       if(from.GetIdentifierCH().compareTo(a) == 0)
       {
           return from;
       }
       if(to.GetIdentifierCH().compareTo(a) == 0)
       {
           return to;
       }
       
       return null;
    }
    
    /**
     * Determine which BigInteger is closest to this key's consisted hashed 
     * key value..
     * 
     * @param from
     * @param to
     * @return 
     */
    public BigInteger WhichIsClosestToKey(BigInteger from, BigInteger to)
    {
        if(from.compareTo(to) == 0)
        {
            return from;
        }
        
        if(from.compareTo(to) <0)
        {
            if( KEY.compareTo(from) >0 && KEY.compareTo(to) <=0 )
                return to;
            
            if( KEY.compareTo(from) <=0 )
                return from;
            
            if( KEY.compareTo(to) >0 )
                return from;
        }
        
        else if( from.compareTo(to) >0 )
        {
            if( KEY.compareTo(from) <=0 && KEY.compareTo(to) >0 )
                return from;
            
            if( KEY.compareTo(to) <=0 )
                return to;
            
            if( KEY.compareTo(from) >0 )
                return to;
        }
        
        return null;
    }
}