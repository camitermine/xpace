import java.time.LocalDate

interface MailSender {
    fun enviarMail(to: String, asunto: String, cuerpo: String)
}

interface SistemaContable {
    fun informarMisionCara(codigo: String, presupuesto: Double, fecha: LocalDate)
}

interface AccionLanzamiento {
    fun ejecutar(mision: Mision)
}

class NotificarTripulantesPorMail(private val mailSender: MailSender) : AccionLanzamiento {

    override fun ejecutar(mision: Mision) {
        val asunto = "Misión ${mision.nombre} lanzada"

        mision.tripulantes.forEach { tripulante ->
            val cuerpo = redactarCuerpo(tripulante, mision)
            mailSender.enviarMail(tripulante.email, asunto, cuerpo)
        }
    }
    private fun redactarCuerpo(tripulante: Tripulante, mision: Mision): String {
        return "Tripulante ${tripulante.nombre}, la misión ${mision.nombre} " +
                "con destino al planeta ${mision.nombreDestino()} ha sido lanzada exitosamente. " +
                "Duración estimada: ${mision.duracion().toInt()} días."
    }
}

class NotificarMisionPionera : AccionLanzamiento {

    override fun ejecutar(mision: Mision) {
        if (!mision.esPionera()) return

        val asunto = "Misión de Colonización Pionera"
        val cuerpo = "Atención tripulante: la misión ${mision.nombre} se dirige al planeta habitable ${mision.nombreDestino()} " +
                "por primera vez en la historia de la agencia."

        mision.tripulantes.forEach { it.recibirMensaje(Mensaje(LocalDate.now(), asunto, cuerpo, Prioridad.ALTA)) }
    }
}
class EvaluarMantenimientoNave : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {

        if (mision.provocaMantenimientoNave()) {
            mision.nave.solicitarMantenimiento()
        }
    }
}
class InformarMisionCara(private val sistemaContable: SistemaContable) : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {
        val presupuesto = mision.calcularPresupuestoFijo()
        if (presupuesto > 1000000.0) {
            // Uso el nombre de la misión como código para informar al sistema
            sistemaContable.informarMisionCara(mision.nombre, presupuesto, LocalDate.now())
        }
    }
}

class PromoverAVeterano(private val maxDiasConfigurables: Int = 300) : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {
        if (mision.altoRiesgo()) {
            mision.tripulantes.forEach { tripulante ->
                // Actualiza el estado usando la clase Veterano que ya estaba
                tripulante.hacerVeterano(maxDiasConfigurables)
            }
        }
    }
}

class NotificarFinanciamiento(private val mailSender: MailSender) : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {
        val subvencion = mision.subvencionAplicada
        if (subvencion != null) {
            val asunto = "Misión ${mision.nombre} con subvención"
            val cuerpo = "Se informa que la misión ${mision.nombre} fue lanzada utilizando la subvención: ${subvencion.nombre}."

            mailSender.enviarMail("financiamiento@xpace.com", asunto, cuerpo)
        }
    }
}