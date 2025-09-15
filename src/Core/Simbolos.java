package src.Core;

public class Simbolos {
    float[] valores = new float[26];
    boolean[] def = new boolean[26];

    public Simbolos() {
        for (int i = 0; i < 26; i++) {
            def[i] = false;
        }
    }

    //pega um char e converte numa posicao de array
    private int indiceLetra(char var) {
        char maiusc = Character.toUpperCase(var);

        if (maiusc < 'A' || maiusc > 'Z') {
                    return -1;
                }

        return (int) maiusc - 97;
    }

    public void set(char var, double valor) throws Exception {
        int indice = indiceLetra(var);
        if (indice != -1) {
            valores[indice] = (float) valor;
            def[indice] = true;
        } else {
            throw new Exception("Variavel " + var + " ainda nao definida");
        }
    }

    public boolean ehDefinido(char var) {
        int indice = indiceLetra(var);
        if (indice != -1) {
            return def[indice];
        }
        return false;
    }

    public float get(char var) throws Exception {
        int indice = indiceLetra(var);
        if (indice != -1) {
            if (def[indice]) {
                return valores[indice];
            } else {
                throw new Exception("Variavel " + var + " ainda nao definida");
            }
        } else {
            throw new Exception("Variavel " + var + " invalida");
        }
    }

    public void reset() {
        for (int i = 0; i < 26; i++) {
            def[i] = false;
        }
    }

    public String listAll() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (def[i]) {
                char var = (char) ('A' + i);
                sb.append(var).append(" = ").append(valores[i]).append("\n");
            }
        }
        return sb.toString();
    }

    public int collectMissing(char[] usadas, int usadasLen, char[] faltantesOut) {
        int faltantesLen = 0;
        boolean[] usadasFlags = new boolean[26];

        for (int i = 0; i < usadasLen; i++) {
            int indice = indiceLetra(usadas[i]);
            if (indice != -1) {
                usadasFlags[indice] = true;
            }
        }

        //pega as variáveis não definidas
        for (int i = 0; i < 26; i++) {
            if (usadasFlags[i] && !def[i]) {
                char var = (char) (i + 97);
                faltantesOut[faltantesLen++] = var;
            }
        }

        return faltantesLen;
    }
    
}
