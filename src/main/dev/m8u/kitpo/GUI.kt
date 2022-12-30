package src.main.dev.m8u.kitpo

import src.main.dev.m8u.kitpo.builders.MyHashableBuilder
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class GUI : JFrame() {
    var tabbedPane: JTabbedPane

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        this.setSize(600, 650)
        tabbedPane = JTabbedPane()
        this.contentPane = tabbedPane
        addSelectionTab()
    }

    fun addSelectionTab() {
        tabbedPane.addTab("New...", DemoTypeSelectionPanel(this))
    }

    fun replaceSelectionTabWithDemo(demoKeyTypeName: String) {
        tabbedPane.removeTabAt(tabbedPane.tabCount - 1)
        tabbedPane.addTab(demoKeyTypeName + "_" + (tabbedPane.tabCount + 1), DemoPanel(demoKeyTypeName, this))
    }

    @Throws(Exception::class)
    fun replaceSelectionTabWithDemo(file: File) {
        tabbedPane.removeTabAt(tabbedPane.tabCount - 1)
        tabbedPane.addTab(file.name, DemoPanel(file, this))
    }
}

internal class DemoTypeSelectionPanel(parent: GUI) : JPanel() {
    init {
        this.layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.insets = Insets(8, 2, 8, 2)
        val typeNames = TypeFactory.typeNames
        val keyTypesCombobox = JComboBox<String?>()
        for (typeName in typeNames) {
            keyTypesCombobox.addItem(typeName)
        }
        this.add(JLabel("Key type:"), constraints)
        constraints.gridy = 1
        this.add(keyTypesCombobox, constraints)
        val createButton = JButton("Create")
        createButton.addActionListener {
            parent.replaceSelectionTabWithDemo(keyTypesCombobox.selectedItem!!.toString())
            parent.addSelectionTab()
            parent.tabbedPane.selectedIndex = parent.tabbedPane.tabCount - 2
        }
        constraints.gridy = 2
        constraints.gridx = 0
        constraints.gridwidth = 2
        this.add(createButton, constraints)
        val loadButton = JButton("Load...")
        loadButton.addActionListener {
            val chooser = JFileChooser()
            chooser.currentDirectory = File(System.getProperty("user.dir"))
            val result = chooser.showOpenDialog(this)
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    parent.replaceSelectionTabWithDemo(chooser.selectedFile)
                } catch (ex: Exception) {
                    JOptionPane.showMessageDialog(this, ex)
                    parent.addSelectionTab()
                    return@addActionListener
                }
                parent.addSelectionTab()
                parent.tabbedPane.selectedIndex = parent.tabbedPane.tabCount - 2
            }
        }
        constraints.gridy = 3
        constraints.gridx = 0
        constraints.gridwidth = 2
        this.add(loadButton, constraints)
    }
}

internal class DemoPanel : JPanel {
    private var hashtable: ChainedHashtable
    private var keyBuilder: MyHashableBuilder?
    private var tableModel: DefaultTableModel? = null

    constructor(keyTypeName: String, parent: GUI) : super() {
        initGUI(parent)
        keyBuilder = TypeFactory.getBuilderByName(keyTypeName)
        hashtable = ChainedHashtable(keyTypeName)
        refillTable()
    }

    constructor(file: File?, parent: GUI) : super() {
        initGUI(parent)
        hashtable = ChainedHashtable.loadFromJSON(FileInputStream(file!!))
        keyBuilder = TypeFactory.getBuilderByName(hashtable.keyTypeName)
        refillTable()
    }

    private fun initGUI(parent: GUI) {
        this.layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.insets = Insets(4, 2, 4, 2)
        constraints.gridx = 0
        val closeButton = JButton("Close")
        closeButton.addActionListener { parent.tabbedPane.remove(parent.tabbedPane.selectedIndex) }
        this.add(closeButton, constraints)
        val saveButton = JButton("Save...")
        saveButton.addActionListener {
            val chooser = JFileChooser()
            chooser.currentDirectory = File(System.getProperty("user.dir"))
            chooser.selectedFile = File(parent.tabbedPane.getTitleAt(parent.tabbedPane.selectedIndex) + ".json")
            val result = chooser.showSaveDialog(this)
            try {
                if (result == JFileChooser.APPROVE_OPTION) {
                    hashtable.saveAsJSON(FileOutputStream(chooser.selectedFile))
                }
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(this, ex)
            }
        }
        constraints.gridx = 1
        this.add(saveButton, constraints)
        tableModel = DefaultTableModel()
        val table = JTable(tableModel)
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        table.cellSelectionEnabled = true
        val scrollPane = JScrollPane(table)
        constraints.gridx = 0
        constraints.gridwidth = 8
        this.add(scrollPane, constraints)
        val toolsPanel = JPanel()
        toolsPanel.layout = BoxLayout(toolsPanel, BoxLayout.X_AXIS)
        val keyValuePanel = JPanel()
        keyValuePanel.layout = BoxLayout(keyValuePanel, BoxLayout.Y_AXIS)
        val keyTextFieldLabel = JLabel("Key:")
        val keyTextField = JTextField(8)
        keyValuePanel.add(keyTextFieldLabel)
        keyValuePanel.add(keyTextField)
        val valueTextFieldLabel = JLabel("Value:")
        val valueTextField = JTextField(8)
        keyValuePanel.add(valueTextFieldLabel)
        keyValuePanel.add(valueTextField)
        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)
                val cellData = table.getValueAt(table.rowAtPoint(e.point),
                        table.columnAtPoint(e.point))
                if (cellData == null) {
                    keyTextField.text = ""
                    valueTextField.text = ""
                    return
                }
                val keyValuePair = cellData.toString().split("->".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                keyTextField.text = keyValuePair[0]
                valueTextField.text = keyValuePair[1]
            }
        })
        val buttonsPanel = JPanel()
        keyValuePanel.layout = BoxLayout(keyValuePanel, BoxLayout.Y_AXIS)
        val setButton = JButton("Set")
        setButton.addActionListener {
            try {
                hashtable[keyBuilder!!.parse(keyTextField.text)] = valueTextField.text
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(this, ex)
            }
            refillTable()
        }
        val getButton = JButton("Get")
        getButton.addActionListener {
            var value: Any? = null
            try {
                value = hashtable[keyBuilder!!.parse(keyTextField.text)]
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(this, ex)
            }
            if (value != null) valueTextField.text = value.toString()
        }
        val removeButton = JButton("Remove")
        removeButton.addActionListener {
            try {
                hashtable.remove(keyBuilder!!.parse(keyTextField.text))
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(this, ex)
            }
            refillTable()
        }
        buttonsPanel.add(setButton)
        buttonsPanel.add(getButton)
        buttonsPanel.add(removeButton)
        toolsPanel.add(keyValuePanel)
        toolsPanel.add(buttonsPanel)
        this.add(toolsPanel, constraints)
    }

    private fun refillTable() {
        for (r in 0 until tableModel!!.rowCount) {
            tableModel!!.removeRow(0)
        }
        tableModel!!.columnCount = hashtable.capacity
        tableModel!!.rowCount = chain.CHAIN_MAX_LENGTH
        val columnIdentifiers = Vector<String>()
        for ((col, chain) in hashtable.withIndex()) {
            for ((row, entry) in chain.withIndex()) {
                tableModel!!.setValueAt(entry.key.toString() + "->" + entry.value, row, col)
            }
            columnIdentifiers.add(col.toString())
        }
        tableModel!!.setColumnIdentifiers(columnIdentifiers)
    }
}