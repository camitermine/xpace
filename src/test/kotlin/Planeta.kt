import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PlanetaTest : FunSpec({

    fun crearPlanetaBase(
        nombre: String = "Kepler",
        temperaturaMedia: Double = 25.0,
        gravedad: Double = 9.8,
        nivelRadiacion: Int = 40,
        hayAguaLiquida: Boolean = true,
        toxicidadAtmosferica: Int = 20,
        actividadTectonica: Int = 10,
        radioKm: Double = 6000.0,
        fechaDescubrimiento: LocalDate = LocalDate.of(2015, 7, 23),
        distanciaATierra: Double = 1400.0,
        fueAterrizado: Boolean = false
    ) = Planeta(nombre, temperaturaMedia, gravedad, nivelRadiacion, hayAguaLiquida, toxicidadAtmosferica, actividadTectonica, radioKm, fechaDescubrimiento, distanciaATierra, fueAterrizado)

    test("un planeta con temperatura entre 0 y 40 grados tiene temperatura ideal") {
        val planetaConTemperaturaIdeal = crearPlanetaBase(temperaturaMedia = 25.0)
        planetaConTemperaturaIdeal.tieneTemperaturaIdeal() shouldBe true
    }

    test("un planeta con temperatura fuera de rango NO tiene temperatura ideal") {
        val planetaConTemperaturaAlta = crearPlanetaBase(temperaturaMedia = 45.0)
        planetaConTemperaturaAlta.tieneTemperaturaIdeal() shouldBe false
    }

    test("un planeta con gravedad entre 3 y 15 tiene gravedad soportable") {
        val planetaConGravedadNormal = crearPlanetaBase(gravedad = 9.8)
        planetaConGravedadNormal.gravedadSoportable() shouldBe true
    }

    test("un planeta con gravedad menor a 3 NO tiene gravedad soportable") {
        val planetaConGravedadBaja = crearPlanetaBase(nombre = "Mercurio", gravedad = 2.5)
        planetaConGravedadBaja.gravedadSoportable() shouldBe false
    }

    test("no se puede crear un planeta con niveles de radiación, toxicidad o actividad tectónica fuera del rango 0-100") {
        shouldThrow<AttributeException>{
            crearPlanetaBase(
                nombre = "Krypton",
                gravedad = 45.0,
                nivelRadiacion = -5,
                toxicidadAtmosferica = -5,
                actividadTectonica = -5
            )
        }
    }

    test("no se puede crear un planeta con nombre vacío") {
        shouldThrow<AttributeException>{
            crearPlanetaBase(nombre = "")
        }
    }

    test("un planeta inhabitable tiene esHabitable en false") {
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

    test("el índice de peligrosidad es el promedio de radiación, toxicidad y actividad tectónica") {
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

    test("un planeta no habitable con índice de peligrosidad menor a 60 es explorable") {
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
})