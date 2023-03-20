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
package com.db4o.consistency;

import java.util.*;

import com.db4o.foundation.*;

public class ConsistencyReport {
	
	private static final int MAX_REPORTED_ITEMS = 50;
	final List<SlotDetail> bogusSlots;
	final OverlapMap overlaps;
	final List<Pair<String,Integer>> invalidObjectIds;
	final List<Pair<String,Integer>> invalidFieldIndexEntries;
	
	ConsistencyReport(
			List<SlotDetail> bogusSlots, 
			OverlapMap overlaps, 
			List<Pair<String,Integer>> invalidClassIds, 
			List<Pair<String,Integer>> invalidFieldIndexEntries) {
		this.bogusSlots = bogusSlots;
		this.overlaps = overlaps;
		this.invalidObjectIds = invalidClassIds;
		this.invalidFieldIndexEntries = invalidFieldIndexEntries;
	}
	
	public boolean consistent() {
		return bogusSlots.size() == 0 && overlaps.overlaps().size() == 0 && overlaps.dupes().size() == 0 && invalidObjectIds.size() == 0 && invalidFieldIndexEntries.size() == 0;
	}
	
	public Set<Pair<SlotDetail, SlotDetail>> overlaps() {
		return overlaps.overlaps();
	}

	public Set<Pair<SlotDetail, SlotDetail>> dupes() {
		return overlaps.dupes();
	}
	
	@Override
	public String toString() {
		if(consistent()) {
			return "no inconsistencies detected";
		}
		StringBuffer message = new StringBuffer("INCONSISTENCIES DETECTED\n")
			.append(overlaps.overlaps().size() + " overlaps\n")
			.append(overlaps.dupes().size() + " dupes\n")
			.append(bogusSlots.size() + " bogus slots\n")
			.append(invalidObjectIds.size() + " invalid class ids\n")
			.append(invalidFieldIndexEntries.size() + " invalid field index entries\n");
		message.append("(slot lengths are non-blocked)\n");
		appendInconsistencyReport(message, "OVERLAPS", overlaps.overlaps());
		appendInconsistencyReport(message, "DUPES", overlaps.dupes());
		appendInconsistencyReport(message, "BOGUS SLOTS", bogusSlots);
		appendInconsistencyReport(message, "INVALID OBJECT IDS", invalidObjectIds);
		appendInconsistencyReport(message, "INVALID FIELD INDEX ENTRIES", invalidFieldIndexEntries);
		return message.toString();
	}
	
	private <T> void appendInconsistencyReport(StringBuffer str, String title, Collection<T> entries) {
		if(entries.size() != 0) {
			str.append(title + "\n");
			int count = 0;
			for (T entry : entries) {
				str.append(entry).append("\n");
				count++;
				if(count > MAX_REPORTED_ITEMS) {
					str.append("and more...\n");
					break;
				}
			}
		}
	}
}