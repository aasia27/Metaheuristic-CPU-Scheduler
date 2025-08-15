package algorithm.metaheuristic;

import model.Process;
import java.util.*;

public class SA {

    private static final double INITIAL_TEMPERATURE = 1000;
    private static final double FINAL_TEMPERATURE = 1;
    private static final double ALPHA = 0.95;
    private static final int ITERATIONS_PER_TEMP = 100;

    public static void schedule(List<Process> originalProcesses) {
        List<Process> currentSolution = deepCopy(originalProcesses);
        Collections.shuffle(currentSolution);
        int currentFitness = calculateFitness(currentSolution);

        List<Process> bestSolution = deepCopy(currentSolution);
        int bestFitness = currentFitness;

        double temperature = INITIAL_TEMPERATURE;

        while (temperature > FINAL_TEMPERATURE) {
            for (int i = 0; i < ITERATIONS_PER_TEMP; i++) {
                List<Process> neighbor = generateNeighbor(currentSolution);
                int neighborFitness = calculateFitness(neighbor);

                if (acceptanceProbability(currentFitness, neighborFitness, temperature) > Math.random()) {
                    currentSolution = neighbor;
                    currentFitness = neighborFitness;

                    if (currentFitness < bestFitness) {
                        bestSolution = deepCopy(currentSolution);
                        bestFitness = currentFitness;
                    }
                }
            }
            temperature *= ALPHA;
        }

        applyTiming(bestSolution);

        originalProcesses.clear();
        originalProcesses.addAll(bestSolution);
    }

    private static double acceptanceProbability(int currentFitness, int newFitness, double temperature) {
        if (newFitness < currentFitness) return 1.0;
        return Math.exp((currentFitness - newFitness) / temperature);
    }

    private static List<Process> generateNeighbor(List<Process> solution) {
        List<Process> neighbor = deepCopy(solution);
        int i = new Random().nextInt(neighbor.size());
        int j = new Random().nextInt(neighbor.size());
        Collections.swap(neighbor, i, j);
        return neighbor;
    }

    private static int calculateFitness(List<Process> schedule) {
        int currentTime = 0;
        int totalTurnaround = 0;

        for (Process p : schedule) {
            if (currentTime < p.getArrivalTime()) currentTime = p.getArrivalTime();
            currentTime += p.getBurstTime();
            totalTurnaround += (currentTime - p.getArrivalTime());
        }

        return totalTurnaround;
    }

    private static void applyTiming(List<Process> processes) {
        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.getArrivalTime()) currentTime = p.getArrivalTime();
            int ct = currentTime + p.getBurstTime();
            p.setCompletionTime(ct);
            p.setTurnaroundTime(ct - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
            currentTime = ct;
        }
    }

    private static List<Process> deepCopy(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime()));
        }
        return copy;
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nSA Scheduling Results:");
        System.out.println("PID\tAT\tBT\tCT\tTAT\tWT");

        int totalWT  = 0;
        int totalTAT = 0;

        for (Process p : processes) {
            System.out.println(p.getPid() + "\t" +
                               p.getArrivalTime() + "\t" +
                               p.getBurstTime()  + "\t" +
                               p.getCompletionTime() + "\t" +
                               p.getTurnaroundTime() + "\t" +
                               p.getWaitingTime());

            totalWT  += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        double avgWT  = (double) totalWT  / processes.size();
        double avgTAT = (double) totalTAT / processes.size();

        System.out.printf("Average Waiting Time   : %.2f%n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f%n", avgTAT);
    }
}
