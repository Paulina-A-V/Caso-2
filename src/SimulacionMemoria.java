import java.io.*;
import java.util.*;

public class SimulacionMemoria {
    private int hits = 0;               // Contador de hits
    private int misses = 0;             // Contador de misses de página
    private int r = 0;                  // Contador de lecturas (R)
    private int w = 0;                  // Contador de escrituras (W)
    private List<Marco> marcosEnUso;    // Lista de marcos de página actualmente en uso
    private List<String> referencias;   // Lista de referencias de páginas
    private int numMarcosPagina;        // Número de marcos de página
    private int tamanioPagina;          // Tamaño de página en bytes (puede ajustarse según sea necesario)
    private int numFilas;               // Número de filas de la imagen
    private int numColumnas;            // Número de columnas de la imagen
    private int numReferencias;         // Número de referencias generadas
    private int numPaginas;             // Número de páginas virtuales
    private int referenciaActual = 0;   // Índice de la referencia actual

    // Clase interna para representar un marco de página


    public SimulacionMemoria(int numMarcosPagina, String archivoReferencias) throws IOException {
        this.numMarcosPagina = numMarcosPagina;
        this.marcosEnUso = new ArrayList<>(numMarcosPagina);
        leerReferencias(archivoReferencias);
    }

    public void simular() {

    }

    // Método para leer las referencias desde el archivo generado
    public void leerReferencias(String archivoReferencias) throws IOException {
        referencias = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias));

        String linea;
        while ((linea = reader.readLine()) != null) {
            // Leer cada referencia y añadirla a la lista
            referencias.add(linea);
        }
        reader.close();

        // Leer configuraciones iniciales
        this.tamanioPagina = Integer.parseInt(referencias.get(0).split("=")[1]);  // Tamaño de página
        this.numFilas = Integer.parseInt(referencias.get(1).split("=")[1]);       // Número de filas
        this.numColumnas = Integer.parseInt(referencias.get(2).split("=")[1]);    // Número de columnas
        this.numReferencias = Integer.parseInt(referencias.get(3).split("=")[1]); // Número de referencias
        this.numPaginas = Integer.parseInt(referencias.get(4).split("=")[1]);     // Número de páginas
    }
    
    

    // Método para simular el comportamiento del sistema de paginación
    public synchronized void actualizarEstado() {
        if (referenciaActual < referencias.size()) {
            String referencia = referencias.get(referenciaActual);
            String[] partes = referencia.split(",");
            int pagina = Integer.parseInt(partes[1]);  // Obtener el número de página de la referencia
            String tipoOperacion = partes[3];          // Obtener el tipo de operación (R o W)

            boolean hit = false;
            for (Marco marco : marcosEnUso) {
                if (marco.pagina == pagina) {
                    hit = true;
                    marco.contador = (marco.contador >> 1) | 0x80;  // Actualizar el contador
                    marco.bitR = true;  // Actualizar el bit R
                    break;
                } else {
                    marco.contador >>= 1;  // Desplazar el contador  a la derecha
                }
            }

            if (hit) {
                hits++;
            } else {
                misses++;
                
                if (marcosEnUso.size() < numMarcosPagina) {
                    // Si hay espacio disponible, añadir un nuevo marco
                    marcosEnUso.add(new Marco(pagina, true));
                } else {
                    // Si no hay espacio disponible, reemplazar un marco
                    Marco marcoReemplazado = marcosEnUso.get(0);
                    for (Marco marco : marcosEnUso) {
                        if (marco.contador < marcoReemplazado.contador) {
                            marcoReemplazado = marco;
                        }
                    }
                    marcosEnUso.remove(marcoReemplazado);
                    marcosEnUso.add(new Marco(pagina, true));
                }
            }

            // Contar lecturas y escrituras
            if (tipoOperacion.equals("R")) {
                r++;
            } else if (tipoOperacion.equals("W")) {
                w++;
            }

            referenciaActual++;
        }
    }

    // Método para ejecutar el algoritmo de actualización del bit R
    public synchronized void actualizarBitR() {
        for (Marco marco : marcosEnUso) {
        marco.bitR = false;  // Resetear el bit R
        }
    }


    // Método para imprimir los resultados
    public void imprimirResultados() {
        int totalReferencias = referencias.size();
        double porcentajeHits = (double) hits / totalReferencias * 100;
        System.out.println("Número de fallas de página: " + misses);
        System.out.println("Porcentaje de hits: " + porcentajeHits + "%");
        System.out.println("Número de lecturas (R): " + r);
        System.out.println("Número de escrituras (W): " + w);
    }

    public void mostrarResultados() {
    }
}