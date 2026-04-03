import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class BaseLanzamientoTest : DescribeSpec({

    fun crearBase(capacidad: Int = 3, x: Int = 0, y: Int = 0) = BaseLanzamiento(
        nombre = "Base Alpha",
        direccion = Direccion(
            pais = "Argentina",
            ciudad = "Buenos Aires",
            calle = "Av. Siempreviva",
            altura = "742",
            ubicacion = Point(x.toDouble(), y.toDouble())
        ),
        capacidadMaximaNaves = capacidad
    )

    fun crearSonda() = Sonda(
        "Voyager",
        1,
        LocalDate.now().minusYears(3),
        100.0,
        1000,
        50.0
    )
    fun crearTripulante(base: BaseLanzamiento, x: Int = 0, y: Int = 0, distanciaMax: Double) = Tripulante(
        nombre = "Juan",
        apellido = "Perez",
        fechaNacimiento = LocalDate.of(1990, 5, 10),
        fechaInicioActividad = LocalDate.now().minusYears(6),
        salarioBase = 100000.0,
        rol = Piloto(),
        perfilAptitud = Conformista(),
        baseLanzamiento = base,
        direccion = Direccion("Argentina", "Buenos Aires", "Falsa",
            "123", Point(x.toDouble(), y.toDouble())),
        distanciaMaxima = distanciaMax


    )


    describe("naves estacionadas") {

        it("puede estacionar una nave en la base") {
            val base = crearBase()
            val nave = crearSonda()

            base.estacionar(nave)

            base.tieneNaveEstacionada(nave) shouldBe true
        }

        it("no puede estacionar más naves que la capacidad máxima") {
            val base = crearBase(capacidad = 1)
            base.estacionar(crearSonda())

            shouldThrow<BusinessException> {
                base.estacionar(crearSonda())
            }
        }

        it("al retirar una nave deja de estar en la base") {
            val base = crearBase()
            val nave = crearSonda()
            base.estacionar(nave)

            base.retirar(nave)

            base.tieneNaveEstacionada(nave) shouldBe false
        }

        it("una nave que nunca fue estacionada no aparece en la base") {
            val base = crearBase()
            val nave = crearSonda()

            base.tieneNaveEstacionada(nave) shouldBe false
        }
    }
})
