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
    //forzamos los valores para que esten entre 0 y 100
    val nivelRadiacion = nivelRadiacion.coerceIn(0,100)
    val toxicidadAtmosferica = toxicidadAtmosferica.coerceIn(0,100)
    val actividadTectonica = actividadTectonica.coerceIn(0,100)

    fun tieneTemperaturaIdeal(): Boolean {
        return temperaturaMedia >= 0 && temperaturaMedia <= 40
    }
    fun gravedadSoportable(): Boolean {
        return gravedad >= 3 && gravedad <= 15
    }
    /*fun tieneAguaLiquida(): Boolean {
        return hayAguaLiquida
    }*/
}