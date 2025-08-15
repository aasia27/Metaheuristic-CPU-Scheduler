// Required dependencies: JFreeChart (add jar to classpath or use Maven)

package gui;

import algorithm.traditional.*;
import algorithm.metaheuristic.*;
import model.Process;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField arrivalField, burstField, priorityField;
    private JButton addButton, runButton, compareButton;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> algorithmComboBox;
    private JTextArea resultArea;

    private JTextField numAntsField, maxIterField, alphaField, betaField, evaporationField;
    private JTextField initialTempField, finalTempField, alphaSAField;

    private int pidCounter = 1;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        JPanel processInputPanel = new JPanel();
        processInputPanel.setBorder(BorderFactory.createTitledBorder("Add Process"));

        processInputPanel.add(new JLabel("Arrival Time:"));
        arrivalField = new JTextField(5);
        processInputPanel.add(arrivalField);

        processInputPanel.add(new JLabel("Burst Time:"));
        burstField = new JTextField(5);
        processInputPanel.add(burstField);

        processInputPanel.add(new JLabel("Priority:"));
        priorityField = new JTextField(5);
        processInputPanel.add(priorityField);

        addButton = new JButton("Add Process");
        processInputPanel.add(addButton);

        inputPanel.add(processInputPanel);

        JPanel algoPanel = new JPanel();
        algoPanel.setBorder(BorderFactory.createTitledBorder("Select Algorithm"));

        String[] algorithms = {"FCFS", "SJF", "RR", "Priority (Non-Preemptive)", "Priority (Preemptive)", "ACO", "SA", "GA"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algoPanel.add(algorithmComboBox);

        algoPanel.add(new JLabel("Quantum (for RR):"));
        JTextField quantumField = new JTextField(5);
        algoPanel.add(quantumField);

        inputPanel.add(algoPanel);

        JPanel paramPanel = new JPanel(new GridLayout(2, 1));
        paramPanel.setBorder(BorderFactory.createTitledBorder("Metaheuristic Parameters"));

        JPanel acoPanel = new JPanel();
        acoPanel.setBorder(BorderFactory.createTitledBorder("ACO Parameters"));
        numAntsField = new JTextField("10", 5);
        maxIterField = new JTextField("100", 5);
        alphaField = new JTextField("1.0", 5);
        betaField = new JTextField("2.0", 5);
        evaporationField = new JTextField("0.5", 5);
        acoPanel.add(new JLabel("Num Ants:")); acoPanel.add(numAntsField);
        acoPanel.add(new JLabel("Max Iterations:")); acoPanel.add(maxIterField);
        acoPanel.add(new JLabel("Alpha:")); acoPanel.add(alphaField);
        acoPanel.add(new JLabel("Beta:")); acoPanel.add(betaField);
        acoPanel.add(new JLabel("Evaporation Rate:")); acoPanel.add(evaporationField);
        paramPanel.add(acoPanel);

        JPanel saPanel = new JPanel();
        saPanel.setBorder(BorderFactory.createTitledBorder("SA Parameters"));
        initialTempField = new JTextField("1000", 5);
        finalTempField = new JTextField("1", 5);
        alphaSAField = new JTextField("0.95", 5);
        saPanel.add(new JLabel("Initial Temp:")); saPanel.add(initialTempField);
        saPanel.add(new JLabel("Final Temp:")); saPanel.add(finalTempField);
        saPanel.add(new JLabel("Alpha:")); saPanel.add(alphaSAField);
        paramPanel.add(saPanel);

        inputPanel.add(paramPanel);
        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"PID", "Arrival Time", "Burst Time", "Priority"}, 0);
        processTable = new JTable(tableModel);
        add(new JScrollPane(processTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        runButton = new JButton("Run Scheduling");
        compareButton = new JButton("Compare Algorithms");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.add(compareButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(this::handleAddProcess);
        runButton.addActionListener(e -> handleRunScheduling(quantumField));
        compareButton.addActionListener(e -> compareAllAlgorithms());

        algorithmComboBox.addActionListener(e -> {
            String selected = (String) algorithmComboBox.getSelectedItem();
            priorityField.setEnabled(selected.contains("Priority"));
        });

        setVisible(true);
    }

    private void handleAddProcess(ActionEvent e) {
        try {
            int arrival = Integer.parseInt(arrivalField.getText().trim());
            int burst = Integer.parseInt(burstField.getText().trim());
            String algo = (String) algorithmComboBox.getSelectedItem();
            int priority = 0;

            if (algo.contains("Priority")) {
                priority = Integer.parseInt(priorityField.getText().trim());
            }

            tableModel.addRow(new Object[]{pidCounter++, arrival, burst, priority});

            arrivalField.setText("");
            burstField.setText("");
            priorityField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric values.");
        }
    }

    private void handleRunScheduling(JTextField quantumField) {
        List<Process> processes = extractProcesses();

        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add some processes first!");
            return;
        }

        String algo = (String) algorithmComboBox.getSelectedItem();

        try {
            switch (algo) {
                case "FCFS": FCFS.schedule(processes); break;
                case "SJF": SJF.schedule(processes); break;
                case "RR":
                    int quantum = Integer.parseInt(quantumField.getText().trim());
                    RR.schedule(processes, quantum); break;
                case "Priority (Non-Preemptive)": PriorityNonPreemptive.schedule(processes, true); break;
                case "Priority (Preemptive)": PriorityPreemptive.schedule(processes, true); break;
                case "ACO":
                    ACO.schedule(processes, Integer.parseInt(numAntsField.getText().trim()),
                            Integer.parseInt(maxIterField.getText().trim()),
                            Double.parseDouble(alphaField.getText().trim()),
                            Double.parseDouble(betaField.getText().trim()),
                            Double.parseDouble(evaporationField.getText().trim()));
                    break;
                case "SA": SA.schedule(processes); break;
                case "GA": GA.schedule(processes); break;
            }

            displayResults(processes);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void displayResults(List<Process> processes) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-10s %-10s %-10s %-10s %-10s%n",
                "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting"));

        int totalWT = 0, totalTAT = 0;

        for (Process p : processes) {
            sb.append(String.format("%-5d %-10d %-10d %-10d %-10d %-10d%n",
                    p.getPid(), p.getArrivalTime(), p.getBurstTime(),
                    p.getCompletionTime(), p.getTurnaroundTime(), p.getWaitingTime()));
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        double avgWT = (double) totalWT / processes.size();
        double avgTAT = (double) totalTAT / processes.size();

        sb.append(String.format("%nAverage Waiting Time   : %.2f%n", avgWT));
        sb.append(String.format("Average Turnaround Time: %.2f%n", avgTAT));
        resultArea.setText(sb.toString());
    }

    private List<Process> extractProcesses() {
        List<Process> list = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int pid = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
            int arrival = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            int burst = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            int priority = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
            list.add(new Process(pid, arrival, burst, priority));
        }
        return list;
    }

    private void compareAllAlgorithms() {
        List<Process> base = extractProcesses();
        List<String> algos = new ArrayList<>();
        List<Double> wtList = new ArrayList<>(), tatList = new ArrayList<>();
        String[] names = {"FCFS", "SJF", "RR", "Priority (Non-Preemptive)", "Priority (Preemptive)", "ACO", "SA", "GA"};

        for (String algo : names) {
            List<Process> copy = new ArrayList<>();
            for (Process p : base) copy.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));

            try {
                switch (algo) {
                    case "FCFS": FCFS.schedule(copy); break;
                    case "SJF": SJF.schedule(copy); break;
                    case "RR": RR.schedule(copy, 2); break;
                    case "Priority (Non-Preemptive)": PriorityNonPreemptive.schedule(copy, true); break;
                    case "Priority (Preemptive)": PriorityPreemptive.schedule(copy, true); break;
                    case "ACO": ACO.schedule(copy, 10, 100, 1.0, 2.0, 0.5); break;
                    case "SA": SA.schedule(copy); break;
                    case "GA": GA.schedule(copy); break;
                }
                double avgWT = copy.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
                double avgTAT = copy.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
                algos.add(algo);
                wtList.add(avgWT);
                tatList.add(avgTAT);
            } catch (Exception ignored) {}
        }

        showComparisonChart(algos, wtList, tatList);
    }

    private void showComparisonChart(List<String> algorithms, List<Double> avgWTs, List<Double> avgTATs) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < algorithms.size(); i++) {
            dataset.addValue(avgWTs.get(i), "Avg Waiting Time", algorithms.get(i));
            dataset.addValue(avgTATs.get(i), "Avg Turnaround Time", algorithms.get(i));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Algorithm Comparison", "Algorithm", "Time (ms)", dataset);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // Create a table for summary
        String[] columnNames = {"Algorithm", "Avg Waiting Time", "Avg Turnaround Time"};
        Object[][] tableData = new Object[algorithms.size()][3];

        // For deciding best algorithm by combined metric
        double minScore = Double.MAX_VALUE;
        String bestAlgo = "";

        for (int i = 0; i < algorithms.size(); i++) {
            tableData[i][0] = algorithms.get(i);
            tableData[i][1] = String.format("%.2f", avgWTs.get(i));
            tableData[i][2] = String.format("%.2f", avgTATs.get(i));

            double score = 0.5 * avgWTs.get(i) + 0.5 * avgTATs.get(i);  // weights can be changed
            if (score < minScore) {
                minScore = score;
                bestAlgo = algorithms.get(i);
            }
        }

        JTable summaryTable = new JTable(tableData, columnNames);
        JScrollPane tableScroll = new JScrollPane(summaryTable);
        tableScroll.setPreferredSize(new Dimension(800, 150));

        // Panel to combine chart and table
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(chartPanel, BorderLayout.NORTH);
        combinedPanel.add(tableScroll, BorderLayout.CENTER);

        // Add label for best algorithm
        JLabel bestAlgoLabel = new JLabel("Best Algorithm (based on weighted WT and TAT): " + bestAlgo);
        bestAlgoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bestAlgoLabel.setFont(new Font("Serif", Font.BOLD, 16));
        combinedPanel.add(bestAlgoLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, combinedPanel, "Performance Comparison Summary", JOptionPane.PLAIN_MESSAGE);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}
