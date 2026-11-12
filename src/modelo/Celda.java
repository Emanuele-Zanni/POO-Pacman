package modelo;

public class Celda {
    private boolean esPared;
    private String contenido;

    public Celda(boolean esPared, String contenido){
        this.esPared = esPared;
        this.contenido = contenido;

    }

    
    public void removerItem(String item) {
        if (contenido.equals(item)) {
            contenido = "";
        }
    }
    
    public boolean esPared() {
        return esPared;
    }

    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    //!
    public boolean estaVacia() {
        return !esPared && "".equals(contenido);
    }
    
    //! 
    public void setPared(boolean esPared) {
        this.esPared = esPared;
    }

}


