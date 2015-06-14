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

import java.util.Random;

/**
 * This class can generate a random IP address and unique ID's.
 * It is used when creating a node in the ChordNode constructor.
 * 
 * @author Simon Edänge
 */
public class IDGenerator
{
    private static int ID = 1;
    
    /**
     * Incremented every call.
     * 
     * @return A unique ID value.
     */
    public static int GenerateID()
    {
        return ID++;
    }
    
    /**
     * 
     * @return A Randomized IP Address.
     */
    public static String GenerateIP()
    {
        Random r = new Random();
        return r.nextInt(256) 
                + "." 
                + r.nextInt(256) 
                + "." + r.nextInt(256) 
                + "." + r.nextInt(256);
    }
}
