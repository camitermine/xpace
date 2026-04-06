# 🚀 Xpace


![image](./images/Imagen1.png)


# 🚀 Xpace – Sistema de Gestión de Expediciones Espaciales

## 📌 Descripción general

Xpace es un sistema diseñado para gestionar expediciones espaciales, incluyendo tripulación, naves, planetas y misiones.  
El objetivo del proyecto es modelar el dominio utilizando programación orientada a objetos, aplicando buenas prácticas de diseño y testing.


# 🧪 Entrega 0 – Prueba de concepto

## 🎯 Objetivo

Configurar el entorno de trabajo y comenzar a modelar las primeras entidades del dominio.

## 🧩 Entidades modeladas

### 👨‍🚀 Tripulante

- Datos personales:
  - Nombre
  - Apellido
  - Fecha de nacimiento

- Experiencia:
  - Años de actividad
  - Misiones:
    - Exitosas
    - Parcialmente exitosas
    - Fracasadas

- Cálculo de experiencia:

---

### 🌍 Planeta

- Atributos:
  - Nombre
  - Temperatura (°C)
  - Gravedad (m/s²)
  - Radiación (0–100)
  - Toxicidad (0–100)
  - Actividad tectónica
  - Tamaño (radio en km)
  - Distancia a la Tierra (años luz)
  - Presencia de agua
  - Estado de aterrizaje

- Comportamientos:
  - Temperatura ideal: entre 0°C y 40°C
  - Gravedad soportable: entre 3 y 15 m/s²

---

## 🧪 Requerimientos técnicos
- Modelado en objetos
- Uso de Git:
  - Branch por integrante
  - Tests individuales
  - Pull Requests con revisión
- Tag final: `entrega-0`

---

# 🚀 Entrega 1 – Gestión completa de expediciones

## 🎯 Objetivo

Extender el modelo para soportar la operatoria completa de misiones espaciales.

---

## 🧩 Nuevas entidades

### 🚀 Nave Espacial

- Atributos:
  - Nombre
  - Código
  - Fecha de fabricación
  - Velocidad promedio
  - Autonomía (días)
  - Consumo base (litros/año luz)
  - Estado (en misión)

- Tipos:
  - **Sonda**
    - No transporta tripulantes
    - Consumo = consumo base

  - **Transbordador**
    - Capacidad de tripulantes
    - Consumo = consumo base + 10% por tripulante

  - **Carguero**
    - Capacidad de carga (toneladas)
    - Consumo = consumo base + 5% por tonelada
    - +20% adicional si antigüedad > 10 años

- Reglas:
  - Puede alcanzar un planeta si:


  - Es moderna si su antigüedad < 5 años

---

### 🌍 Planeta (extensión)

- Es habitable si:
  - Temperatura ideal
  - Gravedad soportable
  - Tiene agua líquida
  - Toxicidad < 30
  - Radiación < 40

- Índice de peligrosidad:

- Es explorable si:
  - No es habitable
  - Índice de peligrosidad < 60

---

### 👨‍🚀 Tripulante (extensión)

- Nuevos atributos:
  - Rol:
    - Comandante
    - Piloto
    - Ingeniero
    - Científico
    - Médico
  - Salario base
  - Perfil de aptitud
  - Base de lanzamiento

- Condiciones para ser apto:
  - Experiencia ≥ 3
  - No estar en una misión en curso

- Perfiles de aptitud (Strategy):
  - Conformista
  - Prudente
  - Explorador
  - Veterano
  - Cauteloso
  - Exigente con la nave

- Salario:

---

### 🎯 Misión

- Atributos:
  - Nombre
  - Descripción
  - Fecha de lanzamiento
  - Nave asignada
  - Tripulación
  - Planeta destino

- Estados:
  - BORRADOR
  - EN CURSO
  - COMPLETADA
  - FALLIDA
  - CANCELADA

- Duración estimada:


- Reglas:
  - Validaciones para lanzamiento
  - Control de capacidad y tipo de nave
  - Verificación de aptitud de tripulantes
  - Actualización de estados
  - Liberación de nave al finalizar

- Alto riesgo:
  - Planeta no habitable
  - Duración > 500 días

---

### 🏢 Base de Lanzamiento

- Atributos:
  - Nombre
  - Dirección:
    - País
    - Ciudad
    - Calle
    - Altura
    - Ubicación geográfica (Point)
  - Naves estacionadas
  - Capacidad máxima

- Funcionalidad:
  - Cálculo de cercanía entre bases usando distancia entre coordenadas

---

## ✅ Validaciones generales

- Strings no vacíos
- Rangos válidos en atributos numéricos
- Fechas coherentes
- Reglas específicas por entidad

---

## 🧪 Requerimientos técnicos

- Tests unitarios completos
- Uso de GitFlow:
  - Branch `develop`
  - Pull Requests con revisión
- Integración con GitHub Actions
- Badge de build y cobertura en README
- Tag final: `entrega-1`



