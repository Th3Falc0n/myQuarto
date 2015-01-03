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

import myQuarto.netprot.QuartoPacket;
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

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

public class PaneQuartoGame extends JPanel {
    private static final long serialVersionUID = 1L;
    
    JLabel label;
    List list;
    private JPanel panel;
    private JLabel lblStatus;
    private JLabel lblGameStatus;
    private JButton btnNewButton;
    private JLabel lblCurrentElo;
    private JLabel lblELO;
    
    public void setStatus(String s) {
        label.setText(s);
        label.repaint();
    }

    public PaneQuartoGame() {
        setLayout(new MigLayout("", "[160.00][grow]", "[16.00][158.00][165.00][]"));
        
        JLabel lblOtherClients = new JLabel("Other clients:");
        add(lblOtherClients, "cell 0 0");
        
        list = new List();
        add(list, "cell 0 1 1 2,grow");
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "John Doe", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel, "cell 1 1,grow");
        panel.setLayout(new MigLayout("", "[][][grow]", "[][]"));
        
        lblStatus = new JLabel("Status:");
        panel.add(lblStatus, "cell 0 0");
        
        lblGameStatus = new JLabel("idle");
        panel.add(lblGameStatus, "cell 1 0");
        
        btnNewButton = new JButton("Watch");
        btnNewButton.setEnabled(false);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        panel.add(btnNewButton, "cell 2 0,alignx right");
        
        lblCurrentElo = new JLabel("Current ELO:");
        panel.add(lblCurrentElo, "cell 0 1");
        
        lblELO = new JLabel("1000");
        panel.add(lblELO, "cell 1 1");
        
        label = new JLabel("Welcome to the server...");
        add(label, "cell 0 3 2 1,grow");
    }

    public void addOnlineClient(String string) {
        list.add(string);
    }
    
    public void removeOnlineClient(String string) {
        list.remove(string);
    }

    public void showPlayerData(QuartoPacket packet) {
        // TODO Auto-generated method stub
        
    }
}
