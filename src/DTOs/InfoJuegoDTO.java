package DTOs;

public class InfoJuegoDTO {
    private final LaberintoDTO laberinto;
    private final PacmanDTO pacman;
    private final FantasmaDTO[] fantasmas;
    private final int puntaje;
    private final String estado;

    public InfoJuegoDTO(LaberintoDTO laberinto, PacmanDTO pacman, FantasmaDTO[] fantasmas,
                        int puntaje, String estado) {
        this.laberinto = laberinto;
        this.pacman = pacman;
        this.fantasmas = fantasmas;
        this.puntaje = puntaje;
        this.estado = estado;
    }

    public LaberintoDTO getLaberinto() {
        return laberinto;
    }

    public PacmanDTO getPacman() {
        return pacman;
    }

    public FantasmaDTO[] getFantasmas() {
        return fantasmas;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public String getEstado() {
        return estado;
    }
}
