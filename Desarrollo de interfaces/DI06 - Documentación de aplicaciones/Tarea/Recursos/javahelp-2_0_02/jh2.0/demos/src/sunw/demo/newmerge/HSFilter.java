/**
 *  @(#) HSFilter.java 1.5 - last change made 09/08/03
 *
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */


package sunw.demo.newmerge;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

/**
 * @author  Richard Gregor
 * @version	1.5	09/08/03
 */
public class HSFilter extends javax.swing.filechooser.FileFilter {
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        
        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("hs"))
                return true;
            else if (extension.equals("jar"))
                return hasHs(f);
        } else {
            return false;
        }
        
        return false;
    }
    
    /*
     * Get the extension of a file.
     */
    public String getExtension(File f) {
        String ext = null;
        if(f != null){
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 &&  i < s.length() - 1) {
                ext = s.substring(i+1).toLowerCase();
            }
        }
        return ext;
    }
    /**
     * Returns true if *.jar file contains *hs file
     */
    public boolean hasHs(File jarfile){
        try{
            JarFile jar = new JarFile(jarfile);
            Enumeration entries = jar.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String entryName = entry.getName();
                if(entryName.endsWith(".hs"))
                    return true;
            }
        }catch(IOException e){
            System.err.println(e);
        }
        return false;
    }
    
    
    // The description of this filter
    public String getDescription() {
        return "*.hs, *.jar";
    }
}
