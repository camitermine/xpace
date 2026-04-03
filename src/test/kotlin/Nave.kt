import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class NaveTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerTest

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

        it("sonda solo permite 0 tripulantes") {
            val sonda = Sonda("V", 1, LocalDate.now(), 10.0, 100, 10.0)

            sonda.puedeTransportar(0) shouldBe true
            sonda.puedeTransportar(1) shouldBe false
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

        it("falla si supera capacidad de tripulantes") {
            shouldThrow<BusinessException> {
                nave.consumoPorAnioLuz(25)
            }
        }

        it("funciona en el límite de capacidad de tripulantes") {
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

        it("puede alcanzar justo en el límite") {
            val nave = Sonda("V", 1, LocalDate.now(), 10.0, 292, 5.0)

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
            shouldThrow<AttributeException> {
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

        it("mensaje correcto en nombre vacío") {
            val exception = shouldThrow<AttributeException> {
                Sonda("", 1, LocalDate.now(), 10.0, 100, 50.0)
            }

            exception.message shouldBe "El nombre no puede estar vacío"
        }

        it("no permite consumo negativo") {
            shouldThrow<AttributeException> {
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
        it("no permite velocidad negativa") {
            shouldThrow<AttributeException> {
                Sonda("Test", 1, LocalDate.now(), 10.0, 100, -5.0)
            }
        }

        it("no permite capacidad de carga negativa") {
            shouldThrow<AttributeException> {
                Carguero(
                    "Error",
                    7,
                    LocalDate.of(2022, 1, 1),
                    1000.0,
                    300,
                    5.0,
                    -10.0
                )
            }
        }
    }
})