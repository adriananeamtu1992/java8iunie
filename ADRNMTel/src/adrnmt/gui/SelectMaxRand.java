/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.gui;

/**
 *
 * @author Adriana
 */
public class SelectMaxRand extends javax.swing.JDialog {

    private CustomGuiEvent customGuiEvent = null;
    /**
     * Creates new form SelectMaxRand
     */
    public SelectMaxRand(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    
    public void addEventListener(CustomGuiEvent customGuiEvent){
        this.customGuiEvent = customGuiEvent;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jbtnAccept = new javax.swing.JButton();
        jSpinner = new javax.swing.JSpinner();
        jLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 153, 153));
        setBounds(new java.awt.Rectangle(0, 23, 200, 130));
        setMinimumSize(new java.awt.Dimension(200, 130));
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jbtnAccept.setText(adrnmt.settings.Settings.getInstance().getBundle("gui.ok"));
        jbtnAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAcceptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        getContentPane().add(jbtnAccept, gridBagConstraints);

        jSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 2, 50, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 20);
        getContentPane().add(jSpinner, gridBagConstraints);

        jLabel.setText(adrnmt.settings.Settings.getInstance().getBundle("gui.select.max.rand"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        getContentPane().add(jLabel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAcceptActionPerformed
        if(customGuiEvent != null){
            customGuiEvent.actionPerformed(jSpinner.getValue());
        }
        this.setVisible(false);
    }//GEN-LAST:event_jbtnAcceptActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel;
    private javax.swing.JSpinner jSpinner;
    private javax.swing.JButton jbtnAccept;
    // End of variables declaration//GEN-END:variables
}