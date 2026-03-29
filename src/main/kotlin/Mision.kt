import java.time.LocalDate


const val IDA_Y_VUELTA = 2
const val DURACION_DE_ALTO_RIESGO = 500

class Mision {
    private val nombre: String
    private val descripción: String
    private val fecha_lanzamiento: LocalDate
    private val nave: Nave
    private val destino: Planeta
    private var estado: EstadoMision = EstadoMision.BORRADOR
    private var tripulantes: List<Tripulante>

    constructor(
        nombre: String,
        descripción: String,
        fecha_lanzamiento: LocalDate,
        nave: Nave,
        destino: Planeta,
        tripulantes: List<Tripulante>?
    ) {
        this.nombre = nombre
        this.descripción = descripción
        this.fecha_lanzamiento = fecha_lanzamiento
        this.nave = nave
        this.destino = destino
        this.tripulantes = tripulantes ?: emptyList()
    }
    fun consumoTotal(): Double {
        return nave.consumoPorAnioLuz(tripulantes.size) * destino.distanciaATierra
    }
    fun duracion(): Double = destino.radioKm * 365 / this.nave.velPromedio * IDA_Y_VUELTA
    fun altoRiesgo(): Boolean =/*!destino.habitable() && */ duracion() > DURACION_DE_ALTO_RIESGO

    fun cancelar() {
        if (altoRiesgo()) {
            tripulantes.forEach { tripulante ->
                tripulante.agregarMision(EstadoMision.PARCIALMENTE_EXITOSA)
            }
        }
        misionFinalizada(EstadoMision.CANCELADA)
    }

    fun completar() {
        destino.fueAterrizado = true
        misionFinalizada(EstadoMision.COMPLETADA)
    }

    fun fallo() {
        misionFinalizada(EstadoMision.FALLIDA)
    }

    fun lanzar() {
        try {

            if (this.estado !== EstadoMision.BORRADOR) {
                throw ErrorDeMision("La mision ya fue lanzada")
            }

            prepararNave()
            prepararTripulantes()

            this.estado = EstadoMision.EN_CURSO

        } catch (e: ErrorDeMision) {
            println("Fallo en el lanzamiento: ${e.message}")
        } catch (e: Exception) {
            println("Error inesperado durante el lanzamiento: ${e.message}")
        }
    }

    private fun misionFinalizada(estado: EstadoMision) {
        this.estado = estado
        nave.liberar()
    }

    private fun prepararNave() {
        if (!this.nave.puedeAlcanzar(this.destino.distanciaATierra)) throw ErrorDeMision("La nave no es capaz de alcanzar el destino de la mision")
        if (this.tripulantes.size > this.nave.capacidadTripulantes) throw ErrorDeMision("La nave no soporta " + this.tripulantes.size + " tripulantes")
        nave.asignar(this)
    }

    private fun prepararTripulantes() {
    }
}
