package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fantasma extends Personaje {
    //* Cuantas casillas alcanza a recorrer el fantasma mientras esta asustado
    private static final int CASILLAS_SUSTO = 36; //* Esto equivale a 10,8 segundos aprox
    private static final String[] DIRECCIONES = {"ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"};

    private int color;
    private int PosCasaInicialX;
    private int PosCasaInicialY;
    private int tiempoAsustado;
    private String estado;
    private Random azar = new Random();

    public Fantasma(int color, int PosCasaInicialX, int PosCasaInicialY, int PosInicialX, int PosInicialY, int velocidad){
        super(PosInicialX, PosInicialY, velocidad);
        this.color = color;
        this.PosCasaInicialX = PosCasaInicialX;
        this.PosCasaInicialY = PosCasaInicialY;
        this.tiempoAsustado = 0;
        this.estado = "NORMAL";
    }

    public void asustar(){
        estado = "ASUSTADO";
        tiempoAsustado = CASILLAS_SUSTO * UNIDADES_POR_CELDA / getVelocidad();   // en ticks
        invertir();
    }

    public void serComido(){
        colocarEn(PosCasaInicialX, PosCasaInicialY);
        quitarSusto();
    }

    public void decidirCruce(Laberinto laberinto, Pacman pacman){
        String atras = opuesta(getDireccion());
        List<String> opciones = new ArrayList<>();

        for (String dir : DIRECCIONES) {
            if (!dir.equals(atras) && puedeMoverse(laberinto, dir)) {
                opciones.add(dir);
            }
        }

        if (opciones.isEmpty()) {
            setDireccion(atras);
            return;
        }

        if (azar.nextInt(4) == 0) {
            setDireccion(opciones.get(azar.nextInt(opciones.size())));
            return;
        }

        String mejor = opciones.get(0);
        for (String dir : opciones) {
            int distancia = distancia(dir, pacman);
            int mejorDistancia = distancia(mejor, pacman);
            boolean esMejor = "ASUSTADO".equals(estado)
                    ? distancia > mejorDistancia
                    : distancia < mejorDistancia;
            if (esMejor) {
                mejor = dir;
            }
        }
        setDireccion(mejor);
    }

    @Override
    public void reiniciarPos(){
        super.reiniciarPos();
        quitarSusto();
    }

    private void quitarSusto() {
        estado = "NORMAL";
        tiempoAsustado = 0;
    }

    @Override
    public boolean mover(Laberinto laberinto) {
        boolean llego = super.mover(laberinto);

        if ("ASUSTADO".equals(estado) && --tiempoAsustado <= 0) {
            estado = "NORMAL";
        }
        return llego;
    }

    private int distancia(String dir, Pacman pacman) {
        int dx = getPosX() + deltaColumna(dir) - pacman.getPosX();
        int dy = getPosY() + deltaFila(dir) - pacman.getPosY();
        return dx * dx + dy * dy;
    }

    // Ticks que le quedan asustado (0 si no lo esta)
    public int getTiempoAsustado() {
        return tiempoAsustado;
    }

    public String getEstado() {
        return estado;
    }

    public int getColor() {
        return color;
    }
}
