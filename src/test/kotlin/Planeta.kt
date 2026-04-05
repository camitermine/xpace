import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PlanetaTest : DescribeSpec({

    fun crearPlanetaBase(
        nombre: String = "Kepler",
        temperaturaMedia: Double = 25.0,
        gravedad: Double = 9.8,
        nivelRadiacion: Int = 40,
        hayAguaLiquida: Boolean = true,
        toxicidadAtmosferica: Int = 20,
        actividadTectonica: Int = 10,
    ) = Planeta(
        nombre = nombre,
        temperaturaMedia = temperaturaMedia,
        gravedad = gravedad,
        nivelRadiacion = nivelRadiacion,
        hayAguaLiquida = hayAguaLiquida,
        toxicidadAtmosferica = toxicidadAtmosferica,
        actividadTectonica = actividadTectonica,
        radioKm = 6000.0,
        fechaDescubrimiento = LocalDate.of(2015, 7, 23),
        distanciaATierra = 1400.0,
    )
    describe("Tests temperatura planeta") {
        it("un planeta con temperatura entre 0 y 40 grados tiene temperatura ideal") {
            val planetaConTemperaturaIdeal = crearPlanetaBase(temperaturaMedia = 25.0)
            planetaConTemperaturaIdeal.tieneTemperaturaIdeal() shouldBe true
        }

        it("un planeta con temperatura fuera de rango NO tiene temperatura ideal") {
            val planetaConTemperaturaAlta = crearPlanetaBase(temperaturaMedia = 45.0)
            planetaConTemperaturaAlta.tieneTemperaturaIdeal() shouldBe false
        }
    }
    describe("Tests gravedad planeta") {
        it("un planeta con gravedad entre 3 y 15 tiene gravedad soportable") {
            val planetaConGravedadNormal = crearPlanetaBase(gravedad = 9.8)
            planetaConGravedadNormal.gravedadSoportable() shouldBe true
        }

        it("un planeta con gravedad menor a 3 NO tiene gravedad soportable") {
            val planetaConGravedadBaja = crearPlanetaBase(nombre = "Mercurio", gravedad = 2.5)
            planetaConGravedadBaja.gravedadSoportable() shouldBe false
        }
    }
    describe("Tests validaciones de creacion de planeta") {
        it("no se puede crear un planeta con niveles de radiación, toxicidad o actividad tectónica fuera del rango 0-100") {
            shouldThrow<AttributeException> {
                crearPlanetaBase(
                    nombre = "Krypton",
                    gravedad = 45.0,
                    nivelRadiacion = -5,
                    toxicidadAtmosferica = -5,
                    actividadTectonica = -5
                )
            }
        }

        it("no se puede crear un planeta con nombre vacío") {
            shouldThrow<AttributeException> {
                crearPlanetaBase(nombre = "")
            }
        }
    }
    describe("Tests planeta para que sea explorable") {
        it("un planeta inhabitable tiene esHabitable en false") {
            val planetaNoHabitable = crearPlanetaBase(
                nombre = "Mercurio",
                temperaturaMedia = 46.0,
                gravedad = 1.5,
                nivelRadiacion = 45,
                hayAguaLiquida = false,
                toxicidadAtmosferica = 45
            )
            planetaNoHabitable.esHabitable() shouldBe false
        }

        it("el índice de peligrosidad es el promedio de radiación, toxicidad y actividad tectónica") {
            val planetaConIndiciosDePeligro = crearPlanetaBase(
                nombre = "Mercurio",
                gravedad = 2.5,
                nivelRadiacion = 10,
                hayAguaLiquida = false,
                toxicidadAtmosferica = 10,
                actividadTectonica = 10
            )
            planetaConIndiciosDePeligro.indiceDePeligrosidad() shouldBe 10.0
        }

        it("un planeta no habitable con índice de peligrosidad menor a 60 es explorable") {
            val planetaExplorable = crearPlanetaBase(
                nombre = "Mercurio",
                temperaturaMedia = 45.0,
                gravedad = 2.5,
                nivelRadiacion = 10,
                hayAguaLiquida = false,
                toxicidadAtmosferica = 10,
                actividadTectonica = 90
            )
            planetaExplorable.esExplorable() shouldBe true
        }
    }
})