
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Baralho {

    private List<Carta> cartas = new ArrayList<>();

    public Baralho() {
        // 4. Popula o baralho (5 de cada carta, total 25)
        for (int i = 0; i < 5; i++) {
            cartas.add(new Duque());
            cartas.add(new Assasino());
            cartas.add(new Capitao());
            cartas.add(new Condessa());
            cartas.add(new Embaixador());
        }

        // 5. Embaralha o baralho assim que ele é criado
        this.embaralhar();
    }


    public void embaralhar() {
        Collections.shuffle(cartas);
        System.out.println("[INFO] Baralho foi embaralhado.");
    }

    /**
     * Retira e retorna a carta do topo do baralho.
     * @return A carta do topo, ou null se o baralho estiver vazio.
     */
    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            System.out.println("[ERRO] O baralho está vazio!");
            return null; // Não há mais cartas
        } else {
            // Remove a carta da posição 0 (topo)
            return cartas.remove(0);
        }
    }


    public int getTamanho() {
        return cartas.size();
    }


    public void devolverCarta(Carta carta) {
        if (carta != null) {
            cartas.add(carta); // Adiciona no final da lista (fundo)
        }
    }
}