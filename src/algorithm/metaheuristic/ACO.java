package algorithm.metaheuristic;

import model.Process;
import java.util.*;

public class ACO {

    private static class Ant {
        List<Process> tour = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int totalTime = 0;

        void visit(Process p) {
            tour.add(p);
            visited.add(p.getPid());
            totalTime += p.getBurstTime();
        }

        boolean visited(Process p) {
            return visited.contains(p.getPid());
        }
    }

    public static void schedule(List<Process> processes, int numAnts, int maxIterations,
                                double alpha, double beta, double evaporationRate) {
        int n = processes.size();

        // Initialize pheromone matrix with small positive values
        double[][] pheromone = new double[n][n];
        for (int i = 0; i < n; i++)
            Arrays.fill(pheromone[i], 1.0);

        // Precompute heuristic info: 1 / burstTime for each pair (simplified)
        double[][] heuristic = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    heuristic[i][j] = 1.0 / processes.get(j).getBurstTime();
                } else {
                    heuristic[i][j] = 0.0;
                }
            }
        }

        List<Process> bestSchedule = null;
        int bestMakespan = Integer.MAX_VALUE;

        Random random = new Random();

        for (int iter = 0; iter < maxIterations; iter++) {
            List<Ant> ants = new ArrayList<>();

            for (int k = 0; k < numAnts; k++) {
                Ant ant = new Ant();

                // Start from a random process
                int currentIndex = random.nextInt(n);
                ant.visit(processes.get(currentIndex));

                // Build a tour
                while (ant.tour.size() < n) {
                    int lastIndex = currentIndex;

                    // Calculate probabilities for next node
                    double[] prob = new double[n];
                    double sum = 0.0;

                    for (int j = 0; j < n; j++) {
                        if (!ant.visited(processes.get(j))) {
                            prob[j] = Math.pow(pheromone[lastIndex][j], alpha) *
                                      Math.pow(heuristic[lastIndex][j], beta);
                            sum += prob[j];
                        } else {
                            prob[j] = 0.0;
                        }
                    }

                    // Roulette wheel selection
                    double r = random.nextDouble() * sum;
                    double cumulative = 0.0;
                    int nextIndex = -1;
                    for (int j = 0; j < n; j++) {
                        cumulative += prob[j];
                        if (cumulative >= r) {
                            nextIndex = j;
                            break;
                        }
                    }
                    if (nextIndex == -1) {
                        // fallback: pick first unvisited
                        for (int j = 0; j < n; j++) {
                            if (!ant.visited(processes.get(j))) {
                                nextIndex = j;
                                break;
                            }
                        }
                    }

                    ant.visit(processes.get(nextIndex));
                    currentIndex = nextIndex;
                }
                ants.add(ant);
            }

            // Find best ant in this iteration (min total burst time schedule)
            for (Ant ant : ants) {
                int makespan = ant.totalTime; // sum of burst times in order, simplified
                if (makespan < bestMakespan) {
                    bestMakespan = makespan;
                    bestSchedule = new ArrayList<>(ant.tour);
                }
            }

            // Evaporate pheromones
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    pheromone[i][j] *= (1 - evaporationRate);
                    if (pheromone[i][j] < 0.0001)
                        pheromone[i][j] = 0.0001; // lower bound to pheromone
                }
            }

            // Deposit pheromones by best ant
            for (int i = 0; i < bestSchedule.size() - 1; i++) {
                int from = processes.indexOf(bestSchedule.get(i));
                int to = processes.indexOf(bestSchedule.get(i + 1));
                pheromone[from][to] += 1.0 / bestMakespan;
            }
        }

        // Calculate completion, turnaround, waiting times for bestSchedule
        int currentTime = 0;
        for (Process p : bestSchedule) {
            if (currentTime < p.getArrivalTime())
                currentTime = p.getArrivalTime();

            int completionTime = currentTime + p.getBurstTime();
            int turnaroundTime = completionTime - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();

            p.setCompletionTime(completionTime);
            p.setTurnaroundTime(turnaroundTime);
            p.setWaitingTime(waitingTime);

            currentTime = completionTime;
        }

        // Replace the original list order with bestSchedule order
        processes.clear();
        processes.addAll(bestSchedule);
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nACO Scheduling Results:");
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
