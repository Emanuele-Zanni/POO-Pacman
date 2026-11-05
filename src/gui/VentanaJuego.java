package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import controlador.ControladorJuego;
import modelo.Fantasma;
import modelo.Laberinto;
import modelo.Pacman;

/*
 * Ventana principal (vista). Maneja todo lo de Swing: los paneles,
 * el teclado y el reloj (ticks). Traduce esos eventos en llamadas al
 * controlador y redibuja con los datos que el controlador le envia.
 */
public class VentanaJuego extends JFrame {

    private static final int MS_POR_TICK = 100;
    private static final int TICKS_PACMAN = 2;
    private static final int TICKS_FANTASMAS = 3;

    private final PanelInfo panelInfo;
    private final PanelTablero panelTablero;
    private final Timer reloj;
    private int tick;
    private ControladorJuego controlador;

    public VentanaJuego() {
        super("POO Pacman");

        panelInfo = new PanelInfo();
        panelTablero = new PanelTablero();
        reloj = new Timer(MS_POR_TICK, e -> avanzarTick());

        JLabel ayuda = new JLabel("Flechas/WASD mover  |  P pausa  |  R reiniciar  |  Esc salir", SwingConstants.CENTER);
        ayuda.setOpaque(true);
        ayuda.setBackground(Color.BLACK);
        ayuda.setForeground(Color.GRAY);
        ayuda.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        setLayout(new BorderLayout());
        add(panelInfo, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(ayuda, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                procesarTecla(e.getKeyCode());
            }
        });
    }

    public void setControlador(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    public void iniciar() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        requestFocusInWindow();
        reloj.start();
    }

    // Recibe del controlador los datos del modelo y redibuja
    public void actualizar(Laberinto laberinto, Pacman pacman, Fantasma[] fantasmas,
                           int puntaje, String estado) {
        panelInfo.actualizar(puntaje, estado, pacman.getVidas());
        panelTablero.actualizar(laberinto, pacman, fantasmas, puntaje, estado);
    }

    public void cerrar() {
        reloj.stop();
        dispose();
        System.exit(0);
    }

    private void avanzarTick() {
        if (tick % TICKS_PACMAN == 0) {
            controlador.avanzarPacman();
        }
        if (tick % TICKS_FANTASMAS == 0) {
            controlador.avanzarFantasmas();
        }

        tick++;
        panelTablero.animar(tick);
    }

    private void procesarTecla(int tecla) {
        switch (tecla) {
            case KeyEvent.VK_ESCAPE: controlador.salir(); break;
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: controlador.cambiarDireccion("ARRIBA"); break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: controlador.cambiarDireccion("ABAJO"); break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: controlador.cambiarDireccion("IZQUIERDA"); break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: controlador.cambiarDireccion("DERECHA"); break;
            case KeyEvent.VK_P: controlador.pausarReanudar(); break;
            case KeyEvent.VK_R: controlador.reiniciar(); break;
            default: controlador.continuar(); break;
        }
    }
}
