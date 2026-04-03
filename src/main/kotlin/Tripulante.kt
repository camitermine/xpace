import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.Period

class Tripulante(
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: LocalDate,
    val fechaInicioActividad: LocalDate,
    var salarioBase: Double,
    var rol: Rol,
    var perfilAptitud: PerfilAptitud,
    var baseLanzamiento: BaseLanzamiento,
    val direccion: Direccion,
    val distanciaMaxima: Double
) {
    private val _historialMisiones: MutableList<Mision> = mutableListOf()
    val historialMisiones: List<Mision> get() = _historialMisiones

    init {
        validar()
    }

    private fun validarNombre() {
        validarAtributo(nombre.isNotBlank(),  "El nombre no debe estar vacío")
    }

    private fun validarApellido() {
        validarAtributo(apellido.isNotBlank(), "El apellido no debe estar vacío")
    }
    private fun validarFechaNacimiento() {
        validarAtributo(fechaNacimiento.isBefore(LocalDate.now()),
            "La fecha de nacimiento debe ser anterior a hoy"
        )
    }

    private fun validar() {
        validarNombre()
        validarApellido()
        validarFechaNacimiento()
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

    fun cantidadMisiones(estado: EstadoMision): Int {
        return historialMisiones.count { it.estado == estado }
    }

    fun calcularExperiencia(): Double {
        val exitosas = cantidadMisiones(EstadoMision.COMPLETADA)
        val parciales = historialMisiones.count {
            it.estado == EstadoMision.CANCELADA && it.altoRiesgo()
        }
        val fracasadas = cantidadMisiones(EstadoMision.FALLIDA)

        return calcularAniosActivo() +
                (exitosas / 2.0) +
                (fracasadas / 2.0) +
                (parciales / 4.0)
    }

    private fun cumpleCondicionesBase(): Boolean {
        val experienciaSuficiente = calcularExperiencia() >= 3.0
        val sinMisionEnCurso = historialMisiones.none { it.estado == EstadoMision.EN_CURSO }

        return experienciaSuficiente && sinMisionEnCurso
    }

    fun esAptoParaMision(mision: Mision): Boolean {
        return cumpleCondicionesBase() && perfilAptitud.aceptaMision(mision)
    }

    fun calcularSalarioTotal(): Double {
        return salarioBase + rol.calcularBonus(this)
    }

    fun consideraUbicacionCercana(ubicacionDeOtraDireccion: Point): Boolean {
        return this.distanciaMaxima >= this.direccion.calcularDistancia(ubicacionDeOtraDireccion)

    }
}

interface PerfilAptitud {
    fun aceptaMision(mision: Mision): Boolean
}

class Conformista : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean = true
}

class Prudente : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean {
        return mision.destino.tieneTemperaturaIdeal() && mision.destino.gravedadSoportable()
    }
}

class Explorador : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean {
        return !mision.destino.fueAterrizado
    }
}

class Veterano(val maxDiasPermitidos: Int) : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean {
        return mision.duracion() <= maxDiasPermitidos
    }
}

class Cauteloso(val umbralRadiacionMaximo: Double) : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean {
        return mision.destino.nivelRadiacion < umbralRadiacionMaximo
    }
}

class Exigente : PerfilAptitud {
    override fun aceptaMision(mision: Mision): Boolean {
        return mision.nave.esModerna()
    }
}

interface Rol {
    fun calcularBonus(tripulante: Tripulante): Double
}

class Comandante : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val misionesExitosas = tripulante.cantidadMisiones(EstadoMision.COMPLETADA)
        return (tripulante.salarioBase * 0.50) + (tripulante.salarioBase * 0.05 * misionesExitosas)
    }
}

class Piloto : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        return tripulante.salarioBase * 0.30
    }
}

class Ingeniero : Rol {
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

class Cientifico : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val cantidadPlanetasAterrizados = tripulante.historialMisiones
            .filter { it.estado == EstadoMision.COMPLETADA }
            .map { it.destino }
            .distinct()
            .count()

        return tripulante.salarioBase * (0.10 * cantidadPlanetasAterrizados)
    }
}

class Medico : Rol {
    override fun calcularBonus(tripulante: Tripulante): Double {
        val misionesFallidas = tripulante.cantidadMisiones(EstadoMision.FALLIDA)
        return (tripulante.salarioBase * 0.25) + (tripulante.salarioBase * 0.02 * misionesFallidas)
    }
}