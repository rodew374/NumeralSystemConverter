import java.util.Scanner;

/**
 * This NumeralSystem class converts any real number, base 36 or less, to any other base, 36 or less
 * @version     1.20 09 Mar 2021 Version implements exception handling
 * @author      Zac Inman
 */
public class NumeralSystem {

    /**
     * Entry point to program.
     * @param args none
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);       // How the program accepts input
        int sourceRadix = Converter.convertRadix(scanner.next());

        String number = scanner.next();
        Converter.checkNumber(sourceRadix, number);

        int targetRadix = Converter.convertRadix(scanner.next());

        Converter converter = new Converter(sourceRadix, number, targetRadix);

        System.out.println(converter.convert());
    }

    static class Converter {
        private final boolean IS_FRACTION_ZERO;         // True if there is no fractional component
        private final boolean IS_MIN_SOURCE_BASE;       // True if number to be converted is in base 1
        private final boolean IS_MIN_TARGET_BASE;       // True if number is to be converted to base 1
        private final int SOURCE_RADIX;                 // The radix the number to be converted starts in
        private final int TARGET_RADIX;                 // The radix the number will be converted to
        private final String SOURCE_FRACTION;            // String representation of the fraction part to be converted
        private final String SOURCE_INTEGER;             // String representation of the integer part to be converted
        private double decimalFraction;                 // Decimal part of the number
        private int decimalInteger;                     // Integer part of the number
        private String targetNumber;                    // String representation of converted number

        /**
         * Creates a numeral system converter
         * @param sourceRadix   The radix the number to be converted starts in
         * @param sourceNumber  The String representation of the whole number to be converted
         * @param targetRadix   The radix the number will be converted to
         */ 
        public Converter(int sourceRadix, String sourceNumber, int targetRadix) {
            this.SOURCE_RADIX = sourceRadix;
            this.TARGET_RADIX = targetRadix;
            this.IS_MIN_SOURCE_BASE = (sourceRadix == 1);
            this.IS_MIN_TARGET_BASE = (targetRadix == 1);

            String[] sourceArray = formatNumber(sourceNumber);

            if(sourceRadix == 1 || Integer.parseInt(sourceArray[1], sourceRadix) == 0) {
                this.SOURCE_INTEGER = sourceArray[0];
                this.SOURCE_FRACTION = "";
                this.IS_FRACTION_ZERO = true;
            } else {
                this.SOURCE_INTEGER = sourceArray[0];
                this.SOURCE_FRACTION = sourceArray[1];
                this.IS_FRACTION_ZERO = false;
            }
        }

        /**
         * Splits a number in string format into an array consisting of its fractional and integer components
         * @param number in string format
         * @return String[]
         */
        private static String[] formatNumber(String number) {
            if (number.contains("."))  {
                return number.split("\\.");
            } else {
                return new String[] {number, "0"};
            }
        }

        /**
         * Converts radix in String format to int
         * Also check if number is between 1 and 36, inclusive
         * If not, it will print an error message and exit the program
         * @param radix in String format to be converted to int
         * @return int
         */
        public static int convertRadix(String radix) {
            try {
                if(Integer.parseInt(radix) < 1 || Integer.parseInt(radix) > 36) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! Radix: [" + radix + "] must be a number between 1 and 36, inclusive.");
                System.exit(0);
            }

            return Integer.parseInt(radix);
        }

        /**
         * Check if number is valid in the given radix, if not, displays an error message and exits the program
         * @param radix the base system that will be checked
         * @param number number in String format to be checked
         */
        public static void checkNumber(int radix, String number) {
            String[] numberArray = formatNumber(number);

            try {
                if (radix == 1) {
                    if (!numberArray[0].contentEquals("1".repeat(numberArray[0].length()))) {
                        throw new NumberFormatException();
                    }
                } else {
                    Integer.parseInt(numberArray[0], radix);
                    Integer.parseInt(numberArray[1], radix);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! [" + number + "] is not a valid number in the source radix.");
                System.exit(0);
            }
        }

        /**
         * Converts sourceNumber to targetNumber
         * @return String
         */
        public String convert() {
            convertToDecimal();

            convertToTarget();

            return targetNumber;
        }

        /**
         * Converts sourceNumber to decimalNumber
         */
        private void convertToDecimal() {
            this.decimalInteger = IS_MIN_SOURCE_BASE ? convertMinIntegerToDecimal()
                                                        : convertIntegerToDecimal();

            this.decimalFraction = IS_MIN_SOURCE_BASE ? 0 : convertFractionToDecimal();
        }

        /**
         * Converts the sourceNumber fractional component to decimal
         * @return double
         */
        private double convertFractionToDecimal() {
            double tempFraction = 0;

            for (int i = 0; i < SOURCE_FRACTION.length(); i++) {
                tempFraction += Character.getNumericValue(SOURCE_FRACTION.charAt(i))
                        / Math.pow(SOURCE_RADIX, i + 1);
            }

            return tempFraction;
        }

        /**
         * Converts the original integral component to decimal
         * @return int
         */
        private int convertIntegerToDecimal() {
            return Integer.parseInt(SOURCE_INTEGER, SOURCE_RADIX);
        }

        /**
         * Converts the base 1 original number to decimal
         * @return int
         */
        private int convertMinIntegerToDecimal() {
            return SOURCE_INTEGER.length();
        }

        /**
         * Converts decimal number to target base
         */
        private void convertToTarget() {
            this.targetNumber = IS_MIN_TARGET_BASE ? convertIntegerToMin()
                                                    : convertIntegerToTarget() + convertFractionToTarget();
        }

        /**
         * Converts decimal integer to base 1
         * @return String
         */
        private String convertIntegerToMin() {
            return "1".repeat(decimalInteger);
        }

        /**
         * Converts fractional decimal component to target base
         * @return String
         */
        private String convertFractionToTarget() {
            if (IS_FRACTION_ZERO) {return "";}

            StringBuilder sb = new StringBuilder(".");
            int integerPart;

            for (int i = 0; i < 5; i++) {
                integerPart = (int) (decimalFraction * TARGET_RADIX);
                decimalFraction = (decimalFraction * TARGET_RADIX) % 1;
                sb.append(Character.forDigit(integerPart, TARGET_RADIX));
            }

            return sb.toString();
        }

        /**
         * Converts integral decimal component to target base
         * @return String
         */
        private String convertIntegerToTarget() {
            return Integer.toString(decimalInteger, TARGET_RADIX);
        }
    }
}