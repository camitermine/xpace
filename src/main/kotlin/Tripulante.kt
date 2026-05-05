import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.Period

private enum class MensajesDeErrorTripulante(val mensaje: String) {

    NOMBRE_INVALIDO("El nombre no puede estar vacío"),
    APELLIDO_INVALIDO("El apellido no puede estar vacio"),
    FECHA_NACIMIENTO_INVALIDA("La fecha de nacimiento debe ser anterior a hoy")
}

class Tripulante(
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: LocalDate,
    val fechaInicioActividad: LocalDate = LocalDate.now(),
    var salarioBase: Double = 0.0,
    var rol: Rol = Piloto,
    var perfilAptitud: PerfilAptitud = Conformista,
    var baseLanzamiento: BaseLanzamiento,
    val direccion: Direccion,
    val distanciaMaxima: Double = 0.0
) : Entidad {

    override var id: Int? = null

    override fun coincideCon(criterio: String): Boolean {
        return nombre.contains(criterio, ignoreCase = true) ||
                apellido.equals(criterio, ignoreCase = true)
    }

    private val _historialMisiones: MutableList<Mision> = mutableListOf()
    val historialMisiones: List<Mision> get() = _historialMisiones

    private fun validarConstruccion() {

        Validaciones.validarNoVacio(nombre, MensajesDeErrorTripulante.NOMBRE_INVALIDO.mensaje)
        Validaciones.validarNoVacio(apellido,MensajesDeErrorTripulante.APELLIDO_INVALIDO.mensaje)
        Validaciones.validarFechaAnteriorAHoy(fechaNacimiento, MensajesDeErrorTripulante.FECHA_NACIMIENTO_INVALIDA.mensaje)
    }

    init {
        validarConstruccion()
    }

    fun agregarMision(mision: Mision) {
        _historialMisiones.add(mision)
    }

    fun calcularEdad(): Int {
        return Period.between(fechaNacimiento, LocalDate.now()).years
    }

    fun calcularAniosActivo(): Int {
        return Period.between(fechaInicioActividad, LocalDate.now()).years
    }

    fun cantidadMisionesPor(estado: EstadoMision): Int {
        return historialMisiones.count { it.estado == estado }
    }

    fun calcularExperiencia(): Double {
        val exitosas = cantidadMisionesPor(EstadoMision.COMPLETADA)
        val parciales = historialMisiones.count {
            it.estado == EstadoMision.CANCELADA && it.altoRiesgo()
        }
        val fracasadas = cantidadMisionesPor(EstadoMision.FALLIDA)

        return calcularAniosActivo() +
                (exitosas / 2.0) +
                (fracasadas / 2.0) +
                (parciales / 4.0)
    }

    private fun cumpleCondicionesBase(): Boolean {
        val experienciaSuficiente = calcularExperiencia() >= Config.CONDICION_BASE_EXPERIENCIA
        val sinMisionEnCurso = historialMisiones.none { it.estado == EstadoMision.EN_CURSO }

        return experienciaSuficiente && sinMisionEnCurso
    }

    fun esAptoParaMision(mision: Mision): Boolean {
        if (!cumpleCondicionesBase()) {
            return false
        }
        return perfilAptitud.aceptaMision(mision, this)
    }

    fun calcularSalarioTotal(): Double {
        return salarioBase + rol.calcularBonus(this)
    }

    fun consideraUbicacionCercana(ubicacionDeOtraDireccion: Point): Boolean {
        return this.distanciaMaxima >= this.direccion.calcularDistancia(ubicacionDeOtraDireccion)

    }
}

interface PerfilAptitud {
    fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean
}

object Conformista : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean = true
}

object Prudente : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return mision.destino.tieneTemperaturaIdeal() && mision.destino.gravedadSoportable()
    }
}

object Explorador : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return !mision.destino.fueAterrizado
    }
}

class Veterano(val maxDiasPermitidos: Int) : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return mision.duracion() <= maxDiasPermitidos
    }
}

class Cauteloso(val umbralRadiacionMaximo: Double) : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return mision.destino.nivelRadiacion < umbralRadiacionMaximo
    }
}

object Exigente : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return mision.nave.esModerna()
    }
}

object Temerario : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return !mision.destino.esHabitable() || mision.altoRiesgo()
    }
}

object Cambiante : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        val edad = tripulante.calcularEdad()
        return if (edad % 2 == 0) {
            Temerario.aceptaMision(mision, tripulante)
        } else {
            Prudente.aceptaMision(mision, tripulante)
        }
    }
}

class PerfilCompuestoY(val perfiles: List<PerfilAptitud>) : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return perfiles.all { it.aceptaMision(mision, tripulante) }
    }
}

class PerfilCompuestoO(val perfiles: List<PerfilAptitud>) : PerfilAptitud {
    override fun aceptaMision(mision: Mision, tripulante: Tripulante): Boolean {
        return perfiles.any { it.aceptaMision(mision, tripulante) }
    }
}

interface Rol {
    fun calcularBonus(tripulante: Tripulante): Double
}

object Comandante : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val misionesExitosas = tripulante.cantidadMisionesPor(EstadoMision.COMPLETADA)
        return (tripulante.salarioBase * 0.50) + (tripulante.salarioBase * 0.05 * misionesExitosas)
    }
}

object Piloto : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        return tripulante.salarioBase * 0.30
    }
}

object Ingeniero : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val ultimaCompletada = tripulante.historialMisiones
            .lastOrNull { it.estado == EstadoMision.COMPLETADA }

        return if (ultimaCompletada != null && ultimaCompletada.nave is Carguero) {
            tripulante.salarioBase * 0.40
        } else {
            tripulante.salarioBase * 0.20
        }
    }
}

object Cientifico : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val cantidadPlanetasAterrizados = tripulante.historialMisiones
            .filter { it.estado == EstadoMision.COMPLETADA }
            .map { it.destino }
            .distinct()
            .count()

        return tripulante.salarioBase * (0.10 * cantidadPlanetasAterrizados)
    }
}

object Medico : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val misionesFallidas = tripulante.cantidadMisionesPor(EstadoMision.FALLIDA)
        return (tripulante.salarioBase * 0.25) + (tripulante.salarioBase * 0.02 * misionesFallidas)
    }
}