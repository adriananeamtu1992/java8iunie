/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.datatypes;

import adrnmt.logger.Loggers;
import adrnmt.settings.Settings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adriana
 */
public class Abonat implements Serializable, Comparable<Abonat> {

    private static int pid = 5;
    private String prenume;
    private String nume;
    private String numarFix;
    private String numarMobil;
    private Date dataNasterii;



    private Abonat(String nume, String prenume, String numarFix, Date dataNasterii, String nrMobil) throws InvalidDataTypeError {
        this.nume = nume;
        validatePrenume(prenume);
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.numarFix = numarFix;
        this.numarMobil = nrMobil;
    }

    public static Abonat create(String nume, String cnp, String telefon, String data) throws InvalidDataTypeError {
                                Abonat abonat = new Abonat(nume, cnp, null,
                    Abonat.fromString(data), null);
                                abonat.setUnNumarTel(telefon);

                                if (abonat.getUnNumarTel() == null) {
            throw new InvalidDataTypeError("INVALID NR TEL", "Invalid nr tel la editare");
        }
                                return abonat;
    }

    public static Date fromString(String input) throws InvalidDataTypeError{
        Date date = null;
        try {
            date = new SimpleDateFormat(Settings.DATA_NASTERII_FORMAT).parse(input);
            return date;
        } catch(Exception ex){
            throw new InvalidDataTypeError("Invalid data format", "Formatul datei este invalid");
        }

    }

    public static String dateToString(Date date){
         return new SimpleDateFormat(Settings.DATA_NASTERII_FORMAT).format(date);
    }

    public static Abonat create(String nume, String cnpStr, String unNrTel, String dataNasteriiStr, boolean eNrMobil) throws InvalidDataTypeError {

        Abonat abonat = new Abonat(nume, cnpStr, null, fromString(dataNasteriiStr), null);
        if(eNrMobil){
            Loggers.getDefaultLogger().message("seteaza numar mobil " + unNrTel);
            abonat.setNumarMobil(unNrTel);
        } else {
            Loggers.getDefaultLogger().message("seteaza numar fix " + unNrTel);
            abonat.setNumarFix(unNrTel);
        }
        return abonat;
    }

public static Abonat createRandomAbonat() {
        Abonat response = null;
        try {
            pid++;
            String[] allowedChars = { "a", "b", "c", "d", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "s", "t", "u",
                "v", "z" };

            StringBuilder numeBuffer = new StringBuilder();
            int rand1 = (int) Math.round(Math.random() * 10) + 5;
            int rand2 = (int) Math.round(Math.random() * (allowedChars.length - 1));

            numeBuffer.append(allowedChars[rand2].toUpperCase());
            for (int i = 0; i < rand1; i++) {
                rand2 = (int) Math.round(Math.random() * (allowedChars.length - 1));

                numeBuffer.append(allowedChars[rand2]);
            }

            String nume = numeBuffer.toString();
            StringBuilder cnpRand = new StringBuilder();
            for(int i = 0; i < 13; i++){
                cnpRand.append(Math.round(Math.random() * 9));
            }
            StringBuilder nrFixRand = new StringBuilder().append("02");
             StringBuilder nrMobRand = new StringBuilder().append("07");

            for(int i = 0; i < 8; i++){
               nrFixRand.append(Math.round(Math.random() * 9));
               nrMobRand.append(Math.round(Math.random() * 9));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.roll(Calendar.DAY_OF_MONTH, (int) (Math.round(Math.random() * 9) * -1));
            calendar.roll(Calendar.YEAR, (int) (Math.round(Math.random() * 9) * -1));

            response = new Abonat(nume, cnpRand.toString(), nrFixRand.toString(), calendar.getTime(), nrMobRand.toString());
            return response;
        } catch (InvalidDataTypeError ex) {
            Logger.getLogger(Abonat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Date getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(Date dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) throws InvalidDataTypeError {
        validatePrenume(prenume);
        this.prenume = prenume;
    }
    
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getNumarFix() {
        return numarFix;
    }
    
    public void setNumarFix(String numarFix) {
        this.numarFix = numarFix;
    }
    
    public String getNumarMobil() {
        return numarMobil;
    }

    public void setNumarMobil(String numarMobil) {
        this.numarMobil = numarMobil;
    }

    /**
     * Valideaza codul numeric personal: not null, not empty, lungime 13 caractere, regex numeric
     *
     * @param cnp
     * @throws InvalidDataTypeError
     */
    private void validatePrenume(String cnp) throws InvalidDataTypeError {
        if (cnp == null || cnp.isEmpty() || cnp.length() < 2) {
            throw new InvalidDataTypeError(Settings.getInstance().getBundle("error.abonat.instance"), Settings
                    .getInstance().getBundle("error.abonat.invalid.prenume"));
        }
    }

    public Object getByIndex(int index) {
        switch (index) {
        case 1:
            return nume;
        case 2:
            return prenume;
        case 3:
            return numarFix != null ? numarFix : "---";
        case 4:
            return numarMobil != null ? numarMobil : "---";
        case 5:
            return dateToString(dataNasterii);
        }
        return null;
    }

        public void setByIndex(int index, Object value) throws InvalidDataTypeError {
        switch (index) {
        case 1:
            setNume(value.toString());
            break;
        case 2:
            setPrenume(value.toString());
            break;
        case 3:
            numarFix = String.valueOf(value);
            break;
        case 4:
            numarMobil = String.valueOf(value);
            break;
        case 5:
            dataNasterii = Abonat.fromString(String.valueOf(value));
            break;
        }
    };

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ nume: ");
        builder.append(nume);
        builder.append(", cnp: ");
        builder.append(prenume);
        builder.append(", numarFix: ");
        builder.append(numarFix.toString());
        builder.append(", numarMobil: ");
        builder.append(numarMobil.toString());
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Abonat){
            Abonat ala = (Abonat)o;

            return String.valueOf(nume).equalsIgnoreCase(ala.getNume())
                    && String.valueOf(numarFix).equalsIgnoreCase(String.valueOf(ala.getNumarFix()))
                    && String.valueOf(numarFix).equalsIgnoreCase(String.valueOf(ala.getNumarFix()))
                    && String.valueOf(prenume).equalsIgnoreCase(String.valueOf(ala.getPrenume()))
                    && dataNasterii.getTime() == ala.getDataNasterii().getTime()
                    ;
        }
        return equals(o);
       
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.prenume);
        hash = 47 * hash + Objects.hashCode(this.nume);
        hash = 47 * hash + Objects.hashCode(this.numarFix);
        hash = 47 * hash + Objects.hashCode(this.numarMobil);
        hash = 47 * hash + Objects.hashCode(this.dataNasterii);
        return hash;
    }

    @Override
    public int compareTo(Abonat o) {
        return this.getNume().compareTo(o.getNume());
    }

    public String getUnNumarTel() {
        return numarFix != null ? numarFix : numarMobil;
    }
    
    public void setUnNumarTel(String input){
        if(ValidatorNumar.FIX.valideaza(input)){
            numarFix = input;
        }else if(ValidatorNumar.MOBIL.valideaza(input)){
            numarMobil = input;
        }
    }

    public boolean eNascutAzi() {
        if(dataNasterii == null){
            return false;
        }
        Calendar acum = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataNasterii);
        int zi = cal.get(Calendar.DAY_OF_MONTH);
        return zi == acum.get(Calendar.DAY_OF_MONTH);
    }
        
    

}
