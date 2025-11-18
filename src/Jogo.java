import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Jogo {

    private Baralho baralho;
    private List<Jogador> jogadores;
    private Scanner scanner;
    private boolean jogoRodando;

    public Jogo() {
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
        System.out.println("=== COUP: VERSÃO JAVA ===");
        
        // 1. Setup Jogadores
        jogadores.add(new Jogador("Jogador 1"));
        jogadores.add(new Jogador("Jogador 2"));
        jogadores.add(new Jogador("Jogador 3"));

        // 2. Distribuir Cartas (2 para cada)
        for (Jogador j : jogadores) {
            j.getMao().add(baralho.comprarCarta());
            j.getMao().add(baralho.comprarCarta());
        }

        // 3. Loop Principal
        int indiceJogadorAtual = 0;

        while (jogoRodando) {

            // Limpeza de jogadores eliminados
            jogadores.removeIf(j -> j.getMao().isEmpty());

            // Jogador Vitorioso
            if (jogadores.size() <= 1) {
                System.out.println("\n*** VITÓRIA DE " + jogadores.get(0).getNome() + "! ***");
                break;
            }

            // Indice para decidir a vez de cada jogador, quando chega ao numero total de jogadores volta do inicio
            if (indiceJogadorAtual >= jogadores.size()) indiceJogadorAtual = 0;
            Jogador jogadorAtual = jogadores.get(indiceJogadorAtual);

            processarTurno(jogadorAtual);
            
            indiceJogadorAtual++;
        }
    }

    private void processarTurno(Jogador jogador) {

        // -- INFOS DO JOGADOR --
        System.out.println("\n========================================");
        System.out.println("VEZ DE: " + jogador.getNome() + " | Moedas: " + jogador.getQnt_moedas());
        System.out.print("\nCartas: ");
        for(Carta c : jogador.getMao()) System.out.print("[" + c.getNome() + "] ");
        System.out.println();

        // -- MENU --
        System.out.println("\n1. Renda (+1 moeda)");
        System.out.println("2. Ajuda Externa (+2 moedas) [Pode ser Bloqueada por Duque]");
        System.out.println("3. Taxar (Duque) (+3 moedas)");
        System.out.println("4. Assassinar (Assassino) (-3 moedas)");
        System.out.println("5. Roubar (Capitão) (+2 moedas)");
        System.out.println("6. Trocar Cartas (Embaixador)");
        System.out.println("7. Golpe de Estado (-7 moedas)");
        
        System.out.print("Escolha: ");
        String escolha = scanner.nextLine();

        switch (escolha) {
            case "1": // (Renda) nao pode ser bloqueada
                jogador.setQnt_moedas(jogador.getQnt_moedas() + 1);
                System.out.println(">> Pegou Renda.");
                break;

            case "2": // Ajuda Externa
                // Verifica se alguém bloqueia com Duque
                if (!tentarBloqueio(null, jogador, "AJUDA_EXTERNA", "Duque")) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() + 2);
                    System.out.println(">> Ajuda Externa bem sucedida.");
                }
                break;

            case "3": // Taxar (Duque)
                if (resolverDesafio(jogador, "Duque")) {
                    Carta duqueTemp = new Duque(); 
                    duqueTemp.executarAcao(jogador); // Usa o metodo da Classe Duque
                }
                break;

            case "4": // Assassinar
                if (jogador.getQnt_moedas() < 3) {
                    System.out.println("Moedas insuficientes (Precisa de 3).");
                    break;
                }
                
                Jogador alvoAssassino = escolherAlvo(jogador);
                jogador.setQnt_moedas(jogador.getQnt_moedas() - 3); // Paga o custo
                System.out.println(">> Pagou 3 moedas para assassinar " + alvoAssassino.getNome());

                // 1. Desafio (Duvidar que tem o Assassino)
                if (resolverDesafio(jogador, "Assassino")) {
                    // 2. Bloqueio (Vítima diz que tem Condessa)
                    if (!tentarBloqueio(alvoAssassino, jogador, "ASSASSINAR", "Condessa")) {
                        System.out.println(">> Assassinato confirmado!");
                        perderVida(alvoAssassino);
                    }
                }
                break;

            case "5": // Roubar (Capitão)
                Jogador alvoRoubo = escolherAlvo(jogador);
                // 1. Desafio (Duvidar que tem Capitão)
                if (resolverDesafio(jogador, "Capitão")) {
                    // 2. Bloqueio (Vítima diz que tem Capitão ou Embaixador)
                    if (!tentarBloqueio(alvoRoubo, jogador, "ROUBAR", "Capitão", "Embaixador")) {
                        int valor = Math.min(alvoRoubo.getQnt_moedas(), 2);
                        alvoRoubo.setQnt_moedas(alvoRoubo.getQnt_moedas() - valor);
                        jogador.setQnt_moedas(jogador.getQnt_moedas() + valor);
                        System.out.println(">> Roubou " + valor + " moedas de " + alvoRoubo.getNome());
                    }
                }
                break;

            case "6": // Embaixador
                if (resolverDesafio(jogador, "Embaixador")) {
                    realizarTrocaEmbaixador(jogador);
                }
                break;

            case "7": // Golpe de Estado
                if (jogador.getQnt_moedas() >= 7) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() - 7);
                    Jogador alvoGolpe = escolherAlvo(jogador);
                    System.out.println(">> GOLPE DE ESTADO em " + alvoGolpe.getNome() + "!");
                    perderVida(alvoGolpe);
                } else {
                    System.out.println("Moedas insuficientes (Precisa de 7).");
                }
                break;

            default:
                System.out.println("Ação inválida.");
        }
    }

    // --- SISTEMA DE DESAFIOS (MENTIRA) ---
    // Retorna TRUE se a ação pode continuar
    private boolean resolverDesafio(Jogador ator, String cartaNecessaria) {
        System.out.println(">> " + ator.getNome() + " diz ter: " + cartaNecessaria);
        
        // Pergunta para todos os outros jogadores
        Jogador desafiante = null;
        for (Jogador oponente : jogadores) {
            if (!oponente.equals(ator)) {
                System.out.print(oponente.getNome() + ", deseja contestar? (s/n): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    desafiante = oponente;
                    break; // Apenas um desafiante por vez
                }
            }
        }

        if (desafiante == null) return true; // Ninguém duvidou

        System.out.println("!!! DESAFIO ACEITO !!! Verificando cartas de " + ator.getNome() + "...");
        
        // Verifica se o ator tem a carta
        int indiceCarta = -1;
        for (int i = 0; i < ator.getMao().size(); i++) {
            if (ator.getMao().get(i).getNome().equalsIgnoreCase(cartaNecessaria)) {
                indiceCarta = i;
                break;
            }
        }

        if (indiceCarta != -1) {
            // Ator disse a verdade
            System.out.println(">> O ATOR FALOU A VERDADE! Mostrou " + cartaNecessaria);
            System.out.println(desafiante.getNome() + " perde uma vida por errar o desafio.");
            perderVida(desafiante);

            // Regra: Troca a carta revelada
            Carta c = ator.getMao().remove(indiceCarta);
            baralho.devolverCarta(c);
            baralho.embaralhar();
            ator.getMao().add(baralho.comprarCarta());
            
            return true; // Ação continua
        } else {
            // Ator mentiu
            System.out.println(">> O ATOR MENTIU! Não tem " + cartaNecessaria);
            System.out.println(ator.getNome() + " perde uma vida.");
            perderVida(ator);
            return false; // Ação cancelada
        }
    }

    // --- SISTEMA DE BLOQUEIOS ---
    // Retorna TRUE se a ação foi bloqueada com sucesso
    private boolean tentarBloqueio(Jogador defensor, Jogador atacante, String acao, String... cartasPossiveis) {
        // Caso especial: Ajuda Externa (qualquer um pode bloquear)
        if (defensor == null && acao.equals("AJUDA_EXTERNA")) {
            for (Jogador j : jogadores) {
                if (!j.equals(atacante)) {
                    System.out.print(j.getNome() + ", deseja bloquear a Ajuda Externa com Duque? (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        System.out.println(j.getNome() + " tenta bloquear!");
                        // O atacante pode desafiar o bloqueio
                        if (resolverDesafio(j, "Duque")) {
                            return true; // Bloqueio validado
                        } else {
                            return false; // Bloqueio falhou (mentira)
                        }
                    }
                }
            }
            return false;
        }

        // Casos direcionados (Roubo/Assassinato)
        System.out.print(defensor.getNome() + ", deseja bloquear o " + acao + "? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            // O defensor precisa escolher qual carta vai usar se houver mais de uma opção (Ex: Capitão ou Embaixador)
            String cartaBloqueio = cartasPossiveis[0];
            if (cartasPossiveis.length > 1) {
                System.out.println("Bloquear com qual carta? (1: " + cartasPossiveis[0] + " / 2: " + cartasPossiveis[1] + ")");
                if (scanner.nextLine().equals("2")) cartaBloqueio = cartasPossiveis[1];
            }

            System.out.println(">> " + defensor.getNome() + " bloqueia usando " + cartaBloqueio);

            // O atacante pode desafiar o bloqueio
            System.out.print(atacante.getNome() + ", duvida do bloqueio? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                // Se o defensor provar que tem a carta de bloqueio, o bloqueio funciona
                if (resolverDesafio(defensor, cartaBloqueio)) {
                    return true; 
                } else {
                    return false; // Defensor mentiu, bloqueio falha
                }
            }
            return true; // Atacante aceitou o bloqueio
        }
        return false; // Defensor não quis bloquear
    }

    // --- MÉTODOS AUXILIARES ---

    private void perderVida(Jogador j) {
        if (j.getMao().isEmpty()) return;
        
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
            System.out.println(j.getNome() + " perdeu " + c.getNome());
        } else {
            // Remove a primeira se a escolha for inválida
            Carta c = j.getMao().remove(0);
            baralho.devolverCarta(c);
            System.out.println(j.getNome() + " perdeu " + c.getNome());
        }
    }

    private Jogador escolherAlvo(Jogador atacante) {
        System.out.println("Escolha o alvo:");
        List<Jogador> alvos = new ArrayList<>();
        for (Jogador j : jogadores) {
            if (!j.equals(atacante)) alvos.add(j);
        }
        
        for (int i = 0; i < alvos.size(); i++) {
            System.out.println(i + ": " + alvos.get(i).getNome());
        }
        
        try {
            int id = Integer.parseInt(scanner.nextLine());
            return alvos.get(id);
        } catch (Exception e) {
            return alvos.get(0);
        }
    }
    
    private void realizarTrocaEmbaixador(Jogador j) {
        System.out.println(">> Comprando 2 cartas do baralho...");
        Carta c1 = baralho.comprarCarta();
        Carta c2 = baralho.comprarCarta();
        
        if (c1 != null) j.getMao().add(c1);
        if (c2 != null) j.getMao().add(c2);
        
        System.out.println("Sua mão agora: ");
        for (int i = 0; i < j.getMao().size(); i++) {
            System.out.println(i + ": " + j.getMao().get(i).getNome());
        }
        
        // Simplificação: Devolver 2 cartas
        System.out.println("Você deve devolver 2 cartas.");
        for (int k = 0; k < 2; k++) {
            System.out.print("Digite o índice da carta para devolver ao baralho: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine());
                if (idx >= 0 && idx < j.getMao().size()) {
                    Carta devolvida = j.getMao().remove(idx);
                    baralho.devolverCarta(devolvida);
                    System.out.println("Devolveu " + devolvida.getNome());
                } else {
                    baralho.devolverCarta(j.getMao().remove(0));
                }
            } catch (Exception e) {
                baralho.devolverCarta(j.getMao().remove(0));
            }
        }
    }
}