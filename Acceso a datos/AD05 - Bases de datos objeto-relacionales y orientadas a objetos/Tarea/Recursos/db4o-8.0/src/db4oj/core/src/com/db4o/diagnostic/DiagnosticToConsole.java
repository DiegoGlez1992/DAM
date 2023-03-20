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
 * prints Diagnostic messsages to the Console.
 * Install this {@link DiagnosticListener} with: <br>
 * <code>
 * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration(); <br>
 * config.common().diagnostic().addListener(new DiagnosticToConsole());</code><br>
 * @see DiagnosticConfiguration
 */
public class DiagnosticToConsole implements DiagnosticListener{

    /**
     * redirects Diagnostic messages to the Console.
     */
    public void onDiagnostic(Diagnostic d) {
        System.out.println(d.toString());
    }
    
}
