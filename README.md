# 🎲 Coup (Implementação em Java)

Uma implementação completa do popular jogo de cartas "Coup", desenvolvido puramente em Java com foco na aplicação de conceitos de Programação Orientada a Objetos (POO).



## 📜 Sobre o Jogo

**Coup** é um jogo de estratégia, blefe e dedução. Cada jogador começa com duas cartas de personagem (viradas para baixo) e uma pequena quantia de moedas. O objetivo é ser o último jogador a manter uma influência (uma carta) no jogo.

Para vencer, os jogadores devem usar as habilidades de seus personagens para ganhar moedas, roubar de outros jogadores ou até mesmo assassinar a influência de um oponente. O ponto central do jogo é o **blefe**: você pode *alegar* ter qualquer personagem para usar sua habilidade, mas se for desafiado e não puder provar, você perde uma influência.

## 🎯 Objetivo do Projeto

Este projeto foi desenvolvido como uma aplicação prática dos pilares da **Programação Orientada a Objetos**. O objetivo não era apenas criar um jogo funcional, mas construir uma arquitetura de software que fosse modular, extensível e fácil de manter, usando os padrões de design e princípios do POO.

## ✨ Funcionalidades

* Jogabilidade completa de Coup via console (text-based).
* Lógica de ações de personagens (Duque, Assassino, Capitão, Embaixador, Condessa).
* Sistema de moedas, ações, desafios e contra-ataques.
* Gerenciamento de turnos e detecção de vitória/derrota.

## 🛠️ Tecnologias Utilizadas

* **Java (Puro):** Todo o projeto foi desenvolvido sem frameworks externos, focando no núcleo da linguagem e no JDK padrão.

## 🧠 Conceitos de POO Aplicados

Este projeto é um *case study* de POO. Veja como os conceitos foram aplicados:

* ### 🏛️ Herança
    Provavelmente utilizada para criar uma classe base `Personagem` ou `Carta`, da qual classes específicas como `Duque`, `Assassino` e `Condessa` herdam comportamentos comuns e implementam seus próprios.

* ### 🧬 Polimorfismo
    Usado de forma crucial nas ações. Um método `executarAcao()` pode existir na classe base, mas seu comportamento muda drasticamente dependendo de qual subclasse (personagem) o está invocando. Isso permite que o motor do jogo trate todas as cartas de forma uniforme, sem precisar saber qual é qual.

* ### 📦 Encapsulamento
    Fundamental para a lógica do jogo. O estado de um `Jogador` (suas cartas, sua quantidade de moedas) é protegido. O jogo só pode interagir com o jogador através de métodos públicos (`receberMoedas()`, `perderInfluencia()`, `desafiar()`), o que previne estados inválidos e corrupção de dados.

* ### 👻 Classes Abstratas (e/ou Interfaces)
    Uma classe `Acao` pode ter sido definida como abstrata, forçando todas as ações específicas (`Roubar`, `Extorquir`, `Assassinar`) a implementar métodos como `podeSerBloqueadaPor()` ou `custoDaAcao()`.

* ### 🧩 Abstração
    O conceito mais importante. O jogo "Coup" é complexo, com regras de blefe, moedas e turnos. Nós abstraímos essa complexidade em entidades de software claras e gerenciáveis: `Jogador`, `Baralho`, `Turno` e `Acao`.

## 🚀 Como Executar

**Pré-requisitos:**
* Ter o **Java Development Kit (JDK)** (versão 11 ou superior) instalado e configurado no seu PATH.

**Passos:**

1.  Clone este repositório:
    ```bash
    git clone [URL_DO_SEU_REPOSITORIO]
    cd coup-java
    ```

2.  Compile os arquivos `.java` (ajuste o caminho se necessário):
    ```bash
    # Se os arquivos estiverem em uma pasta 'src'
    javac src/*.java 
    # Ou se estiverem na raiz
    javac *.java
    ```

3.  Execute a classe principal (substitua `Main` pelo nome da sua classe principal):
    ```bash
    # Se os arquivos compilados estiverem em 'src'
    java -cp src Main
    # Ou se estiverem na raiz
    java Main
    ```

## 🧑‍💻 Autores

Este projeto foi orgulhosamente desenvolvido por:

* **João Pedro**
* **Gustavo Jaques**
* **Gabriel Costa**

---
