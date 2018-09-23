/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.datatypes;

/**
 *
 * @author Adriana
 */
public class InvalidDataTypeError extends Exception {

    private String title;
    private String message;

    public InvalidDataTypeError(String title, String message){
       super(message);
       this.title = title;
       this.message = message;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getMessage(){
        return message;
    }

}
