package DTOs;

public class PacmanDTO extends PersonajeDTO {
    private final int vidas;

    public PacmanDTO(double x, double y, String direccion, int vidas) {
        super(x, y, direccion);
        this.vidas = vidas;
    }

    public int getVidas() {
        return vidas;
    }
}
