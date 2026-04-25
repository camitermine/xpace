
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat

class RepositorioPlanetasTest : DescribeSpec({

    fun planetaKepler() : Planeta {
        val planeta = Planeta(nombre = "Kepler-442b",
            temperaturaMedia = 22.5,
            gravedad = 9.8,
            nivelRadiacion = 15,
            hayAguaLiquida = true,
            toxicidadAtmosferica = 10,
            actividadTectonica = 20,
            radioKm = 7500.0,
            distanciaATierra = 104.7)
        planeta.id = 1
        return planeta
    }

    fun planetaCentauri() : Planeta {
        val planeta = Planeta(nombre = "Proxima Centauri b",
            temperaturaMedia = -39.0,
            gravedad = 11.2,
            nivelRadiacion = 75,
            hayAguaLiquida = false,
            toxicidadAtmosferica = 60,
            actividadTectonica = 45,
            radioKm = 7160.0,
            distanciaATierra = 4.24)
        planeta.id = 2
        return planeta
    }

    describe("Prueba de servicio") {

        it("Dado un servicio probamos que se puede actualizar un planeta ") {
            val jsonString = "[\n" +
                    "{\n" +
                    " \"id\": 1,\n" +
                    " \"nombre\": \"Kepler-442b\",\n" +
                    " \"temperaturaMedia\": 22.5,\n" +
                    " \"gravedad\": 9.8,\n" +
                    " \"nivelRadiacion\": 15,\n" +
                    " \"hayAguaLiquida\": true,\n" +
                    " \"toxicidadAtmosferica\": 10,\n" +
                    " \"actividadTectonica\": 20,\n" +
                    " \"tamanio\": 7500,\n" +
                    " \"distanciaATierra\": 104.7\n" +
                    "}]"
            val servicio = stubService(jsonString)
            val repoPlanetas = RepositorioPlaneta()

            val planetita1 = planetaKepler()

            repoPlanetas.create(planetita1)

            repoPlanetas.importarPlanetas(servicio)

            repoPlanetas.getAll() shouldHaveSize 1
            assertThat(repoPlanetas.getAll()).usingRecursiveComparison().isEqualTo(listOf(planetita1))

        }

        it("Dado un servicio probamos que se puede actualizar el primer planeta y guardar el segundo") {
            val jsonString = "[\n" +
                    "{\n" +
                    " \"id\": 1,\n" +
                    " \"nombre\": \"Kepler-442b\",\n" +
                    " \"temperaturaMedia\": 22.5,\n" +
                    " \"gravedad\": 9.8,\n" +
                    " \"nivelRadiacion\": 15,\n" +
                    " \"hayAguaLiquida\": true,\n" +
                    " \"toxicidadAtmosferica\": 10,\n" +
                    " \"actividadTectonica\": 20,\n" +
                    " \"tamanio\": 7500,\n" +
                    " \"distanciaATierra\": 104.7\n" +
                    "},{\n" +
                    " \"nombre\": \"Proxima Centauri b\",\n" +
                    " \"temperaturaMedia\": -39.0,\n" +
                    " \"gravedad\": 11.2,\n" +
                    " \"nivelRadiacion\": 75,\n" +
                    " \"hayAguaLiquida\": false,\n" +
                    " \"toxicidadAtmosferica\": 60,\n" +
                    " \"actividadTectonica\": 45,\n" +
                    " \"tamanio\": 7160,\n" +
                    " \"distanciaATierra\": 4.24\n" +
                    "}]"
            val servicio = stubService(jsonString)
            val repoPlanetas = RepositorioPlaneta()

            val planetita1 = planetaKepler()
            val planetita2 = planetaCentauri()

            repoPlanetas.create(planetita1)
            repoPlanetas.getAll() shouldHaveSize 1

            repoPlanetas.importarPlanetas(servicio)

            repoPlanetas.getAll() shouldHaveSize 2
            assertThat(repoPlanetas.getAll()).usingRecursiveComparison().isEqualTo(listOf(planetita1, planetita2))

        }

    }

})

fun stubService( json: String) : ServicePlaneta{
    val service = mockk<ServicePlaneta>()
    every { service.getPlanetas() } returns json
    return service
}
