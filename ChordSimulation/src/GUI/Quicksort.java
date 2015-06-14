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

import Chord.ChordNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Quicksort  
{
    
  static public void sort(List<ChordNode> list) 
  {
    if(list == null)
        return;
    
    
    quicksort(0, list.size()-1, list);
    
  }
  
static public ChordNode binarySearch(ArrayList<ChordNode> a, BigInteger x)
{
    if(a.isEmpty())
        return null;
    return binarySearch(a, x, 0, a.size() -1);
}
static private ChordNode binarySearch(ArrayList<ChordNode> a, BigInteger x ,int low, int high) 
{
   if (low > high) return null; 
   int mid = (low + high)/2;
   if (a.get(mid).PeerID().GetIdentifierCH().compareTo(x) == 0) return a.get(mid);
   else if (a.get(mid).PeerID().GetIdentifierCH().compareTo(x) < 0)
      return binarySearch(a, x, mid+1, high);
   else // last possibility: a[mid] > x
      return binarySearch(a, x, low, mid-1);
}

  static private void quicksort(int low, int high, List<ChordNode> list) 
  {
    int i = low, j = high;

    // Get the pivot element from the middle of the list
    int pivot = list.get(low + (high-low)/2).PeerID().GetIdentifierCH().intValue();

    // Divide into two lists
    while (i <= j) {
      // If the current value from the left list is smaller then the pivot
      // element then get the next element from the left list
      while (list.get(i).PeerID().GetIdentifierCH().intValue() < pivot) {
        i++;
      }
      // If the current value from the right list is larger then the pivot
      // element then get the next element from the right list
      while (list.get(j).PeerID().GetIdentifierCH().intValue() > pivot) {
        j--;
      }

      // If we have found a values in the left list which is larger then
      // the pivot element and if we have found a value in the right list
      // which is smaller then the pivot element then we exchange the
      // values.
      // As we are done we can increase i and j
      if (i <= j) {
        exchange(i, j, list);
        i++;
        j--;
      }
    }
    // Recursion
    if (low < j)
      quicksort(low, j, list);
    if (i < high)
      quicksort(i, high, list);
  }

  static private void exchange(int i, int j, List<ChordNode> list) 
  {
    ChordNode temp = list.get(i);
    ChordNode temp2 = list.get(j);

    list.set(i, temp2);
    list.set(j, temp);
    
  }
  
} 