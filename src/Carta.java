public abstract class Carta {

    public abstract void executarAcao(Jogador jogador);

    public abstract String getNome();

    public boolean podeBloquear(String acao) {
        // Por padrão, uma carta não bloqueia nada.
        // As cartas específicas (Duque, Condessa, etc.) irão sobrescrever (override) isto.
        return false;
    }
}

