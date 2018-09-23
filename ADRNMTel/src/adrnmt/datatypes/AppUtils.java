/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adrnmt.datatypes;

import adrnmt.logger.Loggers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa cu metode statice sincronizate: salvare pe disc, deschidere fisier
 * Majoritatea metodelor folosesc o instanta de tipul CarteDeTelefon, iar metodele pe care le
 * apeleaza sunt la randul lor sincronizate, astfel incat daca din mainWindow este apelata o metoda
 * de tipul update/delete/modify asupra instantei pe care AppUtils o foloseste in acelasi timp
 * sa nu existe probleme de sincronizare
 *
 * @author Adriana
 */
public class AppUtils {

    public static synchronized boolean saveToDisk(String filePath, CarteDeTelefon carte) {
        try {
            FileOutputStream f_out = new FileOutputStream(filePath);
            ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject(new CarteDeTelefonSerializable(carte));
            return true;
        } catch (FileNotFoundException ex) {
            Loggers.getDefaultLogger().message("failed save ", ex);
        } catch (IOException ex) {
            Loggers.getDefaultLogger().message("failed save ", ex);
        }
        return false;
    }

    public static synchronized CarteDeTelefon loadFromDisk(String filePath) {
        try {
            CarteDeTelefonSerializable response;
            try (FileInputStream f_in = new FileInputStream(filePath);
                    ObjectInputStream obj_in = new ObjectInputStream(f_in)) {
                response = (CarteDeTelefonSerializable) obj_in.readObject();
            }
            Loggers.getDefaultLogger().message("loading book " + filePath);
            return new CarteDeTelefon(response.getListaDeAbonati());
        } catch (IOException | ClassNotFoundException ex) {
            Loggers.getDefaultLogger().message(ex);
        }
        return null;
    }

    public static synchronized void Add(CarteDeTelefon carteDeTelefon, Abonat abonat) {
        carteDeTelefon.add(abonat);
    }

    public static synchronized CarteDeTelefon createRandomCarte(int maxNr) {
        CarteDeTelefon carteDeTelefon = new CarteDeTelefon();
        for (int i = 0; i < maxNr; i++) {
            carteDeTelefon.add(Abonat.createRandomAbonat());
        }
        Loggers.getDefaultLogger().message("random book created");
        return carteDeTelefon;
    }

    public static synchronized void Edit(CarteDeTelefon carteDeTelefon, int index, Abonat abonat) {
        carteDeTelefon.update(abonat, index);
    }

    public static synchronized void Update(CarteDeTelefon carteDeTelefon, List<Abonat> listaNoua) {
        carteDeTelefon.setListaDeAbonati(listaNoua);
    }

    public static synchronized void Delete(CarteDeTelefon carteDeTelefon, int index) {
        carteDeTelefon.removeRow(index);
    }

    /**
     * Pentru a separa procesarea listei de update-ul ui-ului delete-ul multiplu se face prin
     * crearea unei noi liste excluzand row-urile selectate
     *
     * @param carteDeTelefon
     * @param indeces
     * @param rowCount
     */
    public static synchronized void Delete(CarteDeTelefon carteDeTelefon, int[] indeces, int rowCount) {
        List<Abonat> abonatiAcum = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            if (i < indeces[0] || i > indeces[indeces.length - 1]) {
                abonatiAcum.add(carteDeTelefon.get(i));
            }
        }

        carteDeTelefon.setListaDeAbonati(abonatiAcum);
    }


    public static String random(int length) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int n = (int) Math.round(Math.random() * 9);
            stringBuffer.append(String.valueOf(n));
        }
        return (stringBuffer.toString());
    };

}
