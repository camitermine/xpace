import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class MisionSpec : DescribeSpec({

    describe("estados de la misión") {
        fun crearPilotoJohnCena(base: BaseLanzamiento) = Tripulante(
            nombre = "John",
            apellido = "Cena",
            fechaNacimiento = LocalDate.now().minusYears(30),
            baseLanzamiento = base,
            direccion = Direccion("Argentina", "Buenos Aires", "Falsa", "123", Point(0.0, 0.0)),
            distanciaMaxima = 1.0,
        )

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
            nombre = "Kepler",
            temperaturaMedia = 25.0,
            nivelRadiacion = 40,
            hayAguaLiquida = true,
            toxicidadAtmosferica = 20,
            actividadTectonica = 10,
            radioKm = 6000.0,
            distanciaATierra = 1400.0,
        )

        fun crearPlanetaNoHabitable() = Planeta(
            nombre = "Kepler",
            temperaturaMedia = 25.0,
            nivelRadiacion = 40,
            hayAguaLiquida = false,
            toxicidadAtmosferica = 20,
            actividadTectonica = 10,
            radioKm = 6000.0,
            distanciaATierra = 1400.0,
        )

        fun crearMision(
            nave: Nave,
            planeta: Planeta,
            tripulantes: List<Tripulante> = emptyList()
        ): Mision {
            val m = Mision(
                "MISION 001",
                "",
                LocalDate.now().plusDays(1),
                nave,
                planeta,
            )
            if (tripulantes.isNotEmpty()) m.sumarTripulacion(tripulantes)
            return m
        }

        it("no permite lanzar con tripulacion inadecuada") {
            val sonda = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(sonda, planetaHabitable)
            val tripulacion = listOf<Tripulante>(crearPilotoJohnCena(crearBase()))

            shouldThrow<ErrorDeMision> {
                mision.sumarTripulacion(tripulacion)
                mision.lanzar()
            }
        }

        it("no permite lanzar un transbordador sin tripulantes") {
            val nave = crearTransbordador()
            val mision = crearMision(nave, crearPlanetaHabitable())

            shouldThrow<ErrorDeMision> {
                mision.lanzar()
            }
        }

        it("no permite lanzar una misión ya finalizada") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(nave, planetaHabitable)

            mision.lanzar()
            mision.cancelar()

            shouldThrow<ErrorDeMision> {
                mision.lanzar()
            }
        }

        it("cancela una misión en curso correctamente") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(nave, planetaHabitable)

            mision.lanzar()
            mision.cancelar()

            mision.estado shouldBe EstadoMision.CANCELADA
        }

        it("no permite cancelar una misión que no está en curso") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val misionEnBorrador = crearMision(nave, planetaHabitable)

            shouldThrow<ErrorDeMision> {
                misionEnBorrador.cancelar()
            }
        }

        it("completa una misión en curso correctamente") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(nave, planetaHabitable)

            mision.lanzar()
            mision.completar()

            mision.estado shouldBe EstadoMision.COMPLETADA
        }

        it("no permite completar una misión que no está en curso") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val misionEnBorrador = crearMision(nave, planetaHabitable)

            shouldThrow<ErrorDeMision> {
                misionEnBorrador.completar()
            }
        }

        it("registra fallo de una misión en curso correctamente") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(nave, planetaHabitable)

            mision.lanzar()
            mision.fallo()

            mision.estado shouldBe EstadoMision.FALLIDA
        }

        it("no permite registrar fallo de una misión que no está en curso") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val misionEnBorrador = crearMision(nave, planetaHabitable)

            shouldThrow<ErrorDeMision> {
                misionEnBorrador.fallo()
            }
        }

        it("asigna la nave a la misión al lanzarla") {
            val nave = crearSonda()
            val planetaHabitable = crearPlanetaHabitable()
            val mision = crearMision(nave, planetaHabitable)

            mision.lanzar()

            nave.misionAsignada shouldBe mision
        }

        it("detecta misión de alto riesgo") {
            val nave = crearTransbordador()
            val planetaNoHabitable = crearPlanetaNoHabitable()
            val mision = crearMision(nave, planetaNoHabitable)

            mision.altoRiesgo() shouldBe true
        }
        it("agrega misión al historial del tripulante si se cancela una de alto riesgo") {
            val base = crearBase()
            val johnCena = Tripulante(
                nombre = "John",
                apellido = "Cena",
                fechaNacimiento = LocalDate.now().minusYears(30),
                fechaInicioActividad = LocalDate.now().minusYears(10),
                baseLanzamiento = base,
                direccion = Direccion("Argentina", "Buenos Aires", "Falsa", "123", Point(0.0, 0.0)),
                distanciaMaxima = 0.1,
            )

            val nave = crearTransbordador()
            base.estacionar(nave)
            val planetaNoHabitable = crearPlanetaNoHabitable()
            val mision = crearMision(nave, planetaNoHabitable, listOf(johnCena))

            mision.lanzar()
            mision.cancelar()

            johnCena.historialMisiones.last().estado shouldBe EstadoMision.CANCELADA
        }
    }
})
