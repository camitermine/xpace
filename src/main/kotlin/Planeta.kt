import java.time.LocalDate
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


private enum class MensajesDeErrorPlaneta(val mensaje: String) {
    NOMBRE_INVALIDO("El nombre no puede estar vacío"),
    NIVEL_RADIACION_INVALIDA("El nivel de radiación debe estar entre 0 y 100"),
    TOXICIDAD_ATMOSFERICA_INVALIDA("La toxicidad atmosférica debe estar entre 0 y 100"),
    ACTIVIDAD_TECTONICA_INVALIDA("La actividad tectónica debe estar entre 0 y 100"),
    GRAVEDAD_INVALIDA("La gravedad debe ser mayor a 0"),
    RADIO_KM_INVALIDO("El tamaño (radio) debe ser mayor a 0")
}

class Planeta @JsonCreator constructor (@JsonProperty("nombre")val nombre: String,
        @JsonProperty("temperaturaMedia") val temperaturaMedia: Double = 20.0,
        @JsonProperty("gravedad") val gravedad: Double = 9.8,
        @JsonProperty("nivelRadiacion") val nivelRadiacion: Int = 0,
        @JsonProperty("hayAguaLiquida") val hayAguaLiquida: Boolean = false,
        @JsonProperty("toxicidadAtmosferica") val toxicidadAtmosferica: Int = 0,
        @JsonProperty("actividadTectonica") val actividadTectonica: Int = 0,
        @JsonProperty("tamanio") val radioKm: Double = 6371.0,
               val fechaDescubrimiento: LocalDate = LocalDate.now(),
        @JsonProperty("distanciaATierra")val distanciaATierra: Double = 0.0,
               var fueAterrizado: Boolean = false)
    : Entidad {

    override var id: Int? = null

    override fun coincideCon(criterio: String): Boolean {
        return nombre.contains(criterio, ignoreCase = true)
    }

    private fun validarConstruccion() {

        Validaciones.validarNoVacio(nombre, MensajesDeErrorPlaneta.NOMBRE_INVALIDO.mensaje)
        Validaciones.validarEntre0y100(nivelRadiacion,MensajesDeErrorPlaneta.NIVEL_RADIACION_INVALIDA.mensaje)
        Validaciones.validarEntre0y100(toxicidadAtmosferica,MensajesDeErrorPlaneta.TOXICIDAD_ATMOSFERICA_INVALIDA.mensaje)
        Validaciones.validarEntre0y100(actividadTectonica,MensajesDeErrorPlaneta.ACTIVIDAD_TECTONICA_INVALIDA.mensaje)
        Validaciones.validarMayorACero( gravedad,MensajesDeErrorPlaneta.GRAVEDAD_INVALIDA.mensaje )
        Validaciones.validarMayorACero(radioKm,MensajesDeErrorPlaneta.RADIO_KM_INVALIDO.mensaje)

    }

    init {
        validarConstruccion()
    }

    fun tieneTemperaturaIdeal(): Boolean {
        return temperaturaMedia in Config.TEMPERATURA_IDEAL_MIN_PLANETA..Config.TEMPERATURA_IDEAL_MAX_PLANETA
    }

    fun gravedadSoportable(): Boolean {
        return gravedad in Config.GRAVEDAD_IDEAL_MIN_PLANETA..Config.GRAVEDAD_IDEAL_MAX_PLANETA
    }

    fun indiceDePeligrosidad(): Double {
        return (nivelRadiacion + toxicidadAtmosferica + actividadTectonica).toDouble() / 3
    }

    fun indiceDePeligrosidadMenorATope(): Boolean {
        return indiceDePeligrosidad() < Config.TOPE_INDICE_PELIG_EXPLORABLE
    }

    fun esHabitable(): Boolean {
        return  tieneTemperaturaIdeal() &&
                gravedadSoportable() &&
                hayAguaLiquida &&
                toxicidadAtmosferica < Config.TOPE_TOX_ATMOS_HABITABLE &&
                nivelRadiacion < Config.TOPE_NIVEL_RAD_HABITABLE
    }

    fun esExplorable(): Boolean {
        return !esHabitable() && indiceDePeligrosidadMenorATope()
    }
}