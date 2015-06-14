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

import Crypt.Hashfunction;
import java.math.BigInteger;

/**
 * This class represent the Chord position on the ring.
 * 
 * @author Simon Edänge
 */
public class ChordId implements Comparable<ChordId>
{
   private BigInteger KEY;
   private BigInteger BASE_TEN_RADIX;
   
   private String CHORD_RADIX;
   private String CHORD_KEY;
   private final int MOD;
   
   /**
    * Constructs a ChordId.
    * 
    * Preforms consistent hashing on the provided strings (k, n).
    * 
    * @param k key string (unique identifier).
    * @param n id string (IP address).
    * @param mod Bit value used in this Chord ring.
    */
   public ChordId(String k, String n, int mod)
   {
       MOD = mod;
       CHORD_KEY = k;
       CHORD_RADIX = n;
       
       BASE_TEN_RADIX = Hashfunction.ConsistentHash(Hashfunction.HashValue(n), mod);
       KEY  = Hashfunction.ConsistentHash(Hashfunction.HashValue(k), mod);
   }
   
   /**
    * 
    * @param mod Bit value used in this Chord ring.
    */
   public ChordId(int mod)
   {
       MOD = mod;
   }
   
   /**
    * 
    * @return The Consisted Hashed Key string.
    */
   public BigInteger GetKeyCH()
   {
       return KEY;
   }
   
   /**
    * 
    * @return The original Key string.
    */
   public String GetKey()
   {
       return CHORD_KEY;
   }
   
   /**
    * 
    * @return The original ID string
    */
   public String GetIdentifier()
   {
       return CHORD_RADIX;
   }
   
   /**
    * 
    * @return The Consisted Hashed ID. 
    */
   public BigInteger GetIdentifierCH()
   {
       return BASE_TEN_RADIX;
   }
   
   /**
    * Sets original value. This will also change the hash Key.
    * @param k 
    */
   public void SetKey(String k)
   {
       CHORD_KEY = k;
       KEY  = Hashfunction.ConsistentHash(Hashfunction.HashValue(k), MOD);
   }
   
   /**
    * Sets original value. This will also change the hash ID.
    * @param n 
    */
   public void SetIdentifier(String n)
   {
       CHORD_RADIX = n;
       BASE_TEN_RADIX = Hashfunction.ConsistentHash(Hashfunction.HashValue(n), MOD);
   }
   
   /**
    * Sets ID Consistent Hash, original value will be set to null.
    * 
    * @param n 
    */
   public void SetIdentifierCH(BigInteger n)
   {
       BASE_TEN_RADIX = n;
       CHORD_RADIX = null;
   }
   /**
    * Sets Key Consistent Hash, original value will be set to null.
    * 
    * @param k 
    */
   public void SetKeyCH(BigInteger k)
   {
       KEY = k;
       CHORD_KEY = null;
   }
   
   /**
    * Compare ChordId.
    * 
    * @param other ChordId to compare with.
    * @return True if equal
    */
   boolean Equals( ChordId other )
   {
       if( BASE_TEN_RADIX == null || other == null )
           return false;
       
       if(BASE_TEN_RADIX.compareTo(other.BASE_TEN_RADIX) == 0)
           return true;
       
       return false;
   }
   
    /**
    * Compare ChordId.
    * 
    * @param other ChordId to compare with.
    * @return True if this is Greater
    */
   boolean GreaterThan( ChordId other )
   {
        if( BASE_TEN_RADIX == null || other == null )
           return false;
              
       if(BASE_TEN_RADIX.compareTo(other.BASE_TEN_RADIX) == 1)
           return true;
       
       return false;
   }
    /**
    * Compare ChordId.
    * 
    * @param other ChordId to compare with.
    * @return True if this is Less.
    */
   boolean LessThan( ChordId other )
   {
        if( BASE_TEN_RADIX == null || other == null )
           return false;
              
        return BASE_TEN_RADIX.compareTo(other.BASE_TEN_RADIX) == -1;
   }
    /**
    * Compare ChordId.
    * 
    * @param other ChordId to compare with.
    * @return True if this is Greater or Equal.
    */
   boolean GreaterThanEqual( ChordId other )
   {
       if( BASE_TEN_RADIX == null || other == null )
           return false;
           
       return BASE_TEN_RADIX.compareTo(other.BASE_TEN_RADIX) >= 0;
   }
    /**
    * Compare ChordId.
    * 
    * @param other ChordId to compare with.
    * @return True if this is Less or Equal.
    */
   boolean LessThanEqual( ChordId other )
   {
       if( BASE_TEN_RADIX == null || other == null )
           return false;
           
       return BASE_TEN_RADIX.compareTo(other.BASE_TEN_RADIX) <= 0;
   }
   
   static boolean isBetweenNotify(ChordId id, ChordId from, ChordId to)
   {
        if (from.LessThan(to))
        {
                if (id.GreaterThan(from) && id.LessThan(to))
                {
                        return true;
                }
        }

        if (from.GreaterThan(to))
        {
                if (id.LessThan(from) && id.LessThan(to))
                {
                        return true;
                }
                if (id.GreaterThan(from))
                {
                        return true;
                }
        }

        if (from.Equals(to))
        {
                return true;
        }

        return false;
   }
   
    public static boolean isBetween(ChordId id, ChordId from, ChordId to)
   {
        if ( from.LessThan(to) )
        {
                if ( id.GreaterThan(from) && id.LessThan(to) )
                {
                        return true;
                }
        }

        if ( from.GreaterThan(to) )
        {
                if (id.LessThan(from) && id.LessThan(to))
                {
                        return true;
                }
                if (id.GreaterThan(from))
                {
                        return true;
                }
        }

        return false;
   }
    
    public static boolean isBetweenSuccessor(ChordId id, ChordId from, ChordId to)
    {
        if (from.GreaterThan(to))
        {
                if (id.LessThan(from) && id.LessThanEqual(to))
                {
                        return true;
                }

                if (id.GreaterThan(from))
                {
                        return true;
                }
        }

        if (from.LessThan(to))
        {
                if (id.GreaterThan(from) && id.LessThanEqual(to))
                {
                        return true;
                }
        }

        if (from.Equals(to) && (from.GreaterThan(id) || from.LessThan(id)))
        {
                return true;
        }

        return false;
    }

    @Override
    public int compareTo(ChordId o)
    {
       if(o == null)
           return 1;
       
       return BASE_TEN_RADIX.compareTo(o.BASE_TEN_RADIX);
    }
}
