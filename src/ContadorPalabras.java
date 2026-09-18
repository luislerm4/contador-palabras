import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContadorPalabras {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java ContadorPalabras <archivo>");
            return;
        }

        Path archivo = Path.of(args[0]);
        System.out.println("Archivo: " + archivo.getFileName());
        System.out.println("Ruta: " + archivo.toAbsolutePath());

        if (!Files.exists(archivo)) {
            System.err.println("El archivo no existe: " + archivo);
            return;
        }

        try (
                BufferedReader lector = Files.newBufferedReader(archivo)
        ) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                linea = linea.toLowerCase();
                linea = linea.replaceAll("[^\\p{L}\\p{N}\\s]", "");

                String[] palabras = linea.trim().split("\\s+");

                for (String palabra : palabras) {
                    if (!palabra.isEmpty()) {
                        System.out.println(palabra);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}