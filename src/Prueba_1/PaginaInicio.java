package Prueba_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PaginaInicio extends BaseFrame {

    private JTextField usuarioField;
    private JPasswordField contraseñaField;

    public PaginaInicio() {
        super("Inicio de Sesión");

        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulario de inicio de sesión
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        usuarioField = new JTextField();
        contraseñaField = new JPasswordField();

        formPanel.add(new JLabel("Usuario:"));
        formPanel.add(usuarioField);
        formPanel.add(new JLabel("Contraseña:"));
        formPanel.add(contraseñaField);

        JButton loginButton = crearBoton("Iniciar Sesión", "IniciarSesion");
        formPanel.add(new JLabel()); // Espacio vacío para el diseño
        formPanel.add(loginButton);

        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        String usuario = usuarioField.getText();
        char[] contraseña = contraseñaField.getPassword();

        if (autenticar(usuario, contraseña)) {
            JOptionPane.showMessageDialog(this, "¡Inicio de sesión exitoso!");

            // Redirigir al usuario a la clase Menu
            SwingUtilities.invokeLater(() -> {
                Menu menu = new Menu();
                menu.setVisible(true);
            });

            // Cerrar la ventana de inicio de sesión después de iniciar sesión con éxito
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error de inicio de sesión. Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean autenticar(String usuario, char[] contraseña) {
        return usuario.equals("admin") && new String(contraseña).equals("admin");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaginaInicio paginaInicio = new PaginaInicio();
            paginaInicio.setVisible(true);
        });
    }
}



