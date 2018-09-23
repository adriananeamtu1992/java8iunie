/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.gui;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Adriana
 */
public class SplashScreen extends JFrame {

    private JLabel photoLabel;

    public SplashScreen() {
        initComponents();
    }

    private void initComponents() {

        photoLabel = new javax.swing.JLabel();

        setAlwaysOnTop(true);
        setBounds(new java.awt.Rectangle(0, 0, 600, 250));
        setMaximumSize(new java.awt.Dimension(600, 250));
        setMinimumSize(new java.awt.Dimension(600, 250));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(600, 250));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout());
//        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.3f));
        setLocationRelativeTo(null);

        photoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        photoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/adrnmt/resources/images/yellow-pages.jpg"))); // NOI18N
        photoLabel.setText("adrnmt 0.1 Adriana Stefan Neamtu");
        photoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        photoLabel.setIconTextGap(15);
        photoLabel.setMaximumSize(new java.awt.Dimension(600, 200));
        photoLabel.setMinimumSize(new java.awt.Dimension(600, 200));
        photoLabel.setPreferredSize(new java.awt.Dimension(600, 200));
        photoLabel.setSize(new java.awt.Dimension(600, 200));
        photoLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        photoLabel.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        photoLabel.setOpaque(true);
        getContentPane().add(photoLabel);

    }
}
