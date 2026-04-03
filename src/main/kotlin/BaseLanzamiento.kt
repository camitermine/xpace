import org.uqbar.geodds.Point


class BaseLanzamiento(
    val nombre: String,
    val direccion: Direccion,
    val capacidadMaximaNaves: Int
) {
    val navesEstacionadas: MutableList<Nave> = mutableListOf()

    val ubicacionGeografica: Point
        get() = direccion.ubicacion

    fun estacionar(nave: Nave) {
        require(navesEstacionadas.size < capacidadMaximaNaves) { "La base no tiene capacidad para más naves" }
        navesEstacionadas.add(nave)
    }

    fun retirar(nave: Nave) {
        navesEstacionadas.remove(nave)
    }

    fun tieneNaveEstacionada(nave: Nave): Boolean = navesEstacionadas.contains(nave)
}
