package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fantasma extends Personaje {
    private static final int DURACION_SUSTO = 36;
    private static final String[] DIRECCIONES = {"ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"};

    private int color;
    private int PosCasaInicialX;
    private int PosCasaInicialY;
    private int tiempoAsustado;
    private String estado;
    private int PosInicialX;
    private int PosInicialY;
    private Random azar = new Random();

    public Fantasma(int color, int PosCasaInicialX, int PosCasaInicialY, int tiempoAsustado, String estado, int PosInicialX, int PosInicialY, int velocidad){
        super(PosInicialX, PosInicialY, velocidad, "NINGUNA");
        this.color = color;
        this.PosCasaInicialX = PosCasaInicialX;
        this.PosCasaInicialY = PosCasaInicialY;
        this.tiempoAsustado = 0;
        this.estado = "NORMAL";
        this.PosInicialX = PosInicialX;
        this.PosInicialY = PosInicialY;
    }

    public void asustar(){
        estado = "ASUSTADO";
        tiempoAsustado = DURACION_SUSTO;
        setDireccion(opuesta(getDireccion()));
    }

    public void serComido(){
        colocarEn(PosCasaInicialX, PosCasaInicialY);
        estado = "NORMAL";
        tiempoAsustado = 0;
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

    public void reiniciarPos(){
        colocarEn(PosInicialX, PosInicialY);
        estado = "NORMAL";
        tiempoAsustado = 0;
    }

    @Override
    public void mover(Laberinto laberinto) {
        super.mover(laberinto);

        if ("ASUSTADO".equals(estado) && --tiempoAsustado <= 0) {
            estado = "NORMAL";
        }
    }

    private int distancia(String dir, Pacman pacman) {
        int dx = getPosX() + deltaColumna(dir) - pacman.getPosX();
        int dy = getPosY() + deltaFila(dir) - pacman.getPosY();
        return dx * dx + dy * dy;
    }

    // Pasos que le quedan asustado (0 si no lo esta)
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
