# 🚀 Xpace


![image](./images/Imagen1.png)


Modelo inicial del sistema de gestión de expediciones de la agencia espacial **Xpace**.

Este proyecto corresponde a la **Entrega 0 del TP Integrador 2026 de Algoritmos II (UNSAM)** y tiene como objetivo desarrollar una primera versión del **modelo de dominio en Kotlin**.

---

## 📦 Dominio del sistema

El modelo incluye las siguientes entidades principales:

### 🧑‍🚀 Tripulante
Representa a un miembro de una expedición espacial.

Atributos principales:
- Nombre y apellido
- Fecha de nacimiento
- Fecha de inicio de actividad
- Misiones exitosas
- Misiones parcialmente exitosas
- Misiones fracasadas

Se calcula además la **experiencia del tripulante** en función de sus años de actividad y el resultado de sus misiones.

---

### 🪐 Planeta
Representa un planeta potencialmente explorable.

Información registrada:
- Temperatura media
- Gravedad
- Nivel de radiación
- Presencia de agua líquida
- Toxicidad atmosférica
- Actividad tectónica
- Radio
- Fecha de descubrimiento
- Distancia a la Tierra
- Estado de aterrizaje

El modelo permite determinar si el planeta posee:
- **Temperatura adecuada para humanos (0°C – 40°C)**
- **Gravedad soportable (3 – 15 m/s²)**

---

## 🧪 Testing

Cada integrante del equipo desarrolla al menos un **test unitario**, trabajando en su propio **branch** y generando un **Pull Request hacia `develop`**.

---

## 🛠️ Tecnologías

- **Kotlin**
- **Kotest**
- **Git / GitHub**

---

## 📚 Contexto académico

**Materia:** Algoritmos II  
**Universidad:** UNSAM  
**Trabajo:** TP Integrador 2026  
**Entrega:** 0
