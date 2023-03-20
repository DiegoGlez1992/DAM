/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.foundation;

/**
 * @sharpen.ignore
 */
public class CRC32
{
   private static int crcTable[];

   static {
      buildCRCTable();     
   }

   private static void buildCRCTable()
   {
      final int CRC32_POLYNOMIAL = 0xEDB88320;

      int i, j;
      int crc;

      crcTable = new int[256];

      for (i = 0; i <= 255; i++)
      {
         crc = i;
         for (j = 8; j > 0; j--)
            if ((crc & 1) == 1)
               crc = (crc >>> 1) ^ CRC32_POLYNOMIAL;
            else
               crc >>>= 1;
         crcTable[i] = crc;
      }
   }

   public static long checkSum(byte buffer[], int start, int count)
   {
      int temp1, temp2;
      int i = start;

      int crc = 0xFFFFFFFF;

      while (count-- != 0)
      {
         temp1 = crc >>> 8;
         temp2 = crcTable[(crc ^ buffer[i++]) & 0xFF];
         crc = temp1 ^ temp2;
      }

      return (long) ~crc & 0xFFFFFFFFL;
   }
}
