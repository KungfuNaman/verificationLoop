import java.util.Scanner;

public class NaturalSum {
    public static void main(String[] args) {
        // Potential resource leak; scanner is never closed.
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of terms: ");
        int n = scanner.nextInt();

        if (n <= 0) {
            System.out.println("The number of terms must be positive.");
            return;
        }

        int sum = sumOfNatural(n);
        System.out.println("Sum of the first " + n + " natural numbers is: " + sum);
    }

    public static int sumOfNatural(int n) {
        // Potential division by zero if n is negative or zero, 
        // but we've added a check in the main method.
        return n * (n + 1) / 2;
    }
}
