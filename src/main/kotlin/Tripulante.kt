import mision.EstadoMisionTripulante
import java.time.LocalDate
import java.time.Period

class Tripulante (val nombre: String,
                  val apellido: String,
                  val fechaNacimiento: LocalDate,
                  val fechaInicioActividad: LocalDate)
{
    init {
        require(fechaNacimiento.isBefore(LocalDate.now()))
        {"La fecha de nacimiento debe ser anterior a hoy"}
    }



    val misiones: MutableCollection<EstadoMisionTripulante> = mutableListOf<EstadoMisionTripulante>()
    fun agregarMision(estado: EstadoMisionTripulante) {
        misiones.add(estado)
    }
    fun calcularEdad(): Int {
        return Period.between(fechaNacimiento, LocalDate.now()).years
    }
    fun calcularAniosActivo(): Int {
        return Period.between(fechaInicioActividad, LocalDate.now()).years
    }
    fun cantidadMisiones(estado: EstadoMisionTripulante): Int {
        return misiones.count { it == estado }
    }
    fun calcularExperiencia(): Double {
        val exitosas = cantidadMisiones(EstadoMisionTripulante.EXITOSA)
        val parciales = cantidadMisiones(EstadoMisionTripulante.PARCIALMENTE_EXITOSA)
        val fracasadas = cantidadMisiones(EstadoMisionTripulante.FRACASADA)

        return  calcularAniosActivo() +
                (exitosas / 2.0) +
                (fracasadas / 2.0) +
                (parciales / 4.0)
    }
}