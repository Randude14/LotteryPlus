package com.randude14.lotteryplus.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.configuration.Property;
import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Serves as the main window to house the lottery creator. Creates tabs for each lottery
 * and has multiple menu buttons to aid the user toward tutorials or reporting bugs
 * @see com.randude14.lotteryplus.gui.LotteryCreator
 */
@SuppressWarnings({"serial"})
public class MainFrame extends JFrame {
	private JTabbedPane tabs;
	private JMenuItem[] lafMenuItems;
	private JMenuItem tutorialItem;
	private JMenuItem websiteItem;
	private JMenuItem openItem;
	private JMenuItem resetItem;
	private JMenuItem removeItem;
	private JMenuItem ticketItem;
	private JMenuItem exitItem;
	
	public MainFrame() {
		setTitle(ChatUtils.getRawName("plugin.gui.title"));
		setJMenuBar(createJMenuBar());
		
		tabs = new JTabbedPane(JTabbedPane.LEFT);
		add(tabs);
		
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	/*
	 * Open a lottery creator tab with lotteryName
	 * @param lotteryName - name of the lottery to create/edit
	 */
	public void openCreator(String lotteryName) {
		
		// check that this lottery isn't already open and open that tab instead
		for(int cntr = 0;cntr < tabs.getTabCount();cntr++) {
			String title = tabs.getTitleAt(cntr);
			
			if(title.equalsIgnoreCase(lotteryName)) {
				tabs.setSelectedIndex(cntr);
				return;
			}
		}
		
		// grab properties from lotteries.yml and create the LotteryCreator
		Map<Property<?>, Object> map = new HashMap<Property<?>, Object>();
		lotteryName = LotteryManager.putAll(lotteryName, map);
		LotteryCreator creator = new LotteryCreator(this, lotteryName);
		
		// if the creator isn't successful return
		if(!creator.initComponents(map)) {
			return;
		}
		
		// add lottery creator tab and make it visible
		tabs.addTab(lotteryName, creator);
		removeItem.setEnabled(true);
		resetItem.setEnabled(true);
	}
	
	/*
	 * Close a lottery creator tab if it is open
	 * @param lotteryName - lottery creator tab to close
	 */
	protected void closeCreator(String lotteryName) {
		
		for(int cntr = 0;cntr < tabs.getTabCount();cntr++) {
			
			String title = tabs.getTitleAt(cntr);
			if(title.equalsIgnoreCase(lotteryName)) {
				tabs.removeTabAt(cntr);
				return;
			}
		}
	}
	
	/*
	 * Creates the menu bar for the JFrame
	 * @return - the menu bar for the main frame
	 */
	private JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		// create help menu
		JMenu helpMenu = new JMenu( ChatUtils.getRawName("plugin.gui.menu.help") );
		ActionListener al = new MenuActionListener(this);
		tutorialItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.tutorial") );
		websiteItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.website") );
		ticketItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.ticket") );
		tutorialItem.addActionListener(al);
		websiteItem.addActionListener(al);
		ticketItem.addActionListener(al);
		helpMenu.add(tutorialItem);
		helpMenu.add(websiteItem);
		helpMenu.add(ticketItem);
		
		// allow server to change the look and feel of the gui
		JMenu lafMenu = new JMenu(ChatUtils.getRawName("plugin.gui.menu.skins"));
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		lafMenuItems = new JMenuItem[lafInfo.length];
		
		for(int cntr = 0;cntr < lafInfo.length;cntr++) {
			
			lafMenuItems[cntr] = new JMenuItem( getClassSimpleName(lafInfo[cntr].getClassName()) );
			lafMenuItems[cntr].setActionCommand(lafInfo[cntr].getClassName());
			lafMenuItems[cntr].addActionListener(al);
			lafMenu.add(lafMenuItems[cntr]);
		}
		
		// create options menu
		JMenu optionsMenu = new JMenu( ChatUtils.getRawName("plugin.gui.menu.options") );
		
		resetItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.reset") );
		resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		resetItem.addActionListener(al);
		resetItem.setEnabled(false);
		
		exitItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.exit") );
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		exitItem.addActionListener(al);
		
		openItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.open") );
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openItem.addActionListener(al);
		
		removeItem = new JMenuItem( ChatUtils.getRawName("plugin.gui.menu.remove") );
		removeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));
		removeItem.addActionListener(al);
		removeItem.setEnabled(false);
		
		optionsMenu.add(openItem);
		optionsMenu.add(removeItem);
		optionsMenu.add(resetItem);
		optionsMenu.addSeparator();
		optionsMenu.add(exitItem);
		
		menuBar.add(optionsMenu);
		menuBar.add(lafMenu);
		menuBar.add(helpMenu);
		
		return menuBar;
	}
	
	/*
	 * Given a class name, returns its simplified name. Used for the look and feel menu
	 * @param className - name of class
	 * @return - the simple name of the class
	 */
	private String getClassSimpleName(String className) {
		
		try {
			return Class.forName(className).getSimpleName();
		} catch (Exception ex) {		
		}
		
		return className;
	}
	
	/*
	 * The action listener for the main frame
	 */
	private class MenuActionListener implements ActionListener {
		private final MainFrame parent;
		
		public MenuActionListener(MainFrame frame) {
			this.parent = frame;
		}

		/*
		 * Called when a menu button is pressed
		 * @param event - the event object associated with the press
		 */
		public void actionPerformed(ActionEvent event) {
			
			Object source = event.getSource();
			
			if(source == tutorialItem) {
				browse("https://dev.bukkit.org/projects/lotteryplus/pages/gui-tutorial");
				
			} else if(source == websiteItem) {
				browse("https://dev.bukkit.org/projects/lotteryplus");
				
			} else if(source == openItem) {
				String lotteryName = JOptionPane.showInputDialog(parent, ChatUtils.getRawName("plugin.gui.dialog.lottery-name"));
				
				if(lotteryName != null && !lotteryName.isEmpty()) {
					parent.openCreator(lotteryName);
				}
				
			} else if(source == ticketItem) {
				browse("https://github.com/Randude14/LotteryPlus/issues/new");
				
			} else if(source == removeItem) {
				int index = tabs.getSelectedIndex();
				if(index < 0) return;
				
				if(JOptionPane.showConfirmDialog(parent, ChatUtils.getRawName("plugin.gui.dialog.confirm.remove"), "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					tabs.removeTabAt(index);
				}
				
				removeItem.setEnabled(tabs.getTabCount() > 0);
				resetItem.setEnabled(tabs.getTabCount() > 0);
			} else if(source == resetItem) {
				int index = tabs.getSelectedIndex();
				if(index < 0) return;
				
				if(JOptionPane.showConfirmDialog(parent, ChatUtils.getRawName("plugin.gui.dialog.confirm.reset"), "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					((LotteryCreator) tabs.getComponentAt(index)).reset();
				}
				
			} else if(source == exitItem) {
				parent.setVisible(false);
			}
			
			for(JMenuItem menuItem : lafMenuItems) {
				
				if(source == menuItem) {
					String className = menuItem.getActionCommand();
					
					try {
						UIManager.setLookAndFeel(className);
						SwingUtilities.updateComponentTreeUI(parent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		/*
		 * Given a URL, opens the supported default browser
		 * @param url - URL to open
		 */
		private void browse(String url) {
			
			if(Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				
				if(desktop.isSupported(Desktop.Action.BROWSE)) {
					
					try {
						desktop.browse(new URL(url).toURI());
					} catch (Exception ex) {
						// no need to print out the exception
					}
				}
				
			}
			
		}
		
	}
}
