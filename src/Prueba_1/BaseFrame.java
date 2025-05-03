package Prueba_1;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BaseFrame extends JFrame implements ActionListener {

    public BaseFrame(String title) {
        super(title);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }


    protected JButton crearBoton(String texto, String comando) {
        JButton boton = new JButton(texto);
        boton.setActionCommand(comando);
        boton.addActionListener(this);
        return boton;
    }


    protected void abrirNuevoFrame(JFrame nuevoFrame) {
        nuevoFrame.setVisible(true);
        dispose();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
	
		
	}
}

