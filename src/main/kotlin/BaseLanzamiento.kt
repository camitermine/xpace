import org.uqbar.geodds.Point

class BaseLanzamiento(
    val nombre: String,
    val direccion: Direccion,
    val capacidadMaximaNaves: Int
) : Entidad {

    override var id: Int? = null

    override fun coincideCon(criterio: String): Boolean {
        return nombre.contains(criterio, ignoreCase = true) ||
                direccion.pais.equals(criterio, ignoreCase = true)
    }
    val navesEstacionadas: MutableList<Nave> = mutableListOf()

    //Protego la coleccion interna
    private val _navesEstacionadas: MutableList<Nave> = mutableListOf()

    fun navesEstacionadas(): List<Nave> {
        return _navesEstacionadas.toList()
    }

    fun repararNavesDisponibles() {
        _navesEstacionadas
            .filter { it.puedeSerReparada() }
            .forEach { it.reparar() }
    }

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
