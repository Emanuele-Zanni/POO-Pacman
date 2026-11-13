package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PanelInfo extends JPanel {

    private final JLabel puntaje = crearEtiqueta();
    private final JLabel estado = crearEtiqueta();
    private final JLabel vidas = crearEtiqueta();
    

    public PanelInfo() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        estado.setHorizontalAlignment(JLabel.CENTER);
        add(puntaje, BorderLayout.WEST);
        add(estado, BorderLayout.CENTER);
        add(vidas, BorderLayout.EAST);
    }

    public void actualizar(int puntos, String estadoJuego, int cantVidas) {
        puntaje.setText("PUNTAJE " + puntos);
        estado.setText(estadoJuego.replace('_', ' '));
        vidas.setText("VIDAS " + "● ".repeat(cantVidas).trim());
    }

    private static JLabel crearEtiqueta() {
        JLabel etiqueta = new JLabel();
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        return etiqueta;
    }


}
