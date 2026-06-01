import java.time.LocalDate

abstract class Subvencion(
    val nombre: String,
    val montoBase: Double,
    val fechaEmision: LocalDate,
    val duracionDias: Int
) : Entidad {

    override var id: Int? = null

    override fun coincideCon(criterio: String): Boolean {
        return nombre.startsWith(criterio, ignoreCase = true)
    }

    var estaAplicada: Boolean = false
        private set

    fun estaVigente(): Boolean = LocalDate.now().isBefore(fechaEmision.plusDays(duracionDias.toLong()))

    fun puedeAplicarseA(mision: Mision): Boolean {
        return estaVigente() &&
                !estaAplicada &&
                montoBase <= mision.calcularPresupuestoFijo() &&
                cumpleCondicionParticular(mision)
    }

    fun aplicar(mision: Mision) {
        if (!puedeAplicarseA(mision)) throw Exception("La subvención no es aplicable a esta misión")
        this.estaAplicada = true
        mision.asignarSubvencion(this)
    }

    fun calcularMontoTotal(mision: Mision): Double = montoBase + calcularMontoAdicional(mision)

    protected abstract fun cumpleCondicionParticular(mision: Mision): Boolean
    protected abstract fun calcularMontoAdicional(mision: Mision): Double
}

class SubvencionGubernamental(nombre: String, montoBase: Double, fechaEmision: LocalDate, duracionDias: Int)
    : Subvencion(nombre, montoBase, fechaEmision, duracionDias) {
    override fun cumpleCondicionParticular(mision: Mision) = !mision.destino.fueAterrizado
    override fun calcularMontoAdicional(mision: Mision) = if (mision.destino.esHabitable()) montoBase * 0.30 else montoBase * 0.15
}

class SubvencionPrivada(nombre: String, montoBase: Double, fechaEmision: LocalDate, duracionDias: Int)
    : Subvencion(nombre, montoBase, fechaEmision, duracionDias) {
    override fun cumpleCondicionParticular(mision: Mision): Boolean {
        val expPromedio = mision.tripulantes.map { it.calcularExperiencia() }.average()
        return expPromedio > 5.0
    }
    override fun calcularMontoAdicional(mision: Mision): Double {
        val veteranos = mision.tripulantes.count { it.calcularAniosActivo() > 10 }
        return minOf(veteranos * 10000.0, 50000.0)
    }
}

class SubvencionEmergencia(nombre: String, montoBase: Double, fechaEmision: LocalDate, duracionDias: Int)
    : Subvencion(nombre, montoBase, fechaEmision, duracionDias) {
    override fun cumpleCondicionParticular(mision: Mision) = true
    override fun calcularMontoAdicional(mision: Mision) = mision.calcularPresupuestoFijo() * 0.50
}