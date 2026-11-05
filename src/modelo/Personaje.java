package modelo;


public abstract class Personaje {
    private int posX;   // columna
    private int posY;   // fila
    private int velocidad;
    private String direccion;   // "ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA" o "NINGUNA"

    public Personaje(int posX, int posY, int velocidad, String direccion){
        this.posX = posX;
        this.posY = posY;
        this.velocidad = velocidad;
        this.direccion = direccion;

    }

    public void mover(Laberinto laberinto){
        if (puedeMoverse(laberinto, direccion)) {
            posX += deltaColumna(direccion);
            posY += deltaFila(direccion);
        }
    }

    protected boolean estaAlineado(){

        return true;
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

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getDireccion() {
        return direccion;
    }

    protected void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
