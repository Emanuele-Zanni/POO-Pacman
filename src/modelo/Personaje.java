package modelo;


public abstract class Personaje {

    // Cada celda se recorre en UNIDADES_POR_CELDA pasos de progreso.
    // La velocidad indica cuantas unidades avanza por tick (conviene que divida a 120).
    public static final int UNIDADES_POR_CELDA = 120;

    private int posX;   // columna de la celda de la que sale
    private int posY;   // fila de la celda de la que sale
    private int progreso;   // 0 = en el centro de la celda; avanza hacia la vecina en "direccion"
    private int velocidad;
    private String direccion;   // "ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA" o "NINGUNA"

    public Personaje(int posX, int posY, int velocidad, String direccion){
        this.posX = posX;
        this.posY = posY;
        this.progreso = 0;
        this.velocidad = velocidad;
        this.direccion = direccion;

    }

    // Avanza un tick. Devuelve true si en este tick completo la llegada a una celda nueva
    public boolean mover(Laberinto laberinto){
        if (progreso == 0 && !puedeMoverse(laberinto, direccion)) {
            return false;
        }

        progreso += velocidad;
        if (progreso < UNIDADES_POR_CELDA) {
            return false;
        }

        posX += deltaColumna(direccion);
        posY += deltaFila(direccion);
        progreso = 0;
        return true;
    }

    // Media vuelta sin perder lo recorrido: si estaba a mitad de camino,
    // ahora sale de la celda vecina y le falta lo mismo que ya habia avanzado
    protected void invertir() {
        if (progreso > 0) {
            posX += deltaColumna(direccion);
            posY += deltaFila(direccion);
            progreso = UNIDADES_POR_CELDA - progreso;
        }
        direccion = opuesta(direccion);
    }

    public boolean estaEnCentro(){
        return progreso == 0;
    }

    public boolean estaEnMovimiento(){
        return progreso > 0;
    }

    protected boolean puedeMoverse(Laberinto laberinto, String dir) {
        if ("NINGUNA".equals(dir)) {
            return false;
        }

        return !laberinto.esPared(posY + deltaFila(dir), posX + deltaColumna(dir));
    }

    protected void colocarEn(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.progreso = 0;
        this.direccion = "NINGUNA";
    }

    protected static int deltaFila(String dir) {
        switch (dir) {
            case "ARRIBA": return -1;
            case "ABAJO":  return 1;
            default:       return 0;
        }
    }

    protected static int deltaColumna(String dir) {
        switch (dir) {
            case "IZQUIERDA": return -1;
            case "DERECHA":   return 1;
            default:          return 0;
        }
    }

    protected static String opuesta(String dir) {
        switch (dir) {
            case "ARRIBA":    return "ABAJO";
            case "ABAJO":     return "ARRIBA";
            case "IZQUIERDA": return "DERECHA";
            case "DERECHA":   return "IZQUIERDA";
            default:          return "NINGUNA";
        }
    }

    // Distancia (en celdas) entre las posiciones reales de dos personajes
    public double distanciaA(Personaje otro) {
        return Math.hypot(getX() - otro.getX(), getY() - otro.getY());
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    // Posicion real en celdas, contando el progreso hacia la celda vecina
    public double getX() {
        return posX + deltaColumna(direccion) * (double) progreso / UNIDADES_POR_CELDA;
    }

    public double getY() {
        return posY + deltaFila(direccion) * (double) progreso / UNIDADES_POR_CELDA;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public String getDireccion() {
        return direccion;
    }

    protected void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
