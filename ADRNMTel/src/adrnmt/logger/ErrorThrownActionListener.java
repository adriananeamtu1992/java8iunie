/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.logger;

/**
 *
 * @author Adriana
 */
public interface ErrorThrownActionListener {
    void onErrorThrown(String message);
    void onDisplayableError(String title, String message);
}
