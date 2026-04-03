import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.uqbar.geodds.Point

class TripulanteTest :
        DescribeSpec({
            val baseLanzamientoTest =
                    BaseLanzamiento(
                            nombre = "Base Test",
                            direccion =
                                    Direccion(
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
                    fechaInicioActividad: LocalDate = LocalDate.now().minusYears(6),
                    rol: Rol = Piloto(),
                    perfilAptitud: PerfilAptitud = Conformista(),
                    distanciaMax: Double = 1.0,
                    direccion : Direccion = Direccion("Argentina", "Buenos Aires", "Falsa",
                        "123", Point(0.0, 0.0))
            ) =
                    Tripulante(
                            nombre = nombre,
                            apellido = apellido,
                            fechaNacimiento = fechaNacimiento,
                            fechaInicioActividad = fechaInicioActividad,
                            salarioBase = 100000.0,
                            rol = rol,
                            perfilAptitud = perfilAptitud,
                            baseLanzamiento = baseLanzamientoTest,
                            direccion = direccion,
                            distanciaMaxima = distanciaMax
                    )

            fun crearPlaneta(
                    nombre: String = "Tierra",
                    temperaturaMedia: Double = 20.0,
                    gravedad: Double = 9.8,
                    nivelRadiacion: Int = 10,
                    hayAguaLiquida: Boolean = true,
                    toxicidadAtmosferica: Int = 10,
                    actividadTectonica: Int = 10,
                    radioKm: Double = 6371.0,
                    fechaDescubrimiento: LocalDate = LocalDate.now().minusYears(10),
                    distanciaATierra: Double = 1.0,
                    fueAterrizado: Boolean = false
            ) =
                    Planeta(
                            nombre,
                            temperaturaMedia,
                            gravedad,
                            nivelRadiacion,
                            hayAguaLiquida,
                            toxicidadAtmosferica,
                            actividadTectonica,
                            radioKm,
                            fechaDescubrimiento,
                            distanciaATierra,
                            fueAterrizado
                    )

            fun crearTransbordador(
                    fechaFab: LocalDate = LocalDate.now().minusYears(2),
                    velProm: Double = 100.0
            ) =
                    Transbordador(
                            nombre = "Apolo",
                            codIdentif = 1,
                            fechaFabricacion = fechaFab,
                            consumoBase = 100.0,
                            autonomia = 100000,
                            velPromedio = velProm
                    )

            fun crearSonda(
                    fechaFab: LocalDate = LocalDate.now().minusYears(2),
                    velProm: Double = 100.0
            ) =
                    Sonda(
                            nombre = "Sonda I",
                            codIdentif = 1,
                            fechaFabricacion = fechaFab,
                            consumoBase = 100.0,
                            autonomia = 100000,
                            velPromedio = velProm
                    )

            fun crearMision(
                    planeta: Planeta = crearPlaneta(),
                    nave: Nave = crearTransbordador(),
                    tripulantes: List<Tripulante> = emptyList()
            ) =
                    Mision(
                            nombre = "Mision X",
                            descripcion = "Desc",
                            fechaLanzamiento = LocalDate.now().plusDays(10),
                            nave = nave,
                            destino = planeta,
                            tripulantes = tripulantes
                    )
            fun crearMisionConTransbordador(tripulantes: List<Tripulante> = emptyList()) =
                    Mision(
                            nombre = "Mision X",
                            descripcion = "Desc",
                            fechaLanzamiento = LocalDate.now().plusDays(10),
                            nave = crearTransbordador(),
                            destino = crearPlaneta(),
                            tripulantes = tripulantes
                    )

            describe("Validaciones iniciales de Tripulante") {
                it("lanza error si la fecha de nacimiento es posterior a hoy") {
                    shouldThrow<IllegalArgumentException> {
                        crearTripulante(fechaNacimiento = LocalDate.now().plusDays(1))
                    }
                }

                it("lanza error si el nombre está vacío") {
                    shouldThrow<IllegalArgumentException> { crearTripulante(nombre = "   ") }
                }

                it("lanza error si el apellido está vacío") {
                    shouldThrow<IllegalArgumentException> { crearTripulante(apellido = "") }
                }
            }

            describe("Cálculo de Experiencia y Años Activos") {
                it("calcula correctamente los años activo") {
                    val tripulante =
                            crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(6))
                    tripulante.calcularAniosActivo() shouldBe 6
                }

                it(
                        "la experiencia se compone de años activos contados como base y fracciona las misiones terminadas"
                ) {
                    val tripulante =
                            crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(2))

                    repeat(2) {
                        val misionExitosa = crearMision(nave = crearSonda())
                        misionExitosa.lanzar()
                        misionExitosa.completar()
                        tripulante.agregarMision(misionExitosa)
                    }

                    repeat(2) {
                        val misionFallida = crearMision(nave = crearSonda())
                        misionFallida.lanzar()
                        misionFallida.fallo()
                        tripulante.agregarMision(misionFallida)
                    }

                    val planetaDeAltoRiesgo =
                            crearPlaneta(hayAguaLiquida = false, distanciaATierra = 100.0)
                    val naveLenta = crearTransbordador(velProm = 1.0)
                    baseLanzamientoTest.estacionar(naveLenta)

                    repeat(4) {
                        val misionDeAltoRiesgo =
                                crearMision(planetaDeAltoRiesgo, naveLenta, listOf(tripulante))
                        misionDeAltoRiesgo.lanzar()
                        misionDeAltoRiesgo.cancelar()
                    }

                    tripulante.calcularExperiencia() shouldBe 5.0
                }
            }

            describe("Aptitud Base para Misiones") {
                it("no es apto si su experiencia total es menor a 3") {
                    val novato =
                            crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(2))
                    val mision = crearMision()
                    novato.esAptoParaMision(mision) shouldBe false
                }

                it("no es apto si ya tiene una mision EN CURSO") {
                    val experto =
                            crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(5))
                    val misionEnCurso = crearMision(crearPlaneta(), crearSonda())
                    misionEnCurso.lanzar()
                    experto.agregarMision(misionEnCurso)

                    val nuevaMision = crearMision()
                    experto.esAptoParaMision(nuevaMision) shouldBe false
                }
            }

            describe("Aptitud según Perfiles de Aptitud") {
                val experto = crearTripulante(fechaInicioActividad = LocalDate.now().minusYears(5))

                it("Conformista es apto siempre que cumpla condiciones base") {
                    experto.perfilAptitud = Conformista()
                    experto.esAptoParaMision(crearMision()) shouldBe true
                }

                it(
                        "Prudente es apto si la temperatura es ideal y la gravedad soportable del destino"
                ) {
                    experto.perfilAptitud = Prudente()

                    val planetaIdeal = crearPlaneta(temperaturaMedia = 20.0, gravedad = 9.8)
                    val misionIdeal = crearMision(planeta = planetaIdeal)
                    experto.esAptoParaMision(misionIdeal) shouldBe true

                    val planetaMalo = crearPlaneta(temperaturaMedia = 100.0, gravedad = 9.8)
                    val misionMala = crearMision(planeta = planetaMalo)
                    experto.esAptoParaMision(misionMala) shouldBe false
                }

                it("Explorador solo acepta misiones donde el destino no fue aterrizado") {
                    experto.perfilAptitud = Explorador()

                    val planetaNuevo = crearPlaneta(fueAterrizado = false)
                    val misionExploracion = crearMision(planeta = planetaNuevo)
                    experto.esAptoParaMision(misionExploracion) shouldBe true

                    val planetaViejo = crearPlaneta(fueAterrizado = true)
                    val misionVieja = crearMision(planeta = planetaViejo)
                    experto.esAptoParaMision(misionVieja) shouldBe false
                }

                it("Veterano acepta si duración de misión no supera su máximo impuesto") {
                    experto.perfilAptitud = Veterano(maxDiasPermitidos = 100)

                    val naveFija = crearTransbordador(velProm = 365.0)

                    val planetaCerca = crearPlaneta(distanciaATierra = 10.0)
                    val misionCorta = crearMision(planeta = planetaCerca, nave = naveFija)
                    experto.esAptoParaMision(misionCorta) shouldBe true

                    val planetaLejos = crearPlaneta(distanciaATierra = 100.0)
                    val misionLarga = crearMision(planeta = planetaLejos, nave = naveFija)
                    experto.esAptoParaMision(misionLarga) shouldBe false
                }

                it("Cauteloso acepta si la radiación de destino es menor a su umbral") {
                    experto.perfilAptitud = Cauteloso(umbralRadiacionMaximo = 50.0)

                    val planetaSeguro = crearPlaneta(nivelRadiacion = 40)
                    val misionSegura = crearMision(planeta = planetaSeguro)
                    experto.esAptoParaMision(misionSegura) shouldBe true

                    val planetaPeligroso = crearPlaneta(nivelRadiacion = 60)
                    val misionPeligrosa = crearMision(planeta = planetaPeligroso)
                    experto.esAptoParaMision(misionPeligrosa) shouldBe false
                }

                it("Exigente acepta misiones unicamente si la nave dispuesta es moderna") {
                    experto.perfilAptitud = Exigente()

                    val naveModerna = crearTransbordador(fechaFab = LocalDate.now().minusYears(2))
                    val misionModerna = crearMision(nave = naveModerna)
                    experto.esAptoParaMision(misionModerna) shouldBe true

                    val naveAntigua = crearTransbordador(fechaFab = LocalDate.now().minusYears(10))
                    val misionAntigua = crearMision(nave = naveAntigua)
                    experto.esAptoParaMision(misionAntigua) shouldBe false
                }
            }

            describe("Cálculo de Salario Total por Rol con Bonus") {
                it(
                        "Comandante gana 50% extra base y ademas suma 5% extra por cada misión exitosa"
                ) {
                    val comandante = crearTripulante(rol = Comandante())
                    comandante.salarioBase = 100000.0

                    comandante.calcularSalarioTotal() shouldBe 150000.0

                    val misionOk1 = crearMision(nave = crearSonda())
                    misionOk1.lanzar()
                    misionOk1.completar()
                    comandante.agregarMision(misionOk1)

                    val misionOk2 = crearMision(nave = crearSonda())
                    misionOk2.lanzar()
                    misionOk2.completar()
                    comandante.agregarMision(misionOk2)

                    comandante.calcularSalarioTotal() shouldBe 160000.0
                }

                it("Piloto gana siempre un 30% extra base y no recibe mas bonus") {
                    val piloto = crearTripulante(rol = Piloto())
                    piloto.salarioBase = 100000.0
                    piloto.calcularSalarioTotal() shouldBe 130000.0
                }

                it(
                        "Ingeniero gana 40% si su última mision fue un Carguero, o 20% si fue otra/no tuvo"
                ) {
                    val ingeniero = crearTripulante(rol = Ingeniero())
                    ingeniero.salarioBase = 100000.0

                    ingeniero.calcularSalarioTotal() shouldBe 120000.0

                    val carguero =
                            Carguero(
                                    nombre = "Carguero",
                                    codIdentif = 1,
                                    fechaFabricacion = LocalDate.now(),
                                    consumoBase = 10.0,
                                    autonomia = 100000,
                                    velPromedio = 10.0,
                                    capacidadTn = 50.0
                            )
                    baseLanzamientoTest.estacionar(carguero)

                    val misionCargueroOk =
                            crearMision(nave = carguero, tripulantes = listOf(ingeniero))
                    misionCargueroOk.lanzar()
                    misionCargueroOk.completar()
                    ingeniero.calcularSalarioTotal() shouldBe 140000.0
                }

                it(
                        "Científico gana un 10% extra por cada planeta diferente aterrizado en el historial"
                ) {
                    val cientifico = crearTripulante(rol = Cientifico())
                    cientifico.salarioBase = 100000.0

                    val planetaA = crearPlaneta(nombre = "Planeta A")
                    val planetaB = crearPlaneta(nombre = "Planeta B")

                    val misionA = crearMision(planeta = planetaA, nave = crearSonda())
                    misionA.lanzar()
                    misionA.completar()
                    cientifico.agregarMision(misionA)

                    val misionB = crearMision(planeta = planetaB, nave = crearSonda())
                    misionB.lanzar()
                    misionB.completar()
                    cientifico.agregarMision(misionB)

                    cientifico.calcularSalarioTotal() shouldBe 120000.0
                }

                it("Médico gana el 25% de base más 2% por estrés por cada misión fallida") {
                    val medico = crearTripulante(rol = Medico())
                    medico.salarioBase = 100000.0

                    medico.calcularSalarioTotal() shouldBe 125000.0

                    val misionFallo1 = crearMision(nave = crearSonda())
                    misionFallo1.lanzar()
                    misionFallo1.fallo()
                    medico.agregarMision(misionFallo1)

                    val misionFallo2 = crearMision(nave = crearSonda())
                    misionFallo2.lanzar()
                    misionFallo2.fallo()
                    medico.agregarMision(misionFallo2)

                    medico.calcularSalarioTotal() shouldBe 129000.0
                }
            }


            describe("distancia cercana") {

                it("considera si un tripulante está cerca de una ubicación") {
                    val ubicacion = Point(0.0, 0.0)
                    val tripulante = crearTripulante()

                    tripulante.consideraUbicacionCercana(ubicacion) shouldBe true

                }

                it("considera si un tripulante está lejos de una ubicación") {
                    val ubicacion = Point(5.2, 5.2)
                    val tripulante = crearTripulante()

                    tripulante.consideraUbicacionCercana(ubicacion) shouldBe false
                }
            }


        })
