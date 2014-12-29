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
import java.util.function.Consumer;

public class PaneConnecting extends JPanel {
    private static final long serialVersionUID = 1L;
    
    JLabel label = new JLabel("...");
    
    public void setStatus(String s) {
        label.setText(s);
        label.repaint();
    }

    public PaneConnecting() {
        setLayout(new MigLayout("", "[24px,grow]", "[34px,grow]"));
        label.setFont(new Font("Tahoma", Font.ITALIC, 28));
        add(label, "cell 0 0,alignx center,aligny center");
    }
}
