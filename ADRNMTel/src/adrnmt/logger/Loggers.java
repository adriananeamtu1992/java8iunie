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
public class Loggers {
    private static final ConsoleLogger CONSOLE_LOGGER = new ConsoleLogger();
    private static final GuiLogger GUI_LOGGER = new GuiLogger();
    public static Logger getDefaultLogger() {
        return (Logger) GUI_LOGGER;
    }
}
