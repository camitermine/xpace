import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

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
        val elementoViejo = getById(id)
        val index = elementos.indexOf(elementoViejo)
        elementos[index] = elemento
        return elemento
    }

    fun delete(elemento: T) {
        val id = elemento.id ?: throw ElementoSinIdException("El elemento no tiene id asignado")
        val existente = getById(id)
        elementos.remove(existente)
    }

    fun getAll(): List<T> = elementos.toList()

    fun search(criterio: String): List<T> {
        return elementos.filter { it.coincideCon(criterio) }
    }
}
class RepositorioTripulante : Repositorio<Tripulante>()

class RepositorioNave : Repositorio<Nave>()

class RepositorioMision : Repositorio<Mision>()

class RepositorioBase : Repositorio<BaseLanzamiento>()

class RepositorioSubvencion : Repositorio<Subvencion>()

interface ServicePlaneta {
    fun getPlanetas(): String
}
class RepositorioPlaneta : Repositorio<Planeta>() {

    val mapper = jacksonObjectMapper()

    fun guardarPlaneta(planeta: Planeta) {
        val idExterno = planeta.id
        if (idExterno != null && getAll().any { it.id == idExterno }) {
            update(planeta)
        } else {
            create(planeta)
        }
    }

    fun importarPlanetas(service: ServicePlaneta) {
        val jsonString = service.getPlanetas()
        val planetas: List<Planeta> = mapper.readValue(jsonString)
        planetas.forEach { guardarPlaneta(it) }
    }

}

