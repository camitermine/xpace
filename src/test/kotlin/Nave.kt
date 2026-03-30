import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDate

class NaveTest : DescribeSpec({

    describe("Sonda") {

        it("tiene consumo base") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.of(2020, 1, 1),
                1000.0,
                300,
                5.0
            )

            sonda.consumoPorAnioLuz(0) shouldBe 1000.0
        }

        it("no tiene capacidad de tripulantes") {
            val sonda = Sonda(
                "Voyager",
                1,
                LocalDate.of(2020, 1, 1),
                1000.0,
                300,
                5.0
            )

            sonda.capacidadTripulantes shouldBe 0
        }
    }

    describe("Transbordador") {

        val nave = Transbordador(
            "Endeavour",
            2,
            LocalDate.of(2018, 1, 1),
            1000.0,
            400,
            5.0
        )

        it("calcula consumo con tripulantes") {
            nave.consumoPorAnioLuz(4) shouldBe 1400.0
        }

        it("falla si supera capacidad") {
            shouldThrow<IllegalArgumentException> {
                nave.consumoPorAnioLuz(25)
            }
        }

        it("funciona en el límite de capacidad") {
            nave.consumoPorAnioLuz(20) shouldBe 3000.0
        }
    }

    describe("Carguero") {

        it("calcula consumo por carga") {
            val nave = Carguero(
                "CargoX",
                3,
                LocalDate.of(2022, 1, 1),
                1000.0,
                400,
                5.0,
                10.0
            )

            nave.consumoPorAnioLuz(1) shouldBe 1500.0
        }

        it("aumenta consumo si es antigua") {
            val nave = Carguero(
                "CargoOld",
                4,
                LocalDate.of(2000, 1, 1),
                1000.0,
                400,
                5.0,
                10.0
            )

            nave.consumoPorAnioLuz(1) shouldBe 1800.0
        }
    }

    describe("Alcance") {

        it("puede alcanzar destino si autonomía alcanza") {
            val nave = Sonda(
                "Voyager",
                5,
                LocalDate.of(2020, 1, 1),
                1000.0,
                300,
                5.0
            )

            nave.puedeAlcanzar(2.0) shouldBe true
        }

        it("no puede alcanzar destino si autonomía no alcanza") {
            val nave = Sonda(
                "Voyager",
                6,
                LocalDate.of(2020, 1, 1),
                1000.0,
                200,
                3.0
            )

            nave.puedeAlcanzar(2.0) shouldBe false
        }
    }

    describe("Validaciones") {

        it("no permite nombre vacío") {
            shouldThrow<IllegalArgumentException> {
                Sonda(
                    "",
                    1,
                    LocalDate.of(2020, 1, 1),
                    1000.0,
                    300,
                    5.0
                )
            }
        }

        it("no permite consumo negativo") {
            shouldThrow<IllegalArgumentException> {
                Sonda(
                    "Test",
                    1,
                    LocalDate.of(2020, 1, 1),
                    -100.0,
                    300,
                    5.0
                )
            }
        }
    }
})