package src.main.dev.m8u.kitpo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class GUI extends JFrame {

    JTabbedPane tabbedPane;

    public GUI() throws HeadlessException {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 650);
        tabbedPane = new JTabbedPane();
        this.setContentPane(tabbedPane);

        addSelectionTab();
    }

    public void addSelectionTab() {
        tabbedPane.addTab("New...", new DemoTypeSelectionPanel(this));
    }

    public void replaceSelectionTabWithDemo(String demoKeyTypeName, String demoValueTypeName) {
        tabbedPane.removeTabAt(tabbedPane.getTabCount() - 1);
        tabbedPane.addTab(demoKeyTypeName + "_" + demoValueTypeName + "_" + (tabbedPane.getTabCount() + 1), new DemoPanel(demoKeyTypeName, demoValueTypeName, this));
    }

    public void replaceSelectionTabWithDemo(File file) throws Exception {
        tabbedPane.removeTabAt(tabbedPane.getTabCount() - 1);
        tabbedPane.addTab(file.getName(), new DemoPanel(file, this));
    }
}

class DemoTypeSelectionPanel extends JPanel {

    public DemoTypeSelectionPanel(GUI parent) {
        super();

        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 2, 8, 2);

        ArrayList<String> typeNames = TypeFactory.getTypeNames();

        JComboBox<String> keyTypesCombobox = new JComboBox<>();
        for (String typeName : typeNames) {
            keyTypesCombobox.addItem(typeName);
        }
        this.add(new JLabel("Key type:"), constraints);
        constraints.gridy = 1;
        this.add(keyTypesCombobox, constraints);

        JComboBox<String> valueTypesCombobox = new JComboBox<>();
        for (String typeName : typeNames) {
            valueTypesCombobox.addItem(typeName);
        }
        constraints.gridy = 0;
        constraints.gridx = 1;
        this.add(new JLabel("Value type:"), constraints);
        constraints.gridy = 1;
        this.add(valueTypesCombobox, constraints);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            parent.replaceSelectionTabWithDemo(keyTypesCombobox.getSelectedItem().toString(),
                    valueTypesCombobox.getSelectedItem().toString());
            parent.addSelectionTab();
            parent.tabbedPane.setSelectedIndex(parent.tabbedPane.getTabCount() - 2);
        });
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        this.add(createButton, constraints);

        JButton loadButton = new JButton("Load...");
        loadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    parent.replaceSelectionTabWithDemo(chooser.getSelectedFile());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    parent.addSelectionTab();
                    return;
                }
                parent.addSelectionTab();
                parent.tabbedPane.setSelectedIndex(parent.tabbedPane.getTabCount() - 2);
            }
        });
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        this.add(loadButton, constraints);
    }
}

class DemoPanel extends JPanel {

    ChainedHashtable<Object, Object> hashtable;
    ChainedHashtableStorableBuilder keyBuilder;
    ChainedHashtableStorableBuilder valueBuilder;
    DefaultTableModel tableModel;

    public DemoPanel(String keyTypeName, String valueTypeName, GUI parent) {
        super();

        initGUI(parent);
        keyBuilder = TypeFactory.getBuilderByName(keyTypeName);
        valueBuilder = TypeFactory.getBuilderByName(valueTypeName);
        hashtable = new ChainedHashtable<>(keyTypeName, valueTypeName);
        refillTable();
    }

    public DemoPanel(File file, GUI parent) throws Exception {
        super();

        initGUI(parent);
        this.hashtable = ChainedHashtable.loadFromJSON(new FileInputStream(file));
        keyBuilder = TypeFactory.getBuilderByName(this.hashtable.getKeyTypeName());
        valueBuilder = TypeFactory.getBuilderByName(this.hashtable.getValueTypeName());
        refillTable();
    }

    void initGUI(GUI parent) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 2, 4, 2);
        constraints.gridx = 0;

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            parent.tabbedPane.remove(parent.tabbedPane.getSelectedIndex());
        });
        this.add(closeButton, constraints);

        JButton saveButton = new JButton("Save...");
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            chooser.setSelectedFile(new File(parent.tabbedPane.getTitleAt(parent.tabbedPane.getSelectedIndex()) + ".json"));
            int result = chooser.showSaveDialog(this);
            try {
                if (result == JFileChooser.APPROVE_OPTION) {
                    this.hashtable.saveAsJSON(new FileOutputStream(chooser.getSelectedFile()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        });
        constraints.gridx = 1;
        this.add(saveButton, constraints);

        tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setCellSelectionEnabled(true);
        JScrollPane scrollPane = new JScrollPane(table);
        constraints.gridx = 0;
        constraints.gridwidth = 8;
        this.add(scrollPane, constraints);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));

        JPanel keyValuePanel = new JPanel();
        keyValuePanel.setLayout(new BoxLayout(keyValuePanel, BoxLayout.Y_AXIS));
        JLabel keyTextFieldLabel = new JLabel("Key:");
        JTextField keyTextField = new JTextField(8);
        keyValuePanel.add(keyTextFieldLabel);
        keyValuePanel.add(keyTextField);
        JLabel valueTextFieldLabel = new JLabel("Value:");
        JTextField valueTextField = new JTextField(8);
        keyValuePanel.add(valueTextFieldLabel);
        keyValuePanel.add(valueTextField);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Object cellData = table.getValueAt(table.rowAtPoint(e.getPoint()),
                        table.columnAtPoint(e.getPoint()));
                if (cellData == null) {
                    keyTextField.setText("");
                    valueTextField.setText("");
                    return;
                }
                String[] keyValuePair = cellData.toString().split("->");
                keyTextField.setText(keyValuePair[0]);
                valueTextField.setText(keyValuePair[1]);
            }
        });

        JPanel buttonsPanel = new JPanel();
        keyValuePanel.setLayout(new BoxLayout(keyValuePanel, BoxLayout.Y_AXIS));
        JButton setButton = new JButton("Set");
        setButton.addActionListener(e -> {
            try {
                this.hashtable.set(this.keyBuilder.parseValue(keyTextField.getText()),
                        this.valueBuilder.parseValue(valueTextField.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
            refillTable();
        });
        JButton getButton = new JButton("Get");
        getButton.addActionListener(e -> {
            Object value = this.hashtable.get(keyTextField.getText());
            if (value != null)
                valueTextField.setText(value.toString());
        });
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            this.hashtable.remove(keyTextField.getText());
            refillTable();
        });
        buttonsPanel.add(setButton);
        buttonsPanel.add(getButton);
        buttonsPanel.add(removeButton);

        toolsPanel.add(keyValuePanel);
        toolsPanel.add(buttonsPanel);

        this.add(toolsPanel, constraints);
    }

    void refillTable() {
        for (int r = 0; r < this.tableModel.getRowCount(); r++) {
            this.tableModel.removeRow(r);
        }
        this.tableModel.setColumnCount(this.hashtable.getCapacity());
        this.tableModel.setRowCount(chain.CHAIN_MAX_LENGTH);
        int row, col = 0;
        Vector<String> columnIdentifiers = new Vector<>();
        for (chain<Object, Object> chain : this.hashtable) {
            row = 0;
            for (chainNode<Object, Object> entry : chain) {
                this.tableModel.setValueAt(entry.key + "->" + entry.value, row++, col);
            }
            columnIdentifiers.add(String.valueOf(col));
            col++;
        }
        this.tableModel.setColumnIdentifiers(columnIdentifiers);
    }
}
