import java.time.LocalDate

val PRESUPUESTO_GRANDE = 1000000

class Presupuesto(val mision: Mision) {
    private val sistemasContables: MutableList<SistemaContable> = mutableListOf()
    var subvencionAplicada: Subvencion? = null
        private set

    fun asignarSubvencion(subvencion: Subvencion) {
        this.subvencionAplicada = subvencion
    }

    fun agregarSistema(sist: SistemaContable) {
        sistemasContables.add(sist)
    }

    fun removerSistema(sist: SistemaContable) {
        sistemasContables.remove(sist)
    }

    private fun calcularCostoCombustible(): Double {
        return mision.nave.consumoDeCombustible(mision.destino.distanciaATierra, mision.tripulantes.size)
    }

    private fun calcularCostoSalarial(): Double {
        val duracionEnMeses = mision.duracion() / 30.0
        return mision.tripulantes.sumOf { it.calcularSalarioTotal() } * duracionEnMeses
    }

    private fun aplicarRecargos(montoBase: Double): Double {
        var montoFinal = montoBase
        if (!mision.destino.esHabitable()) montoFinal *= 1.20
        if (!mision.nave.esModerna()) montoFinal *= 1.10
        return montoFinal
    }

    fun calcularPresupuestoFijo(): Double {
        val costoBase = calcularCostoCombustible() + calcularCostoSalarial()
        return aplicarRecargos(costoBase)
    }

    fun calcularPresupuesto(): Double {
        val presupuestoFijo = calcularPresupuestoFijo()
        val montoSubvencion = subvencionAplicada?.calcularMontoTotal(mision) ?: 0.0
        val presupuesto = presupuestoFijo - montoSubvencion

        if (presupuesto > PRESUPUESTO_GRANDE) sistemasContables.forEach { e ->
            e.informarMisionCara(
                "",
                presupuesto,
                LocalDate.now()
            )
        }

        return presupuesto
    }
}