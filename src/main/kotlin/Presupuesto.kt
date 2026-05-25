import java.time.LocalDate

val PRESUPUESTO_GRANDE = 1000000
val MAIL_FINANCIAMIENTO = "financiamiento@xpace.com"

/**
 * @Author Nico
 * Provicional para que no me tire error creando smtp en Presupuesto.
 * Borrar e implementar en un archivo.
 */
class Sender: MailSender {
    override fun enviarMail(to: String, asunto: String, cuerpo: String) {
        TODO("Not yet implemented")
    }

}

class Presupuesto(val mision: Mision) {
    private val sistemasContables: MutableList<SistemaContable> = mutableListOf()
    private val smtp: MailSender =  Sender()

    var subvencionAplicada: Subvencion? = null
        private set

    fun asignarSubvencion(subvencion: Subvencion) {
        this.subvencionAplicada = subvencion
        smtp.enviarMail(MAIL_FINANCIAMIENTO, mision.nombre, "Se uso subvencion")
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