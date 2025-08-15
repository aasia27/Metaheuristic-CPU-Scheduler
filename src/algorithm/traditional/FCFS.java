package algorithm.traditional;

import model.Process;
import java.util.*;

public class FCFS {

    public static void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        for (Process p : processes) {
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
        }
    }

    public static void printResults(List<Process> processes) {
        System.out.println("\nFCFS Scheduling Results:");
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
