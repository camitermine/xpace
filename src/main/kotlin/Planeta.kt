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
        require(nombre.isNotBlank()) { "El nombre no debe estar vacío" }
        require(nivelRadiacion in 0..100) {"El valor debe estar entre cero y cien"}
        require(toxicidadAtmosferica in 0..100) {"El valor debe estar entre cero y cien"}
        require(actividadTectonica in 0..100) {"El valor debe estar entre cero y cien"}
    }

    fun tieneTemperaturaIdeal(): Boolean {
        return temperaturaMedia in 0.0..40.0
    }

    fun gravedadSoportable(): Boolean {
        return gravedad in 3.0..15.0
    }

    fun indiceDePeligrosidad(): Double {
        return (nivelRadiacion + toxicidadAtmosferica + actividadTectonica).toDouble() /3
    }

    fun esHabitable(): Boolean {
        return tieneTemperaturaIdeal() && gravedadSoportable() && hayAguaLiquida && toxicidadAtmosferica < 30 && nivelRadiacion < 40
    }

    fun esExplorable(): Boolean {
        return !esHabitable() && indiceDePeligrosidad() < 60.0
    }

}