package dr.magicalstoneex.takeattention;

import java.util.Arrays;
import java.util.Scanner;

public class TakeAttention {

    private static String[] students;
    /**The first dimension means every students and the second dimension means every time taking attention.
     *
     */
    private static boolean[][] attentionStatus;
    /**In seconds.
     *
     */
    private static int classLength;
    /**Times taking attention.
     *
     */
    private static int times;
    /**The time each student has to input. In seconds.
     *
     */
    private static int timeToWait;
    /**The time this program will take attention.
     * It is random and different every class.
     */
    private static int[] timeToTake;

    /**
     * The method which performs this program's logic.
     * @param args
     */
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        readProperties(input);

        generateRandomTime();

        printProperties();

        takeAttentionTimer(input);

        report();
    }

    private static void generateRandomTime(){
        timeToTake = new int[times];
        double[] weights = new double[times];
        double sum = 0;
        for(int i = 0; i < times; i++){
            double weight = Math.random();
            weights[i] = weight;
            sum += weight;
        }
        sum += Math.random();
        int lastTime = 0;
        double denominator = classLength / sum;
        for(int i = 0; i < times; i++){
            int currentTime = lastTime + (int) (weights[i] * denominator);
            if(currentTime <= classLength) {
                timeToTake[i] = currentTime;
                lastTime = currentTime;
            }
        }
    }

    private static void printProperties(){
        int[] timeFormat = timeFormat(classLength);
        System.out.printf("The students' list is %s.%n", Arrays.toString(students))
                .printf("The class will continue for %d hours %d minutes and %d seconds.%n",
                        timeFormat[0], timeFormat[1], timeFormat[2])
                .printf("Blackboard will take attention %d times at ", times);
        for(int time : timeToTake){
            int[] formattedTime = timeFormat(time);
            int hours = formattedTime[0];
            int minutes = formattedTime[1];
            int seconds = formattedTime[2];
            System.out.printf("%d:%d:%d ", hours, minutes, seconds);
        }
        System.out.println();
    }

    public static int[] timeFormat(int seconds){
        int[] time = new int[3];
        time[0] = seconds / 3600;
        time[1] = seconds % 3600 / 60;
        time[2] = seconds % 60;
        return time;
    }

    private static void readProperties(Scanner input){

        System.out.println("Input students' names. Input an empty line to stop.");
        readNames(input, 0);

        System.out.println("Input the class's length with the format hh:mm:ss or hh:mm.");
        String length = input.next();
        String[] time = length.split(":");
        int hours = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);
        int seconds;
        if(time.length >= 3){
            seconds = Integer.parseInt(time[2]);
        } else {
            seconds = 0;
        }
        classLength = seconds + 60 * (minutes + 60 * hours);

        System.out.println("How many times would you like to take attention?");
        times = input.nextInt();

        System.out.println("How long can students respond to this program?");
        timeToWait = input.nextInt();
        attentionStatus = new boolean[students.length][times];
    }

    private static void readNames(Scanner input, int times){
        String name = input.nextLine();
        if ("".equals(name)) {
            students = new String[times];
        } else {
            readNames(input, times + 1);
            students[times] = name;
        }
    }

    private static void takeAttentionTimer(Scanner input){
        long lastTime = System.currentTimeMillis();
        try {
            for(int i = 0; i < times; i++){
                int millis = timeToTake[i] * 1000;
                int timeToSleep = (int) (millis - System.currentTimeMillis() + lastTime);
                if (timeToSleep >= 0) {
                    Thread.sleep(timeToSleep);
                    lastTime += timeToSleep;
                    takeAttention(input, i);
                } else {
                    for(int j = 0; j < attentionStatus.length; j ++){
                        attentionStatus[j][i] = true;
                    }
                    lastTime = System.currentTimeMillis();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void takeAttention(Scanner input, int times) {
        for(int i = 0; i < students.length; i ++){
            int number = (int) (Math.random() * 10000) + 1;
            System.out.printf("Hi %s, you have to input the number %d to show you are here.", students[i], number)
                    .printf("You have %2d seconds to input.%n", timeToWait);
            long beginning = System.currentTimeMillis();
            while(!input.hasNextInt()){
                input.next();
                System.out.println("You should input a number.");
            }
            while(number != input.nextInt()){
                System.out.println("Incorrect.");
                while(!input.hasNextInt()){
                    input.next();
                }
            }
            if(((System.currentTimeMillis() - beginning) / 1000) <= timeToWait){
                attentionStatus[i][times] = true;
                System.out.println("We took your attention and you can get point.");
            } else {
                System.out.println("We took your attention but you can't get point.");
            }
        }
    }

    private static void report(){
        System.out.print("          Name      |");
        for (int k : timeToTake) {
            int[] format = timeFormat(k);
            System.out.printf(" %02d:%02d:%02d |", format[0], format[1], format[2]);
        }
        System.out.print("    total |  percent |");
        System.out.println();
        for(int i = 0; i < attentionStatus.length; i ++){
            System.out.printf("%20s|", students[i]);
            int total = 0;
            for(int j = 0; j < attentionStatus[i].length; j++){
                if(attentionStatus[i][j]) {
                    System.out.printf("%8s  |", "true");
                    total ++;
                } else {
                    System.out.printf("%8s  |", "false");
                }
            }
            System.out.printf("  %3d:%3d |", total, attentionStatus[i].length);
            System.out.printf("  %6.2f%% |", total * 100.0 / attentionStatus[i].length);
            System.out.println();
        }
    }
}
