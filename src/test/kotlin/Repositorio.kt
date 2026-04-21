import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.uqbar.geodds.Point

class RepositorioTest : DescribeSpec({

    fun direccion(pais: String = "Argentina") =
        Direccion(pais, "Buenos Aires", "Calle Falsa", "123", Point(0.0, 0.0))

    fun base(nombre: String = "Base Alpha", pais: String = "Argentina") =
        BaseLanzamiento(nombre, direccion(pais), 10)

    fun planeta(nombre: String = "Marte") =
        Planeta(nombre = nombre)

    fun nave(cod: Int = 1) =
        Transbordador("Apolo", cod, LocalDate.now().minusYears(2), 100.0, 100000, 100.0)

    fun tripulante(nombre: String = "Juan", apellido: String = "Pérez") =
        Tripulante(
            nombre = nombre,
            apellido = apellido,
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            fechaInicioActividad = LocalDate.now().minusYears(6),
            baseLanzamiento = base(),
            direccion = direccion()
        )

    fun mision(nombre: String = "Mision Alfa", destino: Planeta = planeta(), naveAsignada: Nave = nave()) =
        Mision(nombre, "Descripción", LocalDate.now().plusDays(10), naveAsignada, destino)

    fun subvencion(nombre: String = "SubNASA") =
        SubvencionEmergencia(nombre, 100.0, LocalDate.now(), 365)

    // --- CRUD genérico (se prueba una sola vez, la lógica es compartida por todos los repos) ---

    describe("CRUD genérico y manejo de errores") {
        lateinit var repo: RepositorioTripulante

        beforeEach { repo = RepositorioTripulante() }

        it("create asigna id autoincremental y persiste el elemento") {
            val t1 = repo.create(tripulante(nombre = "Ana"))
            val t2 = repo.create(tripulante(nombre = "Luis"))
            t1.id shouldBe 1
            t2.id shouldBe 2
            repo.getAll() shouldHaveSize 2
        }

        it("getById retorna el elemento correcto") {
            val t = repo.create(tripulante())
            repo.getById(t.id!!) shouldBe t
        }

        it("getById lanza excepción si el id no existe") {
            shouldThrow<ElementoNoEncontradoException> { repo.getById(99) }
        }

        it("update reemplaza el elemento existente") {
            val t = repo.create(tripulante())
            t.salarioBase = 5000.0
            repo.update(t)
            repo.getById(t.id!!).salarioBase shouldBe 5000.0
        }

        it("update lanza excepción si el elemento no tiene id") {
            shouldThrow<ElementoSinIdException> { repo.update(tripulante()) }
        }

        it("update lanza excepción si el id no existe en el repo") {
            val t = tripulante()
            t.id = 99
            shouldThrow<ElementoNoEncontradoException> { repo.update(t) }
        }

        it("delete elimina el elemento y lanza excepción si no existe") {
            val t = repo.create(tripulante())
            repo.delete(t.id!!)
            repo.getAll() shouldHaveSize 0
            shouldThrow<ElementoNoEncontradoException> { repo.delete(t.id!!) }
        }
    }

    describe("RepositorioTripulante - search por nombre parcial o apellido exacto") {
        lateinit var repo: RepositorioTripulante

        beforeEach { repo = RepositorioTripulante() }

        it("encuentra por nombre parcial") {
            val t1 = repo.create(tripulante(nombre = "Juan"))
            val t2 = repo.create(tripulante(nombre = "Juanita"))
            repo.create(tripulante(nombre = "Carlos"))

            repo.search("Jua") shouldContainAll listOf(t1, t2)
        }

        it("encuentra por apellido exacto y no por parcial") {
            val t = repo.create(tripulante(apellido = "López"))
            repo.create(tripulante(apellido = "Lopezito"))

            val resultado = repo.search("López")
            resultado shouldContain t
            resultado shouldHaveSize 1
        }
    }

    describe("RepositorioNave - search por código exacto") {
        lateinit var repo: RepositorioNave

        beforeEach { repo = RepositorioNave() }

        it("encuentra la nave con ese código exacto") {
            val n = repo.create(nave(cod = 42))
            repo.create(nave(cod = 99))

            repo.search("42") shouldContain n
            repo.search("42") shouldHaveSize 1
        }

        it("retorna vacío si el criterio no es un número válido") {
            repo.create(nave(cod = 1))
            repo.search("apolo") shouldHaveSize 0
        }
    }

    describe("RepositorioMision - search por nombre de misión o nombre del planeta") {
        lateinit var repo: RepositorioMision

        beforeEach { repo = RepositorioMision() }

        it("encuentra por nombre exacto de misión") {
            val m1 = repo.create(mision("Exploración Alfa"))
            repo.create(mision("Exploración Beta"))
            repo.create(mision("Rescate Gamma"))

            val resultado = repo.search("Exploración Alfa")
            resultado shouldContain m1
            resultado shouldHaveSize 1
        }

        it("encuentra por nombre del planeta destino") {
            val m = repo.create(mision(destino = planeta("Marte")))
            repo.create(mision(destino = planeta("Venus"), naveAsignada = nave(cod = 2)))

            val resultado = repo.search("Marte")
            resultado shouldContain m
            resultado shouldHaveSize 1
        }
    }

    describe("RepositorioBase - search por nombre parcial o país exacto") {
        lateinit var repo: RepositorioBase

        beforeEach { repo = RepositorioBase() }

        it("encuentra por nombre parcial") {
            val b1 = repo.create(base("Base Alpha"))
            val b2 = repo.create(base("Base Beta"))
            repo.create(base("Estación Gamma"))

            repo.search("Base") shouldContainAll listOf(b1, b2)
        }

        it("encuentra por país exacto y no por parcial") {
            val b = repo.create(base(pais = "Brasil"))
            repo.create(base(pais = "Argentina"))
            repo.create(base(pais = "Brasileño"))

            val resultado = repo.search("Brasil")
            resultado shouldContain b
            resultado shouldHaveSize 1
        }
    }

    describe("RepositorioSubvencion - search por nombre que comienza con") {
        lateinit var repo: RepositorioSubvencion

        beforeEach { repo = RepositorioSubvencion() }

        it("encuentra subvenciones cuyo nombre comienza con el criterio") {
            val s1 = repo.create(subvencion("SubNASA"))
            val s2 = repo.create(subvencion("SubESA"))
            repo.create(subvencion("FondoEspacial"))

            val resultado = repo.search("Sub")
            resultado shouldContainAll listOf(s1, s2)
            resultado shouldNotContain subvencion("FondoEspacial")
        }

        it("no encuentra si el criterio aparece en el medio del nombre") {
            repo.create(subvencion("FondoSubespacial"))
            repo.search("Sub") shouldHaveSize 0
        }
    }

    describe("RepositorioPlaneta - search por nombre parcial") {
        lateinit var repo: RepositorioPlaneta

        beforeEach { repo = RepositorioPlaneta() }

        it("encuentra por coincidencia parcial en el nombre") {
            val p1 = repo.create(planeta("Kepler-11b"))
            val p2 = repo.create(planeta("Kepler-22b"))
            repo.create(planeta("Gliese"))

            repo.search("Kepler") shouldContainAll listOf(p1, p2)
        }
    }
})
