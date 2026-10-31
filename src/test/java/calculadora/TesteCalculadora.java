package calculadora;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica operações, contratos de erro e continuidade da aplicação.
 * @author Vinicius Giroto Francischinelli
 * @version 1.0
 */
class TesteCalculadora {
    /** Calculadora sem estado compartilhada pelos cenários de cada instância. */
    private final Calculadora calculadora = new Calculadora();

    /**
     * Compara cada operação com o resultado esperado.
     * @param a primeiro operando
     * @param b segundo operando
     * @param operador operação testada
     * @param esperado resultado definido pelo cenário
     */
    @ParameterizedTest(name = "{0} {2} {1} = {3}")
    @CsvSource({"10,5,+,15", "10,5,-,5", "10,5,*,50", "10,5,/,2",
        "5,2,/,2.5", "-7,3,+,-4", "-7,3,-,-10", "-7,3,*,-21",
        "-9,2,/,-4.5", "0,3,/,0", "0,5,*,0",
        "2147483647,1,+,2147483648", "-2147483648,1,-,-2147483649",
        "2147483647,2,*,4294967294", "-2147483648,-1,/,2147483648"})
    void verificarOperacoes(int a, int b, String operador, double esperado) {
        double obtido = calculadora.calc(a, b, operador);
        System.out.printf("Entrada: %d %s %d | esperado: %s | obtido: %s%n",
                a, operador, b, esperado, obtido);
        assertEquals(esperado, obtido, 1e-9);
    }

    /** Verifica o tipo e a mensagem do erro de divisão por zero. */
    @Test
    void impedirDivisaoPorZero() {
        ArithmeticException obtido = assertThrows(ArithmeticException.class,
                () -> calculadora.calc(5, 0, "/"));
        String esperado = "Não é possível dividir por zero.";
        System.out.printf("Entrada: 5 / 0 | esperado: %s | obtido: %s%n", esperado, obtido.getMessage());
        assertEquals(esperado, obtido.getMessage());
    }

    /**
     * Verifica operadores inválidos, incluindo ausência de operador.
     * @param operador operador que deve ser rejeitado
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"%", "abc", " ", " + "})
    void rejeitarOperacaoInvalida(String operador) {
        IllegalArgumentException obtido = assertThrows(IllegalArgumentException.class,
                () -> calculadora.calc(5, 2, operador));
        String esperado = "Operação inválida. Use +, -, * ou /.";
        System.out.printf("Entrada: 5 [%s] 2 | esperado: %s | obtido: %s%n", operador, esperado, obtido.getMessage());
        assertEquals(esperado, obtido.getMessage());
    }

    /** Demonstra que erros sucessivos não impedem uma operação válida posterior. */
    @Test
    void continuarDepoisDeErros() {
        String entradas = "5 / 0\n5 % 2\ntexto + 2\n2147483648 + 1\nlinha\n5 / 2\nsair\n9 + 9\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Aplicacao.executar(new ByteArrayInputStream(entradas.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(buffer, true, StandardCharsets.UTF_8));
        String obtido = buffer.toString(StandardCharsets.UTF_8);
        System.out.println("Teste funcional: erros seguidos de 5 / 2 | esperado: continuidade e 2.5 | obtido:\n" + obtido);
        assertTrue(obtido.contains("Não é possível dividir por zero."));
        assertTrue(obtido.contains("Operação inválida."));
        assertTrue(obtido.contains("Informe inteiros"));
        assertTrue(obtido.contains("Exemplo: 5 / 2"));
        assertTrue(obtido.contains("Resultado: 2.5"));
        assertTrue(obtido.contains("Calculadora encerrada."));
        assertFalse(obtido.contains("Resultado: 18.0"));
    }

    /** Verifica encerramento normal quando o fluxo de entrada termina. */
    @Test
    void encerrarNoFimDaEntrada() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Aplicacao.executar(new ByteArrayInputStream(new byte[0]),
                new PrintStream(buffer, true, StandardCharsets.UTF_8));
        assertTrue(buffer.toString(StandardCharsets.UTF_8).contains("Calculadora encerrada."));
    }
}
