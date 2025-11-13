public abstract class Carta extends Baralho {
    protected String nome;
    protected int quantidade;

    public Carta(String nome, int quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public abstract void acao();

    public abstract void bloquear();

    public String getNome() {
        return nome;
    }
}
