import java.util.Scanner;

public class SubmittedCode {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of terms: ");
        int n = scanner.nextInt();

        int[] fibSequence = fibonacci(n);
        System.out.print("Fibonacci Sequence: ");
        for (int i = 0; i < n; i++) {
            System.out.print(fibSequence[i] + " ");
        }
    }

    public static int[] fibonacci(int n) {
        int[] fibSequence = new int[n];
        fibSequence[0] = 0;
        fibSequence[1] = 1;

        for (int i = 2; i < n; i++) {
            fibSequence[i] = fibSequence[i - 1] + fibSequence[i - 2];
        }
        return fibSequence;
    }
}
