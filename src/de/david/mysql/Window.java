package de.david.mysql;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Window extends JFrame {

    JTextArea inputArea = new JTextArea(6, 50);
    JTextArea outputArea = new JTextArea(6, 50);

    JButton encryptButton = new JButton("encrypt");
    JButton decryptButton = new JButton("decrypt");

    JButton copyInputButton = new JButton("copy");
    JButton copyOutputButton = new JButton("copy");

    JButton clearInputButton = new JButton("clear");
    JButton clearOutputButton = new JButton("clear");

    JCheckBox useEncryptionMethodCheck = new JCheckBox("allow use of encryption method");

    JButton crackButton = new JButton("crack input");
    JButton moveOutputButton = new JButton("output -> input");

    Encrypter encrypter = new Encrypter();
    EncryptionCracker encryptionCracker = new EncryptionCracker();

    public Window() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Asymetric Encrypter/Decrpyter/Cracker");

        JPanel panel = new JPanel();

        encryptButton.addActionListener( e -> onEncryptButton() );
        decryptButton.addActionListener( e -> onDecryptButton() );
        crackButton.addActionListener( e -> onCrackButton() );
        copyInputButton.addActionListener( e -> onCopyInputButton() );
        copyOutputButton.addActionListener( e -> onCopyOutputButton() );
        clearInputButton.addActionListener( e -> inputArea.setText("") );
        clearOutputButton.addActionListener( e -> outputArea.setText("") );
        moveOutputButton.addActionListener( e -> onMoveOutputButton() );

        panel.add(new JLabel("input: "));
        panel.add(inputArea);
        panel.add(encryptButton);
        panel.add(copyInputButton);
        panel.add(clearInputButton);
        panel.add(new JLabel("output:"));
        panel.add(outputArea);
        panel.add(decryptButton);
        panel.add(copyOutputButton);
        panel.add(clearOutputButton);
        panel.add(useEncryptionMethodCheck);
        panel.add(crackButton);
        panel.add(moveOutputButton);
        add(panel);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(832, 292);
        setVisible(true);
    }

    void onEncryptButton() {
        outputArea.setText(encrypter.encryptSimpleAsymetric(inputArea.getText()));
    }

    void onDecryptButton() {
        outputArea.setText(encrypter.decryptSimpleAsymetric(inputArea.getText()));
    }

    void onCrackButton() {
        new Thread(() -> {
            if (useEncryptionMethodCheck.isSelected()) {
                outputArea.setText(encryptionCracker.crackSimpleAsymetricEncryption(encrypter, inputArea.getText()));
            }
            else {
                outputArea.setText(formatCrackedStrings(encryptionCracker.crackSimpleAsymetricEncryptionWithoutEncrypter(inputArea.getText())));
            }
        }).start();
    }

    void onMoveOutputButton() {
        inputArea.setText(outputArea.getText());
        outputArea.setText("");
    }

    private String formatCrackedStrings(String[] strings) {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            result += "solution "+(i+1)+": "+strings[i]+System.lineSeparator();
        }
        return result;
    }

    void onCopyInputButton() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(inputArea.getText()), null);
    }

    void onCopyOutputButton() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(outputArea.getText()), null);
    }

}
