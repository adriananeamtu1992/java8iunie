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
public class ConsoleLogger implements Logger {

    @Override
    public void message(Throwable t) {
        System.out.println(t);
    }

    @Override
    public void message(String s) {
        System.out.println(s);
    }

    @Override
    public void message(String s, Throwable t) {
        System.out.println(s);
        System.out.println(t);
    }

    @Override
    public void setOutPut(Object o) {
        System.out.println("output redirect to System.out");
    }

    @Override
    public void addEventListener(ErrorThrownActionListener errorThrownActionListener) {
        System.out.println("output redirect to System.out");
    }

    @Override
    public void smartMessage(String s) {
        System.out.println(s);
    }

}
