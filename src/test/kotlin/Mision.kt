import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class MisionSpec : DescribeSpec({

    describe("estados de la misión") {

        fun crearBase() = BaseLanzamiento(
            nombre = "Base Test",
            direccion = Direccion(
                pais = "Argentina",
                ciudad = "Buenos Aires",
                calle = "Calle Falsa",
                altura = "123",
                ubicacion = Point(0.0, 0.0)
            ),
            capacidadMaximaNaves = 5
        )

        fun crearSonda() = Sonda(
            "Voyager",
            1,
            LocalDate.now(),
            100.0,
            1000000000,
            100.0
        )

        fun crearTransbordador() = Transbordador(
            "Voyager",
            1,
            LocalDate.now(),
            100.0,
            1000000000,
            100.0,
        )

        fun crearPlanetaHabitable() = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015, 7, 23),
            1400.0,
            false
        )

        fun crearPlanetaNoHabitable() = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            false,
            20,
            10,
            6000.0,
            LocalDate.of(2015, 7, 23),
            1400.0,
            false
        )

        fun crearMision(
            nave: Nave,
            planeta: Planeta,
            tripulantes: List<Tripulante> = emptyList()
        ) = Mision(
            "MISION 001",
            "",
            LocalDate.now().plusDays(1),
            nave,
            planeta,
            tripulantes
        )

        it("no permite lanzar una misión ya finalizada") {
            val nave = crearSonda()
            val planeta = crearPlanetaHabitable()
            val mision = crearMision(nave, planeta)

            mision.lanzar()
            mision.cancelar()

            shouldThrow<ErrorDeMision> {
                mision.lanzar()
            }
        }

        it("cancela una misión") {
            val nave = crearSonda()
            val planeta = crearPlanetaHabitable()
            val mision = crearMision(nave, planeta)

            mision.lanzar()
            mision.cancelar()

            mision.estado shouldBe EstadoMision.CANCELADA
        }

        it("asigna la nave a la misión al lanzarla") {
            val nave = crearSonda()
            val planeta = crearPlanetaHabitable()
            val mision = crearMision(nave, planeta)

            mision.lanzar()

            nave.misionAsignada shouldBe mision
        }

        it("detecta misión de alto riesgo") {
            val nave = crearTransbordador()
            val planeta = crearPlanetaNoHabitable()
            val mision = crearMision(nave, planeta)

            mision.altoRiesgo() shouldBe true
        }

        it("agrega misión al historial del tripulante si se cancela una de alto riesgo") {
            val base = crearBase()
            val johnCena = Tripulante(
                nombre = "John",
                apellido = "Cena",
                fechaNacimiento = LocalDate.now().minusYears(30),
                fechaInicioActividad = LocalDate.now().minusYears(10),
                salarioBase = 100000.0,
                rol = Piloto(),
                perfilAptitud = Conformista(),
                baseLanzamiento = base,
                ubicacionGeografica = Point(0.0, 0.0)
            )

            val nave = crearTransbordador()
            base.estacionar(nave)
            val planeta = crearPlanetaNoHabitable()
            val mision = crearMision(nave, planeta, listOf(johnCena))

            mision.lanzar()
            mision.cancelar()

            johnCena.historialMisiones.last().estado shouldBe EstadoMision.CANCELADA
        }
    }
})
