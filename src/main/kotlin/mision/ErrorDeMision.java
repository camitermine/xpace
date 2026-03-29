package mision;

public class ErrorDeMision extends Exception {
    public ErrorDeMision(String mensaje) {
        super("ERROR EN LA GESTION DE LA MISION: " + mensaje.toUpperCase());
    }
}