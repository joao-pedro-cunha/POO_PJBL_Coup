public class Capitao extends Carta {

    @Override
    public void executarAcao(Jogador jogador) {
        // Ação: Roubar 2 moedas, fica no main
        System.out.println("[AÇÃO] " + jogador + " usou o Capitão para tentar roubar 2 moedas.");
    }

    @Override
    public String getNome() {
        return "Capitão";
    }

    @Override
    public boolean podeBloquear(String acao) {
        //O Capitão e o Embaixador bloqueia "ROUBAR"
        return acao.equals("ROUBAR");
    }
}