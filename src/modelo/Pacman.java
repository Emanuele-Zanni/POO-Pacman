package modelo;



public class Pacman extends Personaje {


    private static final int VIDAS_INICIALES = 3;

    private int vidas;
    private String direccionDeseada;

    public Pacman(int PosInicialX, int PosInicialY, int velocidad){
        super(PosInicialX, PosInicialY, velocidad);
        this.vidas = VIDAS_INICIALES;
        this.direccionDeseada = "NINGUNA";
    }

    public void restarVida(){
        if (vidas > 0) {
            vidas--;
        }
    }

    public boolean estaVivo(){
        return vidas > 0;
    }

    @Override
    public void reiniciarPos(){
        super.reiniciarPos();
        direccionDeseada = "NINGUNA";
    }

    public void reiniciarVidas(){
        vidas = VIDAS_INICIALES;
    }

    public void setDirDeseada(String direccion){
        this.direccionDeseada = direccion;
    }

    @Override
    public boolean mover(Laberinto laberinto) {
        
        if (estaEnMovimiento() && direccionDeseada.equals(opuesta(getDireccion()))) {
            invertir();
        }
        
        if (estaEnCentro() && puedeMoverse(laberinto, direccionDeseada)) {
            setDireccion(direccionDeseada);
        }
        return super.mover(laberinto);
    }

    public int getVidas() {
        return vidas;
    }
}
