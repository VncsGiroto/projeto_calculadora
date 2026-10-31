package calculadora;

/**
 * Realiza operações básicas com operandos inteiros e resultado real.
 * @author Vinicius Giroto Francischinelli
 * @version 1.1
 */
public class Calculadora {
    /** Mensagem comum aos operadores ausentes e não suportados. */
    private static final String MENSAGEM_OPERACAO_INVALIDA =
            "Operação inválida. Use +, -, * ou /.";

    /** Cria uma calculadora sem estado. */
    public Calculadora() { }

    /**
     * Calcula a operação informada, sem truncar a divisão.
     * @param a primeiro operando
     * @param b segundo operando
     * @param op operador exato: +, -, * ou /
     * @return resultado da operação em precisão double
     * @throws ArithmeticException se o divisor for zero
     * @throws IllegalArgumentException se o operador for nulo ou não suportado
     */
    public double calc(int a, int b, String op) {
        validarOperador(op);
        // Cada operação mantém a conversão anterior à conta da versão inicial.
        switch (op) {
            case "+": return somar(a, b);
            case "-": return subtrair(a, b);
            case "*": return multiplicar(a, b);
            case "/": return dividir(a, b);
            default: throw new IllegalArgumentException(MENSAGEM_OPERACAO_INVALIDA);
        }
    }

    /**
     * Rejeita ausência de operador antes do despacho por switch.
     * @param operador símbolo informado
     * @throws IllegalArgumentException se o operador for nulo
     */
    private void validarOperador(String operador) {
        if (operador == null) {
            throw new IllegalArgumentException(MENSAGEM_OPERACAO_INVALIDA);
        }
    }

    /**
     * Soma com conversão prévia para double.
     * @param primeiro primeiro operando
     * @param segundo segundo operando
     * @return soma dos operandos
     */
    private double somar(int primeiro, int segundo) {
        return (double) primeiro + segundo;
    }

    /**
     * Subtrai com conversão prévia para double.
     * @param primeiro minuendo
     * @param segundo subtraendo
     * @return diferença entre os operandos
     */
    private double subtrair(int primeiro, int segundo) {
        return (double) primeiro - segundo;
    }

    /**
     * Multiplica sem overflow intermediário de int.
     * @param primeiro primeiro fator
     * @param segundo segundo fator
     * @return produto em precisão double
     */
    private double multiplicar(int primeiro, int segundo) {
        return (double) primeiro * segundo;
    }

    /**
     * Divide preservando a parte fracionária.
     * @param dividendo valor a dividir
     * @param divisor valor pelo qual dividir
     * @return quociente em precisão double
     * @throws ArithmeticException se o divisor for zero
     */
    private double dividir(int dividendo, int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Não é possível dividir por zero.");
        }
        return (double) dividendo / divisor;
    }
}
