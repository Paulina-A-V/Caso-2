import java.io.*;
import java.util.*;

public class SimulacionMemoria {
    private int hits = 0;               // Contador de hits
    private int misses = 0;             // Contador de misses de página
    private int r = 0;                  // Contador de lecturas (R)
    private int w = 0;                  // Contador de escrituras (W)
    private List<String> referencias;   // Lista de referencias de páginas
    private int numMarcosPagina;        // Número de marcos de página
    private int tamanioPagina;          // Tamaño de página en bytes (puede ajustarse según sea necesario)
    private int numFilas;               // Número de filas de la imagen
    private int numColumnas;            // Número de columnas de la imagen
    private int numReferencias;         // Número de referencias generadas
    private int numPaginas;             // Número de páginas virtuales
    private int referenciaActual = 0;   // Índice de la referencia actual
    private boolean fin = false;        // Indicador de finalización de la simulación
    private MemoriaVirtual memoria;            // Objeto de que representa la memoria
    private long tiempoEjecucion;

    // Clase interna para representar un marco de página


    public SimulacionMemoria(int numMarcosPagina, String archivoReferencias) throws IOException {
        this.numMarcosPagina = numMarcosPagina;
        this.memoria = new MemoriaVirtual(this.numMarcosPagina);
        leerReferencias(archivoReferencias);
        simular();
        imprimirResultados();
    }

    public void simular() {
        long startTime = System.nanoTime();
        Thread proceso = new Thread(new Runnable() {
            public void run() {        
                for (String ref : referencias) {
                    referenciaActual++;
                    if (ref.contains("R")) r++;
                    if (ref.contains("W")) w++;
                    Pagina pagina = crearPagina(ref);
                    boolean hit = memoria.cargarPagina(pagina);

                    if (hit) hits++;
                    else {
                        misses++;
                    }
                    try{
                        Thread.sleep(1);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                } 
                fin = true;
            }
        }
        );
        Thread actualizar = new Thread(new Runnable() {
            public void run (){
                while(!fin){
                    actualizarEstado();;
                    try{
                        Thread.sleep(2);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }

        }
        );

        proceso.start();
        actualizar.start();

        try {
            proceso.join();  // Espera hasta que el thread haya terminado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        fin = true;
        
        long endTime = System.nanoTime();
        tiempoEjecucion = endTime - startTime;
    };

    

    // Método para leer las referencias desde el archivo generado
    public void leerReferencias(String archivoReferencias) throws IOException {
        referencias = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias))) {
            String linea;
            int lineNumber = 0;
            while ((linea = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber >= 6) {
                referencias.add(linea);
            } else {
                // Leer configuraciones iniciales
                if (lineNumber == 1) this.tamanioPagina = Integer.parseInt(linea.split(",")[1]);  // Tamaño de página
                if (lineNumber == 2) this.numFilas = Integer.parseInt(linea.split(",")[1]);       // Número de filas
                if (lineNumber == 3) this.numColumnas = Integer.parseInt(linea.split(",")[1]);    // Número de columnas
                if (lineNumber == 4) this.numReferencias = Integer.parseInt(linea.split(",")[1]); // Número de referencias
                if (lineNumber == 5) this.numPaginas = Integer.parseInt(linea.split(",")[1]);     // Número de páginas
            }
}
        }

    }
    
    // Método para simular el comportamiento del sistema de actualizacion
    public synchronized void actualizarEstado() {
        memoria.envejecerPaginas();
    }

    private Pagina crearPagina(String ref){
        String[] partes = ref.split(",");
        int numeroPagina = Integer.parseInt(partes[1]);
        boolean esEscritura = partes[3].equals("W");
        Pagina pagina = new Pagina(numeroPagina);
        pagina.actualizar(esEscritura);
        return pagina;
    }


    // Método para imprimir los resultados
    public void imprimirResultados() {
        int totalReferencias = referencias.size();
        double porcentajeHits = (double) hits / totalReferencias * 100;
        System.out.println("Número de fallas de página: " + misses);
        System.out.println("Número de hits: " + hits);
        System.out.println("Porcentaje de hits: " + porcentajeHits + "%");
        System.out.println("Número de lecturas (R): " + r);
        System.out.println("Número de escrituras (W): " + w);
        System.out.println("Tiempo total de ejecución: " + tiempoEjecucion + " ns");
    }

    public void mostrarResultados() {
    }
}