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
 * provides methods to configure the behaviour of db4o diagnostics.
 * <br><br>Diagnostic system can be enabled on a running db4o database 
 * to notify a user about possible problems or misconfigurations.
 * Diagnostic listeners can be be added and removed with calls
 * to this interface.
 * To install the most basic listener call:<br>
 * <code>
 * EmbeddedConfiguration config = Db4oEmbedded.newConfiguration(); <br>
 * config.common().diagnostic().addListener(new DiagnosticToConsole());</code>
 * @see com.db4o.config.Configuration#diagnostic()
 * @see DiagnosticListener
 */
public interface DiagnosticConfiguration {
    
    /**
     * adds a DiagnosticListener to listen to Diagnostic messages.
     */
    public void addListener(DiagnosticListener listener);
    
    /**
     * removes all DiagnosticListeners.
     */
    public void removeAllListeners();
}
