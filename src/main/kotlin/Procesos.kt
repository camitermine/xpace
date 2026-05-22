abstract class Proceso {
    abstract val nombreProceso: String

    
    fun ejecutar(servicioMail: ServicioMail) {
        this.ejecutarLogica()
        servicioMail.enviar(
            de = "sistema@xpace.com",
            para = "admin@xpace.com",
            asunto = "Se realizó el proceso: $nombreProceso",
            cuerpo = "El proceso $nombreProceso se ha ejecutado."
        )
    }
    

    protected abstract fun ejecutarLogica()
}

class BorrarMensajesAntiguos(val repositorioTripulante: RepositorioTripulante) : Proceso() {
    override val nombreProceso = "Borrar Mensajes Antiguos y Leídos"

    override fun ejecutarLogica() {
        
    }
}

class RepararNaves(val baseLanzamiento: BaseLanzamiento) : Proceso() {
    override val nombreProceso = "Reparar Naves con requerimiento de mantenimiento"

    override fun ejecutarLogica() {
        
    }
}

class ActualizarPlanetas(
    val repositorioPlaneta: RepositorioPlaneta,
    val servicePlanetas: ServicePlaneta
) : Proceso() {
    override val nombreProceso = "Actualización de Planetas"

    override fun ejecutarLogica() {
        repositorioPlaneta.importarPlanetas(servicePlanetas)
    }
}
