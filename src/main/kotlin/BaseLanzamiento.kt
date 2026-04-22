import org.uqbar.geodds.Point

class BaseLanzamiento(
    val nombre: String,
    val direccion: Direccion,
    val capacidadMaximaNaves: Int
) : Entidad {

    override var id: Int? = null

    override fun coincideCon(criterio: String): Boolean {
        // La base busca por su nombre o por el país donde está ubicada
        return nombre.contains(criterio, ignoreCase = true) ||
                direccion.pais.equals(criterio, ignoreCase = true)
    }

    val navesEstacionadas: MutableList<Nave> = mutableListOf()

    val ubicacionGeografica: Point
        get() = direccion.ubicacion

    fun estacionar(nave: Nave) {
        Validaciones.validarReglaNegocio(
            navesEstacionadas.size < capacidadMaximaNaves,
            "La base no tiene capacidad para más naves"
        )
        Validaciones.validarReglaNegocio(
            !this.tieneNaveEstacionada(nave),
            "La nave ya está estacionada"
        )
        navesEstacionadas.add(nave)
    }

    fun retirar(nave: Nave) {
        navesEstacionadas.remove(nave)
    }

    fun tieneNaveEstacionada(nave: Nave): Boolean = navesEstacionadas.contains(nave)
}
