public abstract class Carta {

    public abstract void executarAcao(Jogador jogador); // Executa a acao da carta

    public abstract String getNome(); // Retorna o nome da carta

    // Verifica se a carta pode bloquear uma acao

    public boolean podeBloquear(String acao) {
        return false;
    }

}
