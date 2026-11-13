package DTOs;

public class LaberintoDTO {
    private final CeldaDTO[][] celdas;

    public LaberintoDTO(CeldaDTO[][] celdas) {
        this.celdas = celdas;
    }

    public int getFilas() {
        return celdas.length;
    }

    public int getColumnas() {
        return celdas[0].length;
    }

    public boolean esPared(int fila, int columna) {
        if (fila < 0 || fila >= getFilas() || columna < 0 || columna >= getColumnas()) {
            return true;
        }
        return celdas[fila][columna].esPared();
    }

    public String getContenido(int fila, int columna) {
        return celdas[fila][columna].getContenido();
    }
}
