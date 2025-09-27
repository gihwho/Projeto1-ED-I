/*  
    GRUPO
    Giovanna Borges Coelho - RA 10756784
    Melissa - RA

    Descrição: Implementa o REPL (loop interativo) que interpreta
    os comandos (VARS, RESET, REC, STOP, PLAY, ERASE, EXIT)
*/

package src;

import src.Core.Lexer;
import src.Core.Simbolos;
import src.EstruturasDeDados.FilaCircular;

public class App {
    //TODO: capacidade da gravação
    private static final int REC_CAP = 10;

    //TODO: estado da aplicação
    private final Simbolos tabela = new Simbolos();
    private final Lexer lexer = new Lexer();
    private boolean gravando = false;
    private final FilaCircular<String> rec = new FilaCircular<>(REC_CAP);

    public static void main(String[] args) {
        
    }

    private void repl() throws Exception {
        //TODO
    }

    private void iniciarGravacao() {
        // TODO: gravando=true; imprimir "Iniciando gravação... (REC: i/10)"
    }

    private void pararGravacao() {
        // TODO: gravando=false; imprimir "Encerrando gravação... (REC: i/10)"
    }

    private void cmdVARS() {
        //TODO 
    }

    private void cmdPLAY() throws Exception {
        //TODO
    }


    private boolean tryAtribuicao(String line) {
        //TODO
    }

    private boolean tryExpressao(String line) {
        //TODO
    }


}
