package lexico;

import afds.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Principal {

    public String nomeArquivo = "src/lexico/arquivo.txt";
    public AFD a = new AFD();
    public Estado corrente;
    public String token;

    public Principal() throws Exception {
        a.ler("src/lexico/AFD.XML");
    }

    public static void main(String[] args) {
        try {
            Principal t = new Principal();
            t.inicio();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // filtra caracteres e trata quebras de linha como espaço
    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido;
        while ((charLido = reader.read()) != -1) {
            if (charLido == 13) continue; // Ignora \r do Windows
            if (charLido == 10) return new Simbolo(' '); // Enter = Espaço
            return new Simbolo((char) charLido);
        }
        return null;
    }

    public String lexico(BufferedReader r) throws IOException {
        token = "";
        corrente = a.getEstadoInicial();
        Simbolo p = proximo(r);

        // Pula espaços e quebras de linha iniciais
        while (p != null && p.getSimbolo() == ' ' && corrente.igual(a.getEstadoInicial())) {
            p = proximo(r);
        }

        if (p == null) return "fim";

        boolean erroDetectado = false;

        while (p != null) {
            char c = p.getSimbolo();

            if (c == ' ') {
                if (erroDetectado) return "erro";

                Estado proximoEstado = a.p(corrente, p);
                if (proximoEstado != null && a.getEstadosFinais().pertence(proximoEstado)) {
                    corrente = proximoEstado;
                    return token.trim();
                }
                return "erro";
            }

            token = token + c;

            if (!erroDetectado) {
                Estado proximo = a.p(corrente, p);
                if (proximo == null) {
                    erroDetectado = true;
                } else {
                    corrente = proximo;
                }
            }
            p = proximo(r);
        }

        // Caso o arquivo termine sem um espaço final
        if (erroDetectado) return "erro";
        if (a.getEstadosFinais().pertence(corrente)) return token.trim();

        return "fim";
    }

    public void inicio() {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String resultado = lexico(reader);

            while (!resultado.equals("fim")) {
                if (resultado.equals("erro")) {
                    System.out.println("Erro: O token '" + token.trim() + "' não pertence à linguagem");
                    return;
                } else {
                    String nomeEstadoFinal = corrente.getNome();

                    if (nomeEstadoFinal.equals("F_NUM")) {
                        System.out.println("'" + resultado + "'" + " é um número válido!");
                    }
                    else if (nomeEstadoFinal.equals("F_ID")) {
                        System.out.println("'" + resultado + "'" + " é uma palavra válida!");
                    }
                    else if (nomeEstadoFinal.equals("F_CONCAT")) {
                        System.out.println("'" + resultado + "'" + " é uma concatenação entre letras e números");
                    }
                }
                resultado = lexico(reader);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}