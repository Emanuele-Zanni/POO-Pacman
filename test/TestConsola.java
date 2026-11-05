import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import modelo.Fantasma;
import modelo.Juego;
import modelo.Laberinto;
import modelo.Pacman;


public class TestConsola {

    private static final int MS_POR_TICK = 100;
    private static final int TICKS_PACMAN = 2;
    private static final int TICKS_FANTASMAS = 3;

    private static final Queue<String> teclas = new ConcurrentLinkedQueue<>();
    private static volatile Process lectorPowershell;
    private static volatile boolean modoLineas = false;

    public static void main(String[] args) throws InterruptedException {
        Pacman pacman = new Pacman(3, 14, 15, 1);
        Fantasma[] fantasmas = {
            new Fantasma(0, 14, 9, 0, "NORMAL", 12, 9, 1),
            new Fantasma(1, 14, 9, 0, "NORMAL", 13, 9, 1),
            new Fantasma(2, 14, 9, 0, "NORMAL", 15, 9, 1),
            new Fantasma(3, 14, 9, 0, "NORMAL", 16, 9, 1)
        };
        Juego juego = new Juego(new Laberinto(), pacman, fantasmas);

        Thread hiloTeclado = new Thread(TestConsola::leerTeclado);
        hiloTeclado.setDaemon(true);
        hiloTeclado.start();

        System.out.print("\033[2J");
        int tick = 0;
        boolean salir = false;

        while (!salir) {
            String tecla;
            while ((tecla = teclas.poll()) != null) {
                salir |= procesarTecla(juego, tecla);
            }

            if (tick % TICKS_PACMAN == 0) {
                juego.actualizar();
            }
            if (tick % TICKS_FANTASMAS == 0) {
                juego.moverFantasmas();
            }

            dibujar(juego, tick);
            tick++;
            Thread.sleep(MS_POR_TICK);
        }

        if (lectorPowershell != null) {
            lectorPowershell.destroy();
        }
        System.out.println();
        System.exit(0);
    }

    private static boolean procesarTecla(Juego juego, String tecla) {
        boolean esSalir = tecla.equals("Q") || tecla.equals("Escape");

        if (juego.getEstadoJuego().equals("COMIDO") && !esSalir) {
            juego.continuar();
        }

        switch (tecla) {
            case "UpArrow":    case "W": juego.ingresaInput("ARRIBA"); break;
            case "DownArrow":  case "S": juego.ingresaInput("ABAJO"); break;
            case "LeftArrow":  case "A": juego.ingresaInput("IZQUIERDA"); break;
            case "RightArrow": case "D": juego.ingresaInput("DERECHA"); break;
            case "P": juego.pausarReanudar(); break;
            case "R": juego.reiniciar(); break;
            case "Q": case "Escape": return true;
            default: break;
        }
        return false;
    }

    private static void leerTeclado() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                lectorPowershell = new ProcessBuilder("powershell.exe", "-NoProfile", "-Command",
                        "try { while ($true) { [Console]::Out.WriteLine([Console]::ReadKey($true).Key) } } catch { exit 1 }")
                        .redirectInput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start();

                BufferedReader salida = new BufferedReader(new InputStreamReader(lectorPowershell.getInputStream()));
                String tecla;
                while ((tecla = salida.readLine()) != null) {
                    teclas.add(tecla.trim());
                }
            } catch (IOException e) {
                // sin PowerShell: se pasa al Scanner
            }
        }

        modoLineas = true;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            for (char c : scanner.nextLine().toUpperCase().toCharArray()) {
                teclas.add(String.valueOf(c));
            }
        }
    }

    private static void dibujar(Juego juego, int tick) {
        if (juego.getEstadoJuego().equals("PERDIDO")) {
            dibujarGameOver(juego);
            return;
        }
        if (juego.getEstadoJuego().equals("COMIDO")) {
            dibujarComido(juego);
            return;
        }

        Laberinto laberinto = juego.getLaberinto();
        Pacman pacman = juego.getPacman();
        Fantasma[] fantasmas = juego.getFantasmas();
        StringBuilder pantalla = new StringBuilder();

        pantalla.append(String.format("Tick %-6d Puntaje %-6d Vidas %d   Estado %s",
                tick, juego.getPuntaje(), pacman.getVidas(), juego.getEstadoJuego())).append("\033[K\n\n");

        for (int f = 0; f < laberinto.getFilas(); f++) {
            for (int c = 0; c < laberinto.getColumnas(); c++) {
                pantalla.append(simbolo(laberinto, pacman, fantasmas, f, c));
            }
            pantalla.append('\n');
        }

        pantalla.append(String.format("%nPacman      fila %2d  col %2d  %s\033[K%n",
                pacman.getPosY(), pacman.getPosX(), pacman.getDireccion()));
        for (Fantasma fantasma : fantasmas) {
            pantalla.append(String.format("Fantasma %d  fila %2d  col %2d  %-9s %s\033[K%n",
                    fantasma.getColor() + 1, fantasma.getPosY(), fantasma.getPosX(),
                    fantasma.getDireccion(), fantasma.getEstado()));
        }
        pantalla.append(String.format("Puntos restantes: %d\033[K%n%n", laberinto.getCantPuntos()));

        pantalla.append(mensaje(juego.getEstadoJuego())).append("\033[K\n");
        pantalla.append(modoLineas
                ? "Escribi w/a/s/d/p/r/q y Enter\033[K"
                : "Flechas/WASD mover | P pausa | R reiniciar | Q salir\033[K");

        imprimir(pantalla);
    }

    private static void dibujarGameOver(Juego juego) {
        StringBuilder pantalla = new StringBuilder();

        pantalla.append("\n\n");
        pantalla.append("   ####    ###   #   #  #####      ###   #   #  #####  ####\033[K\n");
        pantalla.append("  #       #   #  ## ##  #         #   #  #   #  #      #   #\033[K\n");
        pantalla.append("  #  ##   #####  # # #  ####      #   #  #   #  ####   ####\033[K\n");
        pantalla.append("  #   #   #   #  #   #  #         #   #   # #   #      #  #\033[K\n");
        pantalla.append("   ###    #   #  #   #  #####      ###     #    #####  #   #\033[K\n");
        pantalla.append("\n\n");
        pantalla.append(String.format("  Los fantasmas te atraparon. Puntaje final: %d\033[K%n%n", juego.getPuntaje()));
        pantalla.append(modoLineas
                ? "  Escribi r + Enter para jugar de nuevo, q + Enter para salir\033[K"
                : "  R para jugar de nuevo | Q para salir\033[K");

        imprimir(pantalla);
    }

    private static void dibujarComido(Juego juego) {
        StringBuilder pantalla = new StringBuilder();
        int vidas = juego.getPacman().getVidas();

        pantalla.append("\n\n");
        pantalla.append("  ****************************\033[K\n");
        pantalla.append("  *                          *\033[K\n");
        pantalla.append("  *      FUISTE COMIDO!      *\033[K\n");
        pantalla.append("  *                          *\033[K\n");
        pantalla.append("  ****************************\033[K\n");
        pantalla.append("\n");
        pantalla.append(String.format("  Vidas restantes: %d %s\033[K%n", vidas, "C ".repeat(vidas)));
        pantalla.append(String.format("  Puntaje: %d\033[K%n%n", juego.getPuntaje()));
        pantalla.append(modoLineas
                ? "  Escribi cualquier letra + Enter para continuar\033[K"
                : "  Toca cualquier tecla para continuar\033[K");

        imprimir(pantalla);
    }

    private static void imprimir(StringBuilder pantalla) {
        int lineas = 1;
        for (int i = 0; i < pantalla.length(); i++) {
            if (pantalla.charAt(i) == '\n') {
                lineas++;
            }
        }

        String inicio = "\033[999;1H" + (lineas > 1 ? "\033[" + (lineas - 1) + "A" : "") + "\033[1J";
        pantalla.insert(0, inicio);
        System.out.print(pantalla);
        System.out.flush();
    }

    // C = Pacman, 1-4 = fantasmas (X si estan asustados), # pared, . punto, o superpunto
    private static String simbolo(Laberinto laberinto, Pacman pacman, Fantasma[] fantasmas, int fila, int columna) {
        if (pacman.getPosY() == fila && pacman.getPosX() == columna) {
            return "C ";
        }
        for (Fantasma fantasma : fantasmas) {
            if (fantasma.getPosY() == fila && fantasma.getPosX() == columna) {
                return fantasma.getEstado().equals("ASUSTADO") ? "x " : (fantasma.getColor() + 1) + " ";
            }
        }
        if (laberinto.esPared(fila, columna)) {
            return "##";
        }
        switch (laberinto.getContenido(fila, columna)) {
            case "PUNTO":      return ". ";
            case "SUPERPUNTO": return "o ";
            default:           return "  ";
        }
    }

    private static String mensaje(String estado) {
        switch (estado) {
            case "INICIADO": return "Presiona una direccion para empezar";
            case "PAUSADO":  return "PAUSA (P para seguir)";
            case "GANADO":   return "GANASTE! R para jugar de nuevo";
            default:         return "";
        }
    }
}
