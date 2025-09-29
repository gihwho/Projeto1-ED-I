/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096

    Descrição: Converte a expressão infixa para posfixa

    FONTES
    1. https://panda.ime.usp.br/panda/static/pythonds_pt/03-EDBasicos/09-ExpressoesInfixaPrefixaPosfixa.html
    2. https://www.guj.com.br/t/conversao-de-uma-expressao-de-infxa-para-posfixa/41945
*/

public class InfixaToPosfixa {

        private static int precedencia(char op) {
        switch (op) {
            case '^':
                return 3;
            case '*':
            case '/':
                return 2;
            case '+', '-':
                return 1;
            default:
                return -1;
        }
    }

    private static boolean ehOperadorValido(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/' || op == '^';
    }

    public static String converter(Lexer.Tokens tk) throws Exception {
        Pilha<Character> pilha = new Pilha<>(256);
        StringBuilder saida = new StringBuilder();

        //validando ordem: operandos/operadores
        int balance = 0;
        int ultimoTipo = 0; //0 inicio, VAR/NUM=1, OP=2, EP=3
        for (int i = 0; i < tk.n; i++) {
            int tipo = tk.type[i];

            if (tipo == Lexer.VAR || tipo == Lexer.NUM) {
                if (ultimoTipo == 1) throw new Exception("Erro: expressão inválida.");
                ultimoTipo = 1;
            } else if (tipo == Lexer.OP) {
                char op = tk.ch[i];
                if (!ehOperadorValido(op)) throw new Exception("Erro: operador inválido.");
                if (ultimoTipo == 0 || ultimoTipo == 2 || ultimoTipo == 3)
                    throw new Exception("Erro: expressão inválida.");
                ultimoTipo = 2;
            } else if (tipo == Lexer.EPAREN) {
                balance++;
                ultimoTipo = 3;
            } else if (tipo == Lexer.DPAREN) {
                balance--;
                if (balance < 0) throw new Exception("Erro: expressão inválida.");
                if (ultimoTipo == 2 || ultimoTipo == 0) throw new Exception("Erro: expressão inválida.");
                ultimoTipo = 1;
            } else if (tipo == Lexer.IGUAL) {
                throw new Exception("Erro: expressão inválida.");
            }
        }

        if (balance != 0 || ultimoTipo == 2) throw new Exception("Erro: expressão inválida.");

        for (int i = 0; i < tk.n; i++) {
            int tipo = tk.type[i];

            if (tipo == Lexer.VAR) {
                saida.append(tk.ch[i]).append(' ');
            } else if (tipo == Lexer.NUM) {
                saida.append(tk.num[i]).append(' ');
            } else if (tipo == Lexer.OP) {
                char op = tk.ch[i];
                int prec = precedencia(op);
                boolean direita = (op == '^');
                while (!pilha.isEmpty()) {
                    char top = pilha.topo();
                    if (top == '(') break;
                    int ptop = precedencia(top);
                    if (ptop > prec || (ptop == prec && !direita)) {
                        saida.append(pilha.pop()).append(' ');
                    } else break;
                }
                pilha.push(op);
            } else if (tipo == Lexer.EPAREN) {
                pilha.push('(');
            } else if (tipo == Lexer.DPAREN) {
                while (!pilha.isEmpty() && pilha.topo() != '(') {
                    saida.append(pilha.pop()).append(' ');
                }
                if (pilha.isEmpty()) throw new Exception("Erro: expressão inválida.");
                pilha.pop();
            }
        }
        while (!pilha.isEmpty()) {
            char s = pilha.pop();
            
            if (s == '(' || s == ')') throw new Exception("Erro: expressão inválida.");
            saida.append(s).append(' ');
        }
        return saida.toString().trim();
    }
}
