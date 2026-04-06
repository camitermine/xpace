import java.time.LocalDate
import java.time.Period

private enum class MensajesDeErrorNave(val mensaje: String) {

    NOMBRE_INVALIDO("El nombre no puede estar vacío"),
    COD_IDENTIFICACION_INVALIDO("El código debe ser mayor a 0"),
    CONSUMO_BASE_INVALIDO("El consumo base debe ser mayor a 0"),
    AUTONOMIA_INVALIDA("La autonomía debe ser mayor a 0"),
    VEL_PROMEDIO_INVALIDA("La velocidad promedio debe ser mayor a 0")
}

abstract class Nave(
    val nombre: String, val codIdentif: Int, val fechaFabricacion: LocalDate,
    val consumoBase: Double, val autonomia: Int, val velPromedio: Double
) {
    abstract val capacidadTripulantes: Int

    var misionAsignada: Mision? = null
        private set

    private fun validarConstruccion() {

        Validaciones.validarNoVacio(nombre, MensajesDeErrorNave.NOMBRE_INVALIDO.mensaje)
        Validaciones.validarMayorACero( codIdentif,MensajesDeErrorNave.COD_IDENTIFICACION_INVALIDO.mensaje )
        Validaciones.validarMayorACero(consumoBase,MensajesDeErrorNave.CONSUMO_BASE_INVALIDO.mensaje)
        Validaciones.validarMayorACero(autonomia,MensajesDeErrorNave.AUTONOMIA_INVALIDA.mensaje)
        Validaciones.validarMayorACero(velPromedio,MensajesDeErrorNave.VEL_PROMEDIO_INVALIDA.mensaje)

    }

    init {
        validarConstruccion()
    }

    abstract fun consumoPorAnioLuz(cantTripulantes: Int): Double

    fun asignar(mision: Mision) {
        this.misionAsignada = mision
    }

    fun liberar() {
        this.misionAsignada = null
    }

    fun antiguedad(): Int = Period.between(fechaFabricacion, LocalDate.now()).years

    fun esModerna(): Boolean = antiguedad() < Config.MAX_NAVE_MODERNA

    fun tiempoDeViaje(distancia: Double): Double {
        return (distancia * Config.DIAS_DEL_ANIO) / velPromedio * 2
    }
    fun puedeAlcanzar(distancia: Double): Boolean {
        return tiempoDeViaje(distancia) <= autonomia
    }

    fun requiereTripulacion(): Boolean = capacidadTripulantes > 0
    open fun puedeTransportar(tripulacion: Int): Boolean {
        Validaciones.validarMayorOIgualACero(
            tripulacion,
            "La tripulación no puede ser negativa"
        )
        return tripulacion in 1..capacidadTripulantes
    }
}
class Sonda(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double, autonomia: Int,
    velPromedio: Double
) : Nave
    (nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 0
    override fun puedeTransportar(tripulacion: Int): Boolean {
        Validaciones.validarMayorOIgualACero(
            tripulacion,
            "La tripulación no puede ser negativa"
        )
        return tripulacion == 0
    }
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double = consumoBase
}
class Transbordador(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double,
    autonomia: Int, velPromedio: Double,
    val capacidadMaxTripulantes: Int = 20
) : Nave(nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = capacidadMaxTripulantes
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        Validaciones.validarReglaNegocio(
            cantTripulantes <= capacidadTripulantes,
            "Excede la capacidad de la nave"
        )
        return consumoBase + (consumoBase * 0.10 * cantTripulantes)
    }
}
class Carguero(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double,
    autonomia: Int, velPromedio: Double, val capacidadTn: Double
) : Nave
    (nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 1

    init {
        validarCapacidadCarga()
    }
    private fun validarCapacidadCarga() {
        Validaciones.validarMayorACero(capacidadTn, "La capacidad de carga no puede ser negativa")
    }
    fun esAntigua(): Boolean = antiguedad() > Config.MAX_NAVE_MODERNA_CARGUERO
    fun consumoPorCarga(): Double =
        consumoBase + (consumoBase * 0.05 * capacidadTn)

    fun factorAntiguedad(): Double =
        if (esAntigua()) Config.FACTOR_ANTIGUEDAD_CARGUERO else Config.FACTOR_MODERNO_CARGUERO

    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        return consumoPorCarga() * factorAntiguedad()
    }
}