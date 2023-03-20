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
package com.db4o.diagnostic;

import com.db4o.*;


/**
 * base class for Diagnostic messages
 */
public abstract class DiagnosticBase implements Diagnostic{
    
    /**
     * returns the reason for the message 
     */
    public abstract Object reason();  
    
    /**
     * returns the potential problem that triggered the message
     */
    public abstract String problem();
    
    /**
     * suggests a possible solution for the possible problem
     */
    public abstract String solution();
    
    public String toString() {
        return ":: db4o " + Db4oVersion.NAME + " Diagnostics ::\n  " + reason() + " :: " + problem() + "\n    " + solution(); 
    }

}
