package com.randude14.lotteryplus.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;

import org.bukkit.Bukkit;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.Property;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

/*
 * Used in the main frame in each tab and allows the user to create lotteries
 * using add and remove buttons and entering in the values with a text field
 */
@SuppressWarnings({"serial", "rawtypes"})
public class LotteryCreator extends JPanel implements ActionListener {
	private DefaultListModel<Property> defaults;
	private DefaultListModel<Value> values;
	private JScrollPane defaultsPane;
	private JScrollPane valuesPane;
	private JList<Property> defaultsList;
	private JList<Value> valuesList;
	private JTextField valueField;
	private JButton add;                     // used to add properties to the values list
	private JButton remove;                  // used to remove properties from the values list
	private JButton create;
	private final MainFrame parent;
	private String lotteryName;
	
	protected LotteryCreator(MainFrame frame, String lotteryName) {
		this.parent = frame;
		this.lotteryName = lotteryName;
		
		setLayout(new BorderLayout());
	}

	/*
	 * Checks if the map of properties is valid and creates the components for the creator
	 */
	protected boolean initComponents(Map<Property<?>, Object> propoerties) {
		defaults = new DefaultListModel<Property>();
		
		for(Property prop : Config.lotteryDefaults) {
			if(!propoerties.containsKey(prop)) {
				defaults.addElement(prop);
			}
		}
		defaultsList = new JList<Property>(defaults);
		defaultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		values = new DefaultListModel<Value>();
		
		// add the values and their properties
		for(Property prop : Config.lotteryDefaults) {
			
			if(propoerties.containsKey(prop)) {
				
				try {
					values.addElement(new Value(prop, propoerties.get(prop)));
					
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(parent, ChatUtils.getRawName("plugin.gui.dialog.error.types"), 
							"", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		
		// add values to the JList
		valuesList = new JList<Value>(values);
		valuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// create buttons
		add = new JButton(ChatUtils.getRawName("plugin.gui.button.add"));
		add.addActionListener(this);
		remove = new JButton(ChatUtils.getRawName("plugin.gui.button.remove"));
		remove.addActionListener(this);
		create = new JButton(ChatUtils.getRawName("plugin.gui.button.create"));
		create.addActionListener(this);
		
		// create text field and lists
		valueField = new JTextField();
		valueField.setText(ChatUtils.getRawName("plugin.gui.textfield.value"));
		defaultsList.addListSelectionListener(new ButtonEnabler(defaultsList, add));
		valuesList.addListSelectionListener(new ButtonEnabler(valuesList, remove));
		defaultsPane = new JScrollPane(defaultsList);
		valuesPane = new JScrollPane(valuesList);
		
		JPanel propPanel = new JPanel();		
		propPanel.setBorder(BorderFactory.createTitledBorder(ChatUtils.getRawName("plugin.gui.border.properties")));
		propPanel.setLayout(new GridLayout());
		propPanel.add(defaultsPane);
		propPanel.add(valuesPane);
		add(propPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(ChatUtils.getRawName("plugin.gui.border.options")));
		panel.setLayout(new GridLayout(2, 2, 40, 15));
		panel.add(add);
		panel.add(remove);
		panel.add(valueField);
		panel.add(create);
		add(panel, BorderLayout.SOUTH);
		return true;
	}
	
	/*
	 * Set the lottery name
	 * @param lotteryName - lottery name to set to
	 */
	protected void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	
	protected void reset() {
		values.removeAllElements();
		defaults.removeAllElements();
		
		for(Property prop : Config.lotteryDefaults) {
			defaults.addElement(prop);
		}

		valueField.setText(ChatUtils.getRawName("plugin.gui.textfield.value"));
		defaultsPane.getViewport().setViewPosition(new Point());
		valuesPane.getViewport().setViewPosition(new Point());
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		
		// adding a property to the lottery
		if(source == add) {
			Property prop = defaultsList.getSelectedValue();
			if(prop == null) 
				return;
			
			// check that inputed value is consistent with the property
			if(!checkEnteredValue(prop, valueField.getText())) {
				JOptionPane.showMessageDialog(this, ChatUtils.getRawName("plugin.gui.dialog.error.incorrect-value"));
				return;
			}
			
			Value value = new Value(prop, valueField.getText());
			defaults.removeElement(prop);
			values.addElement(value);
			sort(defaults);
			sort(values);
			
		// remove a property from the lottery
		} else if(source == remove) {
			Value value = valuesList.getSelectedValue();
			if(value == null) 
				return;
			values.removeElement(value);
			defaults.addElement(value.prop);
			sort(defaults);
			sort(values);
			
		// create the lottery section in the lotteries file and remove the tab from the main frames
		} else if(source == create) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			for(int cntr = 0;cntr < values.size();cntr++) {
				Value v = values.get(cntr);
				map.put(v.prop.getName(), v.value);
			}
			
			LotteryManager.createLotterySection(Bukkit.getConsoleSender(), lotteryName, map);
			JOptionPane.showMessageDialog(this, ChatUtils.getRawName("plugin.gui.dialog.lottery-section-created", "<lottery>", lotteryName));
			parent.closeCreator(lotteryName);
		}
	}
	
	/*
	 * Check the value entered in the text field.
	 * @param property - property to compare
	 * @param entered - value entered by user
	 * @return - whether the value entered is of correct data type
	 */
	private boolean checkEnteredValue(Property property, String entered) {
		Object defaultValue = property.getDefaultValue();
		
		if(defaultValue instanceof Boolean || defaultValue instanceof String) {
			return true;
		}
		
		if(defaultValue instanceof Number) {
			return Utils.isNumber(entered);
		}
		
		return true;
	}
	
	/*
	 * Used to sort a default list model
	 * @param list - default list model to sort
	 */
	@SuppressWarnings("unchecked")
	private void sort(DefaultListModel list) {
		
		Object[] array = list.toArray();
		list.removeAllElements();
		Arrays.sort(array);
		
		for(Object obj : array) {
			list.addElement(obj);
		}
	}
	
	/*
	 * Used in the JScrollPanes for listing
	 */
	private static class Value implements Comparable<Value> {
		private static final NumberFormat format = NumberFormat.getInstance();
		public final Property<?> prop;
		public final Object value;
		
		public Value(Property prop, Object value) {
			this.prop = prop;
			this.value = determineType(value);
		}

		private Object determineType(Object object) {
			String toString = object.toString();
			if(containsDigitsOnly(toString)) {
				try {
					return format.parse(toString);
				} catch (ParseException ex) {
				}
			}
			if(toString.equalsIgnoreCase("true")) {
				return Boolean.TRUE;
			}
			if(toString.equalsIgnoreCase("false")) {
				return Boolean.FALSE;
			}
			return toString;
		}
		
		private boolean containsDigitsOnly(String string) {
			for(int cntr = 0;cntr < string.length();cntr++) {
				char c = string.charAt(cntr);
				if(c == '.') continue;
				if(!Character.isDigit(c)) {
					return false;
				}
			}
			return true;
		}
		
		public int compareTo(Value value) {
			return prop.compareTo(value.prop);
		}
		
		public String toString() {
			String valueToString = value.toString();
			if(valueToString.equals("")) 
				valueToString = "''";
			return prop.getName() + ": " + valueToString;
		}
	}

	// enables the button if a value was selected
	private class ButtonEnabler implements ListSelectionListener {
		private final JList list;
		private final JButton button;
		
		public ButtonEnabler(JList list, JButton button) {
			this.list = list;
			this.button = button;
			this.button.setEnabled(false);
		}
		
		public void valueChanged(ListSelectionEvent event) {
			this.button.setEnabled(list.getSelectedValue() != null);
		}
	}
}
