package algorithm.traditional;

import model.Process;
import java.util.*;

public class PriorityNonPreemptive {

    public static void schedule(List<Process> processes, boolean lowerNumberHigherPriority) {
        int n = processes.size();
        int completed = 0;
        int currentTime = 0;
        boolean[] isCompleted = new boolean[n];

        while (completed < n) {
            int selected = -1;
            int bestPriority = lowerNumberHigherPriority ? Integer.MAX_VALUE : Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.getArrivalTime() <= currentTime && !isCompleted[i]) {
                    int priority = p.getPriority();
                    if (lowerNumberHigherPriority) {
                        if (priority < bestPriority) {
                            bestPriority = priority;
                            selected = i;
                        }
                    } else {
                        if (priority > bestPriority) {
                            bestPriority = priority;
                            selected = i;
                        }
                    }
                }
            }

            if (selected == -1) {
                currentTime++;
            } else {
                Process p = processes.get(selected);

                if (currentTime < p.getArrivalTime()) {
                    currentTime = p.getArrivalTime();
                }

                int completionTime = currentTime + p.getBurstTime();
                int turnaroundTime = completionTime - p.getArrivalTime();
                int waitingTime = turnaroundTime - p.getBurstTime();

                p.setCompletionTime(completionTime);
                p.setTurnaroundTime(turnaroundTime);
                p.setWaitingTime(waitingTime);

                currentTime = completionTime;
                isCompleted[selected] = true;
                completed++;
            }
        }
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nPriority (Non-Preemptive) Scheduling Results:");
        System.out.println("PID\tAT\tBT\tPR\tCT\tTAT\tWT");

        int totalWT = 0, totalTAT = 0;

        for (Process p : processes) {
            System.out.println(p.getPid() + "\t" + p.getArrivalTime() + "\t" + p.getBurstTime() + "\t" +
                               p.getPriority() + "\t" + p.getCompletionTime() + "\t" +
                               p.getTurnaroundTime() + "\t" + p.getWaitingTime());
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
        }

        float avgWT = (float) totalWT / processes.size();
        float avgTAT = (float) totalTAT / processes.size();

        System.out.printf("Average Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT);
    }
}
