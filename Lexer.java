/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa - RA

    Descrição: Lê a string digitada pelo usuário e separa em tokens.

    FONTES
    1. https://stackoverflow.com/questions/43067869/lexical-analyser-in-java
*/

public class Lexer {
    public static final int VAR    = 1; //letra A a Z
    public static final int NUM    = 2; //número (double)
    public static final int OP     = 3; //+ - * / ^
    public static final int EPAREN = 4; // (
    public static final int DPAREN = 5; // )
    public static final int IGUAL  = 6; // =

    public static final class Tokens {
        public final int[]    type; // VAR/NUM/OP/...
        public final char[]   ch;   // para VAR (letra), OP, '(' , ')' , '='
        public final double[] num;  // para NUM
        public final int[]    pos;  // posição do primeiro caractere do token
        public int n;

        public Tokens(int capacity) {
            type = new int[capacity];
            ch   = new char[capacity];
            num  = new double[capacity];
            pos  = new int[capacity];
            n = 0;
        }
    }

    private static boolean isSpace(char c) { return c == ' ' || c == '\t' || c == '\r'; }
    private static boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    private static boolean isLetter(char c){ return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'); }
    private static boolean isOp(char c)    { return c=='+' || c=='-' || c=='*' || c=='/' || c=='^'; }

    public void tokenize(String line, Tokens out) {
        out.n = 0;
        final int max = out.type.length;
        final int len = line.length();
        int i = 0;

        while (i < len) {
            char c = line.charAt(i);

            if (isSpace(c)) { i++; continue; }

            // VAR (1 letra)
            if (isLetter(c)) {
                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                char up = Character.toUpperCase(c);
                out.type[out.n] = VAR;
                out.ch[out.n]   = up;
                out.pos[out.n]  = i;
                out.n++;
                i++;
                continue;
            }

            // NUM: [0-9]+ ('.' [0-9]+)?
            if (isDigit(c)) {
                int start = i;

                // Parte inteira
                while (i < len && isDigit(line.charAt(i))) i++;

                // Parte fracionária opcional
                if (i < len && line.charAt(i) == '.') {
                    int dotPos = i;
                    i++; // consome '.'
                    if (i >= len || !isDigit(line.charAt(i))) {
                        throw new IllegalArgumentException("Erro: número malformado na posição " + dotPos + " (ponto sem dígitos após).");
                    }
                    while (i < len && isDigit(line.charAt(i))) i++;
                }

                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                double value = Double.parseDouble(line.substring(start, i));
                out.type[out.n] = NUM;
                out.num[out.n]  = value;
                out.pos[out.n]  = start;
                out.n++;
                continue;
            }

            if (c == '.') {
                throw new IllegalArgumentException("Erro: número malformado na posição " + i + " (faltou dígito antes do ponto).");
            }

            // Operadores
            if (isOp(c)) {
                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                out.type[out.n] = OP;
                out.ch[out.n]   = c;
                out.pos[out.n]  = i;
                out.n++;
                i++;
                continue;
            }

            // Parênteses
            if (c == '(') {
                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                out.type[out.n] = EPAREN;
                out.ch[out.n]   = '(';
                out.pos[out.n]  = i;
                out.n++;
                i++;
                continue;
            }
            if (c == ')') {
                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                out.type[out.n] = DPAREN;
                out.ch[out.n]   = ')';
                out.pos[out.n]  = i;
                out.n++;
                i++;
                continue;
            }

            // Igual
            if (c == '=') {
                if (out.n >= max) throw new IllegalArgumentException("Erro: expressão muito longa (tokens).");
                out.type[out.n] = IGUAL;
                out.ch[out.n]   = '=';
                out.pos[out.n]  = i;
                out.n++;
                i++;
                continue;
            }

            throw new IllegalArgumentException("Erro: símbolo inválido '" + c + "' na posição " + i + ".");
        }
    }

    /**
     * Versão simples para coletar variáveis usadas (sem repetição), em MAIÚSCULAS.
     * Preenche 'usadasOut' em ordem A..Z e retorna a quantidade escrita.
     */
    public int collectUsedVars(Tokens tks, char[] usadasOut) {
        boolean[] seen = new boolean[26];
        for (int k = 0; k < tks.n; k++) {
            if (tks.type[k] == VAR) {
                char up = tks.ch[k]; // já está maiúscula
                if (up >= 'A' && up <= 'Z') {
                    seen[up - 'A'] = true;
                }
            }
        }
        int m = 0;
        for (int i = 0; i < 26; i++) {
            if (seen[i]) usadasOut[m++] = (char)('A' + i);
        }
        return m;
    }

    public Tokens scan(String line) {
        int cap = Math.max(32, line == null ? 0 : line.length() + 8);
        Tokens t = new Tokens(cap);
        tokenize(line == null ? "" : line, t);
        return t;
    }
}
