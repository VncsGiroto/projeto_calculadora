package calculadora;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Interface de terminal que apresenta resultados e recupera erros de entrada.
 * @author Vinicius Giroto Francischinelli
 * @version 1.0
 */
public final class Aplicacao {
    /** Classe utilitária: não permite instanciação. */
    private Aplicacao() { }

    /**
     * Inicia a interface interativa.
     * @param args argumentos não utilizados
     */
    public static void main(String[] args) {
        executar(System.in, System.out);
    }

    /**
     * Processa operações até receber sair ou o fim da entrada.
     * Erros previstos são apresentados e não encerram o laço.
     * @param entrada fluxo de entrada UTF-8
     * @param saida fluxo de saída para mensagens e resultados
     */
    public static void executar(InputStream entrada, PrintStream saida) {
        Scanner leitor = new Scanner(entrada, StandardCharsets.UTF_8);
        Calculadora calculadora = new Calculadora();
        saida.println("Calculadora FinançApp | formato: inteiro operador inteiro | sair");
        // Não fecha o Scanner: os fluxos pertencem a quem chamou o método.
        while (leitor.hasNextLine()) {
            String linha = leitor.nextLine().trim();
            if (linha.equalsIgnoreCase("sair")) {
                break;
            }
            String[] partes = linha.split("\\s+");
            if (partes.length != 3) {
                saida.println("Entrada inválida. Exemplo: 5 / 2");
                continue;
            }
            try {
                int primeiro = Integer.parseInt(partes[0]);
                int segundo = Integer.parseInt(partes[2]);
                saida.println("Resultado: " + calculadora.calc(primeiro, segundo, partes[1]));
            } catch (NumberFormatException erro) {
                saida.println("Entrada inválida. Informe inteiros entre -2147483648 e 2147483647.");
            } catch (ArithmeticException | IllegalArgumentException erro) {
                saida.println("Erro: " + erro.getMessage());
            }
        }
        saida.println("Calculadora encerrada.");
    }
}
