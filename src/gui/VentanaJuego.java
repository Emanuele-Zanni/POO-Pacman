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
    private static final int MS_POR_FOTOGRAMA = 16;    // ~60 FPS para el desplazamiento suave

    private static final int MS_PASO_PACMAN = TICKS_PACMAN * MS_POR_TICK;
    private static final int MS_PASO_FANTASMAS = TICKS_FANTASMAS * MS_POR_TICK;
    private static final int MAX_CELDAS_EN_COLA = 1;

    private final PanelInfo panelInfo;
    private final PanelTablero panelTablero;
    private final Timer relojPacman;
    private final Timer relojFantasmas;
    private final Timer relojDibujo;
    private ControladorJuego controlador;

    public VentanaJuego() {
        super("POO Pacman");

        panelInfo = new PanelInfo();
        panelTablero = new PanelTablero(MS_PASO_PACMAN, MS_PASO_FANTASMAS);
        relojPacman = new Timer(MS_PASO_PACMAN, e -> controlador.avanzarPacman());
        relojFantasmas = new Timer(MS_PASO_FANTASMAS, e -> controlador.avanzarFantasmas());
        relojDibujo = new Timer(MS_POR_FOTOGRAMA,
                e -> panelTablero.animar((int) (System.currentTimeMillis() / MS_POR_TICK)));

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
        relojPacman.start();
        relojFantasmas.start();
        relojDibujo.start();
    }

    // Limita la cola de movimientos: si a la animacion todavia le falta recorrer
    // mas de MAX_CELDAS_EN_COLA celdas, el input no adelanta otro paso (la
    // direccion queda guardada y se aplica en el proximo paso normal)
    public boolean aceptaPasoInmediato() {
        return panelTablero.msRestantesPacman() <= MAX_CELDAS_EN_COLA * MS_PASO_PACMAN;
    }

    // Pacman acaba de moverse por un input: su proximo paso llega justo cuando
    // la animacion termine de alcanzar la celda nueva, asi mantiene la velocidad
    public void reiniciarPasoPacman() {
        relojPacman.setInitialDelay(Math.max(1, panelTablero.msRestantesPacman()));
        relojPacman.restart();
    }

    // Recibe del controlador los datos del modelo y redibuja
    public void actualizar(Laberinto laberinto, Pacman pacman, Fantasma[] fantasmas,
                           int puntaje, String estado) {
        panelInfo.actualizar(puntaje, estado, pacman.getVidas());
        panelTablero.actualizar(laberinto, pacman, fantasmas, puntaje, estado);
    }

    public void cerrar() {
        relojPacman.stop();
        relojFantasmas.stop();
        relojDibujo.stop();
        dispose();
        System.exit(0);
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
