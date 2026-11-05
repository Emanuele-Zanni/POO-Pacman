package modelo;

public class Juego {

    private static final int PUNTOS_PUNTO = 10;
    private static final int PUNTOS_SUPERPUNTO = 50;
    private static final int PUNTOS_FANTASMA = 200;

    private int puntaje;
    private String estadoJuego;     // "INICIADO", "EN_CURSO", "PAUSADO", "COMIDO", "GANADO" o "PERDIDO"
    private Laberinto laberinto;
    private Pacman pacman;
    private Fantasma[] fantasmas;

    public Juego(Laberinto laberinto, Pacman pacman,
                Fantasma[] fantasmas) {

        this.puntaje = 0;
        this.estadoJuego = "INICIADO";

        this.laberinto = laberinto;
        this.pacman = pacman;
        this.fantasmas = fantasmas;
    }

    public void iniciarPartida(){
        if (estadoJuego.equals("INICIADO")) {
            estadoJuego = "EN_CURSO";
        }
    }

    public void continuar(){
        if (estadoJuego.equals("COMIDO")) {
            estadoJuego = "INICIADO";
        }
    }

    public void reiniciar (){
        laberinto.reiniciarLaberinto();
        pacman.reiniciar();
        for (Fantasma fantasma : fantasmas) {
            fantasma.reiniciarPos();
        }
        puntaje = 0;
        estadoJuego = "INICIADO";
    }

    public void pausarReanudar(){
        if (estadoJuego.equals("EN_CURSO")) {
            estadoJuego = "PAUSADO";
        } else if (estadoJuego.equals("PAUSADO")) {
            estadoJuego = "EN_CURSO";
        }
    }

    public void ingresaInput(String direccion){
        if (estadoJuego.equals("INICIADO") || estadoJuego.equals("EN_CURSO")) {
            pacman.setDirDeseada(direccion);
        }
    }

    public void actualizar(){
        if (estadoJuego.equals("INICIADO")) {
            int x = pacman.getPosX();
            int y = pacman.getPosY();
            pacman.mover(laberinto);

            if (x == pacman.getPosX() && y == pacman.getPosY()) {
                return;
            }
            iniciarPartida();
        } else if (estadoJuego.equals("EN_CURSO")) {
            pacman.mover(laberinto);
        } else {
            return;
        }

        comer();
        verificaColisiones();
        condicionesFin();
    }

    public void moverFantasmas(){
        if (!estadoJuego.equals("EN_CURSO")) {
            return;
        }

        for (Fantasma fantasma : fantasmas) {
            fantasma.decidirCruce(laberinto, pacman);
            fantasma.mover(laberinto);
        }
        verificaColisiones();
    }

    private void comer(){
        String comido = laberinto.consumir(pacman.getPosY(), pacman.getPosX());

        if (comido.equals("PUNTO")) {
            puntaje += PUNTOS_PUNTO;
        } else if (comido.equals("SUPERPUNTO")) {
            puntaje += PUNTOS_SUPERPUNTO;
            for (Fantasma fantasma : fantasmas) {
                fantasma.asustar();
            }
        }
    }

    private void verificaColisiones(){
        for (Fantasma fantasma : fantasmas) {
            boolean mismaCelda = fantasma.getPosX() == pacman.getPosX()
                    && fantasma.getPosY() == pacman.getPosY();
            if (!mismaCelda) {
                continue;
            }

            if (fantasma.getEstado().equals("ASUSTADO")) {
                fantasma.serComido();
                puntaje += PUNTOS_FANTASMA;
            } else {
                pacmanAtrapado();
                return;
            }
        }
    }

    private void pacmanAtrapado(){
        pacman.restarVida();

        if (!pacman.estaVivo()) {
            estadoJuego = "PERDIDO";
            return;
        }

        pacman.reiniciarPos();
        for (Fantasma fantasma : fantasmas) {
            fantasma.reiniciarPos();
        }
        estadoJuego = "COMIDO";
    }

    private void condicionesFin(){
        if (estadoJuego.equals("EN_CURSO") && !laberinto.hayPuntos()) {
            estadoJuego = "GANADO";
        }
    }

    public int getPuntaje() {
        return puntaje;
    }

    public String getEstadoJuego() {
        return estadoJuego;
    }

    public Laberinto getLaberinto() {
        return laberinto;
    }

    public Pacman getPacman() {
        return pacman;
    }

    public Fantasma[] getFantasmas() {
        return fantasmas;
    }
}
