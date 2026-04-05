
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point

class DireccionTest : FunSpec({

    test("calcular correctamente la distancia de una dirección a un punto") {

        val direccion = Direccion(
            "Argentina",
            "Buenos Aires",
            "Calle A",
            "123",
            Point(0.0, 0.0)
        )

        val distancia = direccion.calcularDistancia(Point(0.0, 0.0))

        distancia shouldBe 0.0

    }
})
