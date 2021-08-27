public class FractionCalculator {
    private enum Operator {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION
    }

    private final int[] operandWhole = new int[2];
    private final int[] operandNumerator= new int[2];
    private final int[] operandDenominator= new int[2];
    private Operator operator;
    private int resultWhole;
    private int resultNumerator;
    private int resultDenominator;

    public boolean parseExpression(String expression) {
        if (expression.isEmpty()) {
            System.err.println("Empty expression passed to parseExpression()!");
            return false;
        }

        String[] expressionParts = expression.split(" ");
        if (expressionParts.length != 3) {
            System.err.println("Input is not a valid expression: " + expression);
            return false;
        }

        if (!parseFraction(expressionParts[0], 0)) {
            System.err.println("Could not parse first fraction from input: " + expressionParts[0]);
            return false;
        }
        if (!parseFraction(expressionParts[2], 1)) {
            System.err.println("Could not parse second fraction from input: " + expressionParts[2]);
            return false;
        }

        switch (expressionParts[1]) {
            case "+" -> operator = Operator.ADDITION;
            case "-" -> operator = Operator.SUBTRACTION;
            case "*" -> operator = Operator.MULTIPLICATION;
            case "/" -> operator = Operator.DIVISION;
            default -> {
                System.err.println("Invalid operator: " + expressionParts[1]);
                return false;
            }
        }

        return true;
    }

    public String calculateExpression() {
        switch (operator) {
            case ADDITION -> {
                return performAddition();
            }
            case SUBTRACTION -> {
                return performSubtraction();
            }
            case MULTIPLICATION -> {
                return performMultiplication();
            }
            case DIVISION -> {
                return performDivision();
            }
        }

        System.err.println("Invalid Operand in expression!");
        return null;
    }

    private boolean parseFraction(String fraction, int operandNum) {
        if (operandNum < 0 || operandNum > 1) {
            System.err.println("Invalid operandNum in parseFraction, should be 0 or 1: "
                    + operandNum);
            return false;
        }

        String[] fractionPartsAroundUnderscore = fraction.split("_");
        if (fractionPartsAroundUnderscore.length > 2) {
            System.err.println("Too many underscores in fraction: " + fraction);
            return false;
        }

        try {
            if (fractionPartsAroundUnderscore.length > 1) {
                operandWhole[operandNum] = Integer.parseInt(fractionPartsAroundUnderscore[0]);
                parseFractionalPartPlusWholeIfNecessary(fractionPartsAroundUnderscore[1],
                        operandNum, false);
            } else {
                parseFractionalPartPlusWholeIfNecessary(fractionPartsAroundUnderscore[0],
                        operandNum, true);
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Could not parse Integer from fraction: " + fraction);
            return false;
        }

        return true;
    }

    private boolean parseFractionalPartPlusWholeIfNecessary(String fraction, int operandNum,
                                                            boolean needToParseWholeNumber) {
        String[] fractionPartsAroundSlash = fraction.split("/");
        if (fractionPartsAroundSlash.length > 2) {
            System.err.println("Too many slashes in fraction: " + fraction);
            return false;
        }
        if (fractionPartsAroundSlash.length == 1) {
            if (!needToParseWholeNumber) {
                System.err.println("Slash or denominator was not found in fraction: "
                        + fraction);
                return false;
            }

            operandWhole[operandNum] = Integer.parseInt(fraction);
            return true;
        }

        operandNumerator[operandNum] = Integer.parseInt(fractionPartsAroundSlash[0]);
        if (operandNumerator[operandNum] < 0 && operandWhole[operandNum] != 0) {
            System.err.println("Negative numerator with a non-zero whole part not allowed: "
                    + fraction);
            return false;
        }
        operandDenominator[operandNum] = Integer.parseInt(fractionPartsAroundSlash[1]);
        if (operandDenominator[operandNum] <= 0) {
            System.err.println("Negative or zero denominator not allowed: " + fraction);
            return false;
        }

        return true;
    }

    private String performAddition() {
        resultWhole = operandWhole[0] + operandWhole[1];
        if (operandDenominator[0] == operandDenominator[1]) {
            resultNumerator = operandNumerator[0] + operandNumerator[1];
            resultDenominator = operandDenominator[0];
        } else {
            resultNumerator = operandNumerator[0] * Math.max(operandDenominator[1], 1)
                    + operandNumerator[1] * Math.max(operandDenominator[0], 1);
            resultDenominator = Math.max(operandDenominator[0], 1)
                    * Math.max(operandDenominator[1], 1);
        }

        simplifyResult();
        return getResult();
    }

    private String performSubtraction() {
        if (operandWhole[0] != 0 && !convertMixedToImproperFraction(0)) {
            System.err.println("Could not convert fraction 1 in expression to an improper fraction"
                    + "when subtracting!");
            return null;
        }
        if (operandWhole[1] != 0 && !convertMixedToImproperFraction(1)) {
            System.err.println("Could not convert fraction 2 in expression to an improper fraction"
                    + "when subtracting!");
            return null;
        }

        if (operandDenominator[0] == operandDenominator[1]) {
            resultNumerator = operandNumerator[0] - operandNumerator[1];
            resultDenominator = operandDenominator[0];
        } else {
            resultNumerator = operandNumerator[0] * operandDenominator[1]
                    - operandNumerator[1] * operandDenominator[0];
            resultDenominator = operandDenominator[0] * operandDenominator[1];
        }

        simplifyResult();
        return getResult();
    }

    private String performMultiplication() {
        if (operandWhole[0] != 0 && !convertMixedToImproperFraction(0)) {
            System.err.println("Could not convert fraction 1 in expression to an improper fraction"
                    + "when multiplying!");
            return null;
        }
        if (operandWhole[1] != 0 && !convertMixedToImproperFraction(1)) {
            System.err.println("Could not convert fraction 2 in expression to an improper fraction"
                    + "when multiplying!");
            return null;
        }

        resultNumerator = operandNumerator[0] * operandNumerator[1];
        resultDenominator = operandDenominator[0] * operandDenominator[1];

        simplifyResult();
        return getResult();
    }

    private String performDivision() {
        if (operandWhole[1] != 0 && !convertMixedToImproperFraction(1)) {
            System.err.println("Could not convert fraction 2 in expression to an improper fraction"
                    + "when dividing!");
        }

        int tmpNumerator = operandNumerator[1];
        operandNumerator[1] = operandDenominator[1];
        operandDenominator[1] = Math.abs(tmpNumerator);
        if (tmpNumerator < 0) {
            operandNumerator[1] = -operandNumerator[1];
        }

        return performMultiplication();
    }

    private boolean convertMixedToImproperFraction(int operandNum) {
        if (operandNum < 0 || operandNum > 1) {
            System.err.println("Invalid operandNum in parseFraction, should be 0 or 1: "
                    + operandNum);
            return false;
        }

        if (operandDenominator[operandNum] == 0) {
            operandDenominator[operandNum] = 1;
        }

        operandNumerator[operandNum] += Math.abs(operandWhole[operandNum])
                * operandDenominator[operandNum];
        if (operandWhole[operandNum] < 0) {
            operandNumerator[operandNum] = -operandNumerator[operandNum];
        }
        operandWhole[operandNum] = 0;

        return true;
    }

    private void simplifyResult() {
        if (resultNumerator != 0) {
            resultWhole += resultNumerator / resultDenominator;
            resultNumerator %= resultDenominator;

            int gcd = getGCD(Math.abs(resultNumerator), resultDenominator);
            resultNumerator /= gcd;
            resultDenominator /= gcd;
        }
        if (resultWhole != 0 && resultNumerator < 0) {
            resultNumerator = -resultNumerator;
        }
    }

    private int getGCD(int num1, int num2) {
        if (num2 == 0) {
            return num1;
        }

        return getGCD(num2, num1 % num2);
    }

    private String getResult() {
        StringBuilder result = new StringBuilder();
        if (resultWhole != 0) {
            result.append(resultWhole);
        }
        if (resultNumerator != 0) {
            if (result.length() > 0) {
                result.append('_');
            }
            result.append(resultNumerator);
            result.append('/');
            result.append(resultDenominator);
        }

        return result.toString();
    }
}
