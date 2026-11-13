package DTOs;

public abstract class PersonajeDTO {
    private final double x;          
    private final double y;          
    private final String direccion;   

    public PersonajeDTO(double x, double y, String direccion) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getDireccion() {
        return direccion;
    }
}
