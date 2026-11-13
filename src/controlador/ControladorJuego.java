package controlador;

import DTOs.CeldaDTO;
import DTOs.FantasmaDTO;
import DTOs.InfoJuegoDTO;
import DTOs.LaberintoDTO;
import DTOs.PacmanDTO;
import modelo.Fantasma;
import modelo.Juego;
import modelo.Laberinto;
import modelo.Pacman;

public class ControladorJuego {

    private final Juego juego;

    public ControladorJuego(Juego juego) {
        this.juego = juego;
    }

    public InfoJuegoDTO iniciar() {
        return armarInfo();
    }

    // --- Clock/Reloj de la VISTA (motor) ---

    public InfoJuegoDTO avanzar(int ticks) {
        for (int i = 0; i < ticks; i++) {
            juego.actualizar();
        }
        return armarInfo();
    }

    // --- ACCIONES DEL USUARIO ---

    public InfoJuegoDTO cambiarDireccion(String direccion) {
        juego.ingresaInput(direccion);
        return armarInfo();
    }

    public InfoJuegoDTO pausarReanudar() {
        juego.pausarReanudar();
        return armarInfo();
    }

    public InfoJuegoDTO reiniciar() {
        juego.reiniciar();
        return armarInfo();
    }

    public InfoJuegoDTO continuar() {
        juego.continuar();
        return armarInfo();
    }

    //! Aca le pregunta el CONTROLADOR al NEGOCIO por los valores que se desean actualizar en VENTANA y los devuelve mediante return con un nuevo objeto
    //!  InfoJuegoDTO (que solo contiene DTOs: la ventana nunca recibe objetos del modelo)
    private InfoJuegoDTO armarInfo() {
        LaberintoDTO laberinto = aDTO(juego.getLaberinto());
        PacmanDTO pacman = aDTO(juego.getPacman());

        Fantasma[] fantasmasModelo = juego.getFantasmas();
        FantasmaDTO[] fantasmas = new FantasmaDTO[fantasmasModelo.length];
        for (int i = 0; i < fantasmasModelo.length; i++) {
            fantasmas[i] = aDTO(fantasmasModelo[i]);
        }

        int puntaje = juego.getPuntaje();
        String estado = juego.getEstadoJuego();

        return new InfoJuegoDTO(laberinto, pacman, fantasmas, puntaje, estado);
    }

    // --- Conversiones: copian del modelo solo los datos que la vista necesita ---

    private LaberintoDTO aDTO(Laberinto laberinto) {
        CeldaDTO[][] celdas = new CeldaDTO[laberinto.getFilas()][laberinto.getColumnas()];
        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[f].length; c++) {
                celdas[f][c] = new CeldaDTO(laberinto.esPared(f, c), laberinto.getContenido(f, c));
            }
        }
        return new LaberintoDTO(celdas);
    }

    private PacmanDTO aDTO(Pacman pacman) {
        return new PacmanDTO(pacman.getX(), pacman.getY(), pacman.getDireccion(), pacman.getVidas());
    }

    private FantasmaDTO aDTO(Fantasma fantasma) {
        return new FantasmaDTO(fantasma.getX(), fantasma.getY(), fantasma.getDireccion(),
                               fantasma.getColor(), fantasma.getEstado(), fantasma.getTiempoAsustado());
    }
}
