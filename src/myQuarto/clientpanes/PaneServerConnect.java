package myQuarto.clientpanes;

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

public class PaneServerConnect extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField tfAddress;
    private JTextField tfPasword;
    
    JLabel lblStatus = new JLabel("Waiting for input...");
    
    public void setStatus(String s) {
        lblStatus.setText(s);
        lblStatus.repaint();
    }
    
    public String getEnteredPassword() {
        return tfPasword.getText();
    }

    public PaneServerConnect(BiConsumer<String, Boolean> connectHandler) {
        setLayout(new MigLayout("", "[208.00][8.00][grow]", "[][][][]"));
        
        JLabel lblConnectToServer = new JLabel("Connect to Server:");
        lblConnectToServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(lblConnectToServer, "cell 0 0");
        
        JLabel lblAddress = new JLabel("Address");
        add(lblAddress, "flowx,cell 0 1,alignx left");
        
        JLabel lblServerPassword = new JLabel("Server Password");
        add(lblServerPassword, "flowx,cell 2 1,alignx left");
        
        tfAddress = new JTextField();
        add(tfAddress, "cell 0 1,growx");
        tfAddress.setColumns(10);
        
        tfPasword = new JPasswordField();
        add(tfPasword, "cell 2 1,growx");
        tfPasword.setColumns(10);
        
        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setStatus("Connecting...");
                connectHandler.accept(tfAddress.getText(), true);
            }
        });
        add(btnConnect, "cell 0 2,growx");
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(ABORT);
            }
        });
        add(btnCancel, "cell 2 2,alignx right");
        
        add(lblStatus, "cell 0 3 3 1");        
    }
}
