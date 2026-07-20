import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

/**
 * Программа для вывода календаря на указанный год.
 * Пользователь может вводить годы до тех пор, пока не введет "exit".
 *
 * @author Юрий Захматов
 */
public class YearCalendar {

    private static final String EXIT_COMMAND = "exit";
    private static final int MONTHS_IN_YEAR = 12;
    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_WEEKS_IN_MONTH = 6;
    private static final int MONTHS_PER_ROW = 3;
    private static final int MONTH_WIDTH = 24;

    /**
     * Enum для месяцев с привязкой к константам Calendar.
     */
    private enum Month {
        JANUARY(Calendar.JANUARY, "Январь"),
        FEBRUARY(Calendar.FEBRUARY, "Февраль"),
        MARCH(Calendar.MARCH, "Март"),
        APRIL(Calendar.APRIL, "Апрель"),
        MAY(Calendar.MAY, "Май"),
        JUNE(Calendar.JUNE, "Июнь"),
        JULY(Calendar.JULY, "Июль"),
        AUGUST(Calendar.AUGUST, "Август"),
        SEPTEMBER(Calendar.SEPTEMBER, "Сентябрь"),
        OCTOBER(Calendar.OCTOBER, "Октябрь"),
        NOVEMBER(Calendar.NOVEMBER, "Ноябрь"),
        DECEMBER(Calendar.DECEMBER, "Декабрь");

        private final int calendarConstant;
        private final String russianName;

        Month(int calendarConstant, String russianName) {
            this.calendarConstant = calendarConstant;
            this.russianName = russianName;
        }

        public int getCalendarConstant() {
            return calendarConstant;
        }

        public String getRussianName() {
            return russianName;
        }

        /**
         * Получить месяц по индексу (0-11).
         */
        public static Month fromIndex(int index) {
            if (index < 0 || index >= values().length) {
                throw new IllegalArgumentException("Invalid month index: " + index);
            }
            return values()[index];
        }
    }

    private static final List<String> DAYS_OF_WEEK = List.of("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            printWelcomeMessage();

            while (true) {
                System.out.print("\nВведите год (или 'exit' для выхода): ");
                String input = scanner.nextLine().trim();

                if (isExitCommand(input)) {
                    break;
                }

                if (!isValidYear(input)) {
                    System.out.println("ERROR: введите корректный год (например: 2026) или 'exit'");
                    continue;
                }

                int year = Integer.parseInt(input);
                printYearCalendar(year);
            }

            printGoodbyeMessage();
        } catch (Exception e) {
            System.out.printf("ERROR: %s%n", e.getMessage());
        }
    }

    /**
     * Выводит приветственное сообщение.
     */
    private static void printWelcomeMessage() {
        System.out.println("==================================================");
        System.out.println("        ПРОГРАММА ДЛЯ ВЫВОДА КАЛЕНДАРЯ");
        System.out.println("==================================================");
        System.out.println("Для выхода введите 'exit'");
    }

    /**
     * Проверяет, является ли введенная строка командой выхода.
     *
     * @param input введенная строка
     * @return true, если это команда выхода, иначе false
     */
    private static boolean isExitCommand(String input) {
        return EXIT_COMMAND.equalsIgnoreCase(input);
    }

    /**
     * Проверяет, является ли строка корректным годом.
     *
     * @param input введенная строка
     * @return true, если строка содержит корректный год, иначе false
     */
    private static boolean isValidYear(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            int year = Integer.parseInt(input);
            return year >= 1 && year <= 9999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Выводит календарь на указанный год.
     *
     * @param year год для вывода календаря
     */
    private static void printYearCalendar(int year) {
        System.out.println("\n================== " + year + " ==================\n");

        int rows = MONTHS_IN_YEAR / MONTHS_PER_ROW;

        for (int row = 0; row < rows; row++) {
            printMonthRow(year, row);
        }
    }

    /**
     * Выводит строку из MONTHS_PER_ROW месяцев.
     *
     * @param year год
     * @param row номер строки (0-3)
     */
    private static void printMonthRow(int year, int row) {
        // Заголовки месяцев - фиксированная ширина
        for (int col = 0; col < MONTHS_PER_ROW; col++) {
            int monthIndex = row * MONTHS_PER_ROW + col;
            Month month = Month.fromIndex(monthIndex);
            String header = month.getRussianName() + " " + year;
            // Центрируем заголовок
            int padding = (MONTH_WIDTH - header.length()) / 2;
            System.out.print(" ".repeat(Math.max(0, padding)));
            System.out.print(header);
            System.out.print(" ".repeat(Math.max(0, MONTH_WIDTH - padding - header.length())));
        }
        System.out.println();

        // Заголовки дней недели для каждого месяца
        for (int col = 0; col < MONTHS_PER_ROW; col++) {
            System.out.print(" ");
            for (String day : DAYS_OF_WEEK) {
                System.out.print(day + " ");
            }
            System.out.print("  ");
        }
        System.out.println();

        int[] monthDays = new int[MONTHS_PER_ROW];
        int[] offsets = new int[MONTHS_PER_ROW];

        for (int col = 0; col < MONTHS_PER_ROW; col++) {
            int monthIndex = row * MONTHS_PER_ROW + col;
            Month month = Month.fromIndex(monthIndex);

            Calendar calendar = new GregorianCalendar(year, month.getCalendarConstant(), 1);
            monthDays[col] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            offsets[col] = calculateOffset(firstDayOfWeek);
        }

        // Выводим дни для всех трех месяцев
        for (int week = 0; week < MAX_WEEKS_IN_MONTH; week++) {
            for (int col = 0; col < MONTHS_PER_ROW; col++) {
                System.out.print(" ");
                for (int day = 0; day < DAYS_IN_WEEK; day++) {
                    int dayNumber = week * DAYS_IN_WEEK + day - offsets[col] + 1;
                    if (dayNumber >= 1 && dayNumber <= monthDays[col]) {
                        System.out.printf("%2d ", dayNumber);
                    } else {
                        System.out.print("   ");
                    }
                }
                System.out.print("  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Вычисляет смещение для первого дня месяца.
     *
     * @param firstDayOfWeek день недели первого дня месяца (1 = воскресенье)
     * @return смещение для начала с понедельника
     */
    private static int calculateOffset(int firstDayOfWeek) {
        return (firstDayOfWeek + 5) % DAYS_IN_WEEK;
    }

    /**
     * Выводит прощальное сообщение.
     */
    private static void printGoodbyeMessage() {
        System.out.println("\n==================================================");
        System.out.println("          Спасибо за использование!");
        System.out.println("==================================================");
    }
}