/* ResultFrame.java */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ResultFrame extends JFrame {
	
	public ResultFrame() {
		initComponents();
	}

	private void initComponents() {
		JScrollPane jScrollPanePI = new JScrollPane();
		jTextAreaPI = new JTextArea();
		JScrollPane jScrollPaneN = new JScrollPane();
		jTextAreaN = new JTextArea();
		JScrollPane jScrollPaneLambda = new JScrollPane();
		jTextAreaLambda = new JTextArea();
		JScrollPane jScrollPanePsi = new JScrollPane();
		jTextAreaPsi = new JTextArea();
		JLabel jLabelPI = new JLabel();
		JLabel jLabelPsi = new JLabel();
		JLabel jLabelN = new JLabel();
		JLabel jLabelLambda = new JLabel();
		JButton jButtonClose = new JButton();
		JButton jButtonSaveOutput = new JButton();
		
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Результаты анализа");
		setPreferredSize(new Dimension(600, 500));
		setResizable(false);
		getContentPane().setLayout(null);

		jLabelPI.setText("Стационарное распределение (pi[i][t][k]):");
		getContentPane().add(jLabelPI);
		jLabelPI.setBounds(10, 10, 280, 16);

		jScrollPanePI.setViewportView(jTextAreaPI);

        getContentPane().add(jScrollPanePI);
        jScrollPanePI.setBounds(10, 30, 280, 180);
		
		jLabelN.setText("М.о. числа требований (n[i][t]):");
		getContentPane().add(jLabelN);
		jLabelN.setBounds(310, 10, 200, 16);

        jScrollPaneN.setViewportView(jTextAreaN);

        getContentPane().add(jScrollPaneN);
        jScrollPaneN.setBounds(310, 30, 280, 180);
		
		jLabelLambda.setText("Интенсивности вход. потока (lambda[i][t]):");
		getContentPane().add(jLabelLambda);
		jLabelLambda.setBounds(10, 220, 320, 16);

        jScrollPaneLambda.setViewportView(jTextAreaLambda);

        getContentPane().add(jScrollPaneLambda);
        jScrollPaneLambda.setBounds(10, 240, 280, 180);
		
		jLabelPsi.setText("Коэффициенты использования (psi[i][t]):");
		getContentPane().add(jLabelPsi);
		jLabelPsi.setBounds(310, 220, 260, 16);

        jScrollPanePsi.setViewportView(jTextAreaPsi);

        getContentPane().add(jScrollPanePsi);
        jScrollPanePsi.setBounds(310, 240, 280, 180);

		jButtonClose.setText("Закрыть");
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonCloseActionPerformed();
			}
		});
		getContentPane().add(jButtonClose);
		jButtonClose.setBounds(300, 440, 90, 29);
		
		jButtonSaveOutput.setText("Сохранить результаты");
		jButtonSaveOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonSaveOutputActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonSaveOutput);
		jButtonSaveOutput.setBounds(400, 440, 188, 29);

		pack();
	}
	/**
	 * Закрытие окна
	 */
	private void jButtonCloseActionPerformed() {
		this.setVisible(false);
	}

	/**
	 * Запись результата анализа в файл
	 */
	private void jButtonSaveOutputActionPerformed(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		chooser.setFileFilter(filter);
		
		int ret = chooser.showSaveDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				FileWriter fw = new FileWriter(file + ".txt");
				fw.write("n[i][t]:\n");
				jTextAreaN.write(fw);
				
				fw.write("\n------------------------------\n");
				
				fw.write("\nlambda[i][t]:\n");
				jTextAreaLambda.write(fw);
				
				fw.write("\n------------------------------\n");
				
				fw.write("\npsi[i][t]:\n");
				jTextAreaPsi.write(fw);
				
				fw.write("\n------------------------------\n");
				
				fw.write("\nPI[i][t][k]:\n");
				jTextAreaPI.write(fw);
				
				fw.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Ошибка сохранения файла",
						"Ошибка", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Вывод результата анализа
	 */
	public void showResults(AnalysisQueueingNetwork qNetwork) {
		Locale.setDefault(new Locale("US"));
		DecimalFormat df = new DecimalFormat("#.###");

		String result = "";
		for (int i = 1; i <= qNetwork.L; ++i) {
			result += "i = " + i + ":\n";
			for (int t = 0; t < qNetwork.T; ++t) {
				for (int k = 0; k <= qNetwork.s[i][t]; ++k) {
					result += df.format(qNetwork.PI[i][t][k]) + "\t";
				}
				result += "\n";
			}
		}
		jTextAreaPI.setText(result);

		result = "";
		for (int i = 0; i <= qNetwork.L; ++i) {
			for (int t = 0; t < qNetwork.T; ++t) {
				result += df.format(qNetwork.charN[i][t]) + "\t";
			}
			result += "\n";
		}
		jTextAreaN.setText(result);

		result = "";
		for (int i = 0; i <= qNetwork.L; ++i) {
			for (int t = 0; t < qNetwork.T; ++t) {
				result += df.format(qNetwork.charLambda[i][t]) + "\t";
			}
			result += "\n";
		}
		jTextAreaLambda.setText(result);

		result = "";
		for (int i = 0; i <= qNetwork.L; ++i) {
			for (int t = 0; t < qNetwork.T; ++t) {
				result += df.format(qNetwork.charPsi[i][t]) + "\t";
			}
			result += "\n";
		}
		jTextAreaPsi.setText(result);
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ResultFrame().setVisible(true);
			}
		});
	}
	
	private JTextArea jTextAreaLambda;
	private JTextArea jTextAreaN;
	private JTextArea jTextAreaPI;
	private JTextArea jTextAreaPsi;
}
