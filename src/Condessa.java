public class Condessa extends Carta {

    @Override
    public void executarAcao(Jogador jogador) {
        // A Condessa não possui uma ação de "executar".
        // Ela é puramente defensiva (bloqueia assassinato).
        System.out.println("[INFO] " + jogador + " revelou a Condessa. Nenhuma ação executada.");
    }

    @Override
    public String getNome() {
        return "Condessa";
    }

    @Override
    public boolean podeBloquear(String acao) {
        // A Condessa bloqueia "ASSASSINAR"
        return acao.equals("ASSASSINAR");
    }
}