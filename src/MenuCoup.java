import java.util.ArrayList;
import java.util.Scanner;

public class MenuCoup {

    // Lista de jogadores
    private ArrayList<Jogador> jogadores = new ArrayList<>();
    private Baralho baralho = new Baralho();
    private Scanner entrada = new Scanner(System.in);

    // ------------------------ MENU INICIAL ------------------------

    public void iniciar() {
        int opcao;

        do {
            System.out.println("\n========= JOGO COUP =========");
            System.out.println("1 - Cadastrar jogadores");
            System.out.println("2 - Iniciar jogo");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = entrada.nextInt();

            switch (opcao) {
                case 1:
                    cadastrarJogadores();
                    break;
                case 2:
                    if (jogadores.size() < 2) {
                        System.out.println("É necessário ao menos 2 jogadores!");
                    } else {
                        iniciarJogo();
                    }
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    // ------------------------ CADASTRAR JOGADORES ------------------------

    private void cadastrarJogadores() {
        System.out.print("Quantos jogadores? ");
        int quantidade = entrada.nextInt();

        for (int i = 0; i < quantidade; i++) {
            Jogador jogador = new Jogador();
            jogador.setQnt_moedas(2); // regra inicial de Coup
            jogadores.add(jogador);
        }

        System.out.println("Jogadores cadastrados: " + quantidade);
    }

    // ------------------------ INICIAR JOGO ------------------------

    private void iniciarJogo() {
        System.out.println("\nIniciando jogo...");

        baralho.Embaralhamento();

        // Distribui 2 cartas para cada jogador

        for (Jogador j : jogadores) {
            j.receberCarta(baralho.comprarCarta());
            j.receberCarta(baralho.comprarCarta());
        }

        System.out.println("Cartas distribuídas!");

        // Loop de turnos

        int turno = 0;
        while (!jogoAcabou()) {
            Jogador atual = jogadores.get(turno % jogadores.size());
            executarTurno(atual);

            turno++;
        }

        System.out.println("Jogo encerrado!");
    }

    // ------------------------ VERIFICAR FIM DE JOGO ------------------------
    private boolean jogoAcabou() {
        // verificar jogadores eliminados

        return false;
    }

    // ------------------------ EXECUTAR UM TURNO ------------------------

    private void executarTurno(Jogador jogador) {
        System.out.println("\n--- Turno do Jogador ---");
        System.out.println("Moedas: " + jogador.getQnt_moedas());
        System.out.println("Mão: ");

        for (Carta c : jogador.getMao()) {
            System.out.println(" - " + c.getNome());
        }

        exibirMenuAcoes(jogador);
    }

    // ------------------------ AÇÕES DO COUP ------------------------

    private void exibirMenuAcoes(Jogador jogador) {
        int escolha;

        do {
            System.out.println("\nAÇÕES DISPONÍVEIS:");
            System.out.println("1 - Renda (+1 moeda)");
            System.out.println("2 - Ajuda Externa (+2 moedas)");
            System.out.println("3 - Golpe de Estado (Coup) [-7 moedas]");
            System.out.println("4 - Ação de Carta (Ex: Duque, Assassino etc.)");
            System.out.print("Escolha sua ação: ");
            escolha = entrada.nextInt();

        } while (escolha < 1 || escolha > 4);

        switch (escolha) {
            case 1:
                jogador.setQnt_moedas(jogador.getQnt_moedas() + 1);
                System.out.println("Você recebeu 1 moeda.");
                break;

            case 2:
                jogador.setQnt_moedas(jogador.getQnt_moedas() + 2);
                System.out.println("Você recebeu 2 moedas.");
                break;

            case 3:
                // escolher alvo, remover carta, validar moedas

                System.out.println("Golpe de Estado realizado! (esqueleto)");
                jogador.setQnt_moedas(jogador.getQnt_moedas() - 7);
                break;

            case 4:
                menuAcaoCarta(jogador);
                break;
        }
    }

    // ------------------------ SUBMENU DE AÇÕES DE CARTA ------------------------

    private void menuAcaoCarta(Jogador jogador) {
        System.out.println("\nAÇÕES DE CARTA:");
        System.out.println("1 - Ação da carta revelada");
        System.out.println("2 - Bloquear ação de outro jogador");
        System.out.print("Escolha: ");
        int op = entrada.nextInt();

        switch (op) {
            case 1:
                // chamar ação da carta escolhida

                System.out.println("Ação de carta executada (esqueleto).");
                break;

            case 2:
                // lógica de bloqueio usando Carta.bloquear()
                System.out.println("Bloqueio executado (esqueleto).");
                break;
        }
    }
}
