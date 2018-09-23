/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.logger;

/**
 * Interfata unui obiect logger
 * @author Adriana
 */
public interface Logger {
    void message(Throwable t);
    void message(String s);
    void smartMessage(String s);
    void message(String s, Throwable t);
    void setOutPut(Object o);
    void addEventListener(ErrorThrownActionListener errorThrownActionListener);
}
