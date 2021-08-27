import java.util.Scanner;

public class FractionCalculatorCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Fraction Calculator,"
                + "please enter an expression or \"quit\" to exit");
        System.out.print("? ");
        String input = scanner.nextLine();

        while (!input.equalsIgnoreCase("quit")) {
            FractionCalculator calculator = new FractionCalculator();
            if (!calculator.parseExpression(input)) {
                System.out.println("Invalid expression: " + input);
            } else {
                String result = calculator.calculateExpression();
                if (result == null) {
                    System.out.println("Could not calculate expression: " + input);
                } else {
                    System.out.println("= " + result);
                }
            }

            System.out.print("? ");
            input = scanner.nextLine();

        }
    }
}
