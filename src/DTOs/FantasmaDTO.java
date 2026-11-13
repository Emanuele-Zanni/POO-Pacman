package DTOs;

public class FantasmaDTO extends PersonajeDTO {
    private final int color;       
    private final String estado;        
    private final int tiempoAsustado;   

    public FantasmaDTO(double x, double y, String direccion,
                       int color, String estado, int tiempoAsustado) {
        super(x, y, direccion);
        this.color = color;
        this.estado = estado;
        this.tiempoAsustado = tiempoAsustado;
    }

    public int getColor() {
        return color;
    }

    public String getEstado() {
        return estado;
    }

    public int getTiempoAsustado() {
        return tiempoAsustado;
    }
}
