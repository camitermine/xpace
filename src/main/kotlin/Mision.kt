import java.time.LocalDate

private enum class MensajesDeError(val mensaje: String) {
    NOMBRE_INVALIDO("El nombre no puede estar vacío"),
    NAVE_INVALIDA("Una mision necesita una nave"),
    DESTINO_INVALIDO("Una mision necesita un planeta destino"),
    FECHA_LANZAMIENTO_INVALIDA("La fecha de lanzamiento debe ser posterior a hoy")
}

class Mision(
    val nombre: String,
    val descripcion: String,
    val fechaLanzamiento: LocalDate,
    val nave: Nave,
    val destino: Planeta,
) : Entidad {

    override var id: Int? = null

    var subvencionAplicada: Subvencion? = null
        private set

    var tripulantes: List<Tripulante> = emptyList()
    var estado: EstadoMision = EstadoMision.BORRADOR
        private set

    private fun validarConstruccion() {
        Validaciones.validarNoNull<Nave>(nave, MensajesDeError.NAVE_INVALIDA.mensaje)
        Validaciones.validarNoNull<Planeta>(destino, MensajesDeError.DESTINO_INVALIDO.mensaje)
        Validaciones.validarNoVacio(nombre, MensajesDeError.NOMBRE_INVALIDO.mensaje)
        Validaciones.validarFechaPosteriorAHoy(fechaLanzamiento, MensajesDeError.FECHA_LANZAMIENTO_INVALIDA.mensaje)
    }

    init {
        validarConstruccion()
        prepararNave()
    }

    fun lanzar() {
        if (this.estado !== EstadoMision.BORRADOR) throw ErrorDeMision("La mision ya fue lanzada")
        prepararNave()
        prepararTripulantes()
        this.estado = EstadoMision.EN_CURSO
    }

    fun fallo() {
        if (this.estado !== EstadoMision.EN_CURSO) throw ErrorDeMision("La mision no esta en curso")
        misionFinalizada(EstadoMision.FALLIDA)
    }

    fun cancelar() {
        if (this.estado !== EstadoMision.EN_CURSO) throw ErrorDeMision("La mision no esta en curso")
        misionFinalizada(EstadoMision.CANCELADA)
    }

    fun completar() {
        if (this.estado !== EstadoMision.EN_CURSO) throw ErrorDeMision("La mision no esta en curso")
        destino.fueAterrizado = true
        misionFinalizada(EstadoMision.COMPLETADA)
    }

    fun duracion(): Double =
        destino.distanciaATierra * Config.DIAS_DEL_ANIO / this.nave.velPromedio * Config.IDA_Y_VUELTA

    fun altoRiesgo(): Boolean = !destino.esHabitable() && duracion() > Config.DURACION_DE_ALTO_RIESGO

    fun sumarTripulacion(tripulacion: List<Tripulante>) {
        val nuevaTripulacion = this.tripulantes + tripulacion
        tripulacionAptaParaNave(nuevaTripulacion)
        this.tripulantes = nuevaTripulacion
    }

    private fun tripulacionAptaParaNave(tripulacion: List<Tripulante>) {
        if (!nave.puedeTransportar(tripulacion.size)) {
            throw ErrorDeMision("La nave ${nave.nombre} no soporta ${tripulacion.size} tripulantes")
        }
    }

    private fun agregarMisionATripulante(estado: EstadoMision) {
        val esCancelacionSinRiesgo = estado == EstadoMision.CANCELADA && !altoRiesgo()
        if (!esCancelacionSinRiesgo) this.tripulantes.forEach { it.agregarMision(this) }
    }

    private fun misionFinalizada(estado: EstadoMision) {
        agregarMisionATripulante(estado)
        this.estado = estado
        nave.liberar()
    }

    private fun prepararNave() {
        if (!this.nave.puedeAlcanzar(this.destino.distanciaATierra)) throw ErrorDeMision("La nave no es capaz de alcanzar el destino de la mision")
        nave.asignar(this)
    }

    private fun prepararTripulantes() {
        tripulacionAptaParaNave(this.tripulantes)
        if (this.tripulantes.isEmpty()) return
        val base = this.tripulantes.first().baseLanzamiento
        if (!base.tieneNaveEstacionada(nave)) throw ErrorDeMision("La nave debe estar estacionada en la base de los tripulantes")
        val tripulanteInvalido = tripulantes.firstOrNull { it.baseLanzamiento != base || !it.esAptoParaMision(this) }
        if (tripulanteInvalido == null) return

        if (tripulanteInvalido.baseLanzamiento != base) {
            throw ErrorDeMision("Todos los tripulantes deben pertenecer a la misma base")
        }

        if (!tripulanteInvalido.esAptoParaMision(this)) {
            throw ErrorDeMision("Hay tripulantes que no son aptos para esta misión")
        }
    }
    // --- NUEVOS MÉTODOS (ENTREGA 2) ---

    fun esApto(tripulante: Tripulante): Boolean {
        return tripulante.perfilAptitud.aceptaMision(this, tripulante)
    }

    fun asignarSubvencion(subvencion: Subvencion) {
        this.subvencionAplicada = subvencion
    }

    fun calcularPresupuestoFijo(): Double {
        val costoCombustible = nave.consumoDeCombustible(this);
        val costoSalarial = tripulantes.sumOf { it.calcularSalarioTotal() } * (duracion() / 30.0)
        var presupuesto = costoCombustible + costoSalarial
        if (!destino.esHabitable()) presupuesto *= 1.20
        if (!nave.esModerna()) presupuesto *= 1.10

        return presupuesto
    }
}