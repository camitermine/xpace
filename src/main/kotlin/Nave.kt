import java.time.LocalDate
import java.time.Period

abstract class Nave(
    val nombre: String, val codIdentif: Int, val fechaFabricacion: LocalDate,
    val consumoBase: Double, val autonomia: Int, val velPromedio: Double) {

    abstract val capacidadTripulantes: Int
    private var misionAsignada: Mision? = null

    init {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(codIdentif > 0) { "El código debe ser mayor a 0" }
        require(consumoBase > 0) { "El consumo base debe ser mayor a 0" }
        require(autonomia > 0) { "La autonomía debe ser mayor a 0" }
        require(velPromedio > 0) { "La velocidad promedio debe ser mayor a 0" }
    }
    abstract fun consumoPorAnioLuz(cantTripulantes: Int): Double
    fun asignar(mision: Mision) {
        this.misionAsignada = mision
    }
    fun liberar() {
        this.misionAsignada = null
    }
    fun antiguedad(): Int = Period.between(fechaFabricacion, LocalDate.now()).years
    //fun consumoTotal(distancia: Double): Double = consumoPorAnioLuz(cantTripulantes =1 ) * distancia
    fun puedeAlcanzar(distancia: Double): Boolean {
        val tiempoViaje = (distancia * 365) / velPromedio * 2
        return tiempoViaje <= autonomia
    }
}
class Sonda(
    nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double, autonomia: Int,
    velPromedio: Double
) : Nave
    (nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 0
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double = consumoBase
}
class Transbordador(nombre: String, codIdentif: Int, fechaFabricacion: LocalDate, consumoBase: Double,
                    autonomia: Int, velPromedio: Double
) : Nave(nombre, codIdentif, fechaFabricacion, consumoBase, autonomia, velPromedio) {
    override val capacidadTripulantes: Int = 20
    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        require(cantTripulantes <= capacidadTripulantes) { "Excede la capacidad" }
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
        require(capacidadTn >= 0) { "La capacidad de carga no puede ser negativa" }
    }
    fun esAntigua(): Boolean = antiguedad() > 10
    fun consumoPorCarga(): Double =
        consumoBase + (consumoBase * 0.05 * capacidadTn)
    fun factorAntiguedad(): Double =
        if (esAntigua()) 1.20 else 1.0

    override fun consumoPorAnioLuz(cantTripulantes: Int): Double {
        val consumo = consumoBase + (consumoBase * 0.05 * capacidadTn)
        val factor = if (antiguedad() > 10) 1.20 else 1.0
        return consumo * factor
    }
}