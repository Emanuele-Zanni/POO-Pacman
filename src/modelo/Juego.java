package modelo;

public class Juego {

    private static final int PUNTOS_PUNTO = 10;
    private static final int PUNTOS_SUPERPUNTO = 50;
    private static final int PUNTOS_FANTASMA = 200;
    private static final double MARGEN_COLISION = 0.4;   // 40% de una casilla

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
            // Las posiciones se reinician recien ahora, asi el choque queda a la vista
            reiniciarPosiciones();
            estadoJuego = "INICIADO";
        }
    }

    // Tras una victoria: nuevo laberinto, conservando puntaje y vidas.
    // En cualquier otro caso (derrota o reinicio a mitad de partida): todo desde cero.
    public void reiniciar (){
        boolean gano = estadoJuego.equals("GANADO");

        laberinto.reiniciarLaberinto();
        reiniciarPosiciones();
        if (!gano) {
            pacman.reiniciar();
            puntaje = 0;
        }
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

    // Avanza un tick: mueve a Pacman y a los fantasmas una fraccion de casilla
    public void actualizar(){
        if (!estadoJuego.equals("INICIADO") && !estadoJuego.equals("EN_CURSO")) {
            return;
        }

        boolean llego = pacman.mover(laberinto);
        if (estadoJuego.equals("INICIADO")) {
            if (!llego && !pacman.estaEnMovimiento()) {
                return;     // todavia no arranco
            }
            iniciarPartida();
        }

        // El punto se consume solo al completar la llegada a la casilla
        if (llego) {
            comer();
        }
        verificaColisiones();

        if (estadoJuego.equals("EN_CURSO")) {
            moverFantasmas();
        }
        condicionesFin();
    }

    private void moverFantasmas(){
        for (Fantasma fantasma : fantasmas) {
            // Solo elige camino al estar en el centro de una casilla
            if (fantasma.estaEnCentro()) {
                fantasma.decidirCruce(laberinto, pacman);
            }
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

    // Chocan si sus posiciones reales estan a menos de MARGEN_COLISION casillas
    private void verificaColisiones(){
        for (Fantasma fantasma : fantasmas) {
            if (fantasma.distanciaA(pacman) > MARGEN_COLISION) {
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
        estadoJuego = pacman.estaVivo() ? "COMIDO" : "PERDIDO";
    }

    private void reiniciarPosiciones(){
        pacman.reiniciarPos();
        for (Fantasma fantasma : fantasmas) {
            fantasma.reiniciarPos();
        }
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
