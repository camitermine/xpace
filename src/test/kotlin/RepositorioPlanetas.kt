
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

    /* para usar en proximos test no me borren :)
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

    fun planetaTrappist() : Planeta {
        val planeta = Planeta(nombre = "TRAPPIST-1e",
            temperaturaMedia = -22.0,
            gravedad = 9.1,
            nivelRadiacion = 30,
            hayAguaLiquida = true,
            toxicidadAtmosferica = 25,
            actividadTectonica = 15,
            radioKm = 5800.0,
            distanciaATierra = 39.6)
        planeta.id = 3
        return planeta
    }*/

    describe("Prueba de servicio") {

        it("Dado un servicio pruebo que se puedan importar planetas ") {
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


    }


})

fun stubService( json: String) : ServicePlaneta{
    val service = mockk<ServicePlaneta>()
    every { service.getPlanetas() } returns json
    return service
}
