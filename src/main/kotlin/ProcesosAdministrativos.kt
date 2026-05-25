import com.fasterxml.jackson.module.kotlin.readValue
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
        val limiteFecha = LocalDate.now().minusDays(30)

        tripulantes.forEach { tripulante ->
            val mensajesParaBorrar = tripulante.inbox().filter {
                it.leido && it.fechaEmision.isBefore(limiteFecha)
            }
            tripulante.eliminarMensajes(mensajesParaBorrar)
        }
    }
}

// 2. Reparar Naves con requerimiento de mantenimiento (si no están en misión)
class RepararNaves(
    private val baseLanzamiento: BaseLanzamiento,
    mailSender: MailSender
) : ProcesoAdministracion(mailSender) {

    override fun nombreProceso() = "Reparar Naves con requerimiento de mantenimiento"

    override fun ejecutarProceso() {
        baseLanzamiento.navesEstacionadas
            .filter { it.requiereMantenimiento && it.misionAsignada == null }
            .forEach { it.reparar() }
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
        val jsonPlanetas = servicePlaneta.getPlanetas()

        // convertimos los datos usando el mapper que ya esta configurado en el RepositorioPlaneta
        val planetasNuevos: List<Planeta> = repositorioPlaneta.mapper.readValue(jsonPlanetas)

        planetasNuevos.forEach { planeta ->
            repositorioPlaneta.guardarPlaneta(planeta)
        }
    }
}

