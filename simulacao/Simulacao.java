package simulacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import simulacao.Mapa.Camada;
import simulacao.geracao.Gerador;
import simulacao.geracao.Segmento;

/**
 * Responsavel pela simulacao.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public class Simulacao {
    private JanelaSimulacao janelaSimulacao;
    private Mapa mapa;

    public Simulacao() {
        Random rand = new Random(12345);
        int largura = 64;
        int altura = 64;
        Gerador g = new Gerador(largura, altura, rand);
        mapa = new Mapa(largura, altura);

        List<Segmento> S = g.gerar();
        Direcao[][][] ruas = g.gerarRuas(S);
        boolean[][] faixas = g.gerarFaixas(S);
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < largura; j++) {
                if (ruas[i][j][0] != null) {
                    Rua rua = new Rua(ruas[i][j], faixas[i][j], new Localizacao(j, i), mapa);
                    mapa.adicionarObjeto(rua);
                } else {
                    Calcada c = new Calcada(new Localizacao(j, i), mapa);
                    mapa.adicionarObjeto(c);
                }
            }
        }
        mapa.atualizarGrafos();
        
        List<Integer> posPontosOnibus = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            System.out.println();
            Localizacao loc = getRuaAleatoria(rand);
            System.out.println(loc.getX() + " " + loc.getY());
        }
        Onibus.posPontosOnibus = posPontosOnibus;
        
        long inicio = System.nanoTime();
        for (int i = 0; i < 200; i++) {
            Localizacao s = getRuaAleatoria(rand);
            Localizacao d = getRuaAleatoria(rand);
            Localizacao s1 = getCalcadaAleatoria(rand);
            Localizacao d1 = getCalcadaAleatoria(rand);
            Veiculo v = new Carro(s, d, mapa, rand);
            PedestreAmbulante p = new PedestreAmbulante(s1, d1, mapa, rand);
            mapa.adicionarObjeto(v);
            mapa.adicionarObjeto(p);
            ((Rua) mapa.getObjeto(Camada.BACKGROUND, d)).marcar(true);;
        }
        int tempo = (int) ((System.nanoTime() - inicio) / 1000000);
        System.out.printf("T: %d%n", tempo);
        // Inicializando o mapa com o veículo
        janelaSimulacao = new JanelaSimulacao(mapa);
    }

    private Localizacao getRuaAleatoria(Random r) {
        int x = r.nextInt(mapa.getLargura());
        int y = r.nextInt(mapa.getAltura());
        while (!(mapa.getObjeto(Mapa.Camada.BACKGROUND, x, y) instanceof Rua)) {
            x = r.nextInt(mapa.getLargura());
            y = r.nextInt(mapa.getAltura());
        } 
        return new Localizacao(x, y);
    }

    private Localizacao getCalcadaAleatoria(Random r) {
        int x = r.nextInt(mapa.getLargura());
        int y = r.nextInt(mapa.getAltura());
        while (!(mapa.getObjeto(Mapa.Camada.BACKGROUND, x, y) instanceof Calcada)) {
            x = r.nextInt(mapa.getLargura());
            y = r.nextInt(mapa.getAltura());
        } 
        return new Localizacao(x, y);
    }

    /**
     * Executa n iterações da simulação
     * @param n número de iterações
     */
    public void executarSimulacao(int n) {
        janelaSimulacao.atualizar();
        for (int i = 0; i < n; i++) {
            long inicio = System.nanoTime();
            iteracao();
            int tempo = (int) ((System.nanoTime() - inicio) / 1000000);
            System.out.printf("T: %d  \r", tempo);
            int t = 500 - tempo;
            esperar(t);
        }
    }

    /**
     * Executa uma iteração da simulação
     */
    private void iteracao() {
        // Para cada objeto da simulação,
        for (ObjetoSimulacao v : mapa.getObjetos()) {
            // Sua ação é executada
            v.executarAcao();
        }

        janelaSimulacao.atualizar();
    }

    private void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos < 0 ? 0 : milisegundos);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
