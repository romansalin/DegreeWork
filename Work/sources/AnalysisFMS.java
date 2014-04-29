/* AnalysisFMS.java */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;

public class AnalysisFMS extends JFrame {

	/**
	 * Инициализация фрейма
	 */
	public AnalysisFMS() {
		initComponents();
	}

	/**
	 * Метод вызывается из конструктора для инициализации фрейма
	 */
	private void initComponents() {
		jSpinnerL = new JSpinner();
		jSpinnerT = new JSpinner();
		jTextFieldKappa = new JTextField();
		jTextFieldN = new JTextField();
		JLabel jLabelL = new JLabel();
		JLabel jLabelT = new JLabel();
		JLabel jLabelKappa = new JLabel();
		JLabel jLabelS = new JLabel();
		JLabel jLabelMu = new JLabel();
		JLabel jLabelN = new JLabel();
		JButton jButtonGetResults = new JButton();
		JButton jButtonOpenInput = new JButton();
		JScrollPane jScrollPaneS = new JScrollPane();
		jTextAreaS = new JTextArea();
		JScrollPane jScrollPaneMu = new JScrollPane();
		jTextAreaMu = new JTextArea();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Анализ гибких производственных систем");
		setPreferredSize(new Dimension(600, 410));
		setResizable(false);
		getContentPane().setLayout(null);

		jLabelL.setText("Число СМО (L):");
		getContentPane().add(jLabelL);
		jLabelL.setBounds(10, 16, 96, 16);
		
		SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 999, 1);
		jSpinnerL.setModel(model);
		getContentPane().add(jSpinnerL);
		jSpinnerL.setBounds(110, 10, 60, 28);

		jLabelT.setText("Число классов требований (T):");
		getContentPane().add(jLabelT);
		jLabelT.setBounds(224, 16, 200, 16);
		
		SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1, 999, 1);
		jSpinnerT.setModel(model2);
		getContentPane().add(jSpinnerT);
		jSpinnerT.setBounds(425, 10, 60, 28);

		jLabelN.setText("Вектор числа t-требований (N[t]):");
		getContentPane().add(jLabelN);
		jLabelN.setBounds(10, 55, 220, 16);
		
		getContentPane().add(jTextFieldN);
		jTextFieldN.setBounds(230, 50, 364, 28);

		jLabelKappa.setText("Вектор числа приборов (kappa[i]):");
		getContentPane().add(jLabelKappa);
		jLabelKappa.setBounds(10, 95, 220, 16);
		
		getContentPane().add(jTextFieldKappa);
		jTextFieldKappa.setBounds(230, 90, 364, 28);

		jLabelS.setText("Емкости систем (s[i][t]):");
		getContentPane().add(jLabelS);
		jLabelS.setBounds(10, 130, 218, 16);

		jLabelMu.setText("Интенсивности обслуживания (mu[i][t]):");
		getContentPane().add(jLabelMu);
		jLabelMu.setBounds(310, 130, 300, 16);

		jScrollPaneS.setViewportView(jTextAreaS);

		getContentPane().add(jScrollPaneS);
		jScrollPaneS.setBounds(10, 150, 280, 180);

		jScrollPaneMu.setViewportView(jTextAreaMu);

		getContentPane().add(jScrollPaneMu);
		jScrollPaneMu.setBounds(310, 150, 280, 180);

		jButtonOpenInput.setText("Открыть файл");
		jButtonOpenInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonOpenInputActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonOpenInput);
		jButtonOpenInput.setBounds(270, 350, 130, 29);
		
		jButtonGetResults.setText("Получить результаты");
		jButtonGetResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonGetResultsActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonGetResults);
		jButtonGetResults.setBounds(410, 350, 180, 29);

		pack();
	}

	/**
	 * Формирование результата
	 */
	private void jButtonGetResultsActionPerformed(ActionEvent evt) {
		try {
			int L = (Integer) jSpinnerL.getValue();
			int T = (Integer) jSpinnerT.getValue();
			
			int N[] = new int[T],
					kappa[] = new int[L + 1],
					s[][] = new int[L + 1][T];
			double mu[][] = new double[L + 1][T];
			
			Scanner sc = new Scanner(jTextFieldN.getText());					
			for (int t = 0; t < T; ++t) {
				N[t] = sc.nextInt();
			}

			Scanner sc1 = new Scanner(jTextFieldKappa.getText());
			Scanner sc2 = new Scanner(jTextAreaS.getText());
			Scanner sc3 = new Scanner(jTextAreaMu.getText());
			for (int i = 0; i <= L; ++i) {
				kappa[i] = sc1.nextInt();
				for (int t = 0; t < T; ++t) {
					s[i][t] = sc2.nextInt();
					mu[i][t] = Double.valueOf(sc3.next());
				}
			}
			
			// Проверка входных данных
			if (!AnalysisQueueingNetwork.isInputDataValid(L, T, N, kappa, s, mu)) {
				throw new Exception();
			}
			
			// Создание объекта с результатами анализа
			AnalysisQueueingNetwork qNetwork = new AnalysisQueueingNetwork(L, T, N, kappa, s, mu);
			qNetwork.analyze();
			
			// Создание объекта окна
			ResultFrame resultFrame = new ResultFrame();
			resultFrame.setVisible(true);
			
			// Передача объекта с результатами анализа окну для вывода
			resultFrame.showResults(qNetwork);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Некоторые данные указаны неверно или отсутствуют",
					"Ошибка", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Считывание данных из файла
	 */
	private void jButtonOpenInputActionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		chooser.setFileFilter(filter);

		int ret = chooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			Scanner sc = null;
			try {
				sc = new Scanner(file);
				sc.useLocale(new Locale("US"));
				String string = "";
				
				int L = sc.nextInt();
				jSpinnerL.setValue(L);
				
				int T = sc.nextInt();
				jSpinnerT.setValue(T);
				
				int N[] = new int[T];
				for (int t = 0; t < T; ++t) {
					N[t] = sc.nextInt();
					string += String.valueOf(N[t]) + " ";
				}
				jTextFieldN.setText(string);
				
				int kappa[] = new int[L + 1];
				string = "";
				for (int i = 0; i <= L; ++i) {
					kappa[i] = sc.nextInt();
					string += String.valueOf(kappa[i]) + " ";
				}
				jTextFieldKappa.setText(string);
				
				int s[][] = new int[L + 1][T];
				string = "";
				for (int i = 0; i <= L; ++i) {
					for (int t = 0; t < T; ++t) {
						s[i][t] = sc.nextInt();
						string += String.valueOf(s[i][t]) + "\t";
					}
					string += "\n";
				}
				jTextAreaS.setText(string);
				
				double mu[][] = new double[L + 1][T];
				string = "";
				for (int i = 0; i <= L; ++i) {
					for (int t = 0; t < T; ++t) {
						mu[i][t] = sc.nextDouble();
						string += String.valueOf(mu[i][t]) + "\t";
					}
					string += "\n";
				}
				jTextAreaMu.setText(string);
				
				if (!AnalysisQueueingNetwork.isInputDataValid(L, T, N, kappa, s, mu)) {
					throw new Exception();
				}				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Ошибка чтения из файла.\nНекорректные данные",
						"Ошибка", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (sc != null) {
					sc.close();
				}
			}
		}
	}

	/**
	 * Создание и показ фрейма
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new AnalysisFMS().setVisible(true);
			}
		});
	}

	private JSpinner jSpinnerL;
	private JSpinner jSpinnerT;
	private JTextArea jTextAreaMu;
	private JTextArea jTextAreaS;
	private JTextField jTextFieldN;
	private JTextField jTextFieldKappa;
}
