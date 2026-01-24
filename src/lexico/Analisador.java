package lexico;

public class Analisador {
    public static boolean isLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigito(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean isNumero(char c) {
        return (c >= '1' && c <= '9');
    }
}