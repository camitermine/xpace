import org.uqbar.geodds.Point

class Direccion(
    val pais: String,
    val ciudad: String,
    val calle: String,
    val altura: String,
    val ubicacion: Point
){

    fun calcularDistancia(ubicacionDeOtraDireccion: Point): Double {
        return this.ubicacion.distance(ubicacionDeOtraDireccion)
    }
}