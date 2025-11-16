import java.util.ArrayList;

public class Jogador {
    private String nome;
    private int qnt_moedas;
    private ArrayList<Carta> mao = new ArrayList<>();

    //Constutor
    public Jogador(String nome) {
        this.qnt_moedas = 0;
        this.nome = nome;
    }

    // Getters e setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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

    //Metodo to String
    @Override
    public String toString() {
        return this.nome;
    }
}
