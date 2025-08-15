package algorithm.metaheuristic;

import model.Process;
import java.util.*;

public class GA {

    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.1;

    public static void schedule(List<Process> originalProcesses) {
        List<List<Process>> population = initializePopulation(originalProcesses);

        List<Process> bestSchedule = null;
        int bestFitness = Integer.MAX_VALUE;

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate population
            List<List<Process>> newPopulation = new ArrayList<>();
            for (int i = 0; i < POPULATION_SIZE; i++) {
                List<Process> parent1 = select(population);
                List<Process> parent2 = select(population);
                List<Process> child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }
            population = newPopulation;

            // Find best in current population
            for (List<Process> schedule : population) {
                int fitness = calculateFitness(schedule);
                if (fitness < bestFitness) {
                    bestFitness = fitness;
                    bestSchedule = deepCopy(schedule);
                }
            }
        }

        // Assign completion, turnaround, and waiting times
        applyTiming(bestSchedule);

        // Replace original list
        originalProcesses.clear();
        originalProcesses.addAll(bestSchedule);
    }

    private static List<List<Process>> initializePopulation(List<Process> original) {
        List<List<Process>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Process> shuffled = deepCopy(original);
            Collections.shuffle(shuffled);
            population.add(shuffled);
        }
        return population;
    }

    private static List<Process> select(List<List<Process>> population) {
        // Tournament selection
        Random rand = new Random();
        List<Process> best = null;
        int bestFitness = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            List<Process> candidate = population.get(rand.nextInt(POPULATION_SIZE));
            int fitness = calculateFitness(candidate);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                best = candidate;
            }
        }
        return deepCopy(best);
    }

    private static List<Process> crossover(List<Process> parent1, List<Process> parent2) {
        // Order crossover (OX)
        int size = parent1.size();
        Random rand = new Random();
        int start = rand.nextInt(size);
        int end = rand.nextInt(size - start) + start;

        Set<Integer> pids = new HashSet<>();
        List<Process> child = new ArrayList<>(Collections.nCopies(size, null));
        for (int i = start; i < end; i++) {
            child.set(i, parent1.get(i));
            pids.add(parent1.get(i).getPid());
        }

        int idx = 0;
        for (Process p : parent2) {
            if (!pids.contains(p.getPid())) {
                while (child.get(idx) != null) idx++;
                child.set(idx, p);
            }
        }

        return child;
    }

    private static void mutate(List<Process> individual) {
        Random rand = new Random();
        if (rand.nextDouble() < MUTATION_RATE) {
            int i = rand.nextInt(individual.size());
            int j = rand.nextInt(individual.size());
            Collections.swap(individual, i, j);
        }
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
        System.out.println("\nGA Scheduling Results:");
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
