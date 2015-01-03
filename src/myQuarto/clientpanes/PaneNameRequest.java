package myQuarto.clientpanes;

import javax.swing.JPanel;
import javax.swing.JTextField;

import myQuarto.netprot.QuartoPacket;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaneNameRequest extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    JLabel lblStatusLabel = new JLabel("Waiting for input...");

    public PaneNameRequest(Function<String, String> check, Function<String, String> confirm) {
        setLayout(new MigLayout("", "[24px,grow]", "[][34px,grow][][]"));
        
        JLabel lblChooseANickname = new JLabel("Choose a nickname:");
        add(lblChooseANickname, "cell 0 0,alignx center");
        
        textField = new JTextField();
        add(textField, "flowy,cell 0 1,alignx center");
        textField.setColumns(25);
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                lblStatusLabel.setText(check.apply(textField.getText()));
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                lblStatusLabel.setText(check.apply(textField.getText()));
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                lblStatusLabel.setText(check.apply(textField.getText()));
            }
        });
        
        JButton btnNewButton = new JButton("Register nickname");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lblStatusLabel.setText(confirm.apply(textField.getText()));
            }
        });
        add(btnNewButton, "cell 0 2,alignx center");
        
        lblStatusLabel = new JLabel("Waiting for input...");
        add(lblStatusLabel, "cell 0 3,alignx center");
    }

    public void setStatus(String text) {
        lblStatusLabel.setText(text);
        lblStatusLabel.repaint();
        validate();
        
        Logger.getGlobal().log(Level.INFO, "DEBUG");
    }
}
