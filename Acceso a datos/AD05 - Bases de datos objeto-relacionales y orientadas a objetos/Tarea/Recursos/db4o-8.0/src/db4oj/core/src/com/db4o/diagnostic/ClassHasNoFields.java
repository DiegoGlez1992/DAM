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


/**
 * Diagnostic, if class has no fields.
 */
public class ClassHasNoFields extends DiagnosticBase{
    
    private final String _className;
    
    public ClassHasNoFields(String className){
        _className = className;
    }
    
    public Object reason() {
        return _className;
    }
    
    public String problem() {
        return "Class does not contain any persistent fields";
    }

    public String solution() {
        return "Every class in the hierarchy requires overhead for the maintenance of a class index." 
        + " Consider removing this class from the hierarchy, if it is not needed.";
    }

}
