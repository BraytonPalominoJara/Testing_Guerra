package Prueba_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FacturacionFrame extends JFrame {

    private JComboBox<String> productoComboBox;
    private JButton agregarProductoButton;
    private JButton finalizarFacturaButton;
    private JButton regresarMenuButton;
    private JList<String> productosAgregadosList;

    private DefaultListModel<String> productosListModel;
    private Map<String, Integer> productosSeleccionados;
    private double totalFactura;

    private Connection conexion;
    private final String URL = "jdbc:mysql://localhost:3306/prueba";
    private final String USUARIO = "root";
    private final String CONTRASENA = "";

    private int obtenerIdProducto(String nombreProducto) {
        String sql = "SELECT id_producto FROM inventario WHERE nombre = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_producto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; 
    }

    public FacturacionFrame(String usuario) {
        productosSeleccionados = new HashMap<>();
        totalFactura = 0.0;

       
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        productoComboBox = new JComboBox<>();
        agregarProductosAlComboBox();

        agregarProductoButton = new JButton("Agregar Producto");
        finalizarFacturaButton = new JButton("Finalizar Factura");
        regresarMenuButton = new JButton("Regresar al Menú");

        agregarProductoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });

        finalizarFacturaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalizarFactura();
            }
        });

        regresarMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regresarAlMenu();
            }
        });

        productosListModel = new DefaultListModel<>();
        productosAgregadosList = new JList<>(productosListModel);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelSuperior.add(productoComboBox);
        panelSuperior.add(agregarProductoButton);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(new JScrollPane(productosAgregadosList), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelInferior.add(finalizarFacturaButton);
        panelInferior.add(regresarMenuButton);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);

        setTitle("Facturación - Usuario: " + usuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void agregarProductosAlComboBox() {
        String sql = "SELECT nombre FROM inventario";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombreProducto = rs.getString("nombre");
                productoComboBox.addItem(nombreProducto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarProducto() {
        String nombreProducto = (String) productoComboBox.getSelectedItem();
        if (nombreProducto != null) {

            double precioUnitario = obtenerPrecioProducto(nombreProducto);

            if (productosSeleccionados.containsKey(nombreProducto)) {

                int cantidadActual = productosSeleccionados.get(nombreProducto);
                productosSeleccionados.put(nombreProducto, cantidadActual + 1);
            } else {

                productosSeleccionados.put(nombreProducto, 1);
            }

            totalFactura += precioUnitario;

            actualizarListaProductosAgregados();

            JOptionPane.showMessageDialog(this, "Producto agregado: " + nombreProducto);
        }
    }

    private void finalizarFactura() {

        if (productosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos seleccionados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = sdf.format(new java.util.Date());

        int idFactura = insertarFacturaEnBD(fecha);

        for (Map.Entry<String, Integer> entry : productosSeleccionados.entrySet()) {
            String nombreProducto = entry.getKey();
            int cantidad = entry.getValue();
            double precioUnitario = obtenerPrecioProducto(nombreProducto);
            double totalProducto = cantidad * precioUnitario;

            insertarDetalleFacturaEnBD(idFactura, nombreProducto, cantidad, totalProducto);
        }

        JOptionPane.showMessageDialog(this, "Total de la Factura: $" + formatoDecimal(totalFactura));

        productosSeleccionados.clear();
        totalFactura = 0.0;
        productosListModel.clear();
    }

    private void regresarAlMenu() {
        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            menu.setVisible(true);
            dispose();
        });
    }

    private void actualizarListaProductosAgregados() {
        productosListModel.clear();

        for (Map.Entry<String, Integer> entry : productosSeleccionados.entrySet()) {
            String nombreProducto = entry.getKey();
            int cantidad = entry.getValue();
            productosListModel.addElement(nombreProducto + " - Cantidad: " + cantidad);
        }
    }

    private int insertarFacturaEnBD(String fecha) {
        if (conexion != null) {
            String query = "INSERT INTO facturas (fecha) VALUES (?)";

            try (PreparedStatement pstmt = conexion.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, fecha);

                int filasAfectadas = pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    private void insertarDetalleFacturaEnBD(int idFactura, String nombreProducto, int cantidad, double total) {
        if (conexion != null) {
            int idProducto = obtenerIdProducto(nombreProducto);

            String query = "INSERT INTO detalle_factura (id_factura, id_producto, cantidad, total) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
                pstmt.setInt(1, idFactura);
                pstmt.setInt(2, idProducto);
                pstmt.setInt(3, cantidad);
                pstmt.setDouble(4, total);

                int filasAfectadas = pstmt.executeUpdate();
                System.out.println("Envio exitoso");

            } catch (SQLException e) {
                e.printStackTrace();
            }
           
        }
    }

    private int obtenerProximoIdFactura() {

        int ultimoId = 0;

        String query = "SELECT MAX(id_factura) AS ultimo_id FROM facturas";

        try (PreparedStatement pstmt = conexion.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ultimoId = rs.getInt("ultimo_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ultimoId + 1;
    }
    private double obtenerPrecioProducto(String nombreProducto) {
        String sql = "SELECT precio FROM inventario WHERE nombre = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("precio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    private String formatoDecimal(double valor) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(valor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FacturacionFrame facturaFrame = new FacturacionFrame("admin");
            facturaFrame.setVisible(true);
        });
    }
}




