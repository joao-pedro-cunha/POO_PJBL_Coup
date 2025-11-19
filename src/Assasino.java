public class Assasino extends Carta {
    
    @Override
    public void executarAcao(Jogador jogador) {
        // Ação: Assassinar (Custa 3 moedas)
        
        if (jogador.getQnt_moedas() >= 3) {
            jogador.setQnt_moedas(jogador.getQnt_moedas() - 3);
            System.out.println("[AÇÃO] " + jogador + " pagou 3 moedas para tentar um assassinato.");
            System.out.println("[INFO] " + jogador + " agora tem " + jogador.getQnt_moedas() + " moedas.");
            //A logica do assasinato é feita no main pq precisa de outros jogadores.

        } else {
            System.out.println("[FALHA] " + jogador + " tentou assassinar, mas não tem 3 moedas!");
        }
    }

    @Override
    public String getNome() {
        return "Assassino";
    }

}