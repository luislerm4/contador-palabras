# Práctica guiada: Contador de frecuencia de palabras usando flujos de E/S en Java

**Nombre:** Luis Angel Lerma Coss

**Expediente:** 225203072
**Curso:** Desarrollo de Sistemas III  
**Tema:** Flujos de entrada/salida en Java  
**Duración sugerida:** 90–120 minutos  
**Modalidad:** Laboratorio guiado  
**Organización:** Individual 

## 1. Problema

Se dispone de un archivo de texto que contiene un documento de longitud variable. Se requiere desarrollar una aplicación en Java que permita al usuario proporcionar el nombre del archivo y determine **cuántas veces aparece cada palabra**.

Por ejemplo, para el archivo:

```text
Java es un lenguaje de programación.
Java permite desarrollar aplicaciones.
El lenguaje Java es orientado a objetos.
```

la aplicación deberá obtener resultados similares a:

```text
java          3
es            2
un            1
lenguaje      2
de            1
programación  1
permite       1
desarrollar   1
aplicaciones  1
el            1
orientado     1
a             1
objetos       1
```

## 2. Objetivo de aprendizaje

Al finalizar la práctica, el estudiante será capaz de **utilizar flujos de entrada y salida de caracteres en Java para leer y procesar un archivo de texto y generar un archivo de resultados**, aplicando `BufferedReader`, `PrintWriter`, `Path`, `Files`, manejo de excepciones y estructuras de datos.

### Resultados de aprendizaje

- Representar archivos mediante `Path`.
- Verificar la existencia de un archivo mediante `Files`.
- Leer archivos de texto mediante `BufferedReader`.
- Procesar un archivo línea por línea.
- Normalizar y separar texto en palabras.
- Utilizar un `Map` para contabilizar frecuencias.
- Escribir resultados mediante `PrintWriter`.
- Utilizar `try-with-resources` para administrar recursos de E/S.
- Manejar errores mediante excepciones.
- Diferenciar las etapas de entrada, procesamiento y salida.

# Parte I. Preparación

## 3. Crear el proyecto

```text
contador-palabras/
├── src/
│   └── ContadorPalabras.java
├── datos/
│   └── texto.txt
└── salida/
```

Crear `datos/texto.txt` con:

```text
Java es un lenguaje de programación.
Java permite desarrollar diferentes tipos de aplicaciones.

Un programa Java puede leer información de archivos.
Un programa Java también puede escribir información en archivos.

Los flujos de entrada permiten leer datos.
Los flujos de salida permiten escribir datos.
```

# Parte II. Analizar el problema

## 4. Identificar entrada, procesamiento y salida

| Elemento | Descripción                                                                                           |
|---|-------------------------------------------------------------------------------------------------------|
| Entrada | archivo texto.txt                                                                                     |
| Procesamiento | el programa lee el texto, separa las palabras individualmente y cuenta cuantas veces aparece cada una |
| Salida | un nuevo archivo frecuencias.txt con el resultado de las palabras y el conteo                         |

```text
                   INPUT STREAM
                         │
                         ▼
┌───────────┐     ┌──────────────┐
│ texto.txt │ ──► │   Programa   │
└───────────┘     │ Separar      │
                  │ palabras     │
                  │      ↓       │
                  │ Contabilizar │
                  │ frecuencias  │
                  └──────┬───────┘
                         │
                         ▼
                   OUTPUT STREAM
                         │
                         ▼
                ┌────────────────┐
                │ frecuencias.txt│
                └────────────────┘
```

**Pregunta:** ¿Qué tipo de flujo resulta más apropiado para este problema: bytes o caracteres? Justifique.

Es más apropiado usar un flujo de caracteres por que no estamos hablando de imagenes o binarios, sino que estamos hablando de texto plabo
# Parte III. Recibir y representar el archivo

## 5. Proporcionar el nombre mediante argumento

```bash
java ContadorPalabras datos/texto.txt
```

```java
public class ContadorPalabras {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ContadorPalabras <archivo>");
            return;
        }

        String nombreArchivo = args[0];
        System.out.println("Archivo: " + nombreArchivo);
    }
}
```

### Pruebas

```bash
java ContadorPalabras
java ContadorPalabras datos/texto.txt
```

**Pregunta:** ¿Por qué resulta preferible recibir el nombre del archivo como argumento en lugar de escribirlo directamente en el código?
Por que permite procesar distintos archivos sin necesidad de modificar el codigo fuente, facilitando la integracion del programa.
## 6. Trabajar con `Path`

```java
import java.nio.file.Path;

Path archivo = Path.of(args[0]);

System.out.println("Archivo: " + archivo.getFileName());
System.out.println("Ruta: " + archivo.toAbsolutePath());
```

## 7. Verificar el archivo con `Files.exists()`

```java
import java.nio.file.Files;

if (!Files.exists(archivo)) {
    System.err.println("El archivo no existe: " + archivo);
    return;
}
```

Probar:

```bash
java ContadorPalabras datos/no-existe.txt
```
# Parte IV. Leer el archivo

## 8. Utilizar `BufferedReader`

```java
import java.io.BufferedReader;
import java.io.IOException;

try (
    BufferedReader lector = Files.newBufferedReader(archivo)
) {
    String linea;

    while ((linea = lector.readLine()) != null) {
        System.out.println(linea);
    }

} catch (IOException e) {
    System.err.println("Error al leer el archivo: " + e.getMessage());
}
```

# Parte V. Separar y normalizar palabras

## 9. Primera aproximación

```java
String[] palabras = linea.split("\\s+");

for (String palabra : palabras) {
    System.out.println(palabra);
}
```

Para la línea `Java es un lenguaje de programación.` se observará que `programación.` conserva el punto.

## 10. Normalización

```java
linea = linea.toLowerCase();

// Encontrar cualquier carácter que no sea una letra, 
// un número o un espacio en blanco.
linea = linea.replaceAll(
    "[^\\p{L}\\p{N}\\s]",
    ""
);

String[] palabras = linea.trim().split("\\s+");
```

Para:

```text
Java, JAVA y java.
```

se espera:

```text
java
java
y
java
```

**Pregunta:** ¿Qué ocurriría si no normalizamos las palabras antes de contabilizarlas?

# Parte VI. Contabilizar palabras

## 11. Utilizar un `Map`

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> frecuencias = new HashMap<>();
```

Conceptualmente:

```text
Clave  → palabra
Valor  → número de apariciones
```

## 12. Actualizar frecuencias

Primera solución:

```java
if (frecuencias.containsKey(palabra)) {
    int frecuencia = frecuencias.get(palabra);
    frecuencias.put(palabra, frecuencia + 1);
} else {
    frecuencias.put(palabra, 1);
}
```

Versión simplificada:

```java
frecuencias.put(
    palabra,
    frecuencias.getOrDefault(palabra, 0) + 1
);
```

---

# Parte VII. Integrar lectura y conteo

## 13. Procesamiento completo

```java
Map<String, Integer> frecuencias = new HashMap<>();

try (
    BufferedReader lector = Files.newBufferedReader(archivo)
) {
    String linea;

    while ((linea = lector.readLine()) != null) {
        linea = linea.toLowerCase();
        linea = linea.replaceAll("[^\\p{L}\\p{N}\\s]", "");

        if (linea.isBlank()) {
            continue;
        }

        String[] palabras = linea.trim().split("\\s+");

        for (String palabra : palabras) {
            frecuencias.put(
                palabra,
                frecuencias.getOrDefault(palabra, 0) + 1
            );
        }
    }

} catch (IOException e) {
    System.err.println("Error de lectura: " + e.getMessage());
}
```

# Parte VIII. Mostrar y ordenar resultados

## 14. Recorrer el `Map`

```java
for (Map.Entry<String, Integer> entrada : frecuencias.entrySet()) {
    System.out.printf(
        "%-20s %d%n",
        entrada.getKey(),
        entrada.getValue()
    );
}
```

`HashMap` no garantiza orden alfabético. Para ordenar:

```java
import java.util.TreeMap;

Map<String, Integer> ordenadas = new TreeMap<>(frecuencias);
```

---

# Parte IX. Generar un flujo de salida

## 15. Crear `frecuencias.txt`

```java
Path directorioSalida = Path.of("salida");
Files.createDirectories(directorioSalida);

Path archivoSalida = directorioSalida.resolve("frecuencias.txt");
```

## 16. Utilizar `PrintWriter`

```java
import java.io.PrintWriter;

try (
    PrintWriter escritor = new PrintWriter(
        Files.newBufferedWriter(archivoSalida)
    )
) {
    escritor.printf("%-20s %s%n", "PALABRA", "FRECUENCIA");
    escritor.println("-------------------------------");

    for (Map.Entry<String, Integer> entrada : ordenadas.entrySet()) {
        escritor.printf(
            "%-20s %d%n",
            entrada.getKey(),
            entrada.getValue()
        );
    }

} catch (IOException e) {
    System.err.println("Error al escribir resultados: " + e.getMessage());
}
```

# Parte X. Estadísticas generales

## 17. Número total de palabras

Agregar:

```java
int totalPalabras = 0;
```

Dentro del ciclo de palabras:

```java
totalPalabras++;
```

Mostrar:

```java
System.out.println("Total de palabras: " + totalPalabras);
System.out.println("Palabras diferentes: " + frecuencias.size());
```

## 18. Identificar la palabra más frecuente

```java
String palabraMasFrecuente = "";
int frecuenciaMaxima = 0;

for (Map.Entry<String, Integer> entrada : frecuencias.entrySet()) {
    if (entrada.getValue() > frecuenciaMaxima) {
        palabraMasFrecuente = entrada.getKey();
        frecuenciaMaxima = entrada.getValue();
    }
}
```

Mostrar:

```java
System.out.println(
    "Palabra más frecuente: "
    + palabraMasFrecuente
    + " (" + frecuenciaMaxima + ")"
);
```

---

# Parte XI. Actividad integradora

## 19. Analizador de documentos

A partir de la solución guiada,  deberá ampliar la aplicación para crear:

```text
AnalizadorDocumento.java
```

Ejecución:

```bash
java AnalizadorDocumento documento.txt
```

El programa deberá generar `reporte-documento.txt`:

```text
ANÁLISIS DEL DOCUMENTO
======================

Archivo: documento.txt

Total de líneas: 125
Total de palabras: 1843
Palabras diferentes: 536

Palabra más frecuente:
sistema (47)

FRECUENCIA DE PALABRAS
----------------------

sistema             47
software            35
datos               29
...
```

### Requerimientos obligatorios

1. Recibir el archivo mediante argumento.
2. Utilizar `Path`.
3. Comprobar el archivo mediante `Files.exists()`.
4. Utilizar `BufferedReader`.
5. Procesar el documento línea por línea.
6. Ignorar diferencias entre mayúsculas y minúsculas.
7. Eliminar signos de puntuación.
8. Utilizar un `Map<String,Integer>`.
9. Determinar el total de líneas.
10. Determinar el total de palabras.
11. Determinar el número de palabras diferentes.
12. Identificar la palabra con mayor frecuencia.
13. Ordenar alfabéticamente las palabras.
14. Crear automáticamente el directorio de salida.
15. Utilizar `PrintWriter`.
16. Generar `reporte-documento.txt`.
17. Utilizar `try-with-resources`.
18. Manejar adecuadamente `IOException`.


