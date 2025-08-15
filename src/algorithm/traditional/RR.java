package algorithm.traditional;

import model.Process;
import java.util.*;

public class RR {

    public static void schedule(List<Process> processes, int timeQuantum) {
        int n = processes.size();
        int currentTime = 0;
        int completed = 0;

        int[] remainingBurst = new int[n];
        for (int i = 0; i < n; i++) {
            remainingBurst[i] = processes.get(i).getBurstTime();
        }

        Queue<Integer> readyQueue = new LinkedList<>();
        boolean[] isInQueue = new boolean[n];
        int[] completionTime = new int[n];

        // Start by adding processes which arrive at time 0
        for (int i = 0; i < n; i++) {
            if (processes.get(i).getArrivalTime() == 0) {
                readyQueue.add(i);
                isInQueue[i] = true;
            }
        }

        while (completed < n) {
            if (readyQueue.isEmpty()) {
                currentTime++;
                for (int i = 0; i < n; i++) {
                    if (!isInQueue[i] && remainingBurst[i] > 0 && processes.get(i).getArrivalTime() <= currentTime) {
                        readyQueue.add(i);
                        isInQueue[i] = true;
                    }
                }
                continue;
            }

            int idx = readyQueue.poll();
            Process p = processes.get(idx);

            int execTime = Math.min(timeQuantum, remainingBurst[idx]);

            remainingBurst[idx] -= execTime;
            currentTime += execTime;

            // Add newly arrived processes to the queue
            for (int i = 0; i < n; i++) {
                if (!isInQueue[i] && remainingBurst[i] > 0 && processes.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(i);
                    isInQueue[i] = true;
                }
            }

            if (remainingBurst[idx] > 0) {
                readyQueue.add(idx); // Process not finished, add back to queue
            } else {
                completed++;
                completionTime[idx] = currentTime;

                int tat = completionTime[idx] - p.getArrivalTime();
                int wt = tat - p.getBurstTime();

                p.setCompletionTime(completionTime[idx]);
                p.setTurnaroundTime(tat);
                p.setWaitingTime(wt);
            }
        }
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nRound Robin Scheduling Results:");
        System.out.println("PID\tAT\tBT\tCT\tTAT\tWT");

        int totalWT = 0;
        int totalTAT = 0;

        for (Process p : processes) {
            System.out.println(p.getPid() + "\t" +
                               p.getArrivalTime() + "\t" +
                               p.getBurstTime() + "\t" +
                               p.getCompletionTime() + "\t" +
                               p.getTurnaroundTime() + "\t" +
                               p.getWaitingTime());

            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        float avgWT = (float) totalWT / processes.size();
        float avgTAT = (float) totalTAT / processes.size();

        System.out.printf("Average Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT);
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        List<Process> processList = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            System.out.println("Enter details for Process " + i + ":");
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();

            processList.add(new Process(i, at, bt));
        }

        System.out.print("Enter Time Quantum: ");
        int timeQuantum = sc.nextInt();

        schedule(processList, timeQuantum);
        printResults(processList);

        sc.close();
    }
}
