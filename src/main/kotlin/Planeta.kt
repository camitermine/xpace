import java.time.LocalDate

private enum class MensajesDeErrorPlaneta(val mensaje: String) {
    NOMBRE_INVALIDO("El nombre no puede estar vacío"),
    NIVEL_RADIACION_INVALIDA("El nivel de radiación debe estar entre 0 y 100"),
    TOXICIDAD_ATMOSFERICA_INVALIDA("La toxicidad atmosférica debe estar entre 0 y 100"),
    ACTIVIDAD_TECTONICA_INVALIDA("La actividad tectónica debe estar entre 0 y 100"),
    GRAVEDAD_INVALIDA("La gravedad debe ser mayor a "),
    RADIO_KM_INVALIDO("El tamaño (radio) debe ser mayor a 0")
}

class Planeta (val nombre: String,
               val temperaturaMedia: Double = 20.0,
               val gravedad: Double = 9.8,
               val nivelRadiacion: Int = 0,
               val hayAguaLiquida: Boolean = false,
               val toxicidadAtmosferica: Int = 0,
               val actividadTectonica: Int = 0,
               val radioKm: Double = 6371.0,
               val fechaDescubrimiento: LocalDate = LocalDate.now(),
               val distanciaATierra: Double = 0.0,
               var fueAterrizado: Boolean = false)
{
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
        return temperaturaMedia in 0.0..40.0
    }

    fun gravedadSoportable(): Boolean {
        return gravedad in 3.0..15.0
    }

    fun indiceDePeligrosidad(): Double {
        return (nivelRadiacion + toxicidadAtmosferica + actividadTectonica).toDouble() / 3
    }

    fun esHabitable(): Boolean {
        return tieneTemperaturaIdeal() && gravedadSoportable() && hayAguaLiquida && toxicidadAtmosferica < 30 && nivelRadiacion < 40
    }

    fun esExplorable(): Boolean {
        return !esHabitable() && indiceDePeligrosidad() < 60.0
    }
}