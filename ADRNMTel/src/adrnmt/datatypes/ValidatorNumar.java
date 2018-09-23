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
public interface ValidatorNumar {
        boolean valideaza(String value);
        
        ValidatorNumar FIX = new NrFix();
        ValidatorNumar MOBIL = new NrMobil();
        
        
    public static class NrFix implements ValidatorNumar {
        private NrFix(){
        
        }
        @Override
        public boolean valideaza(String value) {
            return value != null && value.length() == 10 && ( value.startsWith("02") || value.startsWith("03"));
        }
    
    }
    public static class NrMobil implements ValidatorNumar{
        private NrMobil(){}
        
        @Override
        public boolean valideaza(String value) {
            return  value != null && value.length() == 10 && value.startsWith("07");
        }
    }
}

