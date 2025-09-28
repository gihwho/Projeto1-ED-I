/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa Namie Shine - RA
*/

public class FilaCircular <T> {
    private static final int TAM_DEFAULT = 100;
    private int inicio, fim, qtde;
    private T e[];

    public FilaCircular(int tamanho) {
        this.inicio = this.fim = this.qtde = 0;
        e = (T[]) new Object[tamanho];
    }

    public FilaCircular() {
        this(TAM_DEFAULT);
    }

    public boolean qIsEmpty() {
        return (qtde == 0);
    }

    public boolean qIsFull() {
        return (qtde == e.length);
    }

    public void enqueue(T e) throws Exception {
        if (!qIsFull()) {
            this.e[this.fim++] = e;
            this.fim = this.fim % this.e.length;
            this.qtde++;
        } else
            throw new Exception("Oveflow - Estouro de Fila");
    }

    public T dequeue() throws Exception {
        T aux;
        if (!qIsEmpty()) {
            aux = this.e[this.inicio];
            this.inicio = ++this.inicio % this.e.length;
            this.qtde--;
            return aux;
        } else {
            throw new Exception("underflow - Esvaziamento de Fila");
        }
    }

    public T front() throws Exception {
        if (!qIsEmpty())
            return e[inicio];
        else {
            throw new Exception("underflow - Esvaziamento de Fila");
        }
    }

    public T rear() throws Exception {
        if (!qIsEmpty())
            return e[(fim - 1 + e.length) % e.length];
        else {
            throw new Exception("underflow - Esvaziamento de Fila");
        }
    }

    public int sizeElements() {
        return qtde;
    }
}
