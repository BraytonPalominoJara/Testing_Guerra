package Prueba_1;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class HistorialVentasFrame extends JFrame {

    private JTable tablaFacturas;
    private JButton btnMenu;
    private JTable tablaDetalles;

    public HistorialVentasFrame() {
        setTitle("Historial de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        tablaFacturas = new JTable();
        JScrollPane scrollPaneFacturas = new JScrollPane(tablaFacturas);

        tablaDetalles = new JTable();
        JScrollPane scrollPaneDetalles = new JScrollPane(tablaDetalles);

        btnMenu = new JButton("Menú");
        btnMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                volverAlMenu();
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnMenu);

        add(scrollPaneFacturas, BorderLayout.WEST);
        add(scrollPaneDetalles, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);


        tablaFacturas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    // Obtener el ID de la factura seleccionada
                    int selectedRow = tablaFacturas.getSelectedRow();
                    int idFactura = (int) tablaFacturas.getValueAt(selectedRow, 0);


                    cargarDetallesFacturaDesdeBD(idFactura);
                }
            }
        });


        cargarFacturasDesdeBD();
    }

    private void cargarFacturasDesdeBD() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Factura");
        model.addColumn("Fecha");
        model.addColumn("Total Gastado");

        Connection conexion = null;

        try {
 
            Class.forName("com.mysql.cj.jdbc.Driver");


            String URL = "jdbc:mysql://localhost:3306/prueba";
            String USUARIO = "root";
            String CONTRASENA = "";
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);


            String sql = "SELECT f.id_factura, f.fecha, SUM(df.total) AS total_gastado " +
                         "FROM facturas f " +
                         "JOIN detalle_factura df ON f.id_factura = df.id_factura " +
                         "GROUP BY f.id_factura, f.fecha";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {


                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id_factura"));
                    row.add(rs.getString("fecha"));
                    row.add(rs.getDouble("total_gastado"));
                    model.addRow(row);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {

            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        tablaFacturas.setModel(model);
    }

    private void cargarDetallesFacturaDesdeBD(int idFactura) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nombre Producto");
        model.addColumn("Cantidad Vendida");
        model.addColumn("Precio Unitario");
        model.addColumn("Precio Total");

        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");


            String URL = "jdbc:mysql://localhost:3306/prueba";
            String USUARIO = "root";
            String CONTRASENA = "admin";
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);


            String sql = "SELECT inventario.nombre AS nombre_producto, " +
                         "detalle_factura.cantidad AS cantidad_vendida, " +
                         "inventario.precio AS precio_unitario, " +
                         "detalle_factura.total AS precio_total " +
                         "FROM detalle_factura " +
                         "JOIN inventario ON detalle_factura.id_producto = inventario.id_producto " +
                         "WHERE detalle_factura.id_factura = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idFactura);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Recorrer los resultados y agregarlos al modelo de la tabla
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getString("nombre_producto"));
                        row.add(rs.getInt("cantidad_vendida"));
                        row.add(rs.getDouble("precio_unitario"));
                        row.add(rs.getDouble("precio_total"));
                        model.addRow(row);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        tablaDetalles.setModel(model);
    }

    private void volverAlMenu() {
        Menu menu = new Menu();
        menu.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HistorialVentasFrame historialVentasFrame = new HistorialVentasFrame();
            historialVentasFrame.setVisible(true);
        });
    }
}


