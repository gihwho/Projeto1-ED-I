/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA 10401096
*/

public class Pilha <T> {
    private static int TAM_DEFAULT = 100;
    private int topoPilha;
    private T e[ ];

    public Pilha(int tamanho) {
        this.e = (T[]) new Object[tamanho];
        this.topoPilha = -1;
    }

    public Pilha() {
        this(TAM_DEFAULT);
    }

    public boolean isEmpty() {
        return this.topoPilha == -1;
    }

    public boolean isFull() {
        return this.topoPilha == this.e.length - 1;
    }

    public void push(T e) throws Exception {
        if (!this.isFull())
            this.e[++this.topoPilha] = e;
        else
            throw new Exception("Overflow - Estouro de Pilha");
    }

    public T pop() throws Exception {
        if (!this.isEmpty())
            return this.e[this.topoPilha--];
        else {
            throw new Exception("Underflow - Esvaziamento de Pilha");
        }
    }

    public T topo() throws Exception {
        if (!this.isEmpty())
            return this.e[this.topoPilha];
        else {
            throw new Exception("Underlow - Esvaziamento de Pilha");
        }
    }

    public int sizeElements() {
        return topoPilha + 1;
    }
}
