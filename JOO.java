import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class JOO {

    private Baralho baralho;
    private List<Jogador> jogadores;
    private Scanner scanner;
    private boolean jogoRodando;

    // EFEITO: Códigos ANSI para cores no console - Melhora a visualização
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    public JOO() {
        this.baralho = new Baralho();
        this.jogadores = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.jogoRodando = true;
    }

    public static void main(String[] args) {
        Jogo jogo = new Jogo();
        jogo.iniciar();
    }

    public void iniciar() {
        // EFEITO: Título com cores e destaque
        limparConsole();
        System.out.println(BOLD + BLUE + "=== COUP: VERSÃO JAVA ===" + RESET);

        // 1. Setup Jogadores
        jogadores.add(new Jogador("Jogador 1"));
        jogadores.add(new Jogador("Jogador 2"));
        jogadores.add(new Jogador("Jogador 3"));

        // 2. Distribuir Cartas (2 para cada)
        for (Jogador j : jogadores) {
            j.getMao().add(baralho.comprarCarta());
            j.getMao().add(baralho.comprarCarta());
        }

        // EFEITO: Animação de início do jogo
        efeitoInicioJogo();

        // 3. Loop Principal
        int indiceJogadorAtual = 0;

        while (jogoRodando) {

            // Limpeza de jogadores eliminados
            jogadores.removeIf(j -> j.getMao().isEmpty());

            // Jogador Vitorioso - CORREÇÃO: Verificação segura
            if (jogadores.size() == 1) {
                // EFEITO: Vitória com destaque especial
                efeitoVitoria(jogadores.get(0));
                break;
            } else if (jogadores.isEmpty()) {
                System.out.println(RED + BOLD + "\n*** EMPATE! ***" + RESET);
                break;
            }

            // Indice para decidir a vez de cada jogador
            if (indiceJogadorAtual >= jogadores.size()) indiceJogadorAtual = 0;
            Jogador jogadorAtual = jogadores.get(indiceJogadorAtual);

            boolean turnoProcessado = processarTurno(jogadorAtual);

            // CORREÇÃO: Só passa a vez se o turno foi processado corretamente
            if (turnoProcessado) {
                indiceJogadorAtual++;
            }
            // Se turnoProcessado for false, o jogador atual repete a vez
        }

        scanner.close();
    }

    // CORREÇÃO: Método agora retorna boolean para controlar a vez
    private boolean processarTurno(Jogador jogador) {
        limparConsole();

        // EFEITO: Header do turno com cores
        System.out.println("\n" + BOLD + CYAN + "========================================" + RESET);
        System.out.println(BOLD + "VEZ DE: " + YELLOW + jogador.getNome() + RESET + " | " + GREEN + "Moedas: " + jogador.getQnt_moedas() + RESET);

        // EFEITO: Cartas com cor diferente
        System.out.print(BOLD + "\nCartas: " + RESET);
        for(Carta c : jogador.getMao()) {
            System.out.print(PURPLE + "[" + c.getNome() + "] " + RESET);
        }
        System.out.println();

        // EFEITO: Menu colorido para melhor legibilidade
        System.out.println(BOLD + "\n--- AÇÕES DISPONÍVEIS ---" + RESET);
        System.out.println(GREEN + "1. Renda (+1 moeda)" + RESET);
        System.out.println(CYAN + "2. Ajuda Externa (+2 moedas) [Pode ser Bloqueada por Duque]" + RESET);
        System.out.println(BLUE + "3. Taxar (Duque) (+3 moedas)" + RESET);
        System.out.println(RED + "4. Assassinar (Assassino) (-3 moedas)" + RESET);
        System.out.println(YELLOW + "5. Roubar (Capitão) (+2 moedas)" + RESET);
        System.out.println(PURPLE + "6. Trocar Cartas (Embaixador)" + RESET);
        System.out.println(BOLD + RED + "7. Golpe de Estado (-7 moedas)" + RESET);

        System.out.print(BOLD + "\nEscolha: " + RESET);
        String escolha = scanner.nextLine();

        boolean acaoValida = true;

        switch (escolha) {
            case "1": // (Renda) nao pode ser bloqueada
                jogador.setQnt_moedas(jogador.getQnt_moedas() + 1);
                // EFEITO: Feedback visual da ação
                efeitoMensagemSucesso(">> Pegou Renda. +1 moeda");
                break;

            case "2": // Ajuda Externa
                // Verifica se alguém bloqueia com Duque
                if (!tentarBloqueio(null, jogador, "AJUDA_EXTERNA", "Duque")) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() + 2);
                    efeitoMensagemSucesso(">> Ajuda Externa bem sucedida. +2 moedas");
                }
                break;

            case "3": // Taxar (Duque)
                if (resolverDesafio(jogador, "Duque")) {
                    Carta duqueTemp = new Duque();
                    duqueTemp.executarAcao(jogador);
                } else {
                    acaoValida = false;
                }
                break;

            case "4": // Assassinar
                if (jogador.getQnt_moedas() < 3) {
                    // EFEITO: Mensagem de erro em vermelho
                    efeitoMensagemErro("Moedas insuficientes (Precisa de 3).");
                    acaoValida = false;
                    break;
                }

                Jogador alvoAssassino = escolherAlvo(jogador);
                if (alvoAssassino == null) {
                    efeitoMensagemErro("Nenhum alvo disponível.");
                    acaoValida = false;
                    break;
                }

                jogador.setQnt_moedas(jogador.getQnt_moedas() - 3);
                // EFEITO: Destaque para ação perigosa
                efeitoMensagemPerigo(">> Pagou 3 moedas para assassinar " + alvoAssassino.getNome());

                if (resolverDesafio(jogador, "Assassino")) {
                    if (!tentarBloqueio(alvoAssassino, jogador, "ASSASSINAR", "Condessa")) {
                        efeitoMensagemPerigo(">> Assassinato confirmado!");
                        perderVida(alvoAssassino);
                    }
                } else {
                    acaoValida = false;
                }
                break;

            case "5": // Roubar (Capitão)
                Jogador alvoRoubo = escolherAlvo(jogador);
                if (alvoRoubo == null) {
                    efeitoMensagemErro("Nenhum alvo disponível.");
                    acaoValida = false;
                    break;
                }

                if (resolverDesafio(jogador, "Capitão")) {
                    if (!tentarBloqueio(alvoRoubo, jogador, "ROUBAR", "Capitão", "Embaixador")) {
                        int valor = Math.min(alvoRoubo.getQnt_moedas(), 2);
                        alvoRoubo.setQnt_moedas(alvoRoubo.getQnt_moedas() - valor);
                        jogador.setQnt_moedas(jogador.getQnt_moedas() + valor);
                        efeitoMensagemSucesso(">> Roubou " + valor + " moedas de " + alvoRoubo.getNome());
                    }
                } else {
                    acaoValida = false;
                }
                break;

            case "6": // Embaixador - CORREÇÃO COMPLETA
                if (resolverDesafio(jogador, "Embaixador")) {
                    realizarTrocaEmbaixador(jogador);
                } else {
                    acaoValida = false;
                }
                break;

            case "7": // Golpe de Estado
                if (jogador.getQnt_moedas() >= 7) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() - 7);
                    Jogador alvoGolpe = escolherAlvo(jogador);
                    if (alvoGolpe != null) {
                        // EFEITO: Animação especial para golpe de estado
                        efeitoGolpeEstado(alvoGolpe);
                        perderVida(alvoGolpe);
                    }
                } else {
                    efeitoMensagemErro("Moedas insuficientes (Precisa de 7).");
                    acaoValida = false;
                }
                break;

            default:
                // CORREÇÃO: Ação inválida - jogador perde a vez (não repete)
                efeitoMensagemErro("Ação inválida. Você perdeu a vez.");
                acaoValida = true; // A vez passa mesmo com ação inválida
                break;
        }

        // EFEITO: Pausa dramática antes do próximo turno
        if (acaoValida) {
            pausaDramatica(1500);
        }

        return acaoValida;
    }

    // --- SISTEMA DE DESAFIOS (MENTIRA) ---
    private boolean resolverDesafio(Jogador ator, String cartaNecessaria) {
        // EFEITO: Destacar a declaração do jogador
        System.out.println(YELLOW + ">> " + ator.getNome() + " diz ter: " + BOLD + cartaNecessaria + RESET);

        Jogador desafiante = null;
        for (Jogador oponente : jogadores) {
            if (!oponente.equals(ator)) {
                System.out.print(CYAN + oponente.getNome() + RESET + ", deseja contestar? (s/n): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    desafiante = oponente;
                    break;
                }
            }
        }

        if (desafiante == null) return true;

        // EFEITO: Animação de desafio
        efeitoDesafio(ator, desafiante);

        int indiceCarta = -1;
        for (int i = 0; i < ator.getMao().size(); i++) {
            if (ator.getMao().get(i).getNome().equalsIgnoreCase(cartaNecessaria)) {
                indiceCarta = i;
                break;
            }
        }

        if (indiceCarta != -1) {
            // EFEITO: Verdade comprovada
            efeitoVerdadeDesafio(ator, cartaNecessaria, desafiante);
            perderVida(desafiante);

            Carta c = ator.getMao().remove(indiceCarta);
            baralho.devolverCarta(c);
            baralho.embaralhar();
            ator.getMao().add(baralho.comprarCarta());

            return true;
        } else {
            // EFEITO: Mentira descoberta
            efeitoMentiraDesafio(ator, cartaNecessaria);
            perderVida(ator);
            return false;
        }
    }

    // --- SISTEMA DE BLOQUEIOS - CORREÇÃO: Verificação de null ---
    private boolean tentarBloqueio(Jogador defensor, Jogador atacante, String acao, String... cartasPossiveis) {
        // Caso especial: Ajuda Externa (qualquer um pode bloquear)
        if (defensor == null && acao.equals("AJUDA_EXTERNA")) {
            for (Jogador j : jogadores) {
                if (!j.equals(atacante)) {
                    System.out.print(CYAN + j.getNome() + RESET + ", deseja bloquear a Ajuda Externa com Duque? (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        System.out.println(YELLOW + j.getNome() + " tenta bloquear!" + RESET);
                        if (resolverDesafio(j, "Duque")) {
                            efeitoBloqueioSucesso(j.getNome(), "Ajuda Externa");
                            return true;
                        } else {
                            efeitoBloqueioFalha(j.getNome());
                            return false;
                        }
                    }
                }
            }
            return false;
        }

        // CORREÇÃO: Verificar se defensor não é null
        if (defensor == null) {
            return false;
        }

        // Casos direcionados (Roubo/Assassinato)
        System.out.print(CYAN + defensor.getNome() + RESET + ", deseja bloquear o " + acao + "? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            String cartaBloqueio = cartasPossiveis[0];
            if (cartasPossiveis.length > 1) {
                System.out.println("Bloquear com qual carta? (1: " + cartasPossiveis[0] + " / 2: " + cartasPossiveis[1] + ")");
                if (scanner.nextLine().equals("2")) cartaBloqueio = cartasPossiveis[1];
            }

            System.out.println(BLUE + ">> " + defensor.getNome() + " bloqueia usando " + cartaBloqueio + RESET);

            System.out.print(atacante.getNome() + ", duvida do bloqueio? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (resolverDesafio(defensor, cartaBloqueio)) {
                    efeitoBloqueioSucesso(defensor.getNome(), acao);
                    return true;
                } else {
                    efeitoBloqueioFalha(defensor.getNome());
                    return false;
                }
            }
            efeitoBloqueioSucesso(defensor.getNome(), acao);
            return true;
        }
        return false;
    }

    // --- MÉTODOS AUXILIARES ---

    private void perderVida(Jogador j) {
        if (j.getMao().isEmpty()) return;

        // EFEITO: Destaque para momento de perder vida
        System.out.println(RED + BOLD + "💀 " + j.getNome() + " está perdendo uma vida!" + RESET);
        pausaDramatica(1000);

        System.out.println(j.getNome() + ", escolha uma carta para perder:");
        for (int i = 0; i < j.getMao().size(); i++) {
            System.out.println(i + ": " + j.getMao().get(i).getNome());
        }

        int escolha = -1;
        try {
            System.out.print("Número: ");
            escolha = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {}

        if (escolha >= 0 && escolha < j.getMao().size()) {
            Carta c = j.getMao().remove(escolha);
            baralho.devolverCarta(c);
            // EFEITO: Feedback da carta perdida
            efeitoPerdaCarta(j.getNome(), c.getNome());
        } else {
            Carta c = j.getMao().remove(0);
            baralho.devolverCarta(c);
            efeitoPerdaCarta(j.getNome(), c.getNome());
        }
    }

    // CORREÇÃO: Validação de alvo
    private Jogador escolherAlvo(Jogador atacante) {
        List<Jogador> alvos = new ArrayList<>();
        for (Jogador j : jogadores) {
            if (!j.equals(atacante)) alvos.add(j);
        }

        if (alvos.isEmpty()) {
            return null;
        }

        // EFEITO: Lista de alvos com numeração clara
        System.out.println(BOLD + "Escolha o alvo:" + RESET);
        for (int i = 0; i < alvos.size(); i++) {
            System.out.println(YELLOW + i + RESET + ": " + alvos.get(i).getNome());
        }

        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (id >= 0 && id < alvos.size()) {
                return alvos.get(id);
            }
        } catch (Exception e) {}

        return alvos.get(0);
    }

    // CORREÇÃO COMPLETA: Troca de cartas do Embaixador
    private void realizarTrocaEmbaixador(Jogador j) {
        // EFEITO: Feedback visual do processo
        efeitoMensagemInfo(">> Comprando 2 cartas do baralho...");
        pausaDramatica(800);

        Carta c1 = baralho.comprarCarta();
        Carta c2 = baralho.comprarCarta();

        // CORREÇÃO: Criar lista temporária para as novas cartas
        List<Carta> maoTemp = new ArrayList<>(j.getMao());

        if (c1 != null) maoTemp.add(c1);
        if (c2 != null) maoTemp.add(c2);

        System.out.println(BOLD + "Suas cartas disponíveis para troca:" + RESET);
        for (int i = 0; i < maoTemp.size(); i++) {
            System.out.println(PURPLE + i + ": " + maoTemp.get(i).getNome() + RESET);
        }

        // CORREÇÃO: Devolver 2 cartas ao baralho
        System.out.println(BOLD + "Escolha 2 cartas para devolver ao baralho:" + RESET);
        List<Carta> cartasParaDevolver = new ArrayList<>();

        for (int k = 0; k < 2; k++) {
            System.out.print("Digite o índice da carta " + (k+1) + " para devolver: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine());
                if (idx >= 0 && idx < maoTemp.size()) {
                    cartasParaDevolver.add(maoTemp.remove(idx));
                    // Mostrar cartas restantes
                    System.out.println(BOLD + "Cartas restantes:" + RESET);
                    for (int i = 0; i < maoTemp.size(); i++) {
                        System.out.println(PURPLE + i + ": " + maoTemp.get(i).getNome() + RESET);
                    }
                } else {
                    System.out.println(RED + "Índice inválido. Devolvendo a primeira carta." + RESET);
                    cartasParaDevolver.add(maoTemp.remove(0));
                }
            } catch (Exception e) {
                System.out.println(RED + "Entrada inválida. Devolvendo a primeira carta." + RESET);
                cartasParaDevolver.add(maoTemp.remove(0));
            }
        }

        // CORREÇÃO: Atualizar a mão do jogador com as cartas restantes
        j.getMao().clear();
        j.getMao().addAll(maoTemp);

        // Devolver cartas ao baralho
        for (Carta c : cartasParaDevolver) {
            baralho.devolverCarta(c);
            System.out.println(GREEN + "Devolveu: " + c.getNome() + RESET);
        }

        // EFEITO: Resumo final da troca
        efeitoMensagemSucesso("Troca concluída! Sua nova mão:");
        for (Carta c : j.getMao()) {
            System.out.print(PURPLE + "[" + c.getNome() + "] " + RESET);
        }
        System.out.println();
    }

    // ==================================================
    // MÉTODOS DE EFEITOS VISUAIS - JAVA EFFECTS
    // ==================================================

    // EFEITO: Limpar console para melhor visualização
    private void limparConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: imprimir várias linhas vazias
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    // EFEITO: Pausa dramática para tensão
    private void pausaDramatica(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // EFEITO: Animação de início do jogo
    private void efeitoInicioJogo() {
        System.out.println(BOLD + GREEN + "\nIniciando jogo..." + RESET);
        for (int i = 0; i < 3; i++) {
            System.out.print(BLUE + "⚡" + RESET);
            pausaDramatica(300);
        }
        System.out.println(BOLD + GREEN + " PRONTO!\n" + RESET);
        pausaDramatica(1000);
    }

    // EFEITO: Vitória com celebração
    private void efeitoVitoria(Jogador vencedor) {
        System.out.println(BOLD + YELLOW + "\n✨✨✨✨✨✨✨✨✨✨✨✨✨✨" + RESET);
        System.out.println(BOLD + GREEN + "*** VITÓRIA DE " + vencedor.getNome() + "! ***" + RESET);
        System.out.println(BOLD + YELLOW + "✨✨✨✨✨✨✨✨✨✨✨✨✨✨" + RESET);

        // Efeito de confete
        for (int i = 0; i < 5; i++) {
            System.out.print("🎉 ");
            pausaDramatica(200);
        }
        System.out.println();
    }

    // EFEITO: Mensagens coloridas por tipo
    private void efeitoMensagemSucesso(String mensagem) {
        System.out.println(GREEN + BOLD + mensagem + RESET);
    }

    private void efeitoMensagemErro(String mensagem) {
        System.out.println(RED + BOLD + mensagem + RESET);
    }

    private void efeitoMensagemInfo(String mensagem) {
        System.out.println(CYAN + mensagem + RESET);
    }

    private void efeitoMensagemPerigo(String mensagem) {
        System.out.println(RED + BOLD + "⚠️  " + mensagem + RESET);
    }

    // EFEITO: Animação de desafio
    private void efeitoDesafio(Jogador ator, Jogador desafiante) {
        System.out.println(RED + BOLD + "\n!!! DESAFIO ACEITO !!!" + RESET);
        System.out.println(YELLOW + "Verificando cartas de " + ator.getNome() + "..." + RESET);

        // Efeito de suspense
        for (int i = 0; i < 3; i++) {
            System.out.print("🔍 ");
            pausaDramatica(400);
        }
        System.out.println();
    }

    // EFEITO: Verdade no desafio
    private void efeitoVerdadeDesafio(Jogador ator, String carta, Jogador desafiante) {
        System.out.println(GREEN + BOLD + ">> O ATOR FALOU A VERDADE! Mostrou " + carta + RESET);
        System.out.println(RED + desafiante.getNome() + " perde uma vida por errar o desafio." + RESET);
    }

    // EFEITO: Mentira no desafio
    private void efeitoMentiraDesafio(Jogador ator, String carta) {
        System.out.println(RED + BOLD + ">> O ATOR MENTIU! Não tem " + carta + RESET);
        System.out.println(RED + ator.getNome() + " perde uma vida." + RESET);
    }

    // EFEITO: Bloqueios
    private void efeitoBloqueioSucesso(String jogador, String acao) {
        System.out.println(BLUE + BOLD + ">> " + jogador + " bloqueou com sucesso: " + acao + RESET);
    }

    private void efeitoBloqueioFalha(String jogador) {
        System.out.println(RED + ">> " + jogador + " falhou no bloqueio!" + RESET);
    }

    // EFEITO: Golpe de Estado especial
    private void efeitoGolpeEstado(Jogador alvo) {
        System.out.println(RED + BOLD + "\n💥 GOLPE DE ESTADO! 💥" + RESET);
        System.out.println(RED + ">> ALVO: " + alvo.getNome().toUpperCase() + RESET);

        // Efeito dramático
        for (int i = 0; i < 3; i++) {
            System.out.print("💣 ");
            pausaDramatica(300);
        }
        System.out.println();
    }

    // EFEITO: Perda de carta
    private void efeitoPerdaCarta(String jogador, String carta) {
        System.out.println(RED + "💀 " + jogador + " perdeu " + carta + RESET);
    }
}