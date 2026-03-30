import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class TripulanteTest : FunSpec({

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

    fun crearTripulante(
        nombre: String = "Juan",
        apellido: String = "Perez",
        fechaNacimiento: LocalDate = LocalDate.of(1990, 5, 10),
        fechaInicioActividad: LocalDate = LocalDate.now().minusYears(6)
    ) = Tripulante(
        nombre = nombre,
        apellido = apellido,
        fechaNacimiento = fechaNacimiento,
        fechaInicioActividad = fechaInicioActividad,
        salarioBase = 100000.0,
        rol = Piloto(),
        perfilAptitud = Conformista(),
        baseLanzamiento = crearBase(),
        ubicacionGeografica = Point(0.0, 0.0)
    )

    test("lanza error si la fecha de nacimiento es posterior a hoy") {
        shouldThrow<IllegalArgumentException> {
            crearTripulante(fechaNacimiento = LocalDate.now().plusDays(1))
        }
    }

    test("lanza error si el nombre está vacío") {
        shouldThrow<IllegalArgumentException> {
            crearTripulante(nombre = "")
        }
    }

    test("lanza error si el apellido está vacío") {
        shouldThrow<IllegalArgumentException> {
            crearTripulante(apellido = "")
        }
    }

    test("calcula correctamente los años activo") {
        val tripulante = crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(6))
        tripulante.calcularAniosActivo() shouldBe 6
    }

    test("la experiencia sin misiones es igual a los años activo") {
        val tripulante = crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(6))
        tripulante.calcularExperiencia() shouldBe 6.0
    }
})
