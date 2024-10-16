public class Pagina {
    int numeroPagina;
    int marco;
    boolean bitR;
    boolean bitM;
    int contador;

    public Pagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
        this.marco = -1; 
        this.bitR = false;
        this.bitM = false;
        this.contador = 0;
    }

    public void actualizar(boolean escritura) {
        this.bitR = true; 
        if (escritura) {
            this.bitM = true;
        }
    }

    public void envejecer() {
        this.contador >>= 1;
        if (this.bitR) {
            this.contador |= 0x80;
            this.bitR = false;
        }
    }
}

