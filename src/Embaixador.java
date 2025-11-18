public class Embaixador extends Carta {

    @Override
    public void executarAcao(Jogador jogador) {
        // Ação: Trocar cartas, fica no main
        System.out.println("[AÇÃO] " + jogador + " usou o Embaixador para trocar cartas.");

    }

    @Override
    public String getNome() {
        return "Embaixador";
    }

    @Override
    public boolean podeBloquear(String acao) {
        //O Embaixador e o Capitão bloqueia "ROUBAR"
        return acao.equals("ROUBAR");
    }
}