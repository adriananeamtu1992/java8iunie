/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.util.Timer;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import adrnmt.datatypes.Abonat;
import adrnmt.datatypes.AppUtils;
import adrnmt.datatypes.CarteDeTelefon;
import adrnmt.datatypes.CriteriuFiltrare;
import adrnmt.datatypes.CriteriuDeOrdonare;
import adrnmt.logger.ErrorThrownActionListener;
import adrnmt.logger.Logger;
import adrnmt.logger.Loggers;
import adrnmt.settings.Settings;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
/**
 *
 * @author Adriana
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    private CarteDeTelefon carteDeTelefon;
    private static final Logger LOGGER = Loggers.getDefaultLogger();
    private final Settings settings;
    private SelectMaxRand selectMaxRand;
    private EditSubscriberWindow editSubscriberWindow;    
    private String lastCarteDeTelefonFile = null;

    private boolean allowKeyboardEvent = false;
    
    private Timer animationTimer = null;
    private TimerTask animationTimerTask = null;
    private int animationPid = 0;
    private List<String> animationFiles = new ArrayList<String>();
    
    private CriteriuFiltrare criteriuFiltrare;
    private CriteriuDeOrdonare criteriuDeOrdonare;
    private static final String PAROLA = "adrn";

    public CarteDeTelefon getCarteDeTelefon() {
        return carteDeTelefon;
    }

    public String getLastCarteDeTelefonFile() {
        return lastCarteDeTelefonFile;
    }
    
    public MainWindow() {
        animationFiles.add("01.jpg");
        animationFiles.add("02.jpg");
        animationFiles.add("03.jpg");
        animationFiles.add("04.jpg");
        animationFiles.add("05.jpg");
            
        setEnabled(false);
        settings = Settings.getInstance();
        LOGGER.message("starting app, CarteDeTelefon instance");
        String lused = settings.getLastUsedFile();
        
        if(lused!=null){      
           carteDeTelefon = AppUtils.loadFromDisk(lused);
           if(carteDeTelefon == null) {
               JOptionPane.showMessageDialog(this, "Corrupted file " + lused);
               carteDeTelefon = new CarteDeTelefon();
               lused = settings.getBundle("temporary.file");
           }
           else {
               lastCarteDeTelefonFile = lused;
               lused = new File(lused).getName();
           }
        }
        else {
            carteDeTelefon = new CarteDeTelefon();
            lused = "nothing to save...";
        }
        
       
        initComponents();
        buttonsContainer.setVisible(false);
        
        
        filterTextInput.disable();
        filterTextInput.setEditable(false);
        filterByComboBox.disable();
        
        criteriuDeOrdonare = CriteriuDeOrdonare.valueOf(String.valueOf(orderByComboBox.getSelectedItem()));
        criteriuFiltrare = CriteriuFiltrare.valueOf(String.valueOf(filterByComboBox.getSelectedItem()));
        
            
        mainTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = mainTable.columnAtPoint(e.getPoint());
                String name = mainTable.getColumnName(col);
                LOGGER.message("Column index selected " + col + " " + name);
                if(mainTable.getModel() instanceof CarteDeTelefon){
                    ((CarteDeTelefon)mainTable.getModel()).sortByIndex(col);
                }
            }
        });
            
        String[] languages = Settings.getAvailableLanguages();
        
        for(int i = 0; i < languages.length; i++){
            JMenuItem jMenuItem = new JMenuItem();
            jMenuItem.setText(languages[i]);
            jMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String cdata = (((JMenuItem)e.getSource()).getText());
                    try {
                        settings.setCurrentLocale(cdata);
                        JOptionPane.showMessageDialog (null, 
                            settings.getBundle("app.restart.required"),
                            settings.getBundle("gui.warning"), 
                            JOptionPane.WARNING_MESSAGE);
                        
                    } catch(ArrayIndexOutOfBoundsException ex){
                        LOGGER.message(ex);
                    }
                }
            });
          
        }
        
        statLabel.setText(lused);

       
        
        
        
        
        LOGGER.message("init components finished");
        setLocationRelativeTo(null);
        selectMaxRand = new SelectMaxRand(this, true);
        selectMaxRand.setLocationRelativeTo(this);
        editSubscriberWindow = new EditSubscriberWindow(this, true);
        editSubscriberWindow.setLocationRelativeTo(this);

        
        editSubscriberWindow.addEventListener(new CustomGuiEvent() {
            @Override
            public void actionPerformed(Object data) {
                try {
                    Abonat currrentAbonat = (Abonat) data;
                    if(carteDeTelefon.contineDeja(currrentAbonat)){
                        JOptionPane.showMessageDialog(editSubscriberWindow, 
                                settings.getBundle("contact.duplicat")
                                , "erroare", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    AppUtils.Add(carteDeTelefon, currrentAbonat);
                    refreshTable(carteDeTelefon);
                    System.out.println("ADAUGAT");
                }catch(ClassCastException ex){
                    LOGGER.message(" FAILED CAST ", ex);
                }
            }
        });
        
        editSubscriberWindow.addEditEventListener((Abonat a, int index) -> {
            AppUtils.Edit(carteDeTelefon, index, a);
        });
         

       
        SearchSubscriberEvent searchSubscribeEvent = new SearchSubscriberEvent(){
            @Override
            public void subscriberSearched(String name, String cnp, String telefon, String data) {
                lastCarteDeTelefonFile = settings.getTempDbBackup();
                statLabel.setText(settings.getBundle("mod.cautare"));
                CarteDeTelefon result = carteDeTelefon.search(name, cnp, telefon, data);
                refreshTable(result);
                settings.setLastUsedFile(lastCarteDeTelefonFile);
            }
        };

        editSubscriberWindow.addSearchEventListener(searchSubscribeEvent);
        
        selectMaxRand.addEventListener((Object data) -> {
            try {
                carteDeTelefon = AppUtils.createRandomCarte(Integer.parseInt(data.toString()));
                refreshTable(carteDeTelefon);
                menuSaveActionPerformed(null);
            }catch(Exception ex){
                LOGGER.message(ex);
            }
        });
       
        LOGGER.addEventListener(new ErrorThrownActionListener() {
            @Override
            public void onErrorThrown(String message) {
                Color oldColor = viewLogsBtn.getBackground();
                viewLogsBtn.setBackground(Color.red);
                
                javax.swing.Timer timer = new javax.swing.Timer(2000, (ActionEvent e) -> {
                    viewLogsBtn.setBackground(oldColor);
                });
                timer.start();
            }

            @Override
            public void onDisplayableError(String title, String message) {
               JOptionPane.showMessageDialog (null, 
                            message,
                            title, 
                            JOptionPane.ERROR_MESSAGE);
            }
        });
        
        

        
        
        loggerWindow.setLocationRelativeTo(this);
        LOGGER.setOutPut(logTextArea);
        LOGGER.message("components loading finished, logger has jTextArea");  
        
        mainTable.addMouseListener(new MouseAdapter() {
        @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if(column == 0){
                    if (e.getClickCount() == 2) {
                        editSubscriberWindow.editSubscriber(carteDeTelefon.get(row));
                        allowKeyboardEvent = false;
                    } else {
                        allowKeyboardEvent = true;
                    }
                }
                else {
                    allowKeyboardEvent = false;
                }
            }
        });
        refreshTable(carteDeTelefon);
        loggerWindow.pack();
        
        if(!settings.isAppRegistered()){
            appDisabled();
        } else {
            appEnabled();
        }
        
        if(lastCarteDeTelefonFile!= null && lastCarteDeTelefonFile.equals(settings.getTempDbBackup())){
            statLabel.setText(settings.getBundle("temporary.file"));
        }
    }
    
    
    private void appDisabled(){
        menuOpen.setEnabled(false);
        menuSave.setEnabled(false);
        createRandomBtn.setEnabled(false);
        menuRegister.setEnabled(true);
        

        
        animationTimerTask = new TimerTask() {

            @Override
            public void run() {
               animationPid++;
               
               if(animationPid > animationFiles.size() - 1){
                   animationPid = 0;
                   Collections.shuffle(animationFiles);
                   LOGGER.message(" SHUFFLE animationFiles");
               }
               
               URL imagePath = MainWindow.class.getResource("/adrnmt/resources/images/" + animationFiles.get(animationPid));
               LOGGER.message(" set commercial image: " + imagePath.getPath());
               commercial.setIcon(new ImageIcon(imagePath));
            }
        };
        
        
        animationTimer = new java.util.Timer();
        animationTimer.scheduleAtFixedRate(animationTimerTask, 1, 1 * 1000);
    }
    
    
    private void appEnabled() {
        menuOpen.setEnabled(true);
        menuSave.setEnabled(true);
        createRandomBtn.setEnabled(true);
        menuRegister.setEnabled(false);
        this.getContentPane().remove(commercial);
        this.pack();
        
        if (animationTimerTask != null){
            animationTimerTask.cancel();
            animationTimerTask = null;
        }
        
        if(animationTimer != null){
            
            animationTimer.cancel();
            animationTimer = null;
        }
    }
    
    
    
    private void refreshTable(CarteDeTelefon model){
        int column0width = 35;
        mainTable.setModel(model); 
        mainTable.getColumnModel().getColumn(0).setWidth(column0width);
        mainTable.getColumnModel().getColumn(0).setPreferredWidth(column0width);
        mainTable.getColumnModel().getColumn(0).setMinWidth(column0width);
        mainTable.getColumnModel().getColumn(0).setMaxWidth(column0width);
        mainTable.getColumnModel().getColumn(0).setResizable(false);
        
        mainTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            JLabel lbl = new JLabel();
            Color selectedColor = null;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(isSelected){
                    if(selectedColor == null){
                        selectedColor = lbl.getBackground();
                    }
                    lbl.setBackground(selectedColor);
                }else {
                   lbl.setBackground(new Color(240, 255, 255));
                }
                return lbl;
            }
        });
        

        

//        JOptionPane.showConfirmDialog(this, String.valueOf("aaa1".matches("[a-zA-Z]*")));
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

        loggerWindow = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        scrollPaneTable = new javax.swing.JScrollPane();
        mainTable = new javax.swing.JTable();
        buttonsContainer = new javax.swing.JPanel();
        createRandomBtn = new javax.swing.JButton();
        viewLogsBtn = new javax.swing.JButton();
        statLabel = new javax.swing.JLabel();
        reincarcaBTN = new javax.swing.JButton();
        commercial = new javax.swing.JLabel();
        topPanel = new javax.swing.JPanel();
        jtopfilterPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        filterByComboBox = new javax.swing.JComboBox<>();
        filterTextInput = new javax.swing.JTextField();
        filterTogl = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        orderByComboBox = new javax.swing.JComboBox<>();
        orderBtn = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuOpen = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuAbonati = new javax.swing.JMenu();
        menuAdauga = new javax.swing.JMenuItem();
        menuCauta = new javax.swing.JMenuItem();
        menuSterge = new javax.swing.JMenuItem();
        menuModifica = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuRegister = new javax.swing.JMenuItem();
        menuSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuAbout = new javax.swing.JMenuItem();

        loggerWindow.setMinimumSize(new java.awt.Dimension(500, 500));
        loggerWindow.getContentPane().setLayout(new java.awt.GridLayout(1, 0, 4, 4));

        logTextArea.setBackground(new java.awt.Color(204, 204, 204));
        logTextArea.setColumns(20);
        logTextArea.setRows(2);
        jScrollPane2.setViewportView(logTextArea);

        loggerWindow.getContentPane().add(jScrollPane2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        mainTable.setModel(carteDeTelefon);
        mainTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keytPressed(evt);
            }
        });
        scrollPaneTable.setViewportView(mainTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(scrollPaneTable, gridBagConstraints);

        buttonsContainer.setLayout(new java.awt.GridLayout(1, 0));

        createRandomBtn.setText(settings.getBundle("gui.random.data"));
        createRandomBtn.setToolTipText("create random data");
        createRandomBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRandomBtnActionPerformed(evt);
            }
        });
        buttonsContainer.add(createRandomBtn);

        viewLogsBtn.setText(settings.getBundle("gui.view.logs"));
        viewLogsBtn.setToolTipText("view internal application logs and errors");
        viewLogsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLogsBtnActionPerformed(evt);
            }
        });
        buttonsContainer.add(viewLogsBtn);

        statLabel.setBackground(new java.awt.Color(153, 153, 255));
        statLabel.setLabelFor(viewLogsBtn);
        statLabel.setToolTipText("current file saved");
        buttonsContainer.add(statLabel);

        reincarcaBTN.setText(settings.getBundle("gui.reincarca")
        );
        reincarcaBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reincarcaBTNActionPerformed(evt);
            }
        });
        buttonsContainer.add(reincarcaBTN);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(buttonsContainer, gridBagConstraints);

        commercial.setBackground(new java.awt.Color(0, 0, 204));
        commercial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adrnmt/resources/images/03.jpg"))); // NOI18N
        commercial.setText(settings.getBundle("register.message"));
        commercial.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        commercial.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(commercial, gridBagConstraints);

        topPanel.setBackground(new java.awt.Color(255, 255, 204));
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));

        jtopfilterPanel.setBackground(new java.awt.Color(204, 204, 255));
        jtopfilterPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(settings.getBundle("gui.filtrare")
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jtopfilterPanel.add(jLabel1, gridBagConstraints);

        filterByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CONTACT_NR_FIX", "CONTACT_NR_MOBIL", "CONTACTE_NASCUTE_AZI", "CONTACTE_LUNA_CURENTA", "PERSONALIZAT" }));
        filterByComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterByComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jtopfilterPanel.add(filterByComboBox, gridBagConstraints);

        filterTextInput.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                filterTextInputCaretUpdate(evt);
            }
        });
        filterTextInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jtopfilterPanel.add(filterTextInput, gridBagConstraints);

        filterTogl.setText(settings.getBundle("gui.mod.filtrare"));
        filterTogl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterToglActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jtopfilterPanel.add(filterTogl, gridBagConstraints);

        topPanel.add(jtopfilterPanel);

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        jLabel2.setText(settings.getBundle("gui.ordonare")
        );
        jPanel3.add(jLabel2);

        orderByComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NUME", "PRENUME", "TELEFON_FIX", "TELEFON_MOBIL", "DATA_NASTERII" }));
        orderByComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderByComboBoxActionPerformed(evt);
            }
        });
        jPanel3.add(orderByComboBox);

        orderBtn.setText(settings.getBundle("gui.ordoneaza")
        );
        orderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderBtnActionPerformed(evt);
            }
        });
        jPanel3.add(orderBtn);

        topPanel.add(jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(topPanel, gridBagConstraints);

        menuFile.setText(settings.getBundle("gui.file"));

        menuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuOpen.setMnemonic(KeyEvent.VK_O);
        menuOpen.setText(settings.getBundle("gui.open"));
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuOpen);

        menuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuSave.setMnemonic(KeyEvent.VK_S);
        menuSave.setText(settings.getBundle("gui.save"));
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuSave);
        menuFile.add(menuSeparator1);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuExit.setMnemonic(KeyEvent.VK_W);
        menuExit.setText(settings.getBundle("gui.exit"));
        menuExit.setToolTipText("quit application");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        menuBar.add(menuFile);

        menuAbonati.setText(settings.getBundle("gui.subscribers"));
        menuAbonati.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbonatiActionPerformed(evt);
            }
        });

        menuAdauga.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuAdauga.setText(settings.getBundle("gui.add"));
        menuAdauga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAdaugaActionPerformed(evt);
            }
        });
        menuAbonati.add(menuAdauga);

        menuCauta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuCauta.setText(settings.getBundle("gui.search"));
        menuCauta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCautaActionPerformed(evt);
            }
        });
        menuAbonati.add(menuCauta);

        menuSterge.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuSterge.setText(settings.getBundle("gui.delete"));
        menuSterge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStergeActionPerformed(evt);
            }
        });
        menuAbonati.add(menuSterge);

        menuModifica.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuModifica.setText(settings.getBundle("gui.modify"));
        menuModifica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuModificaActionPerformed(evt);
            }
        });
        menuAbonati.add(menuModifica);

        menuBar.add(menuAbonati);

        menuHelp.setText(settings.getBundle("gui.help"));
        menuHelp.setToolTipText("help");

        menuRegister.setText(settings.getBundle("gui.register"));
        menuRegister.setToolTipText(settings.getBundle("gui.register"));
        menuRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRegisterActionPerformed(evt);
            }
        });
        menuHelp.add(menuRegister);
        menuHelp.add(menuSeparator2);

        menuAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuAbout.setText(settings.getBundle("gui.about"));
        menuAbout.setToolTipText(settings.getBundle("gui.about"));
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewLogsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLogsBtnActionPerformed
       loggerWindow.setVisible(true);
       logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }//GEN-LAST:event_viewLogsBtnActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        int reply = JOptionPane.showConfirmDialog(this, settings.getBundle("gui.confirma.iesirea"), "Confirm exit", JOptionPane.YES_NO_OPTION);
        
        if(reply == JOptionPane.YES_OPTION){
            System.exit(0);
        }

    }//GEN-LAST:event_menuExitActionPerformed

    private void createRandomBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRandomBtnActionPerformed
          selectMaxRand.setVisible(true);
    }//GEN-LAST:event_createRandomBtnActionPerformed

    private void menuAdaugaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAdaugaActionPerformed
       editSubscriberWindow.addSubscriber();
    }//GEN-LAST:event_menuAdaugaActionPerformed

    private void menuModificaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuModificaActionPerformed
      editSubscriberWindow.editSubscriber(carteDeTelefon, mainTable.getSelectedRow());
    }//GEN-LAST:event_menuModificaActionPerformed

    
    private CarteDeTelefonBackupJob carteDeTelefonBackupJob = null;
    /**
     * 
     * @param evt 
     */
    private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
       JFileChooser chooser = Settings.FILECHOOSER;
       chooser.setCurrentDirectory(new File(settings.getLastFileChooserPath()));
       int retrival = chooser.showSaveDialog(this);
       if (retrival == JFileChooser.APPROVE_OPTION) {
           settings.setLastFileChooserPath(chooser.getSelectedFile().getParent());
           String fname = null;
           if(chooser.getSelectedFile().exists()){
               int reply = JOptionPane.showConfirmDialog(this, "Ovveride " + chooser.getSelectedFile().getName() + " ?",
                  Settings.getInstance().getBundle("gui.warning"), JOptionPane.YES_NO_OPTION);
         
                if(reply == JOptionPane.YES_OPTION){
                    fname = chooser.getSelectedFile().getAbsolutePath();
                    if(AppUtils.saveToDisk(fname, carteDeTelefon)){
                        lastCarteDeTelefonFile = fname;
                        statLabel.setText(chooser.getSelectedFile().getName());
                        settings.setLastUsedFile(lastCarteDeTelefonFile);
                   }
                } 
           } else {
               fname = chooser.getSelectedFile().getAbsolutePath() + ".adrnmt";
               if(AppUtils.saveToDisk(fname, carteDeTelefon)){
                    lastCarteDeTelefonFile = fname;
                    statLabel.setText(chooser.getSelectedFile().getName());
                    settings.setLastUsedFile(lastCarteDeTelefonFile);
               }
           }
           
           if(carteDeTelefonBackupJob == null){
               carteDeTelefonBackupJob = new CarteDeTelefonBackupJob(WINDOW_INSTANCE);
           }
           
          
       }
    }//GEN-LAST:event_menuSaveActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        JFileChooser chooser = Settings.FILECHOOSER;
        chooser.setCurrentDirectory(new File(settings.getLastFileChooserPath()));
        chooser.setAcceptAllFileFilterUsed(false);
        FileFilter filter = new FileNameExtensionFilter("adrnmt File","adrnmt");
        chooser.addChoosableFileFilter(filter);
         int retrival = chooser.showOpenDialog(this);
           if (retrival == JFileChooser.APPROVE_OPTION) {
                settings.setLastFileChooserPath(chooser.getSelectedFile().getParent());
                carteDeTelefon = AppUtils.loadFromDisk(chooser.getSelectedFile().getAbsolutePath());
               
                if(carteDeTelefon == null){
                   JOptionPane.showMessageDialog (this, 
                            "File might be corrupted",
                            Settings.getInstance().getBundle("gui.warning"), 
                            JOptionPane.ERROR_MESSAGE);
                   LOGGER.message("Corrupted file error");
                   return;
                }
                lastCarteDeTelefonFile = chooser.getSelectedFile().getAbsolutePath();
                refreshTable(carteDeTelefon);
                
                StringBuilder sb = new StringBuilder();
                for(Abonat abonat: carteDeTelefon.getListaDeAbonati()){
                    if (abonat.eNascutAzi()) {
                        sb.append("> " + abonat.getNume() + "\n");
                    }
                }
                
                if(!sb.toString().isEmpty()){
                    JOptionPane.showMessageDialog(this, "LA MULTI ANI!\n" + sb.toString());
                }
                
                statLabel.setText(chooser.getSelectedFile().getName());
                settings.setLastUsedFile(lastCarteDeTelefonFile);
           }
    }//GEN-LAST:event_menuOpenActionPerformed

    private void menuCautaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCautaActionPerformed
        editSubscriberWindow.searchSubscriber();
    }//GEN-LAST:event_menuCautaActionPerformed

    private void menuStergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuStergeActionPerformed

        int[] indeces = mainTable.getSelectedRows();
        int rowCount = mainTable.getRowCount();
        
        if(rowCount < 0 || indeces.length == 0) {
            JOptionPane.showMessageDialog (this, 
                            settings.getBundle("gui.selectRow"),
                            settings.getBundle("gui.warning"), 
                            JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setEnabled(false);
        int reply = JOptionPane.showConfirmDialog(null, "About to delete " + indeces.length + " rows",
                  Settings.getInstance().getBundle("gui.warning"), JOptionPane.YES_NO_OPTION);
         
         if(reply == JOptionPane.YES_OPTION){
             AppUtils.Delete(carteDeTelefon, indeces, rowCount);
         }       
         setEnabled(true);
    }//GEN-LAST:event_menuStergeActionPerformed

    private void menuRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRegisterActionPerformed
        String result = JOptionPane.showInputDialog(this, "Enter registration code:");
        if(PAROLA.equals(result)){
            settings.setAppRegistered(true);
            appEnabled();
            
        }
    }//GEN-LAST:event_menuRegisterActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
       JOptionPane.showMessageDialog(this, "Aplicatie Adriana Stefan Neamtu"
               + "\n - Java 8 iunie - "
               + "\n - codul de inregistare: adrn"
               + "\n - fisierele se salveaza automat cu extensia .adrnmt"
               + "\n - filtrul de deschidere fisiere este setat pentru fisiere de tip .adrnmt"
               + "\n - cautarea PERSONALIZATA suporta wildcard-uri de forma [*cuvant | *cuvant* | cuvant* | cuvant]"
               + "\n - ordonarea se face si dand click pe capetele de tabel"
               + "\n - editarea tabelei se face pe loc, eventurile de keyboard (Del & Enter) si dublu click pentru editare"
               + "\n   sunt active numai cand cursorul se afla pe coloana de index, si numai pentru un singur row"
               + "\n - multiple delete este posibil numai din meniu, dupa selectarea row-urilor"
             );
    }//GEN-LAST:event_menuAboutActionPerformed

    private void keytPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keytPressed
        int keyCode = evt.getExtendedKeyCode();
        if(keyCode == 10 && allowKeyboardEvent){
            menuModificaActionPerformed(null);
            return;
        }
        if(keyCode == 127 && allowKeyboardEvent){
            int rowIndex = mainTable.getSelectedRow();
            if(rowIndex > -1) {
                 int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + carteDeTelefon.get(rowIndex).getNume(),
                 Settings.getInstance().getBundle("gui.warning"), JOptionPane.YES_NO_OPTION);
                 if(reply == JOptionPane.YES_OPTION) {
                     AppUtils.Delete(carteDeTelefon, rowIndex);
                 }
            }
        }
    }//GEN-LAST:event_keytPressed

    private void orderByComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderByComboBoxActionPerformed

        JComboBox jComboBox = (JComboBox) evt.getSource();
        
         criteriuDeOrdonare = CriteriuDeOrdonare.valueOf(String.valueOf(jComboBox.getSelectedItem()));
         
         Loggers.getDefaultLogger().message("criteriuDeOrdonare: " + criteriuDeOrdonare);
    }//GEN-LAST:event_orderByComboBoxActionPerformed

    private void filterByComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByComboBoxActionPerformed

        JComboBox jComboBox = (JComboBox) evt.getSource();
        criteriuFiltrare = CriteriuFiltrare.valueOf(String.valueOf(jComboBox.getSelectedItem()));
        Loggers.getDefaultLogger().message("criteriuFiltrare: " + criteriuFiltrare);
        CarteDeTelefon result = null;
        filterTextInput.disable();
        filterTextInput.setEditable(false);
        if(null != criteriuFiltrare)switch (criteriuFiltrare) {
            case CONTACTE_LUNA_CURENTA:
                result = carteDeTelefon.searchLunaCurenta();
                refreshTable(result);
                break;
            case CONTACTE_NASCUTE_AZI:
                result = carteDeTelefon.searchNascuteAzi();
                refreshTable(result);
                break;
            case CONTACT_NR_FIX:
                result = carteDeTelefon.auNrFix();
                refreshTable(result);
                break;
            case CONTACT_NR_MOBIL:
                result = carteDeTelefon.auNrMobil();
                refreshTable(result);
                break;
            default:
                filterTextInput.enable();
                filterTextInput.setEditable(true);
                break;
        }
    }//GEN-LAST:event_filterByComboBoxActionPerformed

    private void orderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderBtnActionPerformed

        carteDeTelefon.sortByIndex(criteriuDeOrdonare.getIndex());
    }//GEN-LAST:event_orderBtnActionPerformed

    private void filterTextInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTextInputActionPerformed
   
    }//GEN-LAST:event_filterTextInputActionPerformed

    private void menuAbonatiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbonatiActionPerformed
        // TODO add your handling code here:
        
        refreshTable(carteDeTelefon);
    }//GEN-LAST:event_menuAbonatiActionPerformed

    private void reincarcaBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reincarcaBTNActionPerformed
        // TODO add your handling code here:
        refreshTable(carteDeTelefon);
    }//GEN-LAST:event_reincarcaBTNActionPerformed

    private void filterToglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterToglActionPerformed
        JToggleButton jToggleButton = (JToggleButton) evt.getSource();
        if(jToggleButton.isSelected()){
            filterByComboBox.enable();
            jToggleButton.setText(settings.getBundle("gui.mod.filtrare.elimina"));
            
            
             jtopfilterPanel.setBackground(new Color(204, 255, 204));
        } else {
            filterTextInput.disable();
            filterTextInput.setEditable(false);
            filterByComboBox.disable();
            refreshTable(carteDeTelefon);
            jToggleButton.setText(settings.getBundle("gui.mod.filtrare"));
            jtopfilterPanel.setBackground(new Color(204, 204, 255));
        }
        criteriuFiltrare = CriteriuFiltrare.valueOf(String.valueOf(filterByComboBox.getSelectedItem()));
        if(CriteriuFiltrare.PERSONALIZAT.equals(criteriuFiltrare)){
            filterTextInput.setEditable(false);
        }

    }//GEN-LAST:event_filterToglActionPerformed

    private void filterTextInputCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_filterTextInputCaretUpdate
        // TODO add your handling code here:
        criteriuFiltrare = CriteriuFiltrare.valueOf(String.valueOf(filterByComboBox.getSelectedItem()));
        JTextField textField = (JTextField) evt.getSource();
        String text = textField.getText();
        System.out.println(text);
        CarteDeTelefon localcarte = this.carteDeTelefon.search(text, text, text, text);
        refreshTable(localcarte);
    }//GEN-LAST:event_filterTextInputCaretUpdate

    
    public static final MainWindow WINDOW_INSTANCE = new MainWindow();

   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {        
        Logger logger = Loggers.getDefaultLogger();
        
        Thread mainWindowThread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.message("main window thread started");
                MainWindow.WINDOW_INSTANCE.setVisible(true);
            }
        });
        
        
        Thread splashScreenThread = new Thread(new Runnable() {

            @Override
            public void run() {
                SplashScreen splashScreen = new SplashScreen();
                splashScreen.setVisible(true);
                splashScreen.pack();
                logger.message("splash screen window set visible");

                try {
                    mainWindowThread.join();
                } catch (InterruptedException ex) {
                    LOGGER.message(ex);
                }
                logger.message("mainWindowThread finished...");
                
                java.util.Timer t = new java.util.Timer();
                t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        splashScreen.setVisible(false);
                        logger.message("splash screen hidden, exiting thread");
                        MainWindow.WINDOW_INSTANCE.setEnabled(true);
                    }
                   
                }, 1 * 1000);
                
            }
        });
        
       mainWindowThread.start();
       splashScreenThread.start();
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsContainer;
    private javax.swing.JLabel commercial;
    private javax.swing.JButton createRandomBtn;
    private javax.swing.JComboBox<String> filterByComboBox;
    private javax.swing.JTextField filterTextInput;
    private javax.swing.JToggleButton filterTogl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jtopfilterPanel;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JFrame loggerWindow;
    private javax.swing.JTable mainTable;
    private javax.swing.JMenu menuAbonati;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuItem menuAdauga;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuCauta;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuModifica;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuRegister;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JPopupMenu.Separator menuSeparator1;
    private javax.swing.JPopupMenu.Separator menuSeparator2;
    private javax.swing.JMenuItem menuSterge;
    private javax.swing.JButton orderBtn;
    private javax.swing.JComboBox<String> orderByComboBox;
    private javax.swing.JButton reincarcaBTN;
    private javax.swing.JScrollPane scrollPaneTable;
    private javax.swing.JLabel statLabel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JButton viewLogsBtn;
    // End of variables declaration//GEN-END:variables
}
