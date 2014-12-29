package myQuarto.clientpanes;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.function.Function;

public class PaneNameRequest extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    JLabel lblStatusLabel = new JLabel("Waiting for input...");

    public PaneNameRequest(Function<String, String> check, Function<String, String> confirm) {
        setLayout(new MigLayout("", "[24px,grow]", "[34px,grow]"));
        
        JLabel lblChooseANickname = new JLabel("Choose a nickname:");
        add(lblChooseANickname, "flowy,cell 0 0,alignx center");
        
        textField = new JTextField();
        add(textField, "cell 0 0,alignx center");
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
        
        JLabel lblNewLabel = new JLabel("Waiting for input...");
        add(lblNewLabel, "cell 0 0,alignx center");
        add(btnNewButton, "cell 0 0,alignx center");
    }

    public void setStatus(String text) {
        lblStatusLabel.setText(text);
        label.repaint();
    }
}
