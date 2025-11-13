import java.util.ArrayList;

public class Jogador {
    private int qnt_moedas;
    private ArrayList<Carta> mao = new ArrayList<>();

    public ArrayList<Carta> getMao() {
        return mao;
    }

    public void setMao(ArrayList<Carta> mao) {
        this.mao = mao;
    }

    public int getQnt_moedas() {
        return qnt_moedas;
    }

    public void setQnt_moedas(int qnt_moedas) {
        this.qnt_moedas = qnt_moedas;
    }

}
