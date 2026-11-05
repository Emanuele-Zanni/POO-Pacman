import javax.swing.SwingUtilities;

import controlador.ControladorJuego;
import gui.VentanaJuego;
import modelo.Fantasma;
import modelo.Juego;
import modelo.Laberinto;
import modelo.Pacman;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Juego juego = crearJuego();
            VentanaJuego vista = new VentanaJuego();
            ControladorJuego controlador = new ControladorJuego(juego, vista);
            controlador.iniciar();
        });
    }

    private static Juego crearJuego() {
        Pacman pacman = new Pacman(3, 14, 15, 1);
        Fantasma[] fantasmas = {
            new Fantasma(0, 14, 9, 0, "NORMAL", 12, 9, 1),
            new Fantasma(1, 14, 9, 0, "NORMAL", 13, 9, 1),
            new Fantasma(2, 14, 9, 0, "NORMAL", 15, 9, 1),
            new Fantasma(3, 14, 9, 0, "NORMAL", 16, 9, 1)
        };
        return new Juego(new Laberinto(), pacman, fantasmas);
    }
}
