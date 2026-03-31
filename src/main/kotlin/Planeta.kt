import java.time.LocalDate

class Planeta (val nombre: String,
               val temperaturaMedia: Double,
               val gravedad: Double,
               nivelRadiacion: Int,
               val hayAguaLiquida: Boolean,
               toxicidadAtmosferica: Int,
               actividadTectonica: Int,
               val radioKm: Double,
               val fechaDescubrimiento: LocalDate,
               val distanciaATierra: Double,
               var fueAterrizado: Boolean)
{
    init {
        require(nivelRadiacion in 0..100) {"El valor debe estar entre cero y cien"}
        require(toxicidadAtmosferica in 0..100) {"El valor debe estar entre cero y cien"}
        require(actividadTectonica in 0..100) {"El valor debe estar entre cero y cien"}
    }

    fun tieneTemperaturaIdeal(): Boolean {
        return temperaturaMedia >= 0 && temperaturaMedia <= 40
    }
    fun gravedadSoportable(): Boolean {
        return gravedad >= 3 && gravedad <= 15
    }
}