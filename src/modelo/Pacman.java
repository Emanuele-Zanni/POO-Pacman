package modelo;



public class Pacman extends Personaje {


    private int vidas = 3;
    private int vidasIniciales = 3;
    private int PosInicialX = 0;
    private int PosInicialY = 0;
    private String direccionDeseada;

    public Pacman(int vidas, int PosInicialX, int PosInicialY, int velocidad){
        super(PosInicialX, PosInicialY, velocidad, "NINGUNA");
        this.vidas = vidas;
        this.vidasIniciales = vidas;
        this.PosInicialX = PosInicialX;
        this.PosInicialY = PosInicialY;
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

    public void reiniciarPos(){
        colocarEn(PosInicialX, PosInicialY);
        direccionDeseada = "NINGUNA";
    }

    public void reiniciar(){
        vidas = vidasIniciales;
        reiniciarPos();
    }

    public void setDirDeseada(String direccion){
        this.direccionDeseada = direccion;
    }

    @Override
    public void mover(Laberinto laberinto) {
        if (puedeMoverse(laberinto, direccionDeseada)) {
            setDireccion(direccionDeseada);
        }
        super.mover(laberinto);
    }

    public int getVidas() {
        return vidas;
    }
}
