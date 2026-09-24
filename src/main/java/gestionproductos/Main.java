package gestionproductos;

import gestionproductos.vista.Pantalla_principal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Pantalla_principal ventana = new Pantalla_principal();
            ventana.setVisible(true);
        });
    }
}