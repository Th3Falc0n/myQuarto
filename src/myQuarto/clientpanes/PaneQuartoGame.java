package myQuarto.clientpanes;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;

import javax.swing.SpringLayout;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;

import javax.swing.JList;

import java.awt.GridBagConstraints;

import net.miginfocom.swing.MigLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import java.awt.Font;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JMenuBar;

import java.awt.List;

public class PaneQuartoGame extends JPanel {
    private static final long serialVersionUID = 1L;
    
    JLabel label;
    List list;
    
    public void setStatus(String s) {
        label.setText(s);
        label.repaint();
    }

    public PaneQuartoGame() {
        setLayout(new MigLayout("", "[160.00][grow]", "[16.00][grow][]"));
        
        JLabel lblOtherClients = new JLabel("Other clients:");
        add(lblOtherClients, "cell 0 0");
        
        list = new List();
        add(list, "cell 0 1,grow");
        
        label = new JLabel("Welcome to the server...");
        add(label, "cell 0 2 2 1,grow");
    }

    public void addOnlineClient(String string) {
        list.add(string);
    }
    
    public void removeOnlineClient(String string) {
        list.remove(string);
    }
}
