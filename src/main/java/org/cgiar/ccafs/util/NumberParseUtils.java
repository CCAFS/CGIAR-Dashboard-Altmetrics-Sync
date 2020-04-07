/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.util;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class NumberParseUtils {

  public static int tryParseInt(String value) {
    return tryParseInt(value, -1);
  }

  public static int tryParseInt(String value, int defaultValue) {
    int result = defaultValue;
    try {
      result = Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      // nothing we can do
    }

    return result;
  }

  public static long tryParseLong(String value) {
    return tryParseLong(value, -1L);
  }

  public static long tryParseLong(String value, long defaultValue) {
    long result = defaultValue;
    try {
      result = Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      // nothing we can do
    }

    return result;
  }

}