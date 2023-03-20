/**
 *  @(#) JarAccessory.java 1.6 - last change made 09/08/03
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
import java.beans.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.awt.event.*;
/**
 *
 * @author  Richard Gregor
 * @version	1.6	09/08/03
 */
public class JarAccessory extends JPanel implements PropertyChangeListener,ActionListener{
    private DefaultListModel hsModel = new DefaultListModel();
    private File file = null;
    private JTextField nameField;
    private JTextField urlField;
    private GridBagLayout gb = new GridBagLayout();
    private GridBagConstraints gc = new GridBagConstraints();
    private JLabel nameLabel = new JLabel("Name:");
    private JLabel urlLabel = new JLabel("URL:");
    private JFileChooser fileChooser;
    
    public JarAccessory(){
        super();
        setBorder(new TitledBorder("HelpSet"));
        setLayout(new BorderLayout());

        add(createHSPanel(),BorderLayout.CENTER);
        
     }
    
    public JarAccessory(JFileChooser fileChooser){
        this();
        this.fileChooser = fileChooser;
        if(fileChooser != null){
            fileChooser.addPropertyChangeListener(this);
            fileChooser.addActionListener(this);
        }
    }
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            file = (File) e.getNewValue();
            if (isShowing()) {
                String extension = getExtension(file);
                if(extension != null)
                    if(extension.equals("jar"))
                        handleJar(file);
                    else if(extension.equals("hs"))
                        handleHs(file);               
                
            }
        }
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
     * Handles *.jar file
     */
    public void handleJar(File jarfile){
        try{
            JarFile jar = new JarFile(jarfile);
            Enumeration entries = jar.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String entryName = entry.getName();
                if(entryName.endsWith(".hs")){
                    nameField.setText(entryName);
                    break;
                }
            }
        }catch(IOException e){}
        
        String path = jarfile.getPath();
        urlField.setText("file:"+path);

    }
    
    /**
     * Handles *.hs file
     */
    public void handleHs(File hsfile){
        String name = hsfile.getName();
        nameField.setText(name);
        String path = hsfile.getPath();
        urlField.setText("file:"+path);
    }
        
    /**
     * Creates HS panel
     */
    public JTabbedPane createHSPanel(){

        nameField = new JTextField(20);
        urlField = new JTextField(20);
        
        nameField.setEditable(false);
        urlField.setEditable(false);
        
        nameField.setBackground(Color.white);
        urlField.setBackground(Color.white);
        
        JTabbedPane tabPane = new JTabbedPane();
            
        JPanel panel = new JPanel();
        panel.setLayout(gb);
        
        setPlace(nameLabel,0,0,GridBagConstraints.WEST,panel);
        
        setPlace(nameField, 0,1,GridBagConstraints.WEST,panel); 
        
        setPlace(urlLabel,0,2,GridBagConstraints.WEST,panel);
        
        setPlace(urlField,0,3,GridBagConstraints.WEST,panel);
        
        
        tabPane.add("File",panel);
        
        JPanel summary = new JPanel(new BorderLayout());
                    
        JList list = new JList(hsModel);
        JScrollPane listPane = new JScrollPane(list);
        summary.add(listPane, BorderLayout.CENTER);
        tabPane.add("Summary",summary);
        
        return tabPane;

    }
    
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if(command.equals(JFileChooser.APPROVE_SELECTION))
            hsModel.addElement(fileChooser.getSelectedFile().getName());
    }
    /**
     * Removes entry from List model
     */
    public void removeFromModel(){
        if((hsModel != null) && (hsModel.getSize() > 0))
            hsModel.removeElementAt(hsModel.getSize() -1);
    }
     
    /**
     * Returns HelpSet name
     */
    public String getName(){
        if(nameField != null)
            return nameField.getText();
        else
            return null;
    }
            
    public void setPlace(Component component, int x, int y,int anch, Container container){
        gc.gridx = x;
        gc.gridy = y;
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = anch;
        
        gb.setConstraints(component,gc);
        container.add(component);
    }
    
}
