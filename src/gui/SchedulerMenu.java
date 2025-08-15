package gui;

import algorithm.traditional.FCFS;
import algorithm.traditional.SJF;
import algorithm.traditional.RR;
import algorithm.traditional.PriorityPreemptive;
import algorithm.traditional.PriorityNonPreemptive;

import model.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SchedulerMenu {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nChoose Scheduling Algorithm:");
            System.out.println("1. First Come First Serve (FCFS)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Round Robin (RR)");
            System.out.println("4. Priority (Non-Preemptive)");
            System.out.println("5. Priority (Preemptive)");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            if (choice == 6) {
                System.out.println("Exiting program...");
                break;
            }

            System.out.print("Enter number of processes: ");
            int n = sc.nextInt();

            List<Process> processList = new ArrayList<>();

            boolean needsPriority = (choice == 4 || choice == 5);
            boolean lowerNumberHigherPriority = true; // default

            if (needsPriority) {
                System.out.println("How do you want to set priority?");
                System.out.println("1. Lower number = Higher priority");
                System.out.println("2. Higher number = Higher priority");
                System.out.print("Enter choice: ");
                int priorityType = sc.nextInt();
                lowerNumberHigherPriority = (priorityType == 1);
            }

            for (int i = 1; i <= n; i++) {
                System.out.println("Enter details for Process " + i + ":");
                System.out.print("Arrival Time: ");
                int at = sc.nextInt();
                System.out.print("Burst Time: ");
                int bt = sc.nextInt();

                if (needsPriority) {
                    System.out.print("Priority: ");
                    int pr = sc.nextInt();
                    processList.add(new Process(i, at, bt, pr));
                } else {
                    processList.add(new Process(i, at, bt));
                }
            }

            switch (choice) {
                case 1:
                    FCFS.schedule(processList);
                    FCFS.printResults(processList);
                    break;

                case 2:
                    SJF.schedule(processList);
                    SJF.printResults(processList);
                    break;

                case 3:
                    System.out.print("Enter Time Quantum: ");
                    int tq = sc.nextInt();
                    RR.schedule(processList, tq);
                    RR.printResults(processList);
                    break;

                case 4:
                    PriorityNonPreemptive.schedule(processList, lowerNumberHigherPriority);
                    PriorityNonPreemptive.printResults(processList);
                    break;

                case 5:
                    PriorityPreemptive.schedule(processList, lowerNumberHigherPriority);
                    PriorityPreemptive.printResults(processList);
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }

        sc.close();
    }
}
