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
package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;

public class Db4oDefragSolo extends Db4oSolo {

    protected ObjectContainer createDatabase(Configuration config) {
        File origFile = new File(getAbsolutePath());
        if (origFile.exists()) {
            try {
                String backupFile = getAbsolutePath() + ".defrag.backup";
                IdMapping mapping = new InMemoryIdMapping();
                // new
                // BTreeIDMapping(getAbsolutePath()+".defrag.mapping",4096,1,1000);
                DefragmentConfig defragConfig = new DefragmentConfig(
                        getAbsolutePath(), backupFile, mapping);
                defragConfig.forceBackupDelete(true);
                // FIXME Cloning is ugly - wrap original in Decorator within
                // DefragContext instead?
                Configuration clonedConfig = (Configuration) ((DeepClone) config)
                        .deepClone(null);
                defragConfig.db4oConfig(clonedConfig);
                Defragment.defrag(defragConfig, new DefragmentListener() {
                    public void notifyDefragmentInfo(DefragmentInfo info) {
                        System.err.println(info);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.createDatabase(config);
    }

    public boolean accept(Class clazz) {
        return super.accept(clazz)
                && !OptOutDefragSolo.class.isAssignableFrom(clazz);
    }

    public String label() {
        return "Defrag-" + super.label();
    }
}