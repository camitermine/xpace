import java.time.LocalDate

// TEMPLATE METHOD + COMMAND

abstract class ProcesoAdministracion(private val mailSender: MailSender) {

    // TEMPLATE METHOD: Define el esqueleto del algoritmo de ejecución
    fun ejecutar() {
        ejecutarProceso()
        enviarMailNotificacion()
    }

    // Paso primitivo: Cada proceso implementa su tarea específica
    protected abstract fun ejecutarProceso()

    // Cada proceso define su nombre exacto para armar el mail de forma dinámica
    protected abstract fun nombreProceso(): String

    private fun enviarMailNotificacion() {
        val tipoProceso = nombreProceso()
        mailSender.enviarMail(
            to = "admin@xpace.com",
            asunto = "Se realizó el proceso: $tipoProceso",
            cuerpo = "Se realizó el proceso: $tipoProceso"
        )
    }
}

// PROCESOS ADMINISTRATIVOS CONCRETOS

// 1. Borrar Mensajes antiguos y leídos (más de 30 días)
class BorrarMensajesAntiguos(
    private val tripulantes: List<Tripulante>,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Borrar Mensajes antiguos y leídos"

    override fun ejecutarProceso() {
        val limiteFecha = LocalDate.now().minusDays(Config.CANT_DIAS_ANTIGUEDAD_MAIL)

        tripulantes.forEach { it.limpiarMensajesObsoletos(limiteFecha) }
    }
}

class RepararNaves(
    private val baseLanzamiento: BaseLanzamiento,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Reparar Naves con requerimiento de mantenimiento"

    override fun ejecutarProceso() {

        baseLanzamiento.repararNavesDisponibles()
    }
}

// 3. Actualización de Planetas mediante ServicePlanetas externo
class ActualizacionPlanetas(
    private val repositorioPlaneta: RepositorioPlaneta,
    private val servicePlaneta: ServicePlaneta,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Actualización de Planetas"

    override fun ejecutarProceso() {
        repositorioPlaneta.importarPlanetas(servicePlaneta)
    }
}

class BorrarSubvencionesVencidas(
    private val repositorioSubvencion: RepositorioSubvencion,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Borrar Subvenciones vencidas sin aplicar"

    override fun ejecutarProceso() {
        repositorioSubvencion.getAll()
            .filter { it.esBorrable() }
            .forEach { repositorioSubvencion.delete(it) }
    }
}

class RetirarTripulantesInactivos(
    private val repositorioTripulante: RepositorioTripulante,
    private val repositorioMision: RepositorioMision,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Retirar Tripulantes inactivos"

    override fun ejecutarProceso() {
        val dosAniosAtras = LocalDate.now().minusYears(2)
        val todasLasMisiones = repositorioMision.getAll()

        repositorioTripulante.getAll()
            .filter { tripulante -> todasLasMisiones.none { it.tuvoComoTripulanteDespuesDe(tripulante, dosAniosAtras) } }
            .forEach { repositorioTripulante.delete(it) }
    }
}

class AgregarNavesMasivas(
    private val naves: List<Nave>,
    private val repositorioNave: RepositorioNave,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Agregar Naves masivas"

    override fun ejecutarProceso() {
        naves.forEach { repositorioNave.create(it) }
    }
}



