/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA

    Descrição: Calcula o resultado numérico da expressão em notação posfixa,
    aplicando os operadores na ordem.
*/

public class PosfixaResult {
    
    private static boolean isOperador(String t) {
        return t != null && t.length() == 1 && "+-*/^".indexOf(t.charAt(0)) >= 0;
    }

    private static double aplica(char op, double b, double a) throws Exception {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0.0) throw new Exception("Divisão por zero");
                return a / b;
            case '^': return Math.pow(a, b);
            default:  throw new Exception("Erro: operador inválido");
        }
    }

    public static double avaliar(String posfixa, Simbolos tab) throws Exception {
        // Tokens devem estar separados por espaço, ex.: "A B C + * 2 /"
        Pilha<Double> pilha = new Pilha<>(256);

        if (posfixa == null || posfixa.trim().isEmpty()) {
            throw new Exception("Erro: expressão inválida.");
        }

        String[] tokens = posfixa.trim().split("\\s+");

        for (String t : tokens) {
            if (t.isEmpty()) continue;

            // operador binario
            if (isOperador(t)) {
                if (pilha.sizeElements() < 2) throw new Exception("Erro: expressão inválida.");
                double b = pilha.pop();
                double a = pilha.pop();
                double r = aplica(t.charAt(0), b, a);
                pilha.push(r);
                continue;
            }

            // A..Z - a..z
            if (t.length() == 1 && Character.isLetter(t.charAt(0))) {
                char v = Character.toUpperCase(t.charAt(0));
                double val = tab.get(v); // se não definida, quem chama deve ter verificado antes
                pilha.push(val);
                continue;
            }

            try {
                double val = Double.parseDouble(t.replace(',', '.'));
                pilha.push(val);
            } catch (NumberFormatException ex) {
                // token erro
                throw new Exception("Erro: expressão inválida.");
            }
        }

        if (pilha.sizeElements() != 1) throw new Exception("Erro: expressão inválida");
        return pilha.pop();
    }
}
