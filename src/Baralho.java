import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Baralho extends Jogo{
    private int qntCartas;
    List<Carta> cartas = new ArrayList<>();

    public void Embaralhamento() {
        Collections.shuffle(cartas);
    }

    public Carta comprarCarta() {
        if(cartas.isEmpty()){
            return null;
        }
        else {
            return cartas.remove(0);
        }
    }

}
