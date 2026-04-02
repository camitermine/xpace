import java.time.LocalDate

class Planeta (val nombre: String,
               val temperaturaMedia: Double,
               val gravedad: Double,
               val nivelRadiacion: Int,
               val hayAguaLiquida: Boolean,
               val toxicidadAtmosferica: Int,
               val actividadTectonica: Int,
               val radioKm: Double,
               val fechaDescubrimiento: LocalDate,
               val distanciaATierra: Double,
               var fueAterrizado: Boolean)
{
    init {
        validar()
    }

    private fun validarNombre() {
        require(nombre.isNotBlank()) { "El nombre no debe estar vacío" }
    }

    private fun validarNivelRadiacion() {
        require(nivelRadiacion in 0..100) { "El nivel de radiación debe estar entre 0 y 100" }
    }

    private fun validarToxicidad() {
        require(toxicidadAtmosferica in 0..100) { "La toxicidad atmosférica debe estar entre 0 y 100" }
    }

    private fun validarActividadTectonica() {
        require(actividadTectonica in 0..100) { "La actividad tectónica debe estar entre 0 y 100" }
    }

    private fun validarGravedad() {
        require(gravedad > 0) { "La gravedad debe ser mayor a 0" }
    }

    private fun validarRadio() {
        require(radioKm > 0) { "El tamaño (radio) debe ser mayor a 0" }
    }

    private fun validar() {
        validarNombre()
        validarNivelRadiacion()
        validarToxicidad()
        validarActividadTectonica()
        validarGravedad()
        validarRadio()
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