import java.time.LocalDate

fun main() {

    println("=== PRUEBA DE TRIPULANTE ===")

    val tripulante = Tripulante(
        "Juan",
        "Perez",
        LocalDate.of(1990, 5, 10),
        LocalDate.of(2015, 3, 1)
    )

    tripulante.agregarMision(EstadoMision.EXITOSA)
    tripulante.agregarMision(EstadoMision.EXITOSA)
    tripulante.agregarMision(EstadoMision.FRACASADA)
    tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)

    println("Edad: ${tripulante.calcularEdad()}")
    println("Años activo: ${tripulante.calcularAniosActivo()}")
    println("Misiones exitosas: ${tripulante.cantidadMisiones(EstadoMision.EXITOSA)}")
    println("Experiencia: ${tripulante.calcularExperiencia()}")

    println()
    println("=== PRUEBA DE PLANETA ===")

    val planeta = Planeta(
        "Kepler-452b",
        25.0,
        9.8,
        40,
        true,
        20,
        10,
        6371.0,
        LocalDate.of(2015,7,23),
        1400.0,
        false
    )

    println("Temperatura ideal: ${planeta.tieneTemperaturaIdeal()}")
    println("Gravedad soportable: ${planeta.gravedadSoportable()}")
    //println("Tiene agua líquida: ${planeta.tieneAguaLiquida()}")
}