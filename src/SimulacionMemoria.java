import java.io.*;
import java.util.*;

public class SimulacionMemoria {
    private int hits = 0;               // Contador de hits
    private int misses = 0;             // Contador de misses de página
    private int r = 0;                  // Contador de lecturas (R)
    private int w = 0;                  // Contador de escrituras (W)
    private List<String> referencias;   // Lista de referencias de páginas
    private int numMarcosPagina;        // Número de marcos de página
    private boolean fin = false;        // Indicador de finalización de la simulación
    private MemoriaVirtual memoria;     // Objeto de que representa la memoria
    private long tiempoEjecucion;
    private String archivoReferencias;  // Archivo de referencias de páginas

    // Clase interna para representar un marco de página


    public SimulacionMemoria(int numMarcosPagina, String archivoReferencias) throws IOException {
        this.numMarcosPagina = numMarcosPagina;
        this.memoria = new MemoriaVirtual(this.numMarcosPagina);
        this.referencias = new ArrayList<>();
        simular();
        imprimirResultados();
    }

    public void simular() throws IOException {
        long startTime = System.nanoTime();
        Thread proceso = new Thread(new Runnable() {
            public void run() {
                try (BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias))) {
                    String linea;
                    int lineNumber = 0;

                    while ((linea = reader.readLine()) != null) {

                    lineNumber++;

                    if (lineNumber >= 6) {
                        if (linea.contains("R")) r++;
                        if (linea.contains("W")) w++;

                        Pagina pagina = crearPagina(linea);
                        boolean hit = memoria.cargarPagina(pagina);
                        referencias.add(linea);
                        
                        if (hit) hits++;
                        else {
                            misses++;
                        }
                        try{
                            Thread.sleep(1);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        fin = true;

                    }
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                if (lineNumber == 1)
                 Integer.parseInt(linea.split(",")[1]);
                if (lineNumber == 2)
                 Integer.parseInt(linea.split(",")[1]);
                if (lineNumber == 3)
                 Integer.parseInt(linea.split(",")[1]);
                if (lineNumber == 4)
                 Integer.parseInt(linea.split(",")[1]);
                if (lineNumber == 5)
                 Integer.parseInt(linea.split(",")[1]);
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

}