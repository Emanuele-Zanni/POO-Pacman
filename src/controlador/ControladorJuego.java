package controlador;

import gui.VentanaJuego;
import modelo.Juego;

public class ControladorJuego {

    private final Juego juego;
    private final VentanaJuego vista;

    public ControladorJuego(Juego juego, VentanaJuego vista) {
        this.juego = juego;
        this.vista = vista;
        vista.setControlador(this);
    }

    public void iniciar() {
        actualizarVista();
        vista.iniciar();
    }

    // --- Acciones disparadas por el reloj de la vista ---

    public void avanzarPacman() {
        juego.actualizar();
        actualizarVista();
    }

    public void avanzarFantasmas() {
        juego.moverFantasmas();
        actualizarVista();
    }

    // --- Acciones disparadas por el usuario ---

    public void cambiarDireccion(String direccion) {
        juego.continuar();
        juego.ingresaInput(direccion);
        actualizarVista();
    }

    public void pausarReanudar() {
        juego.continuar();
        juego.pausarReanudar();
        actualizarVista();
    }

    public void reiniciar() {
        juego.reiniciar();
        actualizarVista();
    }

    public void continuar() {
        juego.continuar();
        actualizarVista();
    }

    public void salir() {
        vista.cerrar();
    }

    // Pide los datos al modelo y se los entrega a la vista
    private void actualizarVista() {
        vista.actualizar(
            juego.getLaberinto(),
            juego.getPacman(),
            juego.getFantasmas(),
            juego.getPuntaje(),
            juego.getEstadoJuego()
        );
    }
}
