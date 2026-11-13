package DTOs;

public class CeldaDTO {
    private final boolean esPared;
    private final String contenido;

    public CeldaDTO(boolean esPared, String contenido) {
        this.esPared = esPared;
        this.contenido = contenido;
    }

    public boolean esPared() {
        return esPared;
    }

    public String getContenido() {
        return contenido;
    }
}
