# Revista UTEQ — App Android

## Descripción

Aplicación Android nativa en Kotlin que lista las últimas **10 ediciones** de la **Revista Mensual UTEQ**, ordenadas de la más reciente a la más antigua. Cada edición muestra su portada, el año y mes de publicación, y un enlace al PDF que abre directamente en el navegador del dispositivo.

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Lenguaje principal |
| Android SDK | compileSdk 35 / minSdk 34 | Plataforma |
| Volley | 1.2.1 | Peticiones HTTP a la API REST |
| Glide | 4.16.0 | Carga y caché de imágenes (portadas) |
| ArrayAdapter / ListView | — | Listado de revistas |

---

## API

La app consume la API pública de UTEQ:

```
GET https://apiws.uteq.edu.ec/h6RPoSoRaah0Y4Bah28eew/functions/information/entity/5
Authorization: Bearer <API_TOKEN>
```

### Estructura de la respuesta JSON

La API devuelve un arreglo JSON. Cada objeto tiene los siguientes campos:

| Campo | Tipo | Descripción |
|---|---|---|
| `anio` | Int | Año de la edición |
| `mes` | Int | Mes de la edición |
| `urlportada` | String | Nombre de archivo de la portada (incompleto) |
| `urlpw` | String | URL completa del PDF de la revista |

### Truco de `urlportada`

La API devuelve únicamente el **nombre del archivo** de la portada (por ejemplo `revista_2024_01.jpg`), no la URL completa. Para obtener la imagen real hay que **anteponer** la base:

```
https://uteq.edu.ec/assets/images/newspapers/<urlportada>
```

Esto se aplica en `RevistaAdapter.kt` mediante la constante `PORTADA_BASE_URL`.

---

## Configuración del token de autenticación

El token **nunca** se escribe en el código fuente. Se almacena en `local.properties` (excluido de control de versiones) y se expone en tiempo de compilación a través de `BuildConfig.API_TOKEN`.

### Pasos

1. Abre el archivo `local.properties` en la raíz del proyecto.
2. Al final del archivo agrega (sin comillas):

```properties
API_TOKEN=tu_token_real_aqui
```

3. Gradle leerá ese valor y lo inyectará en `BuildConfig.API_TOKEN` durante la compilación.

---

## Instalación y ejecución

```bash
# 1. Clona el repositorio
git clone https://github.com/tu-usuario/RevistaUTEQ2.git
cd RevistaUTEQ2

# 2. Crea/edita local.properties en la raíz y agrega tu token
echo "API_TOKEN=tu_token_aqui" >> local.properties
```

3. Abre el proyecto en **Android Studio**.
4. Ve a **File → Sync Project with Gradle Files** y espera a que termine.
5. Ve a **Build → Rebuild Project** para que se genere la clase `BuildConfig` con el token.
6. Conecta un dispositivo físico con **Android 14+** o inicia un emulador con API 34+.
7. Ejecuta la app con **Run → Run 'app'** (o `Shift + F10`).

> **Nota:** Antes de ejecutar, asegúrate de colocar manualmente los archivos de imagen descritos en la sección siguiente.

---

## Recursos de imagen que debes agregar manualmente

El código espera dos imágenes en `app/src/main/res/drawable/` con estos **nombres exactos**:

| Nombre de archivo | Descripción |
|---|---|
| `logo_uteq` (`.png` / `.webp` / `.jpg`) | Logo oficial de la UTEQ (aparece a la izquierda del encabezado) |
| `uteq_noticias` (`.png` / `.webp` / `.jpg`) | Banner o imagen "UTEQ Noticias" (aparece a la derecha del encabezado) |

Colócalos como `logo_uteq.png` y `uteq_noticias.png` (o la extensión que prefieras) dentro de esa carpeta.

---

## Capturas de pantalla

![Captura 1](capturas/captura1.png)

![Captura 2](capturas/captura2.png)
