package algorithm.traditional;

import model.Process;
import java.util.*;

public class SJF {

    public static void schedule(List<Process> processes) {
        int n = processes.size();
        int completed = 0;
        int currentTime = 0;
        boolean[] isCompleted = new boolean[n];

        while (completed != n) {
            // Find process with minimum burst time among arrived processes and not completed
            Process shortestProcess = null;
            int minBurst = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.getArrivalTime() <= currentTime && !isCompleted[i]) {
                    if (p.getBurstTime() < minBurst) {
                        minBurst = p.getBurstTime();
                        shortestProcess = p;
                    } else if (p.getBurstTime() == minBurst) {
                        // Tie-breaker by arrival time if needed
                        if (shortestProcess != null && p.getArrivalTime() < shortestProcess.getArrivalTime()) {
                            shortestProcess = p;
                        }
                    }
                }
            }

            if (shortestProcess == null) {
                currentTime++; // no process arrived yet, CPU idle
            } else {
                int idx = processes.indexOf(shortestProcess);

                int startTime = currentTime;
                int completionTime = startTime + shortestProcess.getBurstTime();
                int turnaroundTime = completionTime - shortestProcess.getArrivalTime();
                int waitingTime = turnaroundTime - shortestProcess.getBurstTime();

                shortestProcess.setCompletionTime(completionTime);
                shortestProcess.setTurnaroundTime(turnaroundTime);
                shortestProcess.setWaitingTime(waitingTime);

                currentTime = completionTime;
                isCompleted[idx] = true;
                completed++;
            }
        }
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nSJF Scheduling Results:");
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

        schedule(processList);
        printResults(processList);

        sc.close();
    }
}
