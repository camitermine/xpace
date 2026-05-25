import java.time.LocalDate

// INTERFACES DE SERVICIOS EXTERNOS

interface MailSender {
    fun enviarMail(to: String, asunto: String, cuerpo: String)
}

interface SistemaContable {
    fun informarMisionCara(codigo: String, presupuesto: Double, fecha: LocalDate)
}

// PATRÓN OBSERVER: ACCIONES DE LANZAMIENTO

interface AccionLanzamiento {
    fun ejecutar(mision: Mision)
}

class NotificarTripulantesPorMail(private val mailSender: MailSender) : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {
        val asunto = "Misión ${mision.nombre} lanzada"
        mision.tripulantes.forEach { tripulante ->
            val cuerpo = "Tripulante ${tripulante.nombre}, la misión ${mision.nombre} " +
                    "con destino al planeta ${mision.destino.nombre} ha sido lanzada exitosamente. " +
                    "Duración estimada: ${mision.duracion().toInt()} días."
            mailSender.enviarMail(tripulante.email, asunto, cuerpo)
        }
    }
}

class NotificarMisionPionera : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {
        // uso los métodos de negocio del Planeta
        if (mision.destino.esHabitable() && !mision.destino.fueAterrizado) {
            val asunto = "Misión de Colonización Pionera"
            val cuerpo = "Atención tripulante: la misión ${mision.nombre} se dirige al planeta habitable ${mision.destino.nombre} "+"por primera vez en la historia de la agencia."

            // Creo el mensaje con prioridad ALTA
            val mensajePionero = Mensaje(LocalDate.now(), asunto, cuerpo, Prioridad.ALTA)

            mision.tripulantes.forEach { it.recibirMensaje(mensajePionero) }
        }
    }
}

class EvaluarMantenimientoNave : AccionLanzamiento {
    override fun ejecutar(mision: Mision) {

        if (!mision.nave.esModerna() && mision.esDesgastante()) {
            mision.nave.solicitarMantenimiento()
        }
    }
}