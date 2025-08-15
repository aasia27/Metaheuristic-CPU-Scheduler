package util;

import model.Process;
import algorithm.traditional.FCFS;
import algorithm.traditional.SJF;
import algorithm.traditional.RR;
import algorithm.traditional.PriorityPreemptive;
import algorithm.traditional.PriorityNonPreemptive;

import algorithm.metaheuristic.ACO;
import algorithm.metaheuristic.GA;
import algorithm.metaheuristic.SA;

import java.util.*;

public class SchedulerRunner {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> originalProcesses = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        boolean includePriority = false;
        System.out.print("Do you want to enter priorities for processes? (yes=1 / no=0): ");
        int prChoice = sc.nextInt();
        includePriority = (prChoice == 1);

        boolean lowerNumberHigherPriority = true;
        if (includePriority) {
            System.out.println("How do you want to set priority?");
            System.out.println("1. Lower number = Higher priority");
            System.out.println("2. Higher number = Higher priority");
            System.out.print("Enter choice: ");
            int prDir = sc.nextInt();
            lowerNumberHigherPriority = (prDir == 1);
        }

        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for Process " + (i + 1));
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();
            int priority = 0;
            if (includePriority) {
                System.out.print("Priority: ");
                priority = sc.nextInt();
                originalProcesses.add(new Process(i + 1, at, bt, priority));
            } else {
                originalProcesses.add(new Process(i + 1, at, bt));
            }
        }

        System.out.print("Enter time quantum for Round Robin: ");
        int timeQuantum = sc.nextInt();

        // ACO Parameters
        System.out.println("\nEnter ACO Parameters:");
        System.out.print("Number of Ants: ");
        int numAnts = sc.nextInt();
        System.out.print("Number of Iterations: ");
        int numIterations = sc.nextInt();
        System.out.print("Alpha (pheromone influence): ");
        double alpha = sc.nextDouble();
        System.out.print("Beta (heuristic influence): ");
        double beta = sc.nextDouble();
        System.out.print("Evaporation Rate: ");
        double evaporationRate = sc.nextDouble();

        // To store summary results (algorithm name, avg WT, avg TAT)
        List<ResultSummary> summaries = new ArrayList<>();

        // ======= FCFS =======
        List<Process> fcfsList = deepCopy(originalProcesses, includePriority);
        FCFS.schedule(fcfsList);
        FCFS.printResults(fcfsList);
        summaries.add(new ResultSummary("FCFS", fcfsList));

        // ======= SJF =======
        List<Process> sjfList = deepCopy(originalProcesses, includePriority);
        SJF.schedule(sjfList);
        SJF.printResults(sjfList);
        summaries.add(new ResultSummary("SJF", sjfList));

        // ======= RR =======
        List<Process> rrList = deepCopy(originalProcesses, includePriority);
        RR.schedule(rrList, timeQuantum);
        RR.printResults(rrList);
        summaries.add(new ResultSummary("Round Robin", rrList));

        // ======= Priority Non-Preemptive =======
        if (includePriority) {
            List<Process> prNonPreemptList = deepCopy(originalProcesses, true);
            PriorityNonPreemptive.schedule(prNonPreemptList, lowerNumberHigherPriority);
            PriorityNonPreemptive.printResults(prNonPreemptList);
            summaries.add(new ResultSummary("Priority Non-Preemptive", prNonPreemptList));
        }

        // ======= Priority Preemptive =======
        if (includePriority) {
            List<Process> prPreemptList = deepCopy(originalProcesses, true);
            PriorityPreemptive.schedule(prPreemptList, lowerNumberHigherPriority);
            PriorityPreemptive.printResults(prPreemptList);
            summaries.add(new ResultSummary("Priority Preemptive", prPreemptList));
        }

        // ======= ACO =======
        List<Process> acoList = deepCopy(originalProcesses, includePriority);
        ACO.schedule(acoList, numAnts, numIterations, alpha, beta, evaporationRate);
        ACO.printResults(acoList);
        summaries.add(new ResultSummary("ACO", acoList));

        // ======= GA =======
        List<Process> gaList = deepCopy(originalProcesses, includePriority);
        GA.schedule(gaList);
        GA.printResults(gaList);
        summaries.add(new ResultSummary("GA", gaList));

        // ======= SA =======
        List<Process> saList = deepCopy(originalProcesses, includePriority);
        SA.schedule(saList);
        SA.printResults(saList);
        summaries.add(new ResultSummary("SA", saList));

        // Print comparison table
        System.out.println("\n================ Scheduling Algorithms Comparison ================");
        System.out.printf("%-25s %-20s %-20s\n", "Algorithm", "Avg Waiting Time", "Avg Turnaround Time");
        System.out.println("-----------------------------------------------------------------");

        ResultSummary best = null;
        for (ResultSummary res : summaries) {
            System.out.printf("%-25s %-20.2f %-20.2f\n", res.algorithmName, res.avgWaitingTime, res.avgTurnaroundTime);
            if (best == null || res.avgWaitingTime < best.avgWaitingTime) {
                best = res;
            }
        }

        System.out.println("-----------------------------------------------------------------");
        System.out.println("Best Performing Algorithm (lowest Avg Waiting Time): " + best.algorithmName);

        sc.close();
    }

    // Updated deepCopy to handle priority
    private static List<Process> deepCopy(List<Process> original, boolean withPriority) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            if (withPriority)
                copy.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
            else
                copy.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime()));
        }
        return copy;
    }

    // Helper class to store summary of results
    static class ResultSummary {
        String algorithmName;
        float avgWaitingTime;
        float avgTurnaroundTime;

        ResultSummary(String name, List<Process> processes) {
            this.algorithmName = name;
            int totalWT = 0, totalTAT = 0;
            for (Process p : processes) {
                totalWT += p.getWaitingTime();
                totalTAT += p.getTurnaroundTime();
            }
            this.avgWaitingTime = (float) totalWT / processes.size();
            this.avgTurnaroundTime = (float) totalTAT / processes.size();
        }
    }
}
