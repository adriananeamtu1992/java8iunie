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
public enum CriteriuDeOrdonare {
    NUME(1),
    PRENUME(2),
    TELEFON_FIX(3),
    TELEFON_MOBIL(4),
    DATA_NASTERII(5);
    private final int index;

    CriteriuDeOrdonare(int index){
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
}
