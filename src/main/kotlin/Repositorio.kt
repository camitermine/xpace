import kotlinx.serialization.json.Json


abstract class Repositorio<T : Entidad> {
    private val elementos: MutableList<T> = mutableListOf()
    private var nextId = 1

    fun create(elemento: T): T {
        elemento.id = nextId++
        elementos.add(elemento)
        return elemento
    }

    fun getById(id: Int): T =
        elementos.find { it.id == id }
            ?: throw ElementoNoEncontradoException("No se encontró elemento con id $id")

    fun update(elemento: T): T {
        val id = elemento.id ?: throw ElementoSinIdException("El elemento no tiene id asignado")
        val index = elementos.indexOfFirst { it.id == id }
        if (index == -1) throw ElementoNoEncontradoException("No se encontró elemento con id $id para actualizar")
        elementos[index] = elemento
        return elemento
    }

    fun delete(id: Int) {
        val elemento = getById(id)
        elementos.remove(elemento)
    }

    fun getAll(): List<T> = elementos.toList()

    abstract fun search(criterio: String): List<T>
}

class RepositorioTripulante : Repositorio<Tripulante>() {
    override fun search(criterio: String): List<Tripulante> =
        getAll().filter {
            it.nombre.contains(criterio, ignoreCase = true) ||
            it.apellido.equals(criterio, ignoreCase = true)
        }
}

interface ServicePlaneta{
    fun getPlanetas() : String
}

class RepositorioPlaneta : Repositorio<Planeta>() {
    override fun search(criterio: String): List<Planeta> =
        getAll().filter { it.nombre.contains(criterio, ignoreCase = true) }

    fun guardarPlaneta( planetaDTO : PlanetaDTO ){
        val planetaMapeado = Planeta (
            nombre = planetaDTO.nombre,
            temperaturaMedia  = planetaDTO.temperaturaMedia,
            gravedad = planetaDTO.gravedad,
            nivelRadiacion = planetaDTO.nivelRadiacion,
            hayAguaLiquida = planetaDTO.hayAguaLiquida,
            toxicidadAtmosferica = planetaDTO.toxicidadAtmosferica,
            actividadTectonica = planetaDTO.actividadTectonica,
            radioKm = planetaDTO.radioKm,
            distanciaATierra = planetaDTO.distanciaATierra
        )
        planetaMapeado.id = planetaDTO.id

        if (planetaDTO.id == null) {
            create(planetaMapeado)
        } else {
            update(planetaMapeado)
        }
    }

    fun importarPlanetas(service: ServicePlaneta){
        val jsonString = service.getPlanetas() //mi conexion al servicio
        val json = Json { ignoreUnknownKeys = true } // ignora lo que no quiero del Json
        val planetasDTO : List<PlanetaDTO> = json.decodeFromString(jsonString)
        planetasDTO.forEach { guardarPlaneta(it) }
    }


}

class RepositorioNave : Repositorio<Nave>() {
    override fun search(criterio: String): List<Nave> {
        val codigo = criterio.toIntOrNull() ?: return emptyList()
        return getAll().filter { it.codIdentif == codigo }
    }
}

class RepositorioMision : Repositorio<Mision>() {
    override fun search(criterio: String): List<Mision> =
        getAll().filter {
            it.nombre.equals(criterio, ignoreCase = true) ||
            it.destino.nombre.equals(criterio, ignoreCase = true)
        }
}

class RepositorioBase : Repositorio<BaseLanzamiento>() {
    override fun search(criterio: String): List<BaseLanzamiento> =
        getAll().filter {
            it.nombre.contains(criterio, ignoreCase = true) ||
            it.direccion.pais.equals(criterio, ignoreCase = true)
        }
}

class RepositorioSubvencion : Repositorio<Subvencion>() {
    override fun search(criterio: String): List<Subvencion> =
        getAll().filter { it.nombre.startsWith(criterio, ignoreCase = true) }
}
