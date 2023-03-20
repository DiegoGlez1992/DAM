/**
 *  @(#) Merge.java 1.8 - last change made 09/08/03
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



/**
 * Merge demo
 *
 * @author  Richard Gregor
 * @version	1.8	09/08/03
 */

package sunw.demo.newmerge;

import javax.help.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Merge extends Object{
    /**
     * Frame for master and slave
     */
    private JFrame viewFrame = null;
    /**
     * TOCNavigators
     */
    private JHelpTOCNavigator masterTOC,slaveTOC;
    /**
     * IndexNavigators
     */
    private JHelpIndexNavigator masterIndex, slaveIndex;
    /**
     * SearchNavigators
     */
    private JHelpSearchNavigator masterSearch, slaveSearch;
    /**
     * HelpSets' titles
     */
    private String masterTitle, slaveTitle;
    /**
     * HelpSet's labels
     */
    private JLabel masterTOCLabel, masterIndexLabel;
    private JLabel slaveTOCLabel, slaveIndexLabel;
    /**
     * HelpSet chooser
     */
    final JFileChooser hsChooser = new JFileChooser();
    /**
     * HelpSet accessory
     */
    private JarAccessory jarAccessory;
    /**
     * HelpSet file filter - accepts only hs or jar with hs
     */
    private HSFilter hsFilter;
    /**
     * Application frame
     */
    private JFrame frame;
    /**
     * Stack of slave HelpSets -useful for removing
     */
    private Stack slaveSets = new Stack();
    /**
     * Stack of slave copy HelpSets
     */
    private Stack slaveCopySets = new Stack();
    /**
     * remove menu item
     */
    final JMenuItem removeItem = new JMenuItem("Remove");
    /**
     * master and slave view menu item
     */
    final JMenuItem msItem = new JMenuItem("Master and Slave");
    /**
     * help item
     */
    private JMenuItem helpItem;
    /**
     * help model
     */
    private HelpModel helpModel;
    /**
     * HelpSets
     */
    private HelpSet masterHelpSet, slaveHelpSet, masterHSCopy, slaveHSCopy;
    /**
     * ClassLoader
     */
    private ClassLoader loader;
    /**
     * URLs
     */
    private URL masterHsURL,slaveHsURL;
    /**
     * JHelps
     */
    private JHelp help, slaveHelp, masterHelpCopy, slaveHelpCopy;
    /**
     * HelpBroker
     */
    private HelpBroker helpBroker;
    
    /** Creates new Merge from given url*/
    public Merge(String url){
        
        hsFilter = new HSFilter();
        hsChooser.setFileFilter(hsFilter);
        jarAccessory = new JarAccessory(hsChooser);
        hsChooser.setAccessory(jarAccessory);
        
        loader = this.getClass().getClassLoader();
        masterHsURL = HelpSet.findHelpSet(loader,url);
        try{
            masterHelpSet = new HelpSet(loader,masterHsURL);
            masterHSCopy = new HelpSet(loader,masterHsURL);
        }
        catch(HelpSetException ep){
            System.out.println(ep);
        }
        
        masterTitle = masterHSCopy.getTitle();
        masterTOCLabel = new JLabel(masterTitle,SwingConstants.CENTER);
        masterIndexLabel = new JLabel(masterTitle,SwingConstants.CENTER);
        
        
        JHelp help = new JHelp(masterHelpSet);
        masterHelpCopy = new JHelp(masterHSCopy);
        setupMasterNavigators(masterHelpCopy);
        
        frame = new JFrame("help");
        JPanel panel = new JPanel(new GridLayout());
        
        panel.add(help);
        frame.setContentPane(panel);
        help.setNavigatorDisplayed(true);
        
        frame.setJMenuBar(createMenu());
        frame.pack();
        frame.setLocation(computeCenter(frame));
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        }
        );
    }
    
    /**
     * Creates demo menubar
     */
    public JMenuBar createMenu(){
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        JMenu mergeMenu = new JMenu("HelpSet");
        mergeMenu.setMnemonic('s');
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('v');
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('e');
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                System.exit(0);
            }
        });
        
        msItem.setMnemonic('m');
        msItem.setEnabled(false);
        msItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                viewFrame.setVisible(true);
            }
        });
        
        JMenuItem mergeItem = new JMenuItem("Add");
        mergeItem.setMnemonic('a');
        mergeItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                                
                int retVal = hsChooser.showOpenDialog(frame);
                if(retVal == JFileChooser.APPROVE_OPTION){
                    if(!(slaveCopySets.empty())){
                        //adds former slaveCopy to the masterCopy
                        masterHSCopy.add((HelpSet)slaveCopySets.peek());
                    }
                    handleFile();
                    //setups current slaveCopy
                    setSlaveCopy((HelpSet)slaveCopySets.peek());
                    
                }
                if(!(slaveSets.empty())){
                    removeItem.setEnabled(true);
                    msItem.setEnabled(true);                
                }
            }
        });
        
        
        removeItem.setMnemonic('r');
        removeItem.setEnabled(false);
        removeItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                masterHelpSet.remove((HelpSet)slaveSets.pop());
                removeSetFromView();
                jarAccessory.removeFromModel();

                if(slaveSets.empty()){
                    removeItem.setEnabled(false);
                    msItem.setEnabled(false);
                }
            }
        });
        
        helpItem = new JMenuItem("Contents");
        helpItem.setMnemonic('c');
        initHelp();
        
        helpMenu.add(helpItem);
        mergeMenu.add(mergeItem);
        mergeMenu.add(removeItem);
        viewMenu.add(msItem);
        fileMenu.add(exitItem);
        bar.add(fileMenu);
        bar.add(mergeMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);
        return bar;
    }
    
    /**
     * Shows view with last master and slave
     */
    public void view(){
        
        if(viewFrame == null)
            viewFrame = new JFrame("Master and Slave view");
        else{
            viewFrame.setVisible(false);
            viewFrame = new JFrame("Master and Slave view");
        }
        
        JTabbedPane tabPane = new JTabbedPane();
        viewFrame.getContentPane().add(tabPane);
        tabPane.addTab("TOC", createTOCPane());
        tabPane.addTab("Index",createIndexPane());
        tabPane.addTab("Search",createSearchPane());
        
        viewFrame.pack();
        
        viewFrame.setLocation(computeCenter(viewFrame));
        viewFrame.setVisible(false);
        msItem.setEnabled(false);
        
        viewFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent evt) {
                viewFrame.setVisible(false);
                msItem.setEnabled(true);
            }
        }
        );
    }
    /**
     * Setups slave  view's data
     * (slaveTOC, slaveIndex)
     */
    public void setSlaveCopy(HelpSet slaveSet){
        
        DefaultHelpModel model = new DefaultHelpModel(slaveSet);
        try{
            slaveTOC.setModel(model);
            slaveIndex.setModel(model);
            slaveSearch.setModel(model);
        }catch(NullPointerException off){}
        

    }
    
    /**
     * Removes view's data (trick)  and sets new slave
     */
    public void removeSetFromView(){
        slaveCopySets.pop();
        HelpSet hs = null;
        HelpSet lastSet = null;
 
       //set slave
        try{
            lastSet = (HelpSet)slaveCopySets.lastElement();
        }catch(NoSuchElementException exp){
            if(viewFrame != null){
                viewFrame.setVisible(false);
            }            
        }
        if(lastSet != null){
            DefaultHelpModel slaveModel = new DefaultHelpModel(lastSet);
            slaveTOC.setModel(slaveModel);
            slaveIndex.setModel(slaveModel);
            slaveSearch.setModel(slaveModel);
            //slaveHelpCopy.setModel(slaveModel);
        }else{
            if(viewFrame != null){
                viewFrame.setVisible(false);
            }            
        }
                
        if(slaveCopySets.size() > 0)
            masterHSCopy.remove((HelpSet)slaveCopySets.peek());
    }
    
    /**
     * Creates TOC pane with master and slave JHelpTOCNavigators
     */
    public Component createTOCPane(){
        
        JSplitPane splp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel masterPanel = new JPanel(new BorderLayout());

        masterPanel.add(new JLabel("Master",SwingConstants.CENTER),BorderLayout.NORTH);
        masterPanel.add(masterTOC,BorderLayout.CENTER);
        masterPanel.setPreferredSize(new Dimension(200,400));
        splp.setLeftComponent(masterPanel);
        
        JPanel slavePanel = new JPanel(new BorderLayout());
        slavePanel.add(new JLabel("Slave", SwingConstants.CENTER),BorderLayout.NORTH);
        slavePanel.add(slaveTOC, BorderLayout.CENTER);
        slavePanel.setPreferredSize(new Dimension(200,400));
        
        splp.setRightComponent(slavePanel);
        splp.resetToPreferredSizes();
        return splp;
    }
    
    /**
     * Creates Index pane with master and slave JHelpIndexNavigators
     */
    public Component createIndexPane(){
        JSplitPane splp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel masterPanel = new JPanel(new BorderLayout());
        
        masterPanel.add(new JLabel("Master",SwingConstants.CENTER),BorderLayout.NORTH);
        
        masterPanel.add(masterIndex,BorderLayout.CENTER);
        splp.setLeftComponent(masterPanel);
        
        JPanel slavePanel = new JPanel(new BorderLayout());
        
        slavePanel.add(new JLabel("Slave",SwingConstants.CENTER),BorderLayout.NORTH);
        slavePanel.add(slaveIndex, BorderLayout.CENTER);
        
        splp.setRightComponent(slavePanel);
        
        return splp;
    }
    /**
     * Creates Search pane with master and slave JHelpSearchNavigators
     */
    public Component createSearchPane(){
        JSplitPane splp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel masterPanel = new JPanel(new BorderLayout());
        
        masterPanel.add(new JLabel("Master",SwingConstants.CENTER),BorderLayout.NORTH);
        masterPanel.add(masterSearch,BorderLayout.CENTER);
        splp.setLeftComponent(masterPanel);
        
        JPanel slavePanel = new JPanel(new BorderLayout());
        
        slavePanel.add(new JLabel("Slave",SwingConstants.CENTER),BorderLayout.NORTH);
        slavePanel.add(slaveSearch, BorderLayout.CENTER);
        splp.setRightComponent(slavePanel);
        
        return splp;
    }
    
    /**
     * Works with one of each type of navigator (copy of master view)
     * (rethink using JHelp.getHelpNavigators() -- 
     */
    public void setupMasterNavigators(JHelp help){
        
        Class cTOC = null;
        Class cIndex = null;
        Class cSearch = null;
        Enumeration en = help.getHelpNavigators();
        try{
            cTOC = Class.forName("javax.help.JHelpTOCNavigator");
            cIndex = Class.forName("javax.help.JHelpIndexNavigator");
            cSearch = Class.forName("javax.help.JHelpSearchNavigator");
        }
        catch(ClassNotFoundException exp){
            System.err.println(exp);
            System.exit(1);
        }
        while(en.hasMoreElements()){
            JHelpNavigator navigator = (JHelpNavigator)en.nextElement();
            if(cTOC.isInstance(navigator)){
                //is toc navigator
                try{
                    masterTOC = (JHelpTOCNavigator)navigator;
                }catch(Exception e){
                    System.err.println(e);
                }
                
            }else if(cIndex.isInstance(navigator)){
                //is index navigator
                try{
                    masterIndex = (JHelpIndexNavigator)navigator;
                }catch(Exception ex){
                    System.err.println(ex);
                }
            }else if(cSearch.isInstance(navigator)){
                try{
                    masterSearch = (JHelpSearchNavigator)navigator;
                }catch(Exception exp){
                    System.err.println(exp);
                }
            }
        }
    }
    
    /**
     * Setups slave navigators (copy of slave view)
     */
    public void setupSlaveNavigators(JHelp help){
        
        Class cTOC = null;
        Class cIndex = null;
        Class cSearch = null;
        Enumeration en = help.getHelpNavigators();
        try{
            cTOC = Class.forName("javax.help.JHelpTOCNavigator");
            cIndex = Class.forName("javax.help.JHelpIndexNavigator");
            cSearch = Class.forName("javax.help.JHelpSearchNavigator");
        }
        catch(ClassNotFoundException exp){
            System.err.println(exp);
            System.exit(1);
        }
        while(en.hasMoreElements()){
            JHelpNavigator navigator = (JHelpNavigator)en.nextElement();
            if(cTOC.isInstance(navigator)){
                //is toc navigator
                try{
                    slaveTOC = (JHelpTOCNavigator)navigator;
                }catch(Exception p){
                    System.err.println(p);
                }
            }else if(cIndex.isInstance(navigator)){
                //is index navigator
                try{
                    slaveIndex = (JHelpIndexNavigator)navigator;
                }catch(Exception ec){
                    System.err.println(ec);
                }
            }else if(cSearch.isInstance(navigator)){
                try{
                    slaveSearch = (JHelpSearchNavigator)navigator;
                }catch(Exception exp){
                    System.err.println(exp);
                }
            }
        }
    }
    /**
     * Handles file, pushes helpsets to the stacks
     */
    public boolean handleFile(){
        
        File file = hsChooser.getSelectedFile();
        String fileName = file.getAbsolutePath();
        if(fileName.endsWith(".jar")){
            String strURL = jarAccessory.getName();
	    try {
		URL jarURL = new URL("file:"+fileName);
		URL urls[] = { jarURL };
		URLClassLoader urlLoader = new URLClassLoader(urls, loader);
		slaveHsURL = HelpSet.findHelpSet(urlLoader, strURL);
	    } catch (MalformedURLException ex) {
		System.err.println(ex);
	    }

        }else{
            try{
                slaveHsURL = new URL("file:"+fileName);
            }catch(MalformedURLException ex){
                System.err.println(ex);
            }
        }
        
        try{
            slaveHelpSet = new HelpSet(loader,slaveHsURL);
            //add slave to the stack
            slaveSets.push(slaveHelpSet);
            slaveTitle = slaveHelpSet.getTitle();
            slaveTOCLabel = new JLabel(slaveTitle, SwingConstants.CENTER);
            slaveIndexLabel = new JLabel(slaveTitle, SwingConstants.CENTER);
            slaveHSCopy = new HelpSet(loader,slaveHsURL);
            //add slave copy to the copy stack
            slaveCopySets.push(slaveHSCopy);
            slaveHelp = new JHelp(slaveHelpSet);
            slaveHelpCopy = new JHelp(slaveHSCopy);
            masterHelpSet.add(slaveHelpSet);
            //if slave navigators exist - set new model dont't create new navigators
            if((slaveTOC == null) || (slaveIndex == null))
                setupSlaveNavigators(slaveHelpCopy);        
            
        }
        catch(HelpSetException ex){
            System.err.println(ex);
        }
        if(slaveSets.size() == 1)
            view();
        return true;
    }
    /**
     * Computes location for frame to be in centre
     */
    public Point computeCenter(JFrame frame){
        Toolkit toolkit = frame.getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Dimension frameSize = frame.getSize();
        double x = (screenSize.getWidth()/2)-(frameSize.getWidth()/2);
        double y = (screenSize.getHeight()/2)-(frameSize.getHeight()/2);
        return new Point((int)x,(int)y);
    }
    /**
     * Initiates application help
     */
    public void initHelp(){
        HelpSet helpSet = null;
        try{
            helpSet = new HelpSet(loader,HelpSet.findHelpSet(loader,"MergeHelp"));
        }catch(HelpSetException exp){
                System.out.println(exp);
	}
	helpBroker = helpSet.createHelpBroker();     
	helpBroker.enableHelpKey(frame.getRootPane(),"intro",helpSet);
	helpBroker.enableHelpOnButton(helpItem,"intro",helpSet);
            
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        if(args.length >0)
            new Merge(args[0]);
        else
            new Merge("Animals");
        
    }
}
