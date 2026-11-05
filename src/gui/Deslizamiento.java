package gui;

import java.util.ArrayList;
import java.util.List;

/*
 * Estado puramente visual de un personaje: lo dibuja recorriendo las
 * celdas a velocidad constante, en lugar de que salte de celda en celda.
 *
 * - El recorrido siempre pasa por el centro de las celdas, asi que nunca
 *   hay movimientos en diagonal: si gira, primero llega a la celda y despues dobla.
 * - La velocidad es siempre la misma (una celda por paso del modelo),
 *   asi que un cambio de direccion nunca lo hace mas lento ni mas rapido.
 * - Los saltos de mas de una celda (reinicio, fantasma comido) no se
 *   animan: quedan "pendientes" y se aplican cuando el panel lo decide.
 */
class Deslizamiento {

    private static final double EPSILON = 1e-9;

    private final double celdasPorNs;

    // El recorrido actual arranca en "origen" en el instante "inicio"
    // y sigue por las celdas de "camino" en orden
    private double origenX;
    private double origenY;
    private final List<int[]> camino = new ArrayList<>();
    private long inicio;

    private int hastaX;     // ultima celda informada por el modelo
    private int hastaY;
    private boolean iniciado;

    private boolean pendiente;
    private int pendX;
    private int pendY;

    Deslizamiento(int msPorCelda) {
        this.celdasPorNs = 1.0 / (msPorCelda * 1_000_000.0);
    }

    // Se llama cada vez que llega la posicion del modelo
    void irA(int x, int y) {
        if (!iniciado) {
            colocar(x, y);
            iniciado = true;
            return;
        }

        if (pendiente) {
            if (x == pendX && y == pendY) {
                return;
            }
            aplicarPendiente();
        }

        if (x == hastaX && y == hastaY) {
            return;
        }

        if (Math.abs(x - hastaX) + Math.abs(y - hastaY) > 1) {
            pendiente = true;
            pendX = x;
            pendY = y;
            return;
        }

        // Rearma el recorrido desde el punto donde se dibuja ahora
        recortarRecorrido();

        boolean enLinea = Math.abs(origenX - x) < EPSILON || Math.abs(origenY - y) < EPSILON;
        if (camino.size() <= 1 && enLinea) {
            // Sigue derecho o da media vuelta sobre el mismo eje
            camino.clear();
        }
        camino.add(new int[] {x, y});

        hastaX = x;
        hastaY = y;
    }

    boolean enMovimiento() {
        return restante() > EPSILON;
    }

    // Milisegundos que faltan para que termine de llegar a la ultima celda
    int msRestantes() {
        return (int) Math.ceil(restante() / celdasPorNs / 1_000_000.0);
    }

    boolean tienePendiente() {
        return pendiente;
    }

    void aplicarPendiente() {
        if (pendiente) {
            colocar(pendX, pendY);
            pendiente = false;
        }
    }

    boolean vaHacia(int x, int y) {
        return hastaX == x && hastaY == y;
    }

    int getHastaX() {
        return hastaX;
    }

    int getHastaY() {
        return hastaY;
    }

    double distanciaA(int x, int y) {
        double[] p = posicion();
        return Math.abs(p[0] - x) + Math.abs(p[1] - y);
    }

    double getX() {
        return posicion()[0];
    }

    double getY() {
        return posicion()[1];
    }

    private void colocar(int x, int y) {
        origenX = x;
        origenY = y;
        camino.clear();
        hastaX = x;
        hastaY = y;
        inicio = System.nanoTime();
    }

    private double recorrido() {
        return (System.nanoTime() - inicio) * celdasPorNs;
    }

    private double largoTotal() {
        double largo = 0;
        double px = origenX;
        double py = origenY;
        for (int[] celda : camino) {
            largo += Math.abs(celda[0] - px) + Math.abs(celda[1] - py);
            px = celda[0];
            py = celda[1];
        }
        return largo;
    }

    private double restante() {
        return Math.max(0, largoTotal() - recorrido());
    }

    // Avanza "recorrido" celdas sobre el camino (siempre en tramos horizontales o verticales)
    private double[] posicion() {
        double resto = recorrido();
        double px = origenX;
        double py = origenY;

        for (int[] celda : camino) {
            double tramo = Math.abs(celda[0] - px) + Math.abs(celda[1] - py);
            if (resto >= tramo) {
                resto -= tramo;
                px = celda[0];
                py = celda[1];
                continue;
            }
            px += Math.signum(celda[0] - px) * resto;
            py += Math.signum(celda[1] - py) * resto;
            break;
        }
        return new double[] {px, py};
    }

    // Saca del camino las celdas ya recorridas y arranca de nuevo desde la posicion actual
    private void recortarRecorrido() {
        double resto = recorrido();
        double px = origenX;
        double py = origenY;

        while (!camino.isEmpty()) {
            int[] celda = camino.get(0);
            double tramo = Math.abs(celda[0] - px) + Math.abs(celda[1] - py);
            if (resto >= tramo) {
                resto -= tramo;
                px = celda[0];
                py = celda[1];
                camino.remove(0);
                continue;
            }
            px += Math.signum(celda[0] - px) * resto;
            py += Math.signum(celda[1] - py) * resto;
            break;
        }

        origenX = px;
        origenY = py;
        inicio = System.nanoTime();
    }
}
