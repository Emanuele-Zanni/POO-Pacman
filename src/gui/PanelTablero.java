package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import modelo.Fantasma;
import modelo.Laberinto;
import modelo.Pacman;

/*
 * Dibuja el laberinto, los personajes y los carteles de estado.
 * Solo dibuja los datos que recibe del controlador.
 */
public class PanelTablero extends JPanel {

    private static final int TAM_CELDA = 24;

    private static final Color FONDO = Color.BLACK;
    private static final Color PARED = new Color(33, 33, 222);
    private static final Color PUNTO = new Color(255, 184, 151);
    private static final Color PACMAN = Color.YELLOW;
    private static final Color ASUSTADO = new Color(33, 33, 255);
    private static final Color[] COLORES_FANTASMA = {
        Color.RED,
        new Color(255, 184, 255),
        Color.CYAN,
        new Color(255, 184, 82)
    };

    // Datos recibidos del controlador
    private Laberinto laberinto;
    private Pacman pacman;
    private Fantasma[] fantasmas;
    private int puntaje;
    private String estado;
    private int cuadro;     // contador para animar la boca y el parpadeo

    public PanelTablero() {
        setBackground(FONDO);
    }

    public void actualizar(Laberinto laberinto, Pacman pacman, Fantasma[] fantasmas,
                           int puntaje, String estado) {
        this.laberinto = laberinto;
        this.pacman = pacman;
        this.fantasmas = fantasmas;
        this.puntaje = puntaje;
        this.estado = estado;
        repaint();
    }

    public void animar(int cuadro) {
        this.cuadro = cuadro;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (laberinto == null) {
            return super.getPreferredSize();
        }
        return new Dimension(laberinto.getColumnas() * TAM_CELDA, laberinto.getFilas() * TAM_CELDA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (laberinto == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dibujarLaberinto(g2);
        dibujarFantasmas(g2);
        dibujarPacman(g2);
        dibujarCartel(g2);
    }

    private void dibujarLaberinto(Graphics2D g2) {
        for (int f = 0; f < laberinto.getFilas(); f++) {
            for (int c = 0; c < laberinto.getColumnas(); c++) {
                int x = c * TAM_CELDA;
                int y = f * TAM_CELDA;

                if (laberinto.esPared(f, c)) {
                    dibujarPared(g2, laberinto, f, c, x, y);
                    continue;
                }

                g2.setColor(PUNTO);
                switch (laberinto.getContenido(f, c)) {
                    case "PUNTO":
                        g2.fillOval(x + TAM_CELDA / 2 - 2, y + TAM_CELDA / 2 - 2, 4, 4);
                        break;
                    case "SUPERPUNTO":
                        if (cuadro / 3 % 2 == 0) {
                            g2.fillOval(x + 4, y + 4, TAM_CELDA - 8, TAM_CELDA - 8);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    // Traza solo los bordes de la pared que dan a un pasillo
    private void dibujarPared(Graphics2D g2, Laberinto laberinto, int f, int c, int x, int y) {
        g2.setColor(PARED);
        g2.setStroke(new BasicStroke(2));
        int m = 3;
        int fin = TAM_CELDA - m;

        if (!laberinto.esPared(f - 1, c) && f > 0) {
            g2.drawLine(x, y + m, x + TAM_CELDA, y + m);
        }
        if (!laberinto.esPared(f + 1, c) && f < laberinto.getFilas() - 1) {
            g2.drawLine(x, y + fin, x + TAM_CELDA, y + fin);
        }
        if (!laberinto.esPared(f, c - 1) && c > 0) {
            g2.drawLine(x + m, y, x + m, y + TAM_CELDA);
        }
        if (!laberinto.esPared(f, c + 1) && c < laberinto.getColumnas() - 1) {
            g2.drawLine(x + fin, y, x + fin, y + TAM_CELDA);
        }
    }

    private void dibujarPacman(Graphics2D g2) {
        int x = pacman.getPosX() * TAM_CELDA + 2;
        int y = pacman.getPosY() * TAM_CELDA + 2;
        int tam = TAM_CELDA - 4;

        int apertura = cuadro % 2 == 0 ? 50 : 10;
        int angulo;
        switch (pacman.getDireccion()) {
            case "ARRIBA":    angulo = 90; break;
            case "IZQUIERDA": angulo = 180; break;
            case "ABAJO":     angulo = 270; break;
            default:          angulo = 0; break;
        }

        g2.setColor(PACMAN);
        g2.fillArc(x, y, tam, tam, angulo + apertura / 2, 360 - apertura);
    }

    private void dibujarFantasmas(Graphics2D g2) {
        for (Fantasma fantasma : fantasmas) {
            int x = fantasma.getPosX() * TAM_CELDA + 2;
            int y = fantasma.getPosY() * TAM_CELDA + 2;
            int tam = TAM_CELDA - 4;
            boolean asustado = fantasma.getEstado().equals("ASUSTADO");

            g2.setColor(asustado ? ASUSTADO : COLORES_FANTASMA[fantasma.getColor() % COLORES_FANTASMA.length]);
            g2.fillArc(x, y, tam, tam, 0, 180);
            g2.fillRect(x, y + tam / 2, tam, tam / 2 - 3);

            // borde inferior ondulado
            int ancho = tam / 3;
            for (int i = 0; i < 3; i++) {
                g2.fillOval(x + i * ancho, y + tam - 7, ancho, 7);
            }

            if (asustado) {
                g2.setColor(PUNTO);
                g2.fillRect(x + 5, y + 7, 3, 3);
                g2.fillRect(x + tam - 8, y + 7, 3, 3);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillOval(x + 3, y + 5, 6, 7);
                g2.fillOval(x + tam - 9, y + 5, 6, 7);
                g2.setColor(Color.BLUE);
                g2.fillOval(x + 5, y + 7, 3, 3);
                g2.fillOval(x + tam - 7, y + 7, 3, 3);
            }
        }
    }

    private void dibujarCartel(Graphics2D g2) {
        String titulo;
        String subtitulo;

        switch (estado) {
            case "INICIADO":
                titulo = "LISTO!";
                subtitulo = "Presiona una direccion para empezar";
                break;
            case "PAUSADO":
                titulo = "PAUSA";
                subtitulo = "P para continuar";
                break;
            case "COMIDO":
                titulo = "FUISTE COMIDO!";
                subtitulo = "Vidas restantes: " + pacman.getVidas() + "  -  Toca cualquier tecla";
                break;
            case "GANADO":
                titulo = "GANASTE!";
                subtitulo = "Puntaje: " + puntaje + "  -  R para jugar de nuevo";
                break;
            case "PERDIDO":
                titulo = "GAME OVER";
                subtitulo = "Puntaje final: " + puntaje + "  -  R para jugar de nuevo";
                break;
            default:
                return;
        }

        boolean tapaTablero = !estado.equals("INICIADO");
        if (tapaTablero) {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int centroY = getHeight() / 2;
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        g2.setColor(PACMAN);
        dibujarCentrado(g2, titulo, centroY - 6);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        g2.setColor(Color.WHITE);
        dibujarCentrado(g2, subtitulo, centroY + 24);
    }

    private void dibujarCentrado(Graphics2D g2, String texto, int y) {
        FontMetrics metricas = g2.getFontMetrics();
        int x = (getWidth() - metricas.stringWidth(texto)) / 2;
        g2.drawString(texto, x, y);
    }
}
