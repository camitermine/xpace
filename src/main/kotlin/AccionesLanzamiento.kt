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

        val mensajePionero = Mensaje(LocalDate.now(), asunto, cuerpo, Prioridad.ALTA)
        mision.tripulantes.forEach { it.recibirMensaje(mensajePionero) }
    }
}
class EvaluarMantenimientoNave : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {

        if (mision.provocaMantenimientoNave()) {
            mision.nave.solicitarMantenimiento()
        }
    }
}