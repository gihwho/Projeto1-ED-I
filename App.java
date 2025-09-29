/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: Implementa o REPL (loop interativo) que interpreta
    os comandos (VARS, RESET, REC, STOP, PLAY, ERASE, EXIT)
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Locale;

public class App {
    //capacidade da gravação
    private static final int REC_CAP = 10;

     //estado da aplicação
    private final Simbolos tabela = new Simbolos();
    private final Lexer lexer = new Lexer();
    private boolean gravando = false;
    private final FilaCircular<String> rec = new FilaCircular<>(REC_CAP);

     //Inicio do programa
    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.US);
            new App().repl();
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) msg = "Erro: expressão inválida.";
            System.out.println(msg);
        }
    }

     // Loop REPL
    private void repl() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        while (true) {
            String line = br.readLine();
            if (line == null) break;
            line = line.strip();
            if (line.isEmpty()) continue;

            if (gravando) {
                // MODO GRAVAÇÃO aceita apenas expressão, atribuição, VARS e RESET
                String upper = line.toUpperCase();

                if (upper.equals("STOP")) {
                    pararGravacao();
                    continue;
                }
                if (upper.equals("PLAY") || upper.equals("ERASE") || upper.equals("EXIT") || upper.equals("REC")) {
                    System.out.println("Erro: comando inválido para gravação.");
                    continue;
                }

                try {
                    if (rec.qIsFull()) {
                        pararGravacao();
                        continue;
                    }
                    rec.enqueue(line);
                    System.out.println("(REC: " + rec.sizeElements() + "/" + REC_CAP + ") " + line);
                    if (rec.qIsFull()) {
                        pararGravacao();
                    }
                } catch (Exception e) {
                    pararGravacao();
                }
                continue;
            }

            // FORA DO REC
            String upper = line.toUpperCase();
            switch (upper) {
                case "EXIT":
                    return;
                case "VARS":
                    cmdVARS();
                    continue;
                case "RESET":
                    tabela.reset();
                    System.out.println("Variáveis reiniciadas.");
                    continue;
                case "REC":
                    iniciarGravacao();
                    continue;
                case "STOP":
                    System.out.println("Erro: comando inválido.");
                    continue;
                case "ERASE":
                    while (!rec.qIsEmpty()) {
                        try { rec.dequeue(); } catch (Exception ignore) { break; }
                    }
                    System.out.println("Gravação apagada.");
                    continue;
                case "PLAY":
                    cmdPLAY();
                    continue;
                default:
                    if (tryAtribuicao(line)) continue;
                    if (tryExpressao(line)) continue;
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

    // Mostra as variáveis definidos
    private void cmdVARS() {
        String lista = tabela.listAll();
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhuma variável definida");
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

            if (cmd.equalsIgnoreCase("VARS")) {
                cmdVARS();
            } else if (cmd.equalsIgnoreCase("RESET")) {
                tabela.reset();
                System.out.println("Variáveis reiniciadas.");
            } else if (tryAtribuicao(cmd)) {
            } else if (tryExpressao(cmd)) {
            } else {
                System.out.println("Erro: comando inválido.");
            }
        }

        // restaura conteúdo (PLAY não apaga a gravação)
        while (!tmp.qIsEmpty()) rec.enqueue(tmp.dequeue());
    }

    //Tenta fazer atribuição
    private boolean tryAtribuicao(String line) {
        String s = line.trim();
        int eq = s.indexOf('=');
        if (eq < 0) return false;

        String left = s.substring(0, eq).trim();
        String right = s.substring(eq + 1).trim();

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

    //Tenta avaliar expressão
    private boolean tryExpressao(String line) {
        try {
            Lexer.Tokens tks = lexer.scan(line);

            // coleta variáveis usadas
            LinkedHashSet<Character> usadas = new LinkedHashSet<>();
            for (int i = 0; i < tks.n; i++) {
                if (tks.type[i] == Lexer.VAR) usadas.add(tks.ch[i]);
            }

            // converte infixa para posfixa
            String pos = InfixaToPosfixa.converter(tks);

            boolean ok = true;
            for (char v : usadas) {
                if (!tabela.ehDefinido(v)) {
                    System.out.println("Erro: variável " + Character.toUpperCase(v) + " não definida.");
                    ok = false;
                }
            }
            if (!ok) return true;

            double r = PosfixaResult.avaliar(pos, tabela);
            System.out.println(r);
            return true;

        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null || (!msg.startsWith("Erro:") && !msg.equals("Divisão por zero"))) {
                msg = "Erro: expressão inválida.";
            }
            System.out.println(msg);
            return true;
        }
    }
}
