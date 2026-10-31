package calculadora;

/**
 * Realiza operações básicas com operandos inteiros e resultado real.
 * @author Equipe acadêmica FinançApp
 * @version 1.0
 */
public class Calculadora {
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
        if (op == null) {
            throw new IllegalArgumentException("Operação inválida. Use +, -, * ou /.");
        }
        // Converte antes da conta para evitar overflow na aritmética int.
        if (op.equals("+")) {
            return (double) a + b;
        } else if (op.equals("-")) {
            return (double) a - b;
        } else if (op.equals("*")) {
            return (double) a * b;
        } else if (op.equals("/")) {
            if (b == 0) {
                throw new ArithmeticException("Não é possível dividir por zero.");
            }
            return (double) a / b;
        } else {
            throw new IllegalArgumentException("Operação inválida. Use +, -, * ou /.");
        }
    }
}
