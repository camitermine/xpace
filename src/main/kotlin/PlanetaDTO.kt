
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PlanetaDTO(
    val id: Int? = null,
    val nombre : String,
    val temperaturaMedia : Double,
    val gravedad : Double,
    val nivelRadiacion: Int,
    val hayAguaLiquida: Boolean,
    val toxicidadAtmosferica: Int,
    val actividadTectonica: Int,
    @SerialName("tamanio") val radioKm: Double,
    val distanciaATierra: Double,

)


