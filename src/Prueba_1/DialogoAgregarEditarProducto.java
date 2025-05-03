package Prueba_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DialogoAgregarEditarProducto extends JDialog implements ActionListener {

    private JTextField nombreField;
    private JTextField precioField;
    private JTextField cantidadField;
    private JButton guardarButton;

    private Producto producto;

    public DialogoAgregarEditarProducto(JFrame parent, boolean modal, Producto producto) {
        super(parent, modal);
        this.producto = producto;

        setTitle(producto == null ? "Agregar Producto" : "Editar Producto");
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nombreField = new JTextField();
        precioField = new JTextField();
        cantidadField = new JTextField();

        panel.add(new JLabel("Nombre del Producto:"));
        panel.add(nombreField);
        panel.add(new JLabel("Precio:"));
        panel.add(precioField);
        panel.add(new JLabel("Cantidad:"));
        panel.add(cantidadField);

        guardarButton = new JButton("Guardar");
        guardarButton.addActionListener(this);
        panel.add(new JLabel());
        panel.add(guardarButton);

        if (producto != null) {
            nombreField.setText(producto.getNombre());
            precioField.setText(String.valueOf(producto.getPrecio()));
            cantidadField.setText(String.valueOf(producto.getCantidadStock()));
        }

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guardarButton) {
            guardarProducto();
        }
    }

    private void guardarProducto() {
        String nombre = nombreField.getText();
        String precioTexto = precioField.getText();
        String cantidadTexto = cantidadField.getText();

        if (!nombre.isEmpty() && !precioTexto.isEmpty() && !cantidadTexto.isEmpty()) {
            try {
                double precio = Double.parseDouble(precioTexto);
                int cantidad = Integer.parseInt(cantidadTexto);

                if (producto != null) {
                    producto.setNombre(nombre);
                    producto.setPrecio(precio);
                    producto.setCantidadStock(cantidad);

                    actualizarProductoEnBaseDeDatos(producto);
                } else {

                    Producto nuevoProducto = new Producto(0, nombre, precio, cantidad);
                    agregarProductoEnBaseDeDatos(nuevoProducto);
                }

                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese valores válidos para precio y cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProductoEnBaseDeDatos(Producto nuevoProducto) {
        Connection conexion = BaseDeDatos.obtenerConexion();
        String sql = "INSERT INTO inventario (nombre, precio, cantidad) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nuevoProducto.getNombre());
            pstmt.setDouble(2, nuevoProducto.getPrecio());
            pstmt.setInt(3, nuevoProducto.getCantidadStock());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                nuevoProducto.setIdProducto(idGenerado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDeDatos.cerrarConexion();
        }
    }

    private void actualizarProductoEnBaseDeDatos(Producto producto) {
        Connection conexion = BaseDeDatos.obtenerConexion();
        String sql = "UPDATE inventario SET nombre = ?, precio = ?, cantidad = ? WHERE id_producto = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setInt(3, producto.getCantidadStock());
            pstmt.setInt(4, producto.getIdProducto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDeDatos.cerrarConexion();
        }
    }
}

