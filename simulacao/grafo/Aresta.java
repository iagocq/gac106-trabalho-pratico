package simulacao.grafo;

public class Aresta {
    private int peso;
    private int v;

    public Aresta(int v, int peso) {
        this.v = v;
        this.peso = peso;
    }

    public int getV() {
        return v;
    }

    public int getPeso() {
        return peso;
    }
}
