import mision.EstadoMision
import java.time.LocalDate
import java.time.Period

class Tripulante (val nombre: String, val apellido: String, val fechaNacimiento: LocalDate,
                  val fechaInicioActividad: LocalDate)
{
    protected val misiones: MutableCollection<EstadoMision> = mutableListOf<EstadoMision>()
    fun agregarMision(estado: EstadoMision) {
        misiones.add(estado)
    }
    fun calcularEdad(): Int {
        return Period.between(fechaNacimiento, LocalDate.now()).years
    }
    fun calcularAniosActivo(): Int {
        return Period.between(fechaInicioActividad, LocalDate.now()).years
    }
    fun cantidadMisiones(estado: EstadoMision): Int {
        return misiones.count { it == estado }
    }
    fun calcularExperiencia(): Double {
        val exitosas = cantidadMisiones(EstadoMision.EXITOSA)
        val parciales = cantidadMisiones(EstadoMision.PARCIALMENTE_EXITOSA)
        val fracasadas = cantidadMisiones(EstadoMision.FRACASADA)

        return  calcularAniosActivo() +
                (exitosas / 2.0) +
                (fracasadas / 2.0) +
                (parciales / 4.0)
    }
}