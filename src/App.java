
import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Seleccione una opción: ");
        System.out.println("1. Generar referencias de páginas desde imagen BMP");
        System.out.println("2. Calcular fallas de página, hits y tiempos desde archivo de referencias");
        isr = new InputStreamReader(System.in);
        br = new BufferedReader(isr);
        try {
            int opcion = Integer.parseInt(br.readLine());
            switch (opcion) {
                case 1:
                    System.out.println("Nombre del archivo con la imagen a procesar: ");
                    String ruta = "./data/" + br.readLine();
                    Imagen imagen = new Imagen(ruta);

                    System.out.println("Nombre del archivo con el mensaje a esconder: ");
                    String rutaMensaje = "./data/" + br.readLine();

                    // Leer el mensaje desde un archivo de texto
                    // int longitud = leerArchivoTexto(rutaMensaje);
                    // char[] mensaje = new char[longitud];
                    char[] mensaje = leerArchivoTexto(rutaMensaje);
                    int longitud = mensaje.length;

                    imagen.esconder(mensaje, longitud);

                    // Escribir la imagen con el mensaje escondido
                    imagen.escribirImagen(ruta.replace(".bmp", "") + "_mod.bmp");

                    System.out.println(
                            "El mensaje ha sido escondido exitosamente en la imagen: " + ruta.replace(".bmp", "")
                                    + "_mod.bmp");

                    // Recuperación del mensaje escondido
                    System.out.println("Nombre del archivo con el mensaje escondido: ");
                    ruta = "./data/" + br.readLine();
                    imagen = new Imagen(ruta);

                    String rutaSalida = "./data/out.txt";

                    // Leer la longitud del mensaje escondido
                    longitud = imagen.leerLongitud();
                    System.out.println("esta es la longitud: " + longitud);
                    mensaje = new char[longitud];

                    // Recuperar el mensaje
                    imagen.recuperar(mensaje, longitud);

                    // Escribir el mensaje recuperado en un archivo
                    escribirArchivoTexto(rutaSalida, mensaje);

                    // escribir las referencias
                    System.out.println("Tamano de página: ");
                    Integer tamanoPagina = Integer.parseInt(br.readLine());

                    System.out.println("Nombre de archivo para guardar las referencias: ");
                    String nombreArchivo = "./data/" + br.readLine();

                    List<String> listaMat = listaMatriz(imagen, "./data/matriz.txt", tamanoPagina, rutaMensaje);
                    int total = imagen.alto * imagen.ancho * 3;
                    List<String> listaMen = listaMensaje(tamanoPagina, mensaje, total);

                    System.out.println("El mensaje ha sido recuperado y almacenado en: " + rutaSalida);
                    br.close();

                    int P = tamanoPagina;
                    int NF = imagen.alto;
                    int NC = imagen.ancho;
                    int NR = 17 * longitud + 16;
                    // arrelar NP sale -1
                    int NP = ((NF * NC * 3) / P) + (longitud / tamanoPagina) + 1;

                    referencias(nombreArchivo, listaMat, listaMen, P, NF, NC, NR, NP);

                    break;
                case 2:
                    calcularFallasYHits(new Scanner(System.in));
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para leer el contenido de un archivo de texto y devolverlo como un
    // arreglo de char
    public static char[] leerArchivoTexto(String rutaArchivo) {
        StringBuilder contenido = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.toString().toCharArray();
    }

    // Método para escribir el contenido de un arreglo de char en un archivo de
    // texto
    public static void escribirArchivoTexto(String rutaArchivo, char[] mensaje) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo));
            bw.write(mensaje);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // lista Matriz

    public static List<String> listaMatriz(Imagen img, String nombreArchivo, int tamanoPagina, String archivoMensaje) {
        List<String> referencias = new ArrayList<String>();
        try {
            FileWriter writer = new FileWriter(nombreArchivo);
            int pagina = 0; // Contador de páginas virtuales
            int desplazamiento = 0; // Contador de desplazamiento dentro de la página

            // Escribir las referencias RGB de la imagen

            for (int i = 0; i < img.alto; i++) {
                for (int j = 0; j < img.ancho; j++) {
                    String strR = "Imagen[" + i + "][" + j + "].R," + pagina + "," + desplazamiento + ",R";
                    writer.write(strR + "\n");
                    referencias.add(strR);
                    desplazamiento++;
                    if (desplazamiento >= tamanoPagina) {
                        pagina++;
                        desplazamiento = 0;
                    }

                    String strG = "Imagen[" + i + "][" + j + "].G," + pagina + "," + desplazamiento + ",R";
                    writer.write(strG + "\n");
                    referencias.add(strG);
                    desplazamiento++;
                    if (desplazamiento >= tamanoPagina) {
                        pagina++;
                        desplazamiento = 0;
                    }

                    String strB = "Imagen[" + i + "][" + j + "].B," + pagina + "," + desplazamiento + ",R";
                    writer.write(strB + "\n");
                    referencias.add(strB);
                    desplazamiento++;
                    if (desplazamiento >= tamanoPagina) {
                        pagina++;
                        desplazamiento = 0;
                    }
                }
            }
            writer.close();
            System.out.println("Referencias RGB y mensaje guardados en: " + nombreArchivo);
            return referencias;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // listaMensaje
    public static List<String> listaMensaje(int tamanoPagina, char[] mensaje, int tamanoMatriz) throws IOException {
        {

            List<String> referencias = new ArrayList<String>();
            FileWriter writer = new FileWriter("./data/mensaje.txt");
            int inicio = tamanoMatriz / tamanoPagina;
            int desplazamiento = 0;

            for (int i = 0; i < mensaje.length; i++) {
                if (desplazamiento == tamanoPagina) {
                    inicio++;
                    desplazamiento = 0;
                }
                int j = 0;
                while (j < 9) {
                    try {
                        String str = "Mensaje[" + i + "]," + inicio + "," + desplazamiento + ",W";
                        writer.write(str + "\n");
                        referencias.add(str);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    j++;
                }
                desplazamiento++;
            }
            writer.close();

            return referencias;
        }

    }

    // referencias

    public static void referencias(String nombreArchivo, List<String> listaMat, List<String> listaMen, int P, int NF,
            int NC, int NR, int NP)
            throws IOException {
        FileWriter writer = new FileWriter(nombreArchivo);
        writer.write("P," + P + "\n");
        writer.write("NF," + NF + "\n");
        writer.write("NC," + NC + "\n");
        writer.write("NR," + NR + "\n");
        writer.write("NP," + NP + "\n");

        for (int i = 0; i < 16; i++) {
            String elem = listaMat.remove(0);
            writer.write(elem + "\n");
        }

        System.out.println(listaMat.size());
        System.out.println(listaMen.size());

        while (!listaMen.isEmpty()) {
            writer.write(listaMen.remove(0) + "\n");
            for (int i = 0; i < 8; i++) {
                writer.write(listaMat.remove(0) + "\n");
                writer.write(listaMen.remove(0) + "\n");
            }
        }
        writer.close();
    }

    private static void calcularFallasYHits(Scanner scanner) {
        try {

            System.out.println("Ingrese el número de marcos de página: ");
            int marcosTotales = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            System.out.println("Ingrese el nombre del archivo de referencias: ");
            String archivoReferencias = "./data/" + scanner.nextLine();

            SimulacionMemoria simulador = new SimulacionMemoria(marcosTotales, archivoReferencias);
            simulador.simular();
            simulador.mostrarResultados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
