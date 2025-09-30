/* GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: REPL que interpreta comandos (VARS, RESET, REC, STOP, PLAY, ERASE, EXIT)
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class App {

    private static final int REC_CAP = 10;

    // Estado da aplicação
    private final Simbolos tabela = new Simbolos();
    private final Lexer lexer = new Lexer();
    private boolean gravando = false;
    private final FilaCircular<String> rec = new FilaCircular<>(REC_CAP);

    // Entrada principal
    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.US);
            new App().repl();
        } catch (Exception e) {
            System.out.println(e.getMessage() != null ? e.getMessage() : "Erro: expressão inválida.");
        }
    }

    // Loop REPL
    private void repl() throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    while (true) {
        System.out.print("> ");
        String line = br.readLine();
        if (line == null) break;
        line = line.strip();
        if (line.isEmpty()) continue;

        if (gravando) {
            processaGravacao(line);
        } else {
            processaComando(line);
        }
    }
}

    // Processa comandos durante gravação
    private void processaGravacao(String line) {
        String upper = line.toUpperCase();

        if (upper.equals("STOP")) {
            pararGravacao();
            return;
        }

        // Comandos proibidos durante gravação
        if (upper.equals("PLAY") || upper.equals("ERASE") || upper.equals("EXIT") || upper.equals("REC")) {
            System.out.println("Erro: comando inválido para gravação.");
            return;
        }

        try {
            if (rec.qIsFull()) {
                pararGravacao();
                return;
            }
            rec.enqueue(line);
            System.out.println("(REC: " + rec.sizeElements() + "/" + REC_CAP + ") " + line);
            if (rec.qIsFull()) pararGravacao();
        } catch (Exception e) {
            pararGravacao();
        }
    }

    // Processa comandos fora da gravação
    private void processaComando(String line) {
        String upper = line.toUpperCase();

        switch (upper) {
            case "EXIT" -> System.exit(0);
            case "VARS" -> cmdVARS();
            case "RESET" -> {
                tabela.reset();
                System.out.println("Variáveis reiniciadas.");
            }
            case "REC" -> iniciarGravacao();
            case "STOP" -> System.out.println("Erro: comando inválido.");
            case "ERASE" -> {
                while (!rec.qIsEmpty()) {
                    try { rec.dequeue(); } catch (Exception ignore) {}
                }
                System.out.println("Gravação apagada.");
            }
            case "PLAY" -> {
                try { cmdPLAY(); } catch (Exception e) { System.out.println(e.getMessage()); }
            }
            default -> {
                if (tryAtribuicao(line)) return;
                if (tryExpressao(line)) return;
                // Se não for nenhum dos comandos acima, atribuição, ou expressão válida, é comando inválido.
                System.out.println("Erro: comando inválido.");
            }
        }
    }

    // Inicia a gravação
    private void iniciarGravacao() {
        gravando = true;
        System.out.println("Iniciando gravação... (REC: " + rec.sizeElements() + "/" + REC_CAP + ")");
    }

    // Para a gravação
    private void pararGravacao() {
        gravando = false;
        System.out.println("Encerrando gravação... (REC: " + rec.sizeElements() + "/" + REC_CAP + ")");
    }

    // Mostra variáveis
    private void cmdVARS() {
        String lista = tabela.listAll();
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhuma variável definida.");
        } else {
            System.out.print(lista);
        }
    }

    // Reproduz a gravação
    private void cmdPLAY() throws Exception {
        if (rec.qIsEmpty()) {
            System.out.println("Não há gravação para ser reproduzida.");
            return;
        }

        System.out.println("Reproduzindo gravação...");
        int n = rec.sizeElements();
        FilaCircular<String> tmp = new FilaCircular<>(REC_CAP);

        for (int i = 0; i < n; i++) {
            String cmd = rec.dequeue();
            tmp.enqueue(cmd);
            System.out.println(cmd);

            if (cmd.equalsIgnoreCase("VARS")) cmdVARS();
            else if (cmd.equalsIgnoreCase("RESET")) {
                tabela.reset();
                System.out.println("Variáveis reiniciadas.");
            } else if (!tryAtribuicao(cmd) && !tryExpressao(cmd)) {
                System.out.println("Erro: comando inválido.");
            }
        }

        // Restaura gravação
        while (!tmp.qIsEmpty()) rec.enqueue(tmp.dequeue());
    }

    // Tenta atribuição
    private boolean tryAtribuicao(String line) {
        int eq = line.indexOf('=');
        if (eq < 0) return false;

        String left = line.substring(0, eq).trim();
        String right = line.substring(eq + 1).trim();

        if (left.length() != 1 || !Character.isLetter(left.charAt(0))) return false;

        try {
            double val = Double.parseDouble(right.replace(',', '.'));
            char var = Character.toUpperCase(left.charAt(0));
            tabela.set(var, val);
            System.out.println(var + " = " + val);
            return true;
        } catch (NumberFormatException ex) {
            System.out.println("Erro: expressão inválida.");
            return true;
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null) msg = "Erro: expressão inválida.";
            System.out.println(msg);
            return true;
        }
    }

    
    // Tenta avaliar expressão
    private boolean tryExpressao(String line) {
        try {
            Lexer.Tokens tks = lexer.scan(line);
            
            // Se não houver tokens, não é uma expressão válida.
            if (tks.n == 0) return false;

            // Tenta converter para posfixa para checar a sintaxe da expressão.
            String pos = InfixaToPosfixa.converter(tks);

            // 1. Coletar variáveis únicas usadas (sem LinkedHashSet)
            char[] usadas = new char[26];
            int usadasLen = lexer.collectUsedVars(tks, usadas);

            boolean todasDefinidas = true;
            
            // 2. Verificar se todas as variáveis usadas estão definidas (Imprime todos os erros)
            for (int i = 0; i < usadasLen; i++) {
                char v = usadas[i];
                if (!tabela.ehDefinido(v)) {
                    System.out.println("Erro: variável " + v + " não definida.");
                    todasDefinidas = false;
                }
            }
            
            // Se alguma variável não foi definida, o processamento da expressão para aqui.
            if (!todasDefinidas) return true;

            // 3. Avalia a expressão
            double r = PosfixaResult.avaliar(pos, tabela);
            System.out.println(r);
            return true;

        } catch (Exception ex) {
            String msg = ex.getMessage();
            
            // Se o erro é de sintaxe pura (Ex: "TESTE", "X - TESTE"), InfixaToPosfixa lança "Erro: expressão inválida."
            if (msg != null && msg.equals("Erro: expressão inválida.")) {
                
                // Se a linha contém um operador ou parêntese, foi uma tentativa de expressão.
                if (line.contains("+") || line.contains("-") || line.contains("*") || 
                    line.contains("/") || line.contains("^") || line.contains("(") || line.contains(")")) 
                {
                    // Ex: "X - TESTE" ou "(X * X". É uma expressão malformada.
                    System.out.println(msg);
                    return true;
                }

                // Se for apenas uma sequência de letras sem operadores (Ex: "TESTE"), 
                // retorna false para que caia no "Erro: comando inválido." do bloco 'default'.
                return false;
            }

            // Se o erro for de avaliação (Ex: Divisão por zero, operador inválido), é um erro de expressão processado.
            if (msg == null || (!msg.startsWith("Erro:") && !msg.equals("Divisão por zero"))) {
                msg = "Erro: expressão inválida.";
            }
            System.out.println(msg);
            return true;
        }
    }
}
