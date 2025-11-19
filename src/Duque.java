public class Duque extends Carta{

    //Acao da carta
    @Override
    public void executarAcao(Jogador jogador) {
        // Ação: Taxar (Pegar 3 moedas do banco)
        int moedasAtuais = jogador.getQnt_moedas();
        jogador.setQnt_moedas(moedasAtuais + 3);

        System.out.println("[AÇÃO] " + jogador + " usou o Duque para taxar 3 moedas.");
        System.out.println("[INFO] " + jogador + " agora tem " + jogador.getQnt_moedas() + " moedas.");
    }

    //Nome da Carta
    @Override
    public String getNome() {
        return "Duque";
    }

    //Acao de bloqueio
    @Override
    public boolean podeBloquear(String acao) {
        // O Duque pode bloquear a ação "AJUDA_EXTERNA"
        if (acao.equals("AJUDA_EXTERNA")) {
            return true;
        }
        return false;
    }

}
