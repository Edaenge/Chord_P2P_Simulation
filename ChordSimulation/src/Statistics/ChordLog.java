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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An abstract class that is used for printing logs.
 *
 * @author Simon Edänge
 * @version 1.0
 */
public abstract class ChordLog
{
    protected final String mFileName;
    protected final String mFileType = "log";
    protected final String mDest;
    private PrintWriter mWriter;
    private final DateFormat mDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH-mm-ss");

    /**
    * Creates a ChordObject.
    * @param fileName Filename of the log to be created.
    * @param include_date And option, if the date should be included in the log
    * name.
    */
    ChordLog(final String fileName, boolean include_date)
    {
        Calendar cal = Calendar.getInstance();
                
        mDest = "log\\";
        
        if(include_date)
            mFileName = fileName+mDateFormat.format(cal.getTime());
        else
            mFileName = fileName;
        
        InitLogg();
    }
    
    /**
    * Creates a ChordObject.
    * @param fileName Filename of the log to be created.
    * @param include_date And option, if the date should be included in the log
    * name.
    * @param destination Optional, choose custom destination. If not specified,
    * a default destination will be chosen \log.
    */
    ChordLog(final String fileName, boolean include_date, 
            final String destination)
    {
        Calendar cal = Calendar.getInstance();
        
        mDest = destination;
        
        if(include_date)
            mFileName = fileName+mDateFormat.format(cal.getTime());
        else
            mFileName = fileName;

        InitLogg();
    }
    
    /**
    * Initialize the log. Is called by constructor.
    *
    */
    private void InitLogg()
    {     
        try {
            mWriter = new PrintWriter(mDest+mFileName+"."+mFileType, "utf-8");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    /**
    * Writes text to the log file on a new line.
    *
     * @param text The text to be printed.
     * @throws java.io.IOException On input error.
     * @see IOException
    */
    public void Println(String text) throws IOException
    {
        if(text == null)
            text = "NULL";
        
        mWriter.println(text);
    }
    
    /**
    * Writes text to the log file on one line.
    *
     * @param text The text to be printed.
     * @throws java.io.IOException On input error.
    */
    public void Print(String text) throws IOException
    {
        if(text == null)
            text = "NULL";
        
        mWriter.print(text);
    }
    
    /**
    * Get the filename of the opened file.
    *
    * @return The filename as a String
    */
    public String GetFileName()
    {
        return mFileName;
    }
    
    /**
     * Closes the the opened file.
     * Should be called when the log is finished.
     * 
     */
    public void Close()
    {
        mWriter.close();
    }
}