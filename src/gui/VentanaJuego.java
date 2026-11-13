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

import DTOs.InfoJuegoDTO;
import controlador.ControladorJuego;

/*
 * Ventana principal (vista). Maneja todo lo de Swing: los paneles,
 * el teclado y el reloj (ticks). Traduce esos eventos en llamadas al
 * controlador y redibuja con los datos que el controlador le devuelve.
 */
public class VentanaJuego extends JFrame {

    // Cada tick el modelo avanza a los personajes una fraccion de casilla.
    // Con 5 ms (200 ticks por segundo): Pacman (velocidad 3) tarda 200 ms por casilla
    // y los fantasmas (velocidad 2) 300 ms.
    private static final int MS_POR_TICK = 5;
    private static final long NS_POR_TICK = MS_POR_TICK * 1_000_000L;

    // El reloj de dibujo dispara lo mas seguido posible; la logica no depende de el
    private static final int MS_POR_FOTOGRAMA = 4;
    private static final int MAX_TICKS_POR_FOTOGRAMA = 20;   // evita "saltos" si la ventana se congela

    private final PanelInfo panelInfo;
    private final PanelTablero panelTablero;
    private final Timer reloj;
    private final ControladorJuego controlador;

    // Bucle de paso fijo: acumula el tiempo real y ejecuta los ticks que correspondan
    private long ultimoInstante;
    private long tiempoAcumulado;

    public VentanaJuego(ControladorJuego controlador) {
        super("POO Pacman");
        this.controlador = controlador;

        panelInfo = new PanelInfo();
        panelTablero = new PanelTablero(MS_POR_TICK);
        reloj = new Timer(MS_POR_FOTOGRAMA, e -> fotograma());
        reloj.setCoalesce(true);

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

    public void iniciar() {
        mostrar(controlador.iniciar());   // el tablero necesita el laberinto para saber su tamaño antes de pack()
        pack();
        // setSize(800, 650); //! Si se desea aplicar un tamaño arbitrario a la ventana  
        setLocationRelativeTo(null);
        setVisible(true);
        requestFocusInWindow();
        ultimoInstante = System.nanoTime();
        reloj.start();
    }

    // Se ejecuta en cada disparo del reloj: avanza el juego segun el tiempo
    // real transcurrido (no segun cuantas veces disparo el reloj) y redibuja
    private void fotograma() {
        long ahora = System.nanoTime();
        tiempoAcumulado += ahora - ultimoInstante;
        ultimoInstante = ahora;

        int ticks = (int) Math.min(tiempoAcumulado / NS_POR_TICK, MAX_TICKS_POR_FOTOGRAMA);
        if (ticks == MAX_TICKS_POR_FOTOGRAMA) {
            tiempoAcumulado = 0;
        } else {
            tiempoAcumulado -= ticks * NS_POR_TICK;
        }

        // Aunque no haya ticks se redibuja, para que sigan las animaciones (boca, parpadeo)
        mostrar(controlador.avanzar(ticks));
    }

    private void mostrar(InfoJuegoDTO info) {
        panelInfo.actualizar(info.getPuntaje(), info.getEstado(), info.getPacman().getVidas());
        panelTablero.actualizar(info.getLaberinto(), info.getPacman(), info.getFantasmas(),
                                info.getPuntaje(), info.getEstado());
    }

    public void cerrar() {
        reloj.stop();
        dispose();
        System.exit(0);
    }

    private void procesarTecla(int tecla) {
        InfoJuegoDTO info;
        switch (tecla) {
            case KeyEvent.VK_ESCAPE: cerrar(); return;   // cerrar la ventana es tarea de la vista
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: info = controlador.cambiarDireccion("ARRIBA"); break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: info = controlador.cambiarDireccion("ABAJO"); break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: info = controlador.cambiarDireccion("IZQUIERDA"); break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: info = controlador.cambiarDireccion("DERECHA"); break;
            case KeyEvent.VK_P: info = controlador.pausarReanudar(); break;
            case KeyEvent.VK_R: info = controlador.reiniciar(); break;
            case KeyEvent.VK_ENTER: info = controlador.continuar(); break;
            default: return;   // tecla sin uso: no se le pide nada al controlador
        }
        mostrar(info);
    }
}
