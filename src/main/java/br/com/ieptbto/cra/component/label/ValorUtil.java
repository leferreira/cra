package br.com.ieptbto.cra.component.label;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

/**
 * Utilitário para Valores.
 */
@SuppressWarnings("serial")
public class ValorUtil implements Serializable {

	/** Margem de erro aceitável para o sistema (utilizado nos TA´s). */
	public static final BigDecimal ERRO_MAX_PRECISAO = new BigDecimal("0.03");

	/** Margem de erro aceitável para o sistema (utilizado nos TA´s). */
	public static final BigDecimal ERRO_MAX_PRECISAO_SECURITIZADA = new BigDecimal("0.04");

	/**
	 * Margem de erro aceitável para verificação do percentural de juros do
	 * PROER (utilizado nos TA´s).
	 */
	public static final BigDecimal ERRO_MAX_PRECISAO_PERCENTUAL_JUROS = new BigDecimal("0.000000000000001");

	/** Modo de arredondamento utilizado para os cálculos. */
	public static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;

	/** Precisão para cálculo de 15 casas decimais. */
	public static final int QUINZE_CASAS_DECIMAIS = 15;

	/** Precisão para cálculo de 20 casas decimais. */
	public static final int VINTE_CASAS_DECIMAIS = 20;

	/** Quantidade de casas decimais para arredondamento dos índices. */
	public static final int QTD_CASAS_DECIMAIS_INDICE = 6;

	/**
	 * Quantidade de casas decimais para conversão do índice em percentual do
	 * índice.
	 */
	public static final int QTD_CASAS_DECIMAIS_INDICE_PERCENTUAL = 8;

	/** Número 100. Utilizado para cálculo valor percentual. */
	public static final BigDecimal BASE_PERCENTUAL = new BigDecimal(100);

	/** Quantidade de casas decimais para arredondamento dos valores. */
	public static final int QTD_CASAS_ARREDONDAMENTO = 2;

	private static final ValorUtil INSTANCIA = new ValorUtil();

	private static final String SEPARADOR_DECIMAL = "\\.";

	private static final String PONTO = ".";

	private static final String VIRGULA = ",";

	private ValorUtil() {
		super();
	}

	/**
	 * Obtém a única instância da classe {@link ValorUtil}.
	 * 
	 * @return instância de {@link ValorUtil}
	 */
	public static ValorUtil get() {
		return INSTANCIA;
	}

	/**
	 * Verifica se o valor comparado é maior que o valor de referência.
	 * 
	 * @param valor1
	 *            referência para a comparação
	 * @param valor2
	 *            valor comparado com a referência
	 * @return <code>true</code> caso seja maior
	 */
	public boolean isMaior(BigDecimal valor1, BigDecimal valor2) {
		return valor1.compareTo(valor2) > 0;
	}

	/**
	 * Verifica se o 1º valor é >= que o 2º valor.
	 * 
	 * @param valor1
	 *            1º valor
	 * @param valor2
	 *            2º valor
	 * 
	 * @return se é maior ou igual
	 */
	public boolean isMaiorOuIgual(BigDecimal valor1, BigDecimal valor2) {
		return valor1.compareTo(valor2) >= 0;
	}

	/**
	 * Verifica se o valor1 é igual ao valor 2 com tolerância de
	 * {@value #ERRO_MAX_PRECISAO}.
	 * 
	 * @param valor1
	 *            valor1
	 * @param valor2
	 *            valor2
	 * @return <code>true</code> caso a diferença entre o valor1 e o valor2 seja
	 *         maior ou igual a tolerância de {@value #ERRO_MAX_PRECISAO}.
	 */
	public boolean isIgualComTolerancia(BigDecimal valor1, BigDecimal valor2) {
		return isIgualComTolerancia(valor1, valor2, ERRO_MAX_PRECISAO);
	}

	/**
	 * Verifica se o valor1 é igual ao valor 2 com tolerância informada.
	 * 
	 * @param valor1
	 *            valor1
	 * @param valor2
	 *            valor2
	 * @param erroMaximo
	 *            valor máximo do erro de precisão
	 * @return <code>true</code> caso o valor1 seja igual ao valor2 com a
	 *         tolerância informada
	 */
	public boolean isIgualComTolerancia(BigDecimal valor1, BigDecimal valor2, BigDecimal erroMaximo) {
		BigDecimal diferencaAbsoluta = valor1.subtract(valor2).abs();
		return !isMaiorQueTolerancia(diferencaAbsoluta, erroMaximo);
	}

	/**
	 * Verifica se o valor1 é maior que o valor 2 com tolerância informada.
	 * 
	 * @param valor1
	 *            valor1
	 * @param valor2
	 *            valor2
	 * @param erroMaximo
	 *            valor máximo do erro de precisão
	 * @return <code>true</code> caso o valor1 seja maior que o valor2 com a
	 *         tolerância informada
	 */
	public boolean isMaiorComTolerancia(BigDecimal valor1, BigDecimal valor2, BigDecimal erroMaximo) {
		if (valor1.compareTo(valor2) == 1) {
			BigDecimal diferencaAbsoluta = valor1.subtract(valor2).abs();
			return isMaiorQueTolerancia(diferencaAbsoluta, erroMaximo);
		}
		return false;
	}

	private boolean isMaiorQueTolerancia(BigDecimal diferenca, BigDecimal tolerancia) {
		return (diferenca.compareTo(tolerancia) == 1);
	}

	/**
	 * Retorna <code>true</code> se o valor passado como parâmetro é > 0.
	 * 
	 * @param valor
	 *            valor testado
	 * @return se atende a condição
	 */
	protected boolean isMaiorQueZero(BigDecimal valor) {
		return (valor.compareTo(BigDecimal.ZERO) > 0);
	}

	/**
	 * Verifica se o valor é positivo.
	 * 
	 * @param valor
	 *            valor a ser verificado
	 * @return <code>true</code> se o valor for positivo
	 */
	public boolean isValorPositivo(BigDecimal valor) {
		return ((valor != null) && (valor.signum() == 1));
	}

	/**
	 * Este método valida se o valor tem no máximo 13 caracteres antes da
	 * vírgula e 2 após a vírgula.
	 * 
	 * @param valor
	 *            Valor a ser validado
	 * @return <code>true</code> se o valor estiver dentro do padrão e
	 *         <code>false</code> se o valor estiver fora do padrão
	 */
	public boolean validarQuantidadeCaracteres(BigDecimal valor) {
		return validarQuantidadeCaracteres(valor, 13, 2);
	}

	/**
	 * Este método valida se o valor tem no máximo a quantidade de caracteres
	 * informada.
	 * 
	 * @param valor
	 *            Valor a ser validado
	 * @param tamanhoParteInteira
	 *            tamanho máximo da parte inteira
	 * @param tamanhoParteDecimal
	 *            tamanho máximo da parte decimal
	 * @return <code>true</code> se o valor estiver dentro do padrão e
	 *         <code>false</code> se o valor estiver fora do padrão
	 */
	public boolean validarQuantidadeCaracteres(BigDecimal valor, int tamanhoParteInteira, int tamanhoParteDecimal) {
		if (valor == null) {
			return false;
		}
		String valorString = valor.toPlainString();
		String[] valores = valorString.split(SEPARADOR_DECIMAL);
		String parteInteira = valores[0];
		String parteDecimal = "";
		if (valores.length > 1) {
			parteDecimal = valores[1];
		}
		if (parteInteira.length() > tamanhoParteInteira) {
			return false;
		}
		if (parteDecimal.length() > tamanhoParteDecimal) {
			return false;
		}
		return true;
	}

	/**
	 * Este método arredonda um valor recebido em duas casas decimais.
	 * 
	 * @param valor
	 *            valor a ser arredondado
	 * @return valor arredondado
	 * 
	 * @see #arredondar(BigDecimal, int)
	 */
	public BigDecimal arredondar(BigDecimal valor) {
		return arredondar(valor, QTD_CASAS_ARREDONDAMENTO);
	}

	/**
	 * Este método arredonda um valor recebido.
	 * 
	 * @param valor
	 *            valor a ser arredondado
	 * @param quantidadeCasasDecimais
	 *            quantidade de cadas decimais do arredondamento
	 * @return valor arredondado
	 * @see #MODO_ARREDONDAMENTO
	 */
	public BigDecimal arredondar(BigDecimal valor, int quantidadeCasasDecimais) {
		return arredondar(valor, quantidadeCasasDecimais, MODO_ARREDONDAMENTO);
	}

	/**
	 * Este método trunca um valor recebido em duas casas decimais.
	 * 
	 * @param valor
	 *            valor a ser truncado
	 * @return valor truncado
	 * @see #truncar(BigDecimal, int)
	 */
	public BigDecimal truncar(BigDecimal valor) {
		return truncar(valor, QTD_CASAS_ARREDONDAMENTO);
	}

	/**
	 * Este método trunca um valor recebido.
	 * 
	 * @param valor
	 *            valor a ser truncado
	 * @param quantidadeCasasDecimais
	 *            quantidade de cadas decimais a partir da qual o número é
	 *            truncado
	 * @return valor truncado
	 */
	public BigDecimal truncar(BigDecimal valor, int quantidadeCasasDecimais) {
		return arredondar(valor, quantidadeCasasDecimais, RoundingMode.DOWN);
	}

	/**
	 * Este método arredonda um valor recebido.
	 * 
	 * @param valor
	 *            valor a ser arredondado
	 * @param quantidadeCasasDecimais
	 *            quantidade de cadas decimais do arredondamento
	 * @param modoArredondamento
	 *            modo de arredondamento
	 * @return valor arredondado
	 */
	private BigDecimal arredondar(BigDecimal valor, int quantidadeCasasDecimais, RoundingMode modoArredondamento) {
		MathContext mathContext = calcularMathContext(valor, quantidadeCasasDecimais, modoArredondamento);
		BigDecimal valorArredondado = valor.round(mathContext);
		return valorArredondado.setScale(quantidadeCasasDecimais, modoArredondamento);
	}

	private MathContext calcularMathContext(BigDecimal valor, int quantidadeCasasDecimais, RoundingMode modoArredondamento) {
		int precisao = quantidadeCasasDecimais;

		String valorSemSinal = valor.toPlainString().replaceAll("-", "");
		String[] valorString = valorSemSinal.split(SEPARADOR_DECIMAL);
		if (valorString != null) {
			int quantidadeCaracteresParteInteira = valorString[0].length();
			precisao += quantidadeCaracteresParteInteira;
		}
		return new MathContext(precisao, modoArredondamento);
	}

	/**
	 * Realiza operação de divisão. <br>
	 * É utilizado o modo de arredondamento {@link #MODO_ARREDONDAMENTO}. <br>
	 * Arredonda o resultado em {@link #VINTE_CASAS_DECIMAIS}.
	 * 
	 * @param dividendo
	 *            dividendo da operação
	 * @param divisor
	 *            divisor da operação
	 * @return resultado da divisão
	 * @see #VINTE_CASAS_DECIMAIS
	 * @see #MODO_ARREDONDAMENTO
	 */
	public BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor) {
		return dividir(dividendo, divisor, VINTE_CASAS_DECIMAIS);
	}

	/**
	 * Realiza operação de divisão. <br>
	 * É utilizado o modo de arredondamento {@link #MODO_ARREDONDAMENTO}.
	 * 
	 * @param dividendo
	 *            dividendo da operação
	 * @param divisor
	 *            divisor da operação
	 * @param quantidadeCasasDecimais
	 *            quantidade de casas decimais
	 * @return resultado da divisão
	 * @see #MODO_ARREDONDAMENTO
	 */
	public BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor, int quantidadeCasasDecimais) {
		return dividendo.divide(divisor, quantidadeCasasDecimais, MODO_ARREDONDAMENTO);
	}

	/**
	 * Realiza operação de multiplicação, arredondando o resultado de acordo com
	 * a quantidade de casas decimais passada como parâmetro. <br>
	 * É utilizado o modo de arredondamento {@link #MODO_ARREDONDAMENTO} e
	 * precisão de {@link #VINTE_CASAS_DECIMAIS}.
	 * 
	 * @param multiplicando
	 *            multiplicando
	 * @param multiplicador
	 *            multiplicador
	 * @return valor multiplicado arredondado
	 */
	public BigDecimal multiplicar(BigDecimal multiplicando, BigDecimal multiplicador) {
		return multiplicar(multiplicando, multiplicador, VINTE_CASAS_DECIMAIS);
	}

	/**
	 * Realiza operação de multiplicação, arredondando o resultado de acordo com
	 * a quantidade de casas decimais passada como parâmetro. <br>
	 * É utilizado o modo de arredondamento {@link #MODO_ARREDONDAMENTO}.
	 * 
	 * @param multiplicando
	 *            multiplicando
	 * @param multiplicador
	 *            multiplicador
	 * @param quantidadeCasasDecimais
	 *            quantidadade de casas decimais
	 * @return valor multiplicado arredondado
	 */
	public BigDecimal multiplicar(BigDecimal multiplicando, BigDecimal multiplicador, int quantidadeCasasDecimais) {
		BigDecimal multiplicacao = multiplicando.multiply(multiplicador);
		return arredondar(multiplicacao, quantidadeCasasDecimais);
	}

	/**
	 * Converte um número índice em um percentual.
	 * <p>
	 * Exemplo: (1.00123 - 1) * 100 = 0.123%
	 * 
	 * @param numeroIndice
	 *            número índice
	 * @return valor
	 */
	public BigDecimal converterNumeroIndiceParaValor(BigDecimal numeroIndice) {
		return multiplicar(numeroIndice.subtract(BigDecimal.ONE), BASE_PERCENTUAL);
	}

	/**
	 * Converte um percentual em um número índice.
	 * <p>
	 * Exemplo: ((0.123%) / 100) + 1 = 1.00123
	 * 
	 * @param valor
	 *            valor (ex. 0,13)
	 * @return número índice (ex. 1,0013)
	 */
	public BigDecimal converterValorParaNumeroIndice(BigDecimal valor) {
		return converterValorParaNumeroIndice(valor, VINTE_CASAS_DECIMAIS);
	}

	/**
	 * Converte um percentual em um número índice.
	 * <p>
	 * Exemplo: ((0.123%) / 100) + 1 = 1.00123
	 * 
	 * @param valor
	 *            valor (ex. 0,13)
	 * @param quantidadeCasasDecimais
	 *            quantidadade de casas decimais
	 * @return número índice (ex. 1,0013)
	 */
	public BigDecimal converterValorParaNumeroIndice(BigDecimal valor, int quantidadeCasasDecimais) {
		return dividir(valor, BASE_PERCENTUAL, quantidadeCasasDecimais).add(BigDecimal.ONE);
	}

	/**
	 * Calcula a variação entre dois valores.
	 * <p>
	 * Exemplo: (VALOR ATUAL / VALOR ANTERIOR) - 1
	 * 
	 * @param valorAnterior
	 *            valor comparado
	 * @param valorAtual
	 *            valor comparado
	 * @return valor da variação
	 */
	public BigDecimal calcularVariacao(BigDecimal valorAtual, BigDecimal valorAnterior) {
		return dividir(valorAtual, valorAnterior).subtract(BigDecimal.ONE);
	}

	/**
	 * Converte uma string para BigDecimal.
	 * 
	 * @param valor
	 * @return
	 */
	public static BigDecimal converteParaBigDecimal(String valor) {
		if (valor == null) {
			return null;
		}
		String[] origem = new String[] { PONTO, VIRGULA };
		String[] destino = new String[] { "", PONTO };
		String novoValor = StringUtils.replaceEach(valor, origem, destino);
		return new BigDecimal(novoValor);
	}

}
