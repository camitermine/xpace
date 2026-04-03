import java.time.LocalDate
import java.time.Period

abstract class Nave(
    val nombre: String, val codIdentif: Int, val fechaFabricacion: LocalDate,
    val consumoBase: Double, val autonomia: Int, val velPromedio: Double
) {
    abstract val capacidadTripulantes: Int

    var misionAsignada: Mision? = null
        private set

    init {
        validar()
    }
    private fun validarNombre() {
        validarAtributo(nombre.isNotBlank(), "El nombre no puede estar vacío")
    }
    private fun validarCodIdentif() {
        validarAtributo(codIdentif > 0, "El código debe ser mayor a 0")
    }
    private fun validarConsumoBase() {
        validarAtributo(consumoBase > 0, "El consumo base debe ser mayor a 0")
    }
    private fun validarAutonomia() {
        validarAtributo(autonomia > 0, "La autonomía debe ser mayor a 0")
    }
    private fun validarVelocidad() {
        validarAtributo(velPromedio > 0, "La velocidad promedio debe ser mayor a 0")
    }
    private fun validar() {
        validarNombre()
        validarCodIdentif()
        validarConsumoBase()
        validarAutonomia()
        validarVelocidad()
    }

    abstract fun consumoPorAnioLuz(cantTripulantes: Int): Double

    fun asignar(mision: Mision) {
        this.misionAsignada = mision
    }

    fun liberar() {
        this.misionAsignada = null
    }

    fun antiguedad(): Int = Period.between(fechaFabricacion, LocalDate.now()).years

    fun esModerna(): Boolean = antiguedad() < 5

    fun tiempoDeViaje(distancia: Double): Double {
        return (distancia * 365) / velPromedio * 2
    }
    fun puedeAlcanzar(distancia: Double): Boolean {
        return tiempoDeViaje(distancia) <= autonomia
    }

    open fun puedeTransportar(tripulacion: Int): Boolean =
        tripulacion in 1..capacidadTripulantes
}
class Sonda(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double, autonomia: Int,
    velPromedio: Double
) : Nave
    (nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 0
    override fun puedeTransportar(tripulacion: Int) = tripulacion == 0
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double = consumoBase
}
class Transbordador(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double,
    autonomia: Int, velPromedio: Double
) : Nave(nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 20
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        validarReglaDeNegocio(
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
        validarAtributo(capacidadTn >= 0, "La capacidad de carga no puede ser negativa")
    }
    fun esAntigua(): Boolean = antiguedad() > 10
    fun consumoPorCarga(): Double =
        consumoBase + (consumoBase * 0.05 * capacidadTn)

    fun factorAntiguedad(): Double =
        if (esAntigua()) 1.20 else 1.0

    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        return consumoPorCarga() * factorAntiguedad()
    }
}