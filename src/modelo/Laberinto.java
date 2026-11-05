
package modelo;

public class Laberinto {

    private int filas;
    private int columnas;
    private int cantPuntos;
    private Celda[][] celdas;

    /*
     * '1' = pared
     * ' ' = espacio transitable con punto
     * '*' = superpunto
     */

private static final char[][] MAPA = {
   {'1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1'},
    {'1','*',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','*','1'},
    {'1',' ',' ','1','1','1',' ',' ','1','1','1','1',' ','1',' ',' ','1','1','1','1',' ',' ','1','1','1',' ',' ',' ','1'},
    {'1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1'},
    {'1',' ',' ','1','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1',' ',' ','1',' ',' ','1','1','1',' ',' ',' ','1'},
    {'1',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ','1'},
    {'1','1','1','1','1','1',' ',' ','1','1','1','1',' ',' ','1',' ',' ','1','1','1','1',' ',' ','1','1','1','1','1','1'},
    {'1','1','1','1','1','1',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ','1','1','1','1','1','1'},
    {'1','1','1','1','1','1',' ',' ','1',' ',' ','1','1',' ',' ',' ','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1'},
    {' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
    {'1','1','1','1','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1','1'},
    {'1','1','1','1','1','1',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ','1','1','1','1','1','1'},
    {'1','1','1','1','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1',' ',' ','1',' ',' ','1','1','1','1','1','1','1'},
    {'1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1'},
    {'1',' ',' ','1','1','1',' ',' ','1','1','1','1',' ','1',' ',' ','1','1','1','1',' ',' ','1','1','1',' ',' ',' ','1'},
    {'1',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ','1'},
    {'1','1','1',' ',' ','1',' ',' ','1',' ',' ','1','1','1','1','1','1',' ',' ','1',' ',' ','1',' ',' ','1','1','1','1'},
    {'1',' ',' ',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ','1',' ',' ',' ',' ',' ',' ',' ','1'},
    {'1',' ',' ','1','1','1',' ',' ','1','1','1','1','1',' ','1',' ',' ','1','1','1','1',' ',' ','1','1','1',' ',' ','1'},
    {'1','*',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','*','1'},
    {'1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1'}
};



public Laberinto() {
    filas = MAPA.length;
    columnas = MAPA[0].length;
    celdas = new Celda[filas][columnas];

    reiniciarLaberinto();
}

public boolean esPared(int fila, int columna) {
    if (!estaDentroDelMapa(fila, columna)) {
        return true;
    }

    return celdas[fila][columna].esPared();
}

public String consumir(int fila, int columna) {
    if (!estaDentroDelMapa(fila, columna)) {
        return "";
    }

    Celda celda = celdas[fila][columna];
    String contenido = celda.getContenido();

    if ("PUNTO".equals(contenido)
            || "SUPERPUNTO".equals(contenido)) {
        cantPuntos--;
        celda.removerItem(contenido);
        return contenido;
    }

    return "";
}

public String getContenido(int fila, int columna) {
    if (!estaDentroDelMapa(fila, columna)) {
        return "";
    }

    return celdas[fila][columna].getContenido();
}

public int getFilas() {
    return filas;
}

public int getColumnas() {
    return columnas;
}

public int getCantPuntos() {
    return cantPuntos;
}

public void reiniciarLaberinto() {
    cantPuntos = 0;

    for (int f = 0; f < filas; f++) {
        for (int c = 0; c < columnas; c++) {
            char elemento = MAPA[f][c];

            if (elemento == '1') {
                celdas[f][c] = new Celda(true, "");
            } else if (elemento == '*') {
                celdas[f][c] = new Celda(false, "SUPERPUNTO");
                cantPuntos++;
            } else if (elemento == ' ') {
                celdas[f][c] = new Celda(false, "PUNTO");
                cantPuntos++;
            }
        }
    }
}

public boolean hayPuntos() {
    return cantPuntos > 0;
}

private boolean estaDentroDelMapa(int fila, int columna) {
    return fila >= 0 && fila < filas
        && columna >= 0 && columna < columnas;
}

}