// InventarioFrame.java

package Prueba_1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventarioFrame extends BaseFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    private JButton agregarProductoButton;
    private JButton editarProductoButton;
    private JButton eliminarProductoButton;
    private JButton menuButton;

    public InventarioFrame(String usuario) {
        super("Gestión de Inventario - Usuario: " + usuario);

        setSize(800, 500);
        setLocationRelativeTo(null);

        // Crear modelo de tabla
        modeloTabla = new DefaultTableModel();
        tablaProductos = new JTable(modeloTabla);
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Cantidad");

        // Panel para mostrar la tabla
        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setBorder(BorderFactory.createTitledBorder("Productos en Inventario"));
        tablaPanel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        // Botones para agregar, editar y eliminar productos
        agregarProductoButton = crearBoton("Agregar Producto", "AgregarProducto");
        editarProductoButton = crearBoton("Editar Producto", "EditarProducto");
        eliminarProductoButton = crearBoton("Eliminar Producto", "EliminarProducto");
        menuButton = crearBoton("Menu", "Menu");

        JPanel botonesPanel = new JPanel();
        botonesPanel.add(agregarProductoButton);
        botonesPanel.add(editarProductoButton);
        botonesPanel.add(eliminarProductoButton);
        botonesPanel.add(menuButton);

        // Diseño principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tablaPanel, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        add(panel);

        // Obtener datos de la base de datos y mostrarlos en la tabla
        obtenerProductosDesdeBaseDeDatos(modeloTabla);
    }
    private void obtenerProductosDesdeBaseDeDatos(DefaultTableModel modeloTabla) {
        Connection conexion = BaseDeDatos.obtenerConexion();
        String sql = "SELECT * FROM inventario";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                int cantidad = rs.getInt("cantidad");

                // Agregar fila a la tabla
                modeloTabla.addRow(new Object[]{id, nombre, precio, cantidad});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDeDatos.cerrarConexion();
        }
    }

    private void abrirVentanaAgregarProducto() {
        // Implementar la apertura de la interfaz para agregar producto aquí
        DialogoAgregarEditarProducto dialogoAgregarProducto = new DialogoAgregarEditarProducto(this, true, null);
        dialogoAgregarProducto.setVisible(true);

        // Actualizar la tabla después de agregar el producto
        modeloTabla.setRowCount(0);
        obtenerProductosDesdeBaseDeDatos(modeloTabla);
    }

    private void abrirVentanaEditarProducto() {
        // Implementar la apertura de la interfaz para editar producto aquí
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idProducto = (int) tablaProductos.getValueAt(filaSeleccionada, 0);
            String nombre = (String) tablaProductos.getValueAt(filaSeleccionada, 1);
            double precio = (double) tablaProductos.getValueAt(filaSeleccionada, 2);
            int cantidad = (int) tablaProductos.getValueAt(filaSeleccionada, 3);

            Producto producto = new Producto(idProducto, nombre, precio, cantidad);

            DialogoAgregarEditarProducto dialogoEditarProducto = new DialogoAgregarEditarProducto(this, true, producto);
            dialogoEditarProducto.setVisible(true);

            // Actualizar la tabla después de editar el producto
            modeloTabla.setRowCount(0);
            obtenerProductosDesdeBaseDeDatos(modeloTabla);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reorganizarIDs() {
        Connection conexion = BaseDeDatos.obtenerConexion();

        // Consulta para obtener todos los productos ordenados por ID
        String sqlSelect = "SELECT id_producto FROM inventario ORDER BY id_producto";
        // Consulta para actualizar el ID de un producto
        String sqlUpdate = "UPDATE inventario SET id_producto = ? WHERE id_producto = ?";

        try (PreparedStatement pstmtSelect = conexion.prepareStatement(sqlSelect);
             PreparedStatement pstmtUpdate = conexion.prepareStatement(sqlUpdate)) {

            ResultSet rs = pstmtSelect.executeQuery();

            // Contador para el nuevo ID
            int nuevoID = 1;

            // Recorrer los productos y actualizar sus IDs
            while (rs.next()) {
                int idActual = rs.getInt("id_producto");

                // Actualizar el ID solo si es diferente
                if (idActual != nuevoID) {
                    pstmtUpdate.setInt(1, nuevoID);
                    pstmtUpdate.setInt(2, idActual);
                    pstmtUpdate.executeUpdate();
                }

                nuevoID++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDeDatos.cerrarConexion();
        }
    }

    private void eliminarProducto() {
      
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idProducto = (int) tablaProductos.getValueAt(filaSeleccionada, 0);
            eliminarProductoEnBaseDeDatos(idProducto);

        
            modeloTabla.removeRow(filaSeleccionada);

         
            reorganizarIDs();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void eliminarProductoEnBaseDeDatos(int idProducto) {
        Connection conexion = BaseDeDatos.obtenerConexion();
        String sql = "DELETE FROM inventario WHERE id_producto = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDeDatos.cerrarConexion();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == agregarProductoButton) {
            abrirVentanaAgregarProducto();
        } else if (e.getSource() == editarProductoButton) {
            abrirVentanaEditarProducto();
        } else if (e.getSource() == eliminarProductoButton) {
            eliminarProducto();
        } else if (e.getSource() == menuButton) {
            
        	 SwingUtilities.invokeLater(() -> {
                 Menu menu = new Menu();
                 menu.setVisible(true);
             });
        	 dispose();
          
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventarioFrame inventarioFrame = new InventarioFrame("admin");
            inventarioFrame.setVisible(true);
        });
    }
}


