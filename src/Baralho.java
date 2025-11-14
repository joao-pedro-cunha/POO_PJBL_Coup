import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Baralho extends Jogo {

    private int qntCartas;
    List<Carta> cartas = new ArrayList<>();

    // === CONSTRUTOR (AGORA NO LUGAR CORRETO) ===
    public Baralho() {
        // 3 de cada carta, igual ao Coup original
        for (int i = 0; i < 3; i++) {
            cartas.add(new Duque());
            cartas.add(new Assassino());
            cartas.add(new Capitao());
            cartas.add(new Embaixador());
            cartas.add(new Condessa());
        }

        qntCartas = cartas.size();
    }

public static class Duque extends Carta {

    public Duque() {
        super("Duque", 3);
    }

    @Override
    public void acao() {
        System.out.println("Duque executa: Taxação (+3 moedas).");
    }

    @Override
    public void bloquear() {
        System.out.println("Duque bloqueia: Ajuda Externa.");
    }
}

public static class Assassino extends Carta {

    public Assassino() {
        super("Assassino", 3);
    }

    @Override
    public void acao() {
        System.out.println("Assassino executa: Golpe por 3 moedas (Assassinato).");
    }

    @Override
    public void bloquear() {
        System.out.println("Assassino não possui bloqueio.");
    }
}

public static class Capitao extends Carta {

    public Capitao() {
        super("Capitão", 3);
    }

    @Override
    public void acao() {
        System.out.println("Capitão executa: Roubar 2 moedas de outro jogador.");
    }

    @Override
    public void bloquear() {
        System.out.println("Capitão bloqueia: Roubo.");
    }
}

public static class Embaixador extends Carta {

    public Embaixador() {
        super("Embaixador", 3);
    }

    @Override
    public void acao() {
        System.out.println("Embaixador executa: Trocar cartas com o baralho.");
    }

    @Override
    public void bloquear() {
        System.out.println("Embaixador bloqueia: Roubo.");
    }
}

public static class Condessa extends Carta {

    public Condessa() {
        super("Condessa", 3);
    }

    @Override
    public void acao() {
        System.out.println("Condessa não possui ação especial.");
    }

    @Override
    public void bloquear() {
        System.out.println("Condessa bloqueia: Assassinato.");
    }
}

    public void Embaralhamento() {
        Collections.shuffle(cartas);
    }

    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            return null;
        } else {
            return cartas.remove(0);
        }
    }
}
