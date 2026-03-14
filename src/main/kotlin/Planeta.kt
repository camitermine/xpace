import java.time.LocalDate

class Planeta (val nombre: String, val temperaturaMedia: Double, val gravedad: Double, val nivelRadiacion: Int,
               val hayAguaLiquida: Boolean, val toxicidadAtmosferica: Int, val actividadTectonica: Int, val radioKm: Double,
               val fechaDescubrimiento: LocalDate, val distanciaATierra: Double, var fueAterrizado: Boolean)
{
    fun tieneTemperaturaIdeal(): Boolean {
        return temperaturaMedia >= 0 && temperaturaMedia <= 40
    }
    fun gravedadSoportable(): Boolean {
        return gravedad >= 3 && gravedad <= 15
    }
    fun tieneAguaLiquida(): Boolean {
        return hayAguaLiquida
    }
}