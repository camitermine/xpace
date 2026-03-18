import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PlanetaTest : FunSpec({

    test("verificar temperatura ideal") {
        val planeta = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015,7,23),
            1400.0,
            false
        )
        planeta.tieneTemperaturaIdeal() shouldBe true
    }
    test("verificar gravedad soportable") {
        val planeta = Planeta(
            "Kepler",
            25.0,
            9.8,
            40,
            true,
            20,
            10,
            6000.0,
            LocalDate.of(2015,7,23),
            1400.0,
            false
        )
        planeta.gravedadSoportable() shouldBe true
    }
    test("verificar temperatura NO ideal (demasiado calor)") {
        val planeta = Planeta(
            "Kepler",
            45.0, // Temperatura superior a 40 grados
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
        planeta.tieneTemperaturaIdeal() shouldBe false
    }

    test("verificar que la gravedad no sea soportable (cuando es menor a 3)") {
        val planeta = Planeta(
            "Mercurio",
            20.0,
            2.5,
            30,
            false,
            15,
            5,
            3000.0,
            LocalDate.of(2020, 1, 1),
            500.0,
            false
        )

        planeta.gravedadSoportable() shouldBe false
    }

    test("verificar que un valor de toxicidad, radiacion y actividad tectonica no pueda tener un valor imposible") {
        shouldThrow<IllegalArgumentException> {
            Planeta(
            "Krypton",
            18.0,
            45.0,
            -5,
            true,
            -5,
            -5,
            9556.5,
            LocalDate.of(1938, 6, 18),
            27.1,
            true
            )
        }
    }


})