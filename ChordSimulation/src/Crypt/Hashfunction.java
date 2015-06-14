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
package Crypt;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * This class has static methods that will hash (sha1) a value or perform
 * Consistent hashing on the hash value.
 * It is used to determine the position of a node/key on the ring.
 * 
 * @author Simon Edänge
 */
public class Hashfunction
{
    /**
     * Takes an input value String that will be hashed with sha1, and returned.
     * 
     * @param value To be hashed
     * @return A hashed String value
     */
    public static String HashValue(String value)
    {
        if( value == null || value.isEmpty() )
            return null;
        
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(value.getBytes("UTF-8"));
            sha1 = ByteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }
    
    /**
     * Converts an array of Bytes into a Hex Decimal String
     * 
     * @param hash The array to convert
     * @return Hex Decimal String
     */
    private static String ByteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        
        return result;
    }
    
    /**
     * Turns a sha1 hash string into a number between 0 and 2^mod - 1
     * 
     * @param hash Sha1 hash String
     * @param mod Modulus value to be used
     * @return A number(between 0 and 2^mod - 1)
     */
    public static BigInteger ConsistentHash(String hash, int mod)
    {
        if( hash == null || hash.isEmpty() )
            return null;
                
       BigInteger bi1 = new BigInteger(hash, 16);
       BigInteger bi2 = new BigInteger("2");
       
       BigInteger a = (bi1.mod(bi2.pow(mod)));
       
       return a;
    }

}