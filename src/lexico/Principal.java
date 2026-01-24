package lexico;
import afds.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Principal {

    public String nomeArquivo = "src/lexico/arquivo.txt";
    public AFD a = new AFD();
    public Estado corrente;
    public String token;

    public Principal() throws Exception {
        a.ler("src/lexico/AFD.XML");
    }

    public static void main(String[] args) {
        Principal t;
        try {
            t = new Principal();
            t.inicio();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido;
        while ((charLido = reader.read()) != -1) {
            if (charLido == 13) continue;
            if (charLido == 10) return new Simbolo(' ');
            return new Simbolo((char) charLido);
        }
        return null;
    }

    // Le um token retona 1: se sucesso e 0: se erro -1 se fim
    @SuppressWarnings("empty-statement")
    public String lexico(BufferedReader r) throws IOException {
        token = "";
        corrente = a.getEstadoInicial();

        Simbolo p = proximo(r);

        // IGNORA ESPAÇOS ANTES DA PALAVRA
        while (p != null && p.getSimbolo() == ' ' && corrente.igual(a.getEstadoInicial())) {
            p = proximo(r);
        }

        if (p == null) return "fim";

        while (p != null) {
            token = token + p.toString();
            corrente = a.p(corrente, p);
            if (corrente == null) return "erro";

            if (a.getEstadosFinais().pertence(corrente)) return token;
            p = proximo(r);
        }
        return "fim";
    }

    // chama lexico até chegar no final de arquivo ou erro léxico
    public void inicio() {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String resultado = lexico(reader);
            while (!(resultado.equals("erro") || resultado.equals("fim"))) {
                String tipo = "";
                // Verifica em qual estado final o autômato parou
                if (corrente.getNome().equals("F_NUM")) tipo = "Número";
                else if (corrente.getNome().equals("F_ID")) tipo = "Identificador";

                System.out.println("Achou: " + resultado.trim() + " [" + tipo + "]");
                resultado = lexico(reader);
            }
            System.out.println("Fim da análise: " + resultado);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}