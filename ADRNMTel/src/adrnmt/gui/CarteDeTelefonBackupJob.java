/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.gui;

import adrnmt.datatypes.AppUtils;
import adrnmt.datatypes.CarteDeTelefon;
import adrnmt.logger.Loggers;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Alexandru
 */
public class CarteDeTelefonBackupJob {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private MainWindow windowInstance;
    
    
    /**
     * Initiaza threadul de salvare periodica a ultimului fiser salvat/deschis
     * @param windowInstance 
     */
    public CarteDeTelefonBackupJob(MainWindow windowInstance){
        this.windowInstance = windowInstance;
        initTimer();
        System.out.println(new Date() + " Backup job initiat");
    }
    
    
    private void initTimer(){
        
   
        
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if(windowInstance != null) {
                    CarteDeTelefon carteDeTelefon = windowInstance.getCarteDeTelefon();
                    String lastSavedFile = windowInstance.getLastCarteDeTelefonFile();
                    System.out.println(new Date() + " backup job incepe cu file " + lastSavedFile);
                    if(carteDeTelefon != null && lastSavedFile != null) {
                       AppUtils.saveToDisk(lastSavedFile, carteDeTelefon);
                       System.out.println(new Date() +  " backup job saved file " + lastSavedFile);
                    }
                    else {
                        System.out.println(new Date() + " backup job has nothing to do ...");
                    }
                }
            }
        }, 0L, 1L, TimeUnit.MINUTES);
       
        
        
    }
}
