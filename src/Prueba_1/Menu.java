package Prueba_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Menu extends BaseFrame {

    public Menu() {
        super("Menú Principal");

        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

      
        JButton inventarioButton = crearBoton("Inventario", "Inventario");
        JButton facturaButton = crearBoton("Factura", "Factura");
        JButton historialVentasButton = crearBoton("Historial de Ventas", "HistorialVentas");
        JButton salirButton = crearBoton("Salir", "Salir");

      
        panel.add(inventarioButton);
        panel.add(facturaButton);
        panel.add(historialVentasButton);
        panel.add(salirButton);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Inventario")) {
            abrirInventario();
        } else if (e.getActionCommand().equals("Factura")) {
            abrirFactura();
        } else if (e.getActionCommand().equals("HistorialVentas")) {
            abrirHistorialVentas();
        } else if (e.getActionCommand().equals("Salir")) {
            despedidaUsuario();
        }
    }

    private void abrirInventario() {
        InventarioFrame inventarioFrame = new InventarioFrame("admin");
        inventarioFrame.setVisible(true);
        dispose();
    }

    private void abrirFactura() {

        FacturacionFrame facturaFrame = new FacturacionFrame("admin");
        facturaFrame.setVisible(true);
        dispose();
    }

    private void abrirHistorialVentas() {

        HistorialVentasFrame historialVentasFrame = new HistorialVentasFrame();
        historialVentasFrame.setVisible(true);
        dispose();
    }

    private void cerrarPrograma() {
        System.exit(0);
    }

    private void despedidaUsuario() {
        JOptionPane.showMessageDialog(this, "Gracias por utilizar nuestro sistema, ¡Hasta luego!");
        cerrarPrograma();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            menu.setVisible(true);
        });
    }
}

