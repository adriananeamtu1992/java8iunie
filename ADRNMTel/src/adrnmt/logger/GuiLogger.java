/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.swing.*;

import adrnmt.datatypes.InvalidDataTypeError;

/**
 *
 * @author Adriana
 */
public class GuiLogger implements Logger {

    private JTextArea textArea = null;
    private StringBuffer stringBuilder = new StringBuffer();
    private boolean oldDataDisplayed = false;
    private ErrorThrownActionListener errorThrownActionListener;
    private int maxWait = (int) Math.round(Math.random() * 5) + 5;
    private int numWait = 0;

    private void digestError(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        digestString(sw.toString());

        if (t instanceof SQLException) {
            if (errorThrownActionListener != null) {
                errorThrownActionListener.onDisplayableError("SQLException", t.getMessage());
            }
        }

        if (t instanceof InvalidDataTypeError) {
            if (errorThrownActionListener != null) {
                errorThrownActionListener.onDisplayableError(((InvalidDataTypeError) t).getTitle(), t.getMessage());
            }
        }
    }

    @Override
    public void message(Throwable t) {
        digestError(t);
        if (errorThrownActionListener != null) {
            errorThrownActionListener.onErrorThrown(t.getMessage());
        }
    }

    @Override
    public void message(String s) {
        digestString(s);
    }

    @Override
    public void message(String s, Throwable t) {
        digestString(s);
        digestError(t);
        if (errorThrownActionListener != null) {
            errorThrownActionListener.onErrorThrown(t.getMessage());
        }
    }

    @Override
    public void setOutPut(Object o) {
        textArea = (JTextArea) o;
    }

    /**
     * daca exista un outputObject afiseaza in el, altfel appenduieste la un StringBuilder pana in
     * momentul in care exista un outputObject
     * muta caret-ul obiectului textarea in josul paginii daca root pane-ul este vizibil    
     * @param s
     */
    private void digestString(String s) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        SimpleDateFormat fmt = new SimpleDateFormat("[yyyy-MMM-dd HH:mm:ss:SSSS]");
        String dateFormatted = fmt.format(gregorianCalendar.getTime());
        System.out.println(dateFormatted + " : " + s);

        if (textArea != null) {
            if (!oldDataDisplayed) {
                textArea.append(stringBuilder.toString());
                oldDataDisplayed = true;
            }
            textArea.append(dateFormatted);
            textArea.append(" : ");
            textArea.append(s);
            textArea.append("\n");
            if(textArea.getRootPane().isVisible()){
              textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
        else {
            stringBuilder.append(dateFormatted);
            stringBuilder.append(" : ");
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
    }

    @Override
    public void addEventListener(ErrorThrownActionListener errorThrownActionListener) {
        this.errorThrownActionListener = errorThrownActionListener;
    }

    @Override
    public void smartMessage(String s) {
        numWait++;
        if (numWait < maxWait) {
            return;
        }
        int n = (int) Math.round(Math.random() * 437) + 5;
        int rand = (int) Math.round(Math.random() * 5) + 1;
        if (n % rand == 0) {
            digestString(s);
            numWait = 0;
            maxWait = (int) Math.round(Math.random() * 5) + 5;
        }
    }

}
