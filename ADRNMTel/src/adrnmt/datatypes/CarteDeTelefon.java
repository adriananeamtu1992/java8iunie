/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.datatypes;

import adrnmt.logger.Logger;
import adrnmt.logger.Loggers;
import adrnmt.settings.Settings;

import javax.swing.table.DefaultTableModel;
import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class CarteDeTelefonSerializable implements Serializable {
    private List<Abonat> listaDeAbonati;

    public CarteDeTelefonSerializable(CarteDeTelefon carteDeTelefon) {
        this.listaDeAbonati = carteDeTelefon.getListaDeAbonati();
    }

    public List<Abonat> getListaDeAbonati() {
        return listaDeAbonati;
    }
}

/**
 *
 * @author Adriana
 */
public class CarteDeTelefon extends DefaultTableModel {

    private static final Logger LOGGER = Loggers.getDefaultLogger();
    private List<Abonat> listaDeAbonati = new ArrayList<>();
    private Comparator<Abonat> comparatorDupaCnp;
    private Comparator<Abonat> comparatorDupaNumarFix;
    private Comparator<Abonat> comparatorDupaNumarMobil;
    private Comparator<Abonat> comparatorDupaDataNasterii;

    public CarteDeTelefon(Abonat... lista) {
        for (Abonat abonat : lista) {
            listaDeAbonati.add(abonat);
        }
        createComparatorInstances();
    }

    public CarteDeTelefon(List<Abonat> listaDeAbonati) {
        this.listaDeAbonati = listaDeAbonati;
        createComparatorInstances();
    }

    public List<Abonat> getListaDeAbonati() {
        return listaDeAbonati;
    }

    public synchronized void setListaDeAbonati(List<Abonat> listaDeAbonati) {
        this.listaDeAbonati = listaDeAbonati;
        fireTableDataChanged();
    }

    public synchronized void update(Abonat a, int index) {
        try {
            listaDeAbonati.set(index, a);
            fireTableDataChanged();
        } catch (Exception ex) {
            LOGGER.message("update failed at " + index, ex);
        }
    }

    public synchronized void update(int index, String nume, String cnp, String nrfix, String nrmobil, String dataNasterii) {
        try {
            listaDeAbonati.get(index).setNume(nume);
            listaDeAbonati.get(index).setPrenume(cnp);
            listaDeAbonati.get(index).setNumarFix(nrfix);
            listaDeAbonati.get(index).setNumarMobil(nrmobil);
//            listaDeAbonati.get(index).setEmail(email);
//            listaDeAbonati.get(index).setData(email);
        } catch (InvalidDataTypeError ex) {
            LOGGER.message(ex);
        }

        catch (NumberFormatException ex) {
            LOGGER.message("failed parsing cnp index " + index, ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.message("failed at index " + index, ex);
        }
    }

    public synchronized void add(Abonat a) {
        listaDeAbonati.add(a);
        fireTableDataChanged();
    }

    public Abonat get(int index) {
        try {
            return listaDeAbonati.get(index);
        } catch (Exception ex) {
            LOGGER.message(ex);
        }
        return null;
    }

    public boolean hasIndex(int index) {
        return listaDeAbonati.indexOf(index) > -1;
    }

    @Override
    public synchronized void removeRow(int index) {
        listaDeAbonati.remove(index);
        fireTableDataChanged();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        for (int i = 0; i < listaDeAbonati.size(); i++) {
            builder.append(listaDeAbonati.get(i).toString());
            if (i < listaDeAbonati.size() - 1) {
                builder.append(" , ");
            }
        }
        builder.append(" ]");
        return builder.toString();
    }

    private void createComparatorInstances() {
        comparatorDupaCnp = (Abonat t, Abonat t1) -> t.getPrenume().compareTo(t1.getPrenume());
        comparatorDupaNumarFix = new  Comparator<Abonat>() {
            @Override
            public int compare(Abonat o1, Abonat o2) {
                if (o1.getNumarFix() != null && o2.getNumarFix() != null) {
                    return o1.getNumarFix().compareTo(o2.getNumarFix());
                }
                return 0;
            }
        };
        
        comparatorDupaNumarMobil = new Comparator<Abonat>() {
            @Override
            public int compare(Abonat o1, Abonat o2) {
                if (o1.getNumarMobil()!= null && o2.getNumarMobil()!= null) {
                    return o1.getNumarMobil().compareTo(o2.getNumarMobil());
                }
                return 0;
            }
        };
        comparatorDupaDataNasterii = (Abonat o1, Abonat o2) -> o1.getDataNasterii().compareTo(o2.getDataNasterii());
    }

//    private boolean stringIsSet(String s) {
//        return (s != null);
//    }

    /**
     * implementeaza un algoritm simplu de comparatie bazat pe wildcard
     *
     * @param toSearch
     * @param realData
     * @return
     */
    public boolean wildcardCompare(String toSearch, String realData) {
        if(toSearch == null || realData == null){
            return false;
        }
        String input = toSearch;
        if (input.length() >= 3 && input.charAt(0) == '*' && input.charAt(input.length() - 1) == '*') {
                input = input.substring(1);
                input = input.substring(0, input.length() - 1);
                return realData.contains(input) && (!realData.endsWith(input) && !realData.startsWith(input));
        }

        if (input.length() >= 2) {
            if (input.charAt(0) == '*') {
                return realData.endsWith(input.substring(1));
            }
            else if (input.charAt(input.length() - 1) == '*') {
                return realData.startsWith(input.substring(0, input.length() - 1));
            }
        }

        return input.equals(realData);
    }

    public synchronized CarteDeTelefon search(String nume, String cnp, String nrTelefon, String dataNasteriiStr) {
        List<Abonat> response = new ArrayList<>();

        for (Abonat abonat : listaDeAbonati) {
            if (nume != null && wildcardCompare(nume, abonat.getNume())) {
                response.add(abonat);
            }
            else if (cnp != null && wildcardCompare(cnp, abonat.getPrenume())) {
                response.add(abonat);
            }
            else if (nrTelefon != null && wildcardCompare(nrTelefon, abonat.getNumarFix())) {
                response.add(abonat);
            }
            else if (nrTelefon != null && wildcardCompare(nrTelefon, abonat.getNumarMobil())) {
                response.add(abonat);
            }
            else if (dataNasteriiStr != null && wildcardCompare(dataNasteriiStr, abonat.getDataNasterii().toString())) {
                response.add(abonat);
            }
        }
        return new CarteDeTelefon(response);
    }
    

    public synchronized CarteDeTelefon cautaNrFix(String input) {
        List<Abonat> response = new ArrayList<>();
        for (Abonat abonat : listaDeAbonati) {
            if (wildcardCompare(input, abonat.getNumarFix())) {
                response.add(abonat);
            }    
        }
        return new CarteDeTelefon(response);
    }
    
    
    
    public synchronized CarteDeTelefon cautaNrMobil(String input) {
        List<Abonat> response = new ArrayList<>();
        for (Abonat abonat : listaDeAbonati) {
            if (wildcardCompare(input, abonat.getNumarMobil())) {
                response.add(abonat);
            }    
        }
        return new CarteDeTelefon(response);
    }
    
    
    public CarteDeTelefon auNrMobil() {
        List<Abonat> response = listaDeAbonati.stream()
                .filter((Abonat t) -> t.getNumarMobil() != null).collect(Collectors.toList());
        return new CarteDeTelefon(response);
    }

    public CarteDeTelefon auNrFix() {
         List<Abonat> response = listaDeAbonati.stream()
                .filter((Abonat t) -> t.getNumarFix()!= null).collect(Collectors.toList());
        return new CarteDeTelefon(response);
    }


    
    public synchronized CarteDeTelefon searchLunaCurenta() {
        List<Abonat> response = new ArrayList<>();
        Calendar acum = Calendar.getInstance();
        for(Abonat abonat: listaDeAbonati){
            Calendar cal = Calendar.getInstance();
            cal.setTime(abonat.getDataNasterii());
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            if(month == acum.get(Calendar.MONTH) && day > acum.get(Calendar.DAY_OF_MONTH)){
                response.add(abonat);
            }
        }
        return new CarteDeTelefon(response);
    }

    public CarteDeTelefon searchNascuteAzi() {
         List<Abonat> response = listaDeAbonati.stream()
                .filter((Abonat t) -> t.eNascutAzi()).collect(Collectors.toList());
        return new CarteDeTelefon(response);
    }
    


    public void sortByIndex(int index) {
        switch (index) {
        case 1:
            Collections.sort(listaDeAbonati);
            break;
        case 2:
            Collections.sort(listaDeAbonati, comparatorDupaCnp);
            break;
        case 3:
            Collections.sort(listaDeAbonati, comparatorDupaNumarFix);
            break;
        case 4:
            Collections.sort(listaDeAbonati, comparatorDupaNumarMobil);
            break;
        case 5:
            Collections.sort(listaDeAbonati, comparatorDupaDataNasterii);
            break;
        }

        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return Settings.getInstance().getBundle("table.index");
        case 1:
            return Settings.getInstance().getBundle("table.subscriber.name");
        case 2:
            return Settings.getInstance().getBundle("table.subscriber.cnp");
        case 3:
            return Settings.getInstance().getBundle("table.subscriber.phone.1");
        case 4:
            return Settings.getInstance().getBundle("table.subscriber.phone.2");
        case 5:
            return "Data Nasterii";
        }
        return "data" + column;
    }

    @Override
    public boolean isCellEditable(int row, int column) { // custom isCellEditable function
        if (column > 0) {
            return true;
        }
        return false;
    }

    @Override
    public int getRowCount() {
        if (listaDeAbonati == null) {
            return 0;
        }
        return listaDeAbonati.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return (rowIndex + 1) + "";
        }
        Abonat currentAbonat = listaDeAbonati.get(rowIndex);

        if (currentAbonat != null) {
            return currentAbonat.getByIndex(columnIndex);
        }

        return null;
    }

    @Override
    public void setValueAt(Object o, int row, int columnIndex) {
        try {
            listaDeAbonati.get(row).setByIndex(columnIndex, o);
            Loggers.getDefaultLogger().message("setValueAt " + columnIndex + "__" + o.toString());
        } catch (InvalidDataTypeError ex) {
            LOGGER.message(ex);
        }
    }

    public boolean contineDeja(Abonat currrentAbonat) {
        for(Abonat abonat: listaDeAbonati){
            if (currrentAbonat.equals(abonat)) {
                return true;
            }
        }
        return false;
    }



}
