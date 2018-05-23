/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventory;

import java.io.*;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.*;
import java.util.concurrent.*;

/**
 *
 * @author Pereza
 */
public class InventoryFrame extends javax.swing.JFrame {

	/**
	 * Creates new form InventoryFrame
	 */

	static DefaultTableModel model = new DefaultTableModel();

	public void loadData() {
		// Read the data
		model.addColumn("Name");
		model.addColumn("Make");
		model.addColumn("Model");
		model.addColumn("Quantity");
		model.addColumn("Description");
		model.addRow(new Object[] { null, null, null, null, null });

		try {
			// creates a BufferedReader that will read the file
			//BufferedReader reader = new BufferedReader(new FileReader("inventory.csv"));
			BufferedReader reader = new BufferedReader(new FileReader("\\\\10.48.1.133\\inventory\\inventory.csv"));
			// this line will store whatever is read from the file
			String line = "temporary value";
			// this stores how many lines have been read, this is important because this is
			// the index for the table row
			int linesRead = 0;
			// as long as there's still stuff to read...
			while (line != null) {
				// reads one line
				line = reader.readLine();
				// splits the values between commas
				String[] splitLine = line.split(",");
				// this loop keeps running until it goes over all the split values
				for (int i = 0; i < splitLine.length; i++) {
					// updates the table value
					// the row index is based on the number of lines that have been read, this way
					// the method will update from top to bottom
					// the column index is the loop variable i since it goes from left to right
					// dataEntries is the array of values stored on the table, it's defined as a
					// global variable outside of this function
					if (model.getRowCount() <= linesRead) {
						model.addRow(new Object[] { null, null, null, null, null });
					}
					model.setValueAt(splitLine[i], linesRead, i);
				}
				// now that it has gone over the line, it increases the number of lines read
				linesRead++;
			}

			reader.close();
		} catch (Exception e) {
			System.out.println("Exception caught: " + e);
		}

	}
	
	public static void saveData() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("\\\\10.48.1.133\\inventory\\inventory.csv"));
			for (int i = 0; i < model.getRowCount(); i++) {
				writer.write(inventoryTable.getValueAt(i, 0).toString() + "," + inventoryTable.getValueAt(i, 1).toString() + "," + inventoryTable.getValueAt(i, 2).toString() + "," + inventoryTable.getValueAt(i, 3).toString() + "," + inventoryTable.getValueAt(i, 4).toString() + "\n");
			}
			writer.close();
			
		} catch (Exception e) {
			
		}
	}
		

	public int[] firstSearch(int[] selectedRows, int columnIndex, String fieldText, boolean description) {
		inventoryTable.clearSelection();
		inventoryTable.getSelectionModel().removeSelectionInterval(0, inventoryTable.getRowCount() - 1);
		for (int i = 0; i <= inventoryTable.getRowCount() - 1; i++) {
			if (columnIndex != 4) {
				if (model.getValueAt(i, columnIndex).toString().equalsIgnoreCase(fieldText)) {
					inventoryTable.getSelectionModel().addSelectionInterval(i, i);
				}
			} else {
				if (model.getValueAt(i, columnIndex).toString().contains(fieldText)) {
					inventoryTable.getSelectionModel().addSelectionInterval(i, i);
				}
			}
    	}
		selectedRows = inventoryTable.getSelectedRows();
		if (selectedRows.length == 0) {
			exactSearch = false;
		}
		return selectedRows;
	}
	
	public int[] filterTable(int[] selectedRows, int columnIndex, String fieldText, boolean description) {
		if (columnIndex != 3) {
			inventoryTable.clearSelection();
			inventoryTable.getSelectionModel().removeSelectionInterval(0, inventoryTable.getRowCount() - 1);
		}
		for (int i = 0; i <= selectedRows.length - 1; i++) {
			if (columnIndex != 4) {
				if (model.getValueAt(selectedRows[i], columnIndex).toString().equalsIgnoreCase(fieldText)) {
					inventoryTable.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
				} else {
					if (exactSearch == true && columnIndex == 3) {
						quantityMismatch = true;
					}
					exactSearch = false;
				}
			} else {
				if (model.getValueAt(selectedRows[i], columnIndex).toString().contains(fieldText)) {
					inventoryTable.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
				} else {
					if (exactSearch == true && columnIndex == 3) {
						quantityMismatch = true;
					}
					exactSearch = false;
				}
			}
		}
		selectedRows = inventoryTable.getSelectedRows();
		return selectedRows;
	}
	
	public int[] searchTable (int[] selectedRows, int columnIndex, String fieldText, boolean description) {
		if (firstScan == true) {
			selectedRows = firstSearch(selectedRows, columnIndex, fieldText, description);
			firstScan = false;
		} else {
			selectedRows = filterTable(selectedRows, columnIndex, fieldText, description);
		}
		return selectedRows;
	}
	
	private void resetSearch() {
		firstScan = true;
		searchCount = 0;
		selectedRows = null;
	}	
	
	public DocumentListener documentListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			
			resetSearch();
			
		}
		public void removeUpdate(DocumentEvent e) {
			
			resetSearch();
		}
		public void insertUpdate(DocumentEvent e) {
			resetSearch();
		}
	};
	
	public void scan(String code) {
		System.out.println(code);
		cleanFields();
		boolean valueFound = false;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("\\\\10.48.1.133\\inventory\\codes.csv"));
			String line = reader.readLine();
			System.out.println(line);
			while (line != null) {
				String[] splitLine = line.split(",");
				System.out.println(splitLine[0]);
				System.out.println(code);
				if (code.equals(splitLine[0])) {
					valueFound = true;
					nameField.setText(splitLine[1]);
					makeField.setText(splitLine[2]);
					modelField.setText(splitLine[3]);
					
					startSearch();
					quantityField.setText("1");
					break;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (valueFound == false) {
			cleanFields();
		}
	}
	
	public void cleanFields() {
		nameField.setText("");
		makeField.setText("");
		modelField.setText("");
		quantityField.setText("");
		descriptionField.setText("");
	}
	
	public void startSearch() {
		exactSearch = true;
		if (firstScan == true) {
			inventoryTable.clearSelection();
			selectedRows = null;
			searchCount = 0;
			
			if(descriptionField.getText().trim().isEmpty() == false) {
				selectedRows = searchTable(selectedRows, 4, descriptionField.getText().trim(), true);
			}
			
			if(nameField.getText().trim().isEmpty() == false) {
				selectedRows = searchTable(selectedRows, 0, nameField.getText().trim(), false);
			}
    	
			if(makeField.getText().trim().isEmpty() == false) {
				selectedRows = searchTable(selectedRows, 1, makeField.getText().trim(), false);
			}
    	
			if(modelField.getText().trim().isEmpty() == false) {
				selectedRows = searchTable(selectedRows, 2, modelField.getText().trim(), false);
			}
    	
			if(quantityField.getText().trim().isEmpty() == false) {
				selectedRows = searchTable(selectedRows, 3, quantityField.getText().trim(), false);
			}
		}
		
		inventoryTable.clearSelection();
		if (selectedRows.length != 0) {
			inventoryTable.getSelectionModel().setSelectionInterval(selectedRows[searchCount],selectedRows[searchCount]);
			searchCount++;
			if (searchCount > selectedRows.length - 1) {
				searchCount = 0;
			}
		}
	}
	
	public void checkForBarcode() {
		try {
			if (nameField.getText(nameField.getDocument().getLength() - 1, 1).equals("*")) {
				scan(nameField.getText());
			} else if (makeField.getText(makeField.getDocument().getLength() - 1, 1).equals("*")) {
				scan(makeField.getText());
			} else if (modelField.getText(makeField.getDocument().getLength() - 1, 1).equals("*")) {
				scan(modelField.getText());
			} else if (quantityField.getText(quantityField.getDocument().getLength() - 1, 1).equals("*")) {
				scan(quantityField.getText());
			} else if (descriptionField.getText(descriptionField.getDocument().getLength() - 1, 1).equals("*")) {
				scan(descriptionField.getText());
			}/* else {
				cleanFields();
			}*/
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public InventoryFrame() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		inventoryTable = new javax.swing.JTable();
		jLabel1 = new javax.swing.JLabel();
		nameField = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		makeField = new javax.swing.JTextField();
		modelField = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		quantityField = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		descriptionField = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		addButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		findButton = new javax.swing.JButton();

		inventoryTable.setModel(model);
		jScrollPane1.setViewportView(inventoryTable);

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel1.setText("Name:");

		jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel2.setText("Make:");

		jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel3.setText("Model:");

		jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel4.setText("Quantity:");

		jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel5.setText("Description:");

		addButton.setText("Add");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		deleteButton.setText("Delete");
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		findButton.setText("Find");
		findButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				findButtonActionPerformed(evt);
			}
		});
		
		nameField.getDocument().addDocumentListener(documentListener);
		makeField.getDocument().addDocumentListener(documentListener);
		modelField.getDocument().addDocumentListener(documentListener);
		quantityField.getDocument().addDocumentListener(documentListener);
		descriptionField.getDocument().addDocumentListener(documentListener);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 580,
						javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(nameField)
						.addComponent(makeField).addComponent(modelField).addComponent(quantityField)
						.addComponent(descriptionField)
						.addGroup(layout.createSequentialGroup().addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1)
								.addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5)
								.addGroup(layout.createSequentialGroup().addComponent(addButton).addGap(18, 18, 18)
										.addComponent(deleteButton).addGap(18, 18, 18).addComponent(findButton)))
								.addGap(0, 0, Short.MAX_VALUE)))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
						.createSequentialGroup().addComponent(jLabel1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(makeField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel3)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel4)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(quantityField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(addButton).addComponent(deleteButton).addComponent(findButton)))
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 347,
								javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		loadData();

		pack();
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				checkForBarcode();
			}
		}, 500, 500, TimeUnit.MILLISECONDS);
	}// </editor-fold>

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		resetSearch();
		startSearch();
		if (((exactSearch == true && quantityMismatch == false) || (exactSearch == false && quantityMismatch == true)) && model.getValueAt(selectedRows[0],0).toString().equals(nameField.getText()) && model.getValueAt(selectedRows[0], 1).toString().equals(makeField.getText()) && model.getValueAt(selectedRows[0], 2).toString().equals(modelField.getText())) {
			//System.out.println(model.getValueAt(selectedRows[0], 3).toString());
			model.setValueAt(Integer.parseInt(model.getValueAt(selectedRows[0],3).toString()) + Integer.parseInt(quantityField.getText()), selectedRows[0], 3);
		} else {
			if (nameField.getText().trim().isEmpty() == false || makeField.getText().trim().isEmpty() == false 
					|| modelField.getText().trim().isEmpty() == false || quantityField.getText().trim().isEmpty() == false 
					|| descriptionField.getText().trim().isEmpty() == false) {
				/*if (nameField.getText().trim().isEmpty() == true) {
					nameField.setText("---");
				}
				if (makeField.getText().trim().isEmpty() == true) {
					makeField.setText("---");
				}
				if (modelField.getText().trim().isEmpty() == true) {
					modelField.setText("---");
				}
				if (quantityField.getText().trim().isEmpty() == true) {
					quantityField.setText("---");
				}
				if (descriptionField.getText().trim().isEmpty() == true) {
					descriptionField.setText("---");
				}*/
				model.addRow(new Object[] { nameField.getText().trim(), makeField.getText().trim(), modelField.getText().trim(), quantityField.getText().trim(), descriptionField.getText().trim() });
			}
		}
		inventoryTable.clearSelection();
		inventoryTable.getSelectionModel().removeSelectionInterval(0, inventoryTable.getRowCount() - 1);
		cleanFields();
		quantityMismatch = false;
	}

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		resetSearch();
		if (inventoryTable.getSelectedRow() != -1) {
			int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this?",
					"Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (confirm == 0) {
				while (inventoryTable.getSelectedRowCount() > 0) {
					model.removeRow(inventoryTable.getSelectedRow());
				}
			}
		}
	}

	private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
		startSearch();
		System.out.println(selectedRows);
	}
	
	public static WindowAdapter windowAdapter = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			switch(JOptionPane.showConfirmDialog(null, "Do you want to save your changes?")) {
				case JOptionPane.YES_OPTION:
					saveData();
					System.exit(0);
					break;
				case JOptionPane.NO_OPTION:
					System.exit(0);
					break;
			}
		}
	};

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equalsIgnoreCase(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		
		
		
		// </editor-fold>
		
		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				InventoryFrame inventoryFrame = new InventoryFrame();
				inventoryFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				inventoryFrame.addWindowListener(windowAdapter);
				inventoryFrame.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	public javax.swing.JButton addButton;
	public javax.swing.JButton deleteButton;
	public javax.swing.JButton findButton;
	public static javax.swing.JTextField descriptionField;
	public static javax.swing.JTable inventoryTable;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JScrollPane jScrollPane1;
	public static javax.swing.JTextField makeField;
	public static javax.swing.JTextField modelField;
	public static javax.swing.JTextField nameField;
	public static javax.swing.JTextField quantityField;
	boolean firstScan = true;
	int[] selectedRows = null;
	int searchCount = 0;
	boolean exactSearch = false;
	boolean quantityMismatch = false;
	// End of variables declaration
}
