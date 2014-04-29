/* AnalysisQueueingNetwork.java */

public class AnalysisQueueingNetwork {

	public int L, // число СМО
			T, // число классов требований
			NTotal, // общее число требований
			N[], // вектор числа t-требований
			kappa[], // вектор числа прибов
			s[][]; // вектор емкостей
	public double mu[][], // интенсивности обслуживания требований
			PI[][][], // стационарное распределение
			charN[][], // м. о. числа требований
			charLambda[][], // интенсивности входящего потока
			charPsi[][]; // коэффициенты использования приборов

	/**
	 * Инициализация данных
	 */
	public AnalysisQueueingNetwork(int L, int T, int[] N, int[] kappa, int[][] s, double[][] mu) {
		this.L = L;
		this.T = T;
		this.N = N;
		this.kappa = kappa;
		this.s = s;
		this.mu = mu;
		this.PI = new double[L + 1][T][L + 1];
		this.charN = new double[L + 1][T];
		this.charLambda = new double[L + 1][T];
		this.charPsi = new double[L + 1][T];
		this.NTotal = 0;
		for (int n = 0; n < N.length; ++n) {
			this.NTotal += N[n];
		}
	}

	/**
	 * Перестановка системы i с последней
	 */
	private void swapServers(int i) {
		if (i != L) {
			int tmp1, tmp2[];
			double tmp3[];

			tmp1 = kappa[L];
			kappa[L] = kappa[i];
			kappa[i] = tmp1;

			tmp2 = s[L];
			s[L] = s[i];
			s[i] = tmp2;

			tmp3 = mu[L];
			mu[L] = mu[i];
			mu[i] = tmp3;
		}
	}

	/**
	 * Вспомогательная функция f
	 */
	private double f(int i, int t, int n) {
		int r;
		double f, nu;

		nu = Math.min(n, kappa[i]) / (double) n;
		if (i > 0) {
			r = s[i][t] - (n - 1);
		} else {
			r = n - N[t];
			for (int j = 1; j <= L; ++j) {
				r += s[j][t];
			}
		}
		f = r / (nu * n * mu[i][t]);
		return f;
	}

	/**
	 * Вычисление стационарного распределения
	 */
	private double[][] getStateDistribution() {
		int NN[][] = new int[T][L + 1];
		double R[][] = new double[T][NTotal + 1],
				pi[][] = new double[T][NTotal + 1];
		int kk;
		double sum;

		for (int t = 0; t < T; ++t) {
			NN[t][0] = s[0][t];
			for (int n = 1; n <= NN[t][0]; ++n) {
				R[t][n] = Math.pow(f(0, t, n), -1);
			}

			for (int i = 1; i <= L; ++i) {
				NN[t][i] = Math.min(N[t], NN[t][i - 1] + s[i][t]);
				pi[t][0] = 1;

				for (int n = 1; n <= NN[t][i]; ++n) {
					sum = 0;
					kk = Math.min(s[i][t], n);

					for (int k = kk; k >= 1; --k) {
						pi[t][k] = pi[t][k - 1] * f(i, t, k);
						sum += pi[t][k];
					}

					if (n > NN[t][i - 1]) {
						pi[t][0] = 0;
					} else {
						pi[t][0] /= R[t][n];
					}

					R[t][n] = 1 / (sum + pi[t][0]);

					for (int k = 0; k <= kk; ++k) {
						pi[t][k] *= R[t][n];
					}
				}
			}
		}
		return pi;
	}

	/**
	 * Вычисление стационарных характеристик
	 */
	private void estimateCharacteristics() {
		double h, sum1, sum2;

		for (int i = 1; i <= L; ++i) {
			for (int t = 0; t < T; ++t) {
				// charN
				charN[i][t] = 0;
				for (int k = 0; k <= s[i][t]; ++k) {
					charN[i][t] += k * PI[i][t][k];
				}

				// charLambda
				sum1 = 0;
				sum2 = 0;
				if (kappa[i] < s[i][t]) {
					for (int k = 0; k <= kappa[i]; ++k) {
						sum1 += k * PI[i][t][k];
					}
					for (int k = kappa[i] + 1; k <= s[i][t]; ++k) {
						sum2 += PI[i][t][k];
					}
				} else {
					for (int k = 0; k <= s[i][t]; ++k) {
						sum1 += k * PI[i][t][k];
					}
				}
				h = sum1 + kappa[i] * sum2;
				charLambda[i][t] = mu[i][t] * h;

				// charPsi
				if (mu[i][t] != 0) {
					int S = Math.min(kappa[i], s[i][t]);
					charPsi[i][t] = charLambda[i][t] / (S * mu[i][t]);
				} else {
					charPsi[i][t] = 0;
				}
			}
		}

		// Для i = 0 (MHS)
		for (int t = 0; t < T; ++t) {
			charN[0][t] = N[t];
			charLambda[0][t] = 0;
			for (int i = 1; i <= L; ++i) {
				charN[0][t] -= charN[i][t];
				charLambda[0][t] += charLambda[i][t];
			}
			charPsi[0][t] = charLambda[0][t] / (kappa[0] * mu[0][t]);
		}
	}

	/**
	 * Анализ СеМО (управляющая процедура)
	 */
	public void analyze() {
		for (int i = 1; i <= L; ++i) {
			swapServers(i);
			PI[i] = getStateDistribution();
			swapServers(i);
		}
		estimateCharacteristics();
	}

	/**
	 * Проверка входных данных на корректность
	 */
	public static boolean isInputDataValid(int L, int T, int[] N, int[] kappa, int[][] s, double[][] mu) {
		if (L <= 0) {
			return false;
		}

		if (T <= 0) {
			return false;
		}

		for (int t = 0; t < T; ++t) {
			if (N[t] <= 0) {
				return false;
			}
		}

		for (int i = 0; i <= L; ++i) {
			if (kappa[i] <= 0) {
				return false;
			}
			for (int t = 0; t < T; ++t) {
				if (s[i][t] < 0) {
					return false;
				}
				if (mu[i][t] < 0) {
					return false;
				}
			}
		}

		int s_t[] = new int[T];
		for (int t = 0; t < T; ++t) {
			for (int i = 1; i <= L; ++i) {
				s_t[t] += s[i][t];
			}
			if (N[t] > s_t[t]) {
				return false;
			}
		}
				
		int s_i[] = new int[L + 1];
		for (int i = 1; i <= L; ++i) {
			for (int t = 0; t < T; ++t) {
				s_i[i] += s[i][t];
			}
			if (s_i[i] < kappa[i]) {
				return false;
			}
		}
		
		for (int t = 0; t < T; ++t) {
			if (s[0][t] != N[t]) {
				return false;
			}
		}

		return true;
	}
}
