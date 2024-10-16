import java.util.*;

public class MemoriaVirtual {
    int marcosTotales;
    List<Pagina> marcos;

    public MemoriaVirtual(int marcosTotales) {
        this.marcosTotales = marcosTotales;
        this.marcos = new ArrayList<>();
    }

    public synchronized boolean cargarPagina(Pagina nuevaPagina) {
        for (Pagina pagina : marcos) {
            if (pagina.numeroPagina == nuevaPagina.numeroPagina) {
                pagina.actualizar(nuevaPagina.bitM); //Se actualiza bitR y bitM si es escritura
                return true; // Hit
            }
        }
        if (marcos.size() < marcosTotales) {
            marcos.add(nuevaPagina);
        } else {
            reemplazarPagina(nuevaPagina);
        }
        return false;
    }

    private synchronized void reemplazarPagina(Pagina nuevaPagina) {
        if (marcos.isEmpty()) {
            marcos.add(nuevaPagina);
        }else{
            Pagina paginaMenor = marcos.get(0);
            for (int i = 1; i < marcos.size(); i++) {
                Pagina paginaActual = marcos.get(i);
                if (paginaActual.contador < paginaMenor.contador) {
                    paginaMenor = paginaActual;
                    }
                }
            marcos.remove(paginaMenor);
            marcos.add(nuevaPagina);
        }
    }
    
    public synchronized  void envejecerPaginas() {
        for (Pagina pagina : marcos) {
            pagina.envejecer();
        }
    }
}
