import java.time.LocalDate

const val IDA_Y_VUELTA = 2
const val DURACION_DE_ALTO_RIESGO = 500

class Mision(
    private val nombre: String,
    private val descripcion: String,
    private val fechaLanzamiento: LocalDate,
    val nave: Nave,
    val destino: Planeta,
    private var tripulantes: List<Tripulante> = emptyList()
) {

    var estado: EstadoMision = EstadoMision.BORRADOR
        private set

    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(fechaLanzamiento.isAfter(LocalDate.now())) {
            "La fecha de lanzamiento debe ser posterior a hoy"
        }
    }


    fun duracion(): Double = destino.distanciaATierra * 365 / this.nave.velPromedio * IDA_Y_VUELTA
    fun altoRiesgo(): Boolean = !destino.esHabitable() && duracion() > DURACION_DE_ALTO_RIESGO
    fun fallo() = misionFinalizada(EstadoMision.FALLIDA)
    fun cancelar() {
        if (this.estado !== EstadoMision.EN_CURSO) throw ErrorDeMision("La mision no esta en curso")
        misionFinalizada(EstadoMision.CANCELADA)
    }

    fun completar() {
        destino.fueAterrizado = true
        misionFinalizada(EstadoMision.COMPLETADA)
    }

    fun lanzar() {
        if (this.estado !== EstadoMision.BORRADOR) throw ErrorDeMision("La mision ya fue lanzada")
        prepararNave()
        prepararTripulantes()
        this.estado = EstadoMision.EN_CURSO

    }

    private fun agregarMisionATripulante(estado: EstadoMision) {
        val esCancelacionSinRiesgo = estado == EstadoMision.CANCELADA && !altoRiesgo()

        if (!esCancelacionSinRiesgo) {
            tripulantes.forEach { it.agregarMision(this) }
        }
    }

    private fun misionFinalizada(estado: EstadoMision) {
        try {
            agregarMisionATripulante(estado)
        } finally {
            this.estado = estado
            nave.liberar()
        }
    }

    private fun prepararNave() {
        if (!this.nave.puedeAlcanzar(this.destino.distanciaATierra)) throw ErrorDeMision("La nave no es capaz de alcanzar el destino de la mision")
        if (this.tripulantes.size > this.nave.capacidadTripulantes) throw ErrorDeMision("La nave no soporta " + this.tripulantes.size + " tripulantes")
        nave.asignar(this)
    }

    private fun prepararTripulantes() {
        if (tripulantes.isEmpty()) return

        val base = tripulantes.first().baseLanzamiento

        tripulantes.forEach {
            if (!it.esAptoParaMision(this)) throw ErrorDeMision("Hay tripulantes que no son aptos para esta misión")
            if (it.baseLanzamiento != base) throw ErrorDeMision("Todos los tripulantes deben pertenecer a la misma base")
        }

        if (!base.tieneNaveEstacionada(nave)) throw ErrorDeMision("La nave debe estar estacionada en la base de los tripulantes")

    }

}