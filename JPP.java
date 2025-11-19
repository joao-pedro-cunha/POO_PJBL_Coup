import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JPP {
    private Baralho baralho;
    private List<Jogador> jogadores;
    private boolean jogoRodando;
    private int indiceJogadorAtual;

    private int jogosJogados;
    private static final String Cont = "Dados/cou_jogos_jogados.txt";
    private List<String> historicoVencedores;

    // COMPONENTES DA INTERFACE GRÁFICA - JAVA EFFECTS VISUAIS
    private JFrame frame;
    private JTextArea areaTexto;
    private JPanel painelBotoes;
    private JLabel labelJogadorAtual;
    private JLabel labelMoedas;
    private JTextArea areaCartas;
    private JPanel painelJogadores;
    private JButton[] botoesAcoes;
    ;
    private JLabel labelContadorJogos;

    // CORES PARA EFEITOS VISUAIS - JAVA EFFECTS
    private final Color COR_FUNDO = new Color(240, 240, 255);
    private final Color COR_DESTAQUE = new Color(70, 130, 180);
    private final Color COR_PERIGO = new Color(220, 20, 60);
    private final Color COR_SUCESSO = new Color(34, 139, 34);
    private final Color COR_AVISO = new Color(255, 165, 0);

    public JPP() {
        this.baralho = new Baralho();
        this.jogadores = new ArrayList<>();
        this.jogoRodando = true;
        this.indiceJogadorAtual = 0;

        //Carrega o contador de jogos
        this.jogosJogados = carregarContador();

        this.historicoVencedores = new ArrayList<>();
        carregarHistorico();
        this.jogosJogados = this.historicoVencedores.size();

        // EFEITO: Inicializar interface gráfica
        inicializarInterface();
        criarPainelContador();
    }


    public static void main(String[] args) {
        // EFEITO: Executar na Thread de EDT do Swing
        SwingUtilities.invokeLater(() -> {
            new JPP().iniciar();
        });
    }

    // EFEITO: Método para inicializar todos os componentes da interface
    private void inicializarInterface() {
        frame = new JFrame("COUP - Versão Gráfica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(COR_FUNDO);

        // EFEITO: Criar painel superior com informações do jogador
        criarPainelSuperior();

        // EFEITO: Criar área central de texto com rolagem
        criarAreaTexto();

        // EFEITO: Criar painel de botões de ações
        criarPainelBotoes();

        // EFEITO: Criar painel de informações dos jogadores
        criarPainelJogadores();

        frame.pack();
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // EFEITO: Configurar painel superior com informações do jogador atual
    private void criarPainelSuperior() {
        JPanel painelSuperior = new JPanel(new GridLayout(1, 3));
        painelSuperior.setBackground(COR_DESTAQUE);
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelJogadorAtual = new JLabel("", SwingConstants.CENTER);
        labelJogadorAtual.setForeground(Color.WHITE);
        labelJogadorAtual.setFont(new Font("Arial", Font.BOLD, 16));

        labelMoedas = new JLabel("", SwingConstants.CENTER);
        labelMoedas.setForeground(Color.YELLOW);
        labelMoedas.setFont(new Font("Arial", Font.BOLD, 14));

        areaCartas = new JTextArea(3, 20);
        areaCartas.setEditable(false);
        areaCartas.setBackground(new Color(50, 50, 70));
        areaCartas.setForeground(Color.WHITE);
        areaCartas.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaCartas.setBorder(BorderFactory.createTitledBorder("Cartas na Mão"));

        painelSuperior.add(labelJogadorAtual);
        painelSuperior.add(labelMoedas);
        painelSuperior.add(new JScrollPane(areaCartas));

        frame.add(painelSuperior, BorderLayout.NORTH);
    }

    //Configuração do painel do contador de partidas
    private void criarPainelContador() {
        JPanel painelContador = new JPanel();
        painelContador.setLayout(new FlowLayout(FlowLayout.CENTER));
        painelContador.setBackground(COR_FUNDO);
        painelContador.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        painelContador.setPreferredSize(new Dimension(150, 0));

        // Inicializa o label aqui
        labelContadorJogos = new JLabel("Jogos: 0", SwingConstants.CENTER);
        labelContadorJogos.setForeground(Color.BLUE);
        labelContadorJogos.setFont(new Font("Arial", Font.BOLD, 18));

        painelContador.add(labelContadorJogos);
        frame.add(painelContador, BorderLayout.WEST); // Adiciona na lateral Oeste
    }

    // EFEITO: Configurar área de texto principal com estilo
    private void criarAreaTexto() {
        areaTexto = new JTextArea(20, 50);
        areaTexto.setEditable(false);
        areaTexto.setBackground(new Color(30, 30, 30));
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaTexto.setCaretColor(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Histórico do Jogo"));

        frame.add(scrollPane, BorderLayout.CENTER);
    }

    // EFEITO: Configurar painel de botões com ações do jogo
    private void criarPainelBotoes() {
        painelBotoes = new JPanel(new GridLayout(2, 4, 5, 5));
        painelBotoes.setBackground(COR_FUNDO);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] acoes = {
                "1. Renda (+1 moeda)", "2. Ajuda Externa (+2)", "3. Taxar - Duque (+3)",
                "4. Assassinar (-3)", "5. Roubar - Capitão", "6. Trocar - Embaixador",
                "7. Golpe Estado (-7)", "Desistir", "Ver Histórico"
        };

        botoesAcoes = new JButton[acoes.length];

        for (int i = 0; i < acoes.length; i++) {
            botoesAcoes[i] = new JButton(acoes[i]);
            botoesAcoes[i].setFont(new Font("Arial", Font.BOLD, 12));
            botoesAcoes[i].addActionListener(new JPP.ActionListenerAcoes(i + 1));

            // EFEITO: Cores diferentes para cada tipo de ação
            if (i == 0 || i == 1 || i == 2) botoesAcoes[i].setBackground(new Color(200, 255, 200)); // Ações de ganho
            if (i == 3 || i == 6) botoesAcoes[i].setBackground(new Color(255, 200, 200)); // Ações de custo
            if (i == 4 || i == 5) botoesAcoes[i].setBackground(new Color(255, 255, 200)); // Ações especiais

            painelBotoes.add(botoesAcoes[i]);
        }

        frame.add(painelBotoes, BorderLayout.SOUTH);
    }

    // EFEITO: Configurar painel lateral com informações dos jogadores
    private void criarPainelJogadores() {
        painelJogadores = new JPanel();
        painelJogadores.setLayout(new BoxLayout(painelJogadores, BoxLayout.Y_AXIS));
        painelJogadores.setBackground(new Color(200, 200, 220));
        painelJogadores.setBorder(BorderFactory.createTitledBorder("Jogadores"));
        painelJogadores.setPreferredSize(new Dimension(200, 0));

        frame.add(painelJogadores, BorderLayout.EAST);
    }

    public void iniciar() {
        adicionarMensagem("=== COUP: VERSÃO GRÁFICA ===", COR_DESTAQUE, true);

        // 1. Setup Jogadores - MANTIDO DO CÓDIGO ORIGINAL
        jogadores.add(new Jogador("Jogador 1"));
        jogadores.add(new Jogador("Jogador 2"));
        jogadores.add(new Jogador("Jogador 3"));

        // 2. Distribuir Cartas (2 para cada) - MANTIDO DO CÓDIGO ORIGINAL
        for (Jogador j : jogadores) {
            j.getMao().add(baralho.comprarCarta());
            j.getMao().add(baralho.comprarCarta());
        }

        this.jogosJogados = this.historicoVencedores.size() + 1;
        salvarContador();

        labelContadorJogos.setText("PARTIDA # " + this.jogosJogados);

        // EFEITO: Animação de início
        efeitoAnimacaoInicio();

        atualizarPainelJogadores();
        processarProximoTurno();
    }

    // MANTIDO DO CÓDIGO ORIGINAL: Loop principal adaptado para interface gráfica
    private void processarProximoTurno() {
        if (!jogoRodando) return;

        // Limpeza de jogadores eliminados - MANTIDO DO CÓDIGO ORIGINAL
        jogadores.removeIf(j -> j.getMao().isEmpty());

        // Jogador Vitorioso - CORREÇÃO: Verificação segura - MANTIDO DO CÓDIGO ORIGINAL
        if (jogadores.size() == 1) {
            efeitoVitoria(jogadores.get(0));
            return;
        } else if (jogadores.isEmpty()) {
            adicionarMensagem("\n*** EMPATE! ***", COR_PERIGO, true);
            return;
        }

        // Indice para decidir a vez de cada jogador - MANTIDO DO CÓDIGO ORIGINAL
        if (indiceJogadorAtual >= jogadores.size()) indiceJogadorAtual = 0;
        Jogador jogadorAtual = jogadores.get(indiceJogadorAtual);

        processarTurno(jogadorAtual);
    }

    // CORREÇÃO: Método agora adaptado para interface gráfica
    private void processarTurno(Jogador jogador) {
        // EFEITO: Atualizar interface com informações do jogador atual
        atualizarInterfaceJogador(jogador);

        // EFEITO: Destacar jogador atual no painel
        destacarJogadorAtual(jogador);

        // EFEITO: Habilitar/desabilitar botões baseado nas moedas
        atualizarBotoesAcoes(jogador);

        adicionarMensagem("\n========================================", COR_DESTAQUE, false);
        adicionarMensagem("VEZ DE: " + jogador.getNome() + " | Moedas: " + jogador.getQnt_moedas(),
                Color.WHITE, true);

        adicionarMensagem("Cartas: " + obterTextoCartas(jogador), Color.WHITE, false);
    }

    // EFEITO: Atualizar informações do jogador na interface
    private void atualizarInterfaceJogador(Jogador jogador) {
        labelJogadorAtual.setText("Jogador: " + jogador.getNome());
        labelMoedas.setText("Moedas: " + jogador.getQnt_moedas() + " 💰");

        StringBuilder cartas = new StringBuilder();
        for (Carta c : jogador.getMao()) {
            cartas.append("[").append(c.getNome()).append("]\n");
        }
        areaCartas.setText(cartas.toString());
    }

    // EFEITO: Atualizar estado dos botões baseado nas moedas
    private void atualizarBotoesAcoes(Jogador jogador) {
        int moedas = jogador.getQnt_moedas();

        // Habilitar/desabilitar ações que requerem moedas específicas
        botoesAcoes[3].setEnabled(moedas >= 3); // Assassinar (4)
        botoesAcoes[6].setEnabled(moedas >= 7); // Golpe de Estado (7)

        // EFEITO: Feedback visual para botões desabilitados
        for (int i = 0; i < botoesAcoes.length; i++) {
            JButton botao = botoesAcoes[i];
            if (!botao.isEnabled()) {
                botao.setBackground(Color.GRAY);
                botao.setForeground(Color.DARK_GRAY);
            } else {
                // Restaurar cores originais baseadas no tipo de ação
                if (i == 0 || i == 1 || i == 2) botao.setBackground(new Color(200, 255, 200));
                else if (i == 3 || i == 6) botao.setBackground(new Color(255, 200, 200));
                else if (i == 4 || i == 5) botao.setBackground(new Color(255, 255, 200));
                botao.setForeground(Color.BLACK);
            }
        }
    }

    // EFEITO: Destacar jogador atual no painel lateral
    private void destacarJogadorAtual(Jogador jogadorAtual) {
        Component[] componentes = painelJogadores.getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains(jogadorAtual.getNome())) {
                    label.setBackground(Color.YELLOW);
                    label.setOpaque(true);
                    label.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
                } else {
                    label.setBackground(null);
                    label.setOpaque(false);
                    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                }
            }
        }
    }

    // EFEITO: Atualizar painel de jogadores
    private void atualizarPainelJogadores() {
        painelJogadores.removeAll();

        for (Jogador j : jogadores) {
            JLabel label = new JLabel(j.getNome() + " - " + j.getQnt_moedas() + "💰 - " +
                    j.getMao().size() + "🃏");
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            label.setForeground(Color.DARK_GRAY);
            painelJogadores.add(label);
        }

        painelJogadores.revalidate();
        painelJogadores.repaint();
    }

    // EFEITO: Classe para tratar ações dos botões
    private class ActionListenerAcoes implements ActionListener {
        private int acao;

        public ActionListenerAcoes(int acao) {
            this.acao = acao;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (acao == 8) { // Desistir
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Tem certeza que deseja desistir?", "Desistir",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    jogoRodando = false;
                    adicionarMensagem("\n*** JOGO ENCERRADO ***", COR_PERIGO, true);
                }
                return;
            }

            if (acao == 9) {
                mostrarHistorico();
                return; // Não avança o turno, apenas mostra a janela
            }

            Jogador jogadorAtual = jogadores.get(indiceJogadorAtual);
            executarAcao(acao, jogadorAtual);
        }
    }

    // MÉTODO CORRIGIDO: Executar ação baseada na escolha do botão
    private void executarAcao(int escolha, Jogador jogador) {
        boolean acaoValida = true;

        switch (escolha) {
            case 1: // (Renda) nao pode ser bloqueada
                jogador.setQnt_moedas(jogador.getQnt_moedas() + 1);
                adicionarMensagem(">> " + jogador.getNome() + " pegou Renda. +1 moeda", COR_SUCESSO, false);
                break;

            case 2: // Ajuda Externa
                if (!tentarBloqueioGUI(null, jogador, "AJUDA_EXTERNA", "Duque")) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() + 2);
                    adicionarMensagem(">> Ajuda Externa bem sucedida. +2 moedas", COR_SUCESSO, false);
                } else {
                    adicionarMensagem(">> Ajuda Externa foi bloqueada!", COR_AVISO, false);
                }
                break;

            case 3: // Taxar (Duque)
                if (resolverDesafioGUI(jogador, "Duque")) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() + 3);
                    adicionarMensagem(">> " + jogador.getNome() + " taxou com sucesso. +3 moedas", COR_SUCESSO, false);
                } else {
                    acaoValida = false;
                    adicionarMensagem(">> Taxação falhou!", COR_PERIGO, false);
                }
                break;

            case 4: // Assassinar
                // CORREÇÃO: Verificar moedas ANTES de executar ação
                if (jogador.getQnt_moedas() < 3) {
                    adicionarMensagem("Moedas insuficientes (Precisa de 3).", COR_PERIGO, false);
                    acaoValida = false;
                    break;
                }

                Jogador alvoAssassino = escolherAlvoGUI(jogador);
                if (alvoAssassino == null) {
                    adicionarMensagem("Nenhum alvo disponível.", COR_PERIGO, false);
                    acaoValida = false;
                    break;
                }

                // CORREÇÃO: Só debitar moedas se o desafio for bem-sucedido
                if (resolverDesafioGUI(jogador, "Assassino")) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() - 3); // Agora debita aqui
                    adicionarMensagem(">> " + jogador.getNome() + " pagou 3 moedas para assassinar " + alvoAssassino.getNome(),
                            COR_AVISO, false);

                    if (!tentarBloqueioGUI(alvoAssassino, jogador, "ASSASSINAR", "Condessa")) {
                        adicionarMensagem(">> Assassinato confirmado!", COR_PERIGO, false);
                        perderVidaGUI(alvoAssassino);
                    } else {
                        adicionarMensagem(">> Assassinato bloqueado pela Condessa!", COR_AVISO, false);
                    }
                } else {
                    acaoValida = false;
                    adicionarMensagem(">> Assassinato falhou no desafio!", COR_PERIGO, false);
                }
                break;

            case 5: // Roubar (Capitão)
                Jogador alvoRoubo = escolherAlvoGUI(jogador);
                if (alvoRoubo == null) {
                    adicionarMensagem("Nenhum alvo disponível.", COR_PERIGO, false);
                    acaoValida = false;
                    break;
                }

                if (resolverDesafioGUI(jogador, "Capitão")) {
                    if (!tentarBloqueioGUI(alvoRoubo, jogador, "ROUBAR", "Capitão", "Embaixador")) {
                        int valor = Math.min(alvoRoubo.getQnt_moedas(), 2);
                        alvoRoubo.setQnt_moedas(alvoRoubo.getQnt_moedas() - valor);
                        jogador.setQnt_moedas(jogador.getQnt_moedas() + valor);
                        adicionarMensagem(">> " + jogador.getNome() + " roubou " + valor + " moedas de " + alvoRoubo.getNome(),
                                COR_SUCESSO, false);
                    } else {
                        adicionarMensagem(">> Roubo bloqueado!", COR_AVISO, false);
                    }
                } else {
                    acaoValida = false;
                    adicionarMensagem(">> Roubo falhou no desafio!", COR_PERIGO, false);
                }
                break;

            case 6: // Embaixador - CORREÇÃO COMPLETA DA SELEÇÃO
                if (resolverDesafioGUI(jogador, "Embaixador")) {
                    realizarTrocaEmbaixadorGUICorrigido(jogador);
                } else {
                    acaoValida = false;
                    adicionarMensagem(">> Troca de cartas falhou no desafio!", COR_PERIGO, false);
                }
                break;

            case 7: // Golpe de Estado
                // CORREÇÃO: Verificar moedas ANTES
                if (jogador.getQnt_moedas() < 7) {
                    adicionarMensagem("Moedas insuficientes (Precisa de 7).", COR_PERIGO, false);
                    acaoValida = false;
                    break;
                }

                Jogador alvoGolpe = escolherAlvoGUI(jogador);
                if (alvoGolpe != null) {
                    jogador.setQnt_moedas(jogador.getQnt_moedas() - 7);
                    adicionarMensagem(">> GOLPE DE ESTADO em " + alvoGolpe.getNome() + "!", COR_PERIGO, true);
                    perderVidaGUI(alvoGolpe);
                    // CORREÇÃO: Golpe de Estado elimina diretamente (não pode ser bloqueado)
                    if (alvoGolpe.getMao().size() > 0) {
                        perderVidaGUI(alvoGolpe); // Segunda vida perdida
                    }
                }
                break;

            default:
                adicionarMensagem("Ação inválida. Você perdeu a vez.", COR_PERIGO, false);
                acaoValida = true;
                break;
        }

        if (acaoValida) {
            // CORREÇÃO: Avançar para o próximo jogador após ação válida
            indiceJogadorAtual++;

            // EFEITO: Pequena pausa antes do próximo turno
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((Timer) e.getSource()).stop();
                    processarProximoTurno();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // CORREÇÃO: Se ação não foi válida, o jogador atual repete a vez
            adicionarMensagem(">> " + jogador.getNome() + " repete a vez.", COR_AVISO, false);
            processarTurno(jogador);
        }

        atualizarPainelJogadores();
    }

    // ==================================================
    // MÉTODOS ADAPTADOS PARA GUI - CORREÇÕES COMPLETAS
    // ==================================================

    // CORREÇÃO: Sistema de desafios adaptado para GUI
    private boolean resolverDesafioGUI(Jogador ator, String cartaNecessaria) {
        adicionarMensagem(">> " + ator.getNome() + " diz ter: " + cartaNecessaria, Color.ORANGE, false);

        // CORREÇÃO: Encontrar todos os possíveis desafiantes
        List<Jogador> possiveisDesafiantes = new ArrayList<>();
        for (Jogador j : jogadores) {
            if (!j.equals(ator)) {
                possiveisDesafiantes.add(j);
            }
        }

        if (possiveisDesafiantes.isEmpty()) {
            return true; // Ninguém para contestar
        }

        // CORREÇÃO: Diálogo interativo para desafio com todos os jogadores
        JPanel painelDesafio = new JPanel(new BorderLayout());
        JLabel pergunta = new JLabel(ator.getNome() + " está usando " + cartaNecessaria + ". Quem desafia?");
        JComboBox<String> comboDesafiantes = new JComboBox<>();

        comboDesafiantes.addItem("Ninguém desafia");
        for (Jogador j : possiveisDesafiantes) {
            comboDesafiantes.addItem(j.getNome());
        }

        painelDesafio.add(pergunta, BorderLayout.NORTH);
        painelDesafio.add(comboDesafiantes, BorderLayout.CENTER);

        int resultado = JOptionPane.showConfirmDialog(frame, painelDesafio,
                "Desafio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION && comboDesafiantes.getSelectedIndex() > 0) {
            String nomeDesafiante = (String) comboDesafiantes.getSelectedItem();
            Jogador desafiante = null;
            for (Jogador j : possiveisDesafiantes) {
                if (j.getNome().equals(nomeDesafiante)) {
                    desafiante = j;
                    break;
                }
            }

            adicionarMensagem("!!! DESAFIO ACEITO por " + desafiante.getNome() + "!!!", COR_PERIGO, true);

            // CORREÇÃO: Verificação real das cartas
            boolean temCarta = false;
            for (Carta c : ator.getMao()) {
                if (c.getNome().equalsIgnoreCase(cartaNecessaria)) {
                    temCarta = true;
                    break;
                }
            }

            if (temCarta) {
                adicionarMensagem(">> " + ator.getNome() + " FALOU A VERDADE! Tinha " + cartaNecessaria, COR_SUCESSO, false);
                adicionarMensagem(">> " + desafiante.getNome() + " perde uma vida por errar o desafio.", COR_PERIGO, false);
                perderVidaGUI(desafiante);
                return true;
            } else {
                adicionarMensagem(">> " + ator.getNome() + " MENTIU! Não tinha " + cartaNecessaria, COR_PERIGO, false);
                adicionarMensagem(">> " + ator.getNome() + " perde uma vida.", COR_PERIGO, false);
                perderVidaGUI(ator);
                return false;
            }
        }

        return true; // Ninguém contestou
    }

    private boolean tentarBloqueioGUI(Jogador defensor, Jogador atacante, String acao, String... cartasPossiveis) {
        // Caso especial: Ajuda Externa (qualquer um pode bloquear)
        if (defensor == null && acao.equals("AJUDA_EXTERNA")) {
            List<Jogador> possiveisBloqueadores = new ArrayList<>();
            for (Jogador j : jogadores) {
                if (!j.equals(atacante)) {
                    possiveisBloqueadores.add(j);
                }
            }

            if (possiveisBloqueadores.isEmpty()) return false;

            JPanel painelBloqueio = new JPanel(new BorderLayout());
            JLabel pergunta = new JLabel("Quem bloqueia a Ajuda Externa com Duque?");
            JComboBox<String> comboBloqueadores = new JComboBox<>();

            comboBloqueadores.addItem("Ninguém bloqueia");
            for (Jogador j : possiveisBloqueadores) {
                comboBloqueadores.addItem(j.getNome());
            }

            painelBloqueio.add(pergunta, BorderLayout.NORTH);
            painelBloqueio.add(comboBloqueadores, BorderLayout.CENTER);

            int resultado = JOptionPane.showConfirmDialog(frame, painelBloqueio,
                    "Bloqueio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (resultado == JOptionPane.OK_OPTION && comboBloqueadores.getSelectedIndex() > 0) {
                String nomeBloqueador = (String) comboBloqueadores.getSelectedItem();
                Jogador bloqueador = null;
                for (Jogador j : possiveisBloqueadores) {
                    if (j.getNome().equals(nomeBloqueador)) {
                        bloqueador = j;
                        break;
                    }
                }

                adicionarMensagem(">> " + bloqueador.getNome() + " tenta bloquear com Duque!", COR_AVISO, false);
                return resolverDesafioGUI(bloqueador, "Duque");
            }
            return false;
        }

        if (defensor == null) {
            return false;
        }

        // CORREÇÃO: Bloqueio direcionado
        int resposta = JOptionPane.showConfirmDialog(frame,
                defensor.getNome() + ", deseja bloquear " + acao + "?",
                "Bloqueio",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            String cartaBloqueio = cartasPossiveis[0];
            if (cartasPossiveis.length > 1) {
                // CORREÇÃO: Escolher carta de bloqueio
                cartaBloqueio = (String) JOptionPane.showInputDialog(frame,
                        "Bloquear com qual carta?",
                        "Escolha da Carta",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        cartasPossiveis,
                        cartasPossiveis[0]);
            }

            adicionarMensagem(">> " + defensor.getNome() + " bloqueia usando " + cartaBloqueio, COR_AVISO, false);

            // CORREÇÃO: Desafio do bloqueio
            int desafio = JOptionPane.showConfirmDialog(frame,
                    atacante.getNome() + ", duvida do bloqueio?",
                    "Desafio do Bloqueio",
                    JOptionPane.YES_NO_OPTION);

            if (desafio == JOptionPane.YES_OPTION) {
                return !resolverDesafioGUI(defensor, cartaBloqueio);
            }
            return true; // Bloqueio aceito
        }
        return false; // Ninguém bloqueou
    }

    // CORREÇÃO: Perder vida adaptado para GUI
    private void perderVidaGUI(Jogador j) {
        if (j.getMao().isEmpty()) return;

        adicionarMensagem("💀 " + j.getNome() + " está perdendo uma vida!", COR_PERIGO, false);

        // CORREÇÃO: Diálogo para escolher carta para perder
        String[] opcoesCartas = new String[j.getMao().size()];
        for (int i = 0; i < j.getMao().size(); i++) {
            opcoesCartas[i] = j.getMao().get(i).getNome();
        }

        String cartaEscolhida = (String) JOptionPane.showInputDialog(frame,
                j.getNome() + ", escolha uma carta para perder:",
                "Perder Vida",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesCartas,
                opcoesCartas[0]);

        if (cartaEscolhida != null) {
            for (int i = 0; i < j.getMao().size(); i++) {
                if (j.getMao().get(i).getNome().equals(cartaEscolhida)) {
                    Carta c = j.getMao().remove(i);
                    baralho.devolverCarta(c);
                    adicionarMensagem(">> " + j.getNome() + " perdeu " + c.getNome(), COR_PERIGO, false);
                    break;
                }
            }
        } else {
            // CORREÇÃO: Perder carta aleatória se não escolher
            Carta c = j.getMao().remove(0);
            baralho.devolverCarta(c);
            adicionarMensagem(">> " + j.getNome() + " perdeu " + c.getNome(), COR_PERIGO, false);
        }

        // CORREÇÃO: Verificar se jogador foi eliminado
        if (j.getMao().isEmpty()) {
            adicionarMensagem("🚫 " + j.getNome() + " FOI ELIMINADO!", COR_PERIGO, true);
        }

        atualizarPainelJogadores();
    }

    // CORREÇÃO: Escolher alvo adaptado para GUI
    private Jogador escolherAlvoGUI(Jogador atacante) {
        List<Jogador> alvos = new ArrayList<>();
        for (Jogador j : jogadores) {
            if (!j.equals(atacante)) alvos.add(j);
        }

        if (alvos.isEmpty()) {
            return null;
        }

        String[] opcoesAlvos = new String[alvos.size()];
        for (int i = 0; i < alvos.size(); i++) {
            opcoesAlvos[i] = alvos.get(i).getNome() + " (" + alvos.get(i).getQnt_moedas() + "💰, " +
                    alvos.get(i).getMao().size() + "🃏)";
        }

        String alvoEscolhido = (String) JOptionPane.showInputDialog(frame,
                "Escolha o alvo:",
                "Selecionar Alvo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesAlvos,
                opcoesAlvos[0]);

        if (alvoEscolhido != null) {
            for (Jogador j : alvos) {
                if (alvoEscolhido.contains(j.getNome())) {
                    return j;
                }
            }
        }

        return alvos.isEmpty() ? null : alvos.get(0);
    }

    // CORREÇÃO COMPLETA: Troca de cartas do Embaixador - VERSÃO CORRIGIDA DA SELEÇÃO
    private void realizarTrocaEmbaixadorGUICorrigido(Jogador j) {
        adicionarMensagem(">> " + j.getNome() + " está trocando cartas com Embaixador...", COR_AVISO, false);

        // Comprar 2 cartas do baralho
        Carta c1 = baralho.comprarCarta();
        Carta c2 = baralho.comprarCarta();

        List<Carta> maoTemp = new ArrayList<>(j.getMao());
        if (c1 != null) maoTemp.add(c1);
        if (c2 != null) maoTemp.add(c2);

        // CORREÇÃO: Usar JList com seleção múltipla para escolher cartas
        JPanel painelTroca = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html>Selecione <b>EXATAMENTE 2</b> cartas para devolver ao baralho:<br>"
                + "(Use Ctrl+Click para selecionar múltiplas)</html>");

        // Lista de nomes das cartas
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Carta carta : maoTemp) {
            listModel.addElement(carta.getNome());
        }

        JList<String> listaCartas = new JList<>(listModel);
        listaCartas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaCartas.setVisibleRowCount(6);

        JScrollPane scrollPane = new JScrollPane(listaCartas);

        painelTroca.add(label, BorderLayout.NORTH);
        painelTroca.add(scrollPane, BorderLayout.CENTER);

        int resultado;
        do {
            resultado = JOptionPane.showConfirmDialog(frame, painelTroca,
                    "Troca de Cartas - Embaixador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (resultado == JOptionPane.OK_OPTION) {
                List<String> selecionadas = listaCartas.getSelectedValuesList();
                if (selecionadas.size() == 2) {
                    // CORREÇÃO: Remover cartas selecionadas
                    List<Carta> cartasParaRemover = new ArrayList<>();
                    for (String nomeCarta : selecionadas) {
                        for (Carta c : maoTemp) {
                            if (c.getNome().equals(nomeCarta) && !cartasParaRemover.contains(c)) {
                                cartasParaRemover.add(c);
                                break;
                            }
                        }
                    }

                    // CORREÇÃO: Remover da lista temporária
                    maoTemp.removeAll(cartasParaRemover);

                    // CORREÇÃO: Devolver cartas ao baralho
                    for (Carta c : cartasParaRemover) {
                        baralho.devolverCarta(c);
                        adicionarMensagem(">> Devolveu: " + c.getNome(), COR_AVISO, false);
                    }

                    // CORREÇÃO: Atualizar mão do jogador
                    j.getMao().clear();
                    j.getMao().addAll(maoTemp);

                    adicionarMensagem(">> " + j.getNome() + " completou a troca de cartas", COR_SUCESSO, false);
                    break;
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Você deve selecionar EXATAMENTE 2 cartas!\nSelecionadas: " + selecionadas.size(),
                            "Seleção Incorreta",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                // CORREÇÃO: Se cancelar, devolver as cartas compradas
                if (c1 != null) baralho.devolverCarta(c1);
                if (c2 != null) baralho.devolverCarta(c2);
                adicionarMensagem(">> Troca de cartas cancelada", COR_AVISO, false);
                break;
            }
        } while (true);
    }

    // ==================================================
    // MÉTODOS DE EFEITOS VISUAIS - JAVA EFFECTS GRÁFICOS
    // ==================================================

    // EFEITO: Adicionar mensagem colorida na área de texto
    private void adicionarMensagem(String texto, Color cor, boolean negrito) {
        // CORREÇÃO: Manter o estilo base e adicionar formatação
        areaTexto.setForeground(cor);
        if (negrito) {
            areaTexto.setFont(new Font("Consolas", Font.BOLD, 12));
        } else {
            areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));
        }
        areaTexto.append(texto + "\n");
        areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
    }

    // EFEITO: Animação de início do jogo
    private void efeitoAnimacaoInicio() {
        Timer timer = new Timer(500, new ActionListener() {
            int contador = 0;
            String[] mensagens = {"Iniciando jogo", "Distribuindo cartas", "Pronto!"};

            @Override
            public void actionPerformed(ActionEvent e) {
                if (contador < mensagens.length) {
                    adicionarMensagem(mensagens[contador] + "...", COR_DESTAQUE, true);
                    contador++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    // EFEITO: Animação de vitória
    private void efeitoVitoria(Jogador vencedor) {
        adicionarMensagem("\n✨✨✨✨✨✨✨✨✨✨✨✨✨✨", Color.YELLOW, true);
        adicionarMensagem("*** VITÓRIA DE " + vencedor.getNome() + "! ***", COR_SUCESSO, true);
        adicionarMensagem("✨✨✨✨✨✨✨✨✨✨✨✨✨✨", Color.YELLOW, true);

        String registro = "Partida " + this.jogosJogados + " | Vencedor: " + vencedor.getNome();
        this.historicoVencedores.add(registro);
        salvarHistorico();
        System.out.println("Registro salvo: " + registro);

        // EFEITO: Piscar o label do vencedor
        Timer piscar = new Timer(500, new ActionListener() {
            boolean visivel = true;
            int contador = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (contador < 6) {
                    for (Component comp : painelJogadores.getComponents()) {
                        if (comp instanceof JLabel && ((JLabel) comp).getText().contains(vencedor.getNome())) {
                            comp.setVisible(visivel);
                        }
                    }
                    visivel = !visivel;
                    contador++;
                } else {
                    ((Timer) e.getSource()).stop();
                    for (Component comp : painelJogadores.getComponents()) {
                        comp.setVisible(true);
                    }
                }
            }
        });
        piscar.start();
    }

    // EFEITO: Obter texto formatado das cartas
    private String obterTextoCartas(Jogador jogador) {
        StringBuilder sb = new StringBuilder();
        for (Carta c : jogador.getMao()) {
            sb.append("[").append(c.getNome()).append("] ");
        }
        return sb.toString();
    }

    private int carregarContador() {
        Path path = Paths.get(Cont);
        if (!Files.exists(path)) {
            return 0;
        }
        try {
            String content = Files.readString(path).trim();
            return Integer.parseInt(content);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Aviso: Arquivo de contador inválido. Iniciando em 0.");
            return 0;
        }
    }

    private void salvarContador() {
        Path path = Paths.get(Cont);
        try {
            Files.writeString(path, String.valueOf(this.jogosJogados));
        } catch (IOException e) {
            System.err.println("Erro ao salvar contador: " + e.getMessage());
        }
    }

    private void carregarHistorico() {
        Path path = Paths.get(Cont);
        if (!Files.exists(path)) {
            return; // Retorna lista vazia se o arquivo não existir
        }

        try {
            List<String> linhas = Files.readAllLines(path);

            this.historicoVencedores.addAll(linhas);

            System.out.println("Histórico de " + historicoVencedores.size() + " partidas carregado.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    private void salvarHistorico() {
        Path path = Paths.get(Cont);
        try {
            // Escreve todas as linhas da lista no arquivo
            Files.write(path, this.historicoVencedores);
        } catch (IOException e) {
            System.err.println("Erro ao salvar histórico: " + e.getMessage());
        }
    }

    private void mostrarHistorico() {
        if (historicoVencedores.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nenhum jogo registrado ainda.",
                    "Histórico de Jogos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTÓRICO DE VENCEDORES ===\n");

        // Adiciona cada registro do histórico, linha por linha
        for (String registro : historicoVencedores) {
            sb.append(registro).append("\n");
        }

        // Usa JTextArea em um JScrollPane para permitir rolagem se a lista for grande
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(frame, scrollPane,
                "Histórico de Jogos (" + historicoVencedores.size() + " total)",
                JOptionPane.PLAIN_MESSAGE);
    }

}