public class Question2 {

    public static void main(String[] args) {
        Question2 question = new Question2();

        StringRepeater repeater = new StringRepeater();
        MeasuredBiConsumer<String, Integer> consumer = repeater::repeatString;
        String toMeasure = "hello";

        question.naiveMeasure(consumer, toMeasure);
        question.preciseMeasure(consumer, toMeasure);
    }

    /**
     * Performs and logs the timings required in Question 2a.
     *
     * @param consumer The consumer containing the {@link StringRepeater#repeatString(String, int)}} method
     * @param toMeasure The string to measure
     */
    public void naiveMeasure(MeasuredBiConsumer<String, Integer> consumer, String toMeasure) {
        consumer.acceptNaively(toMeasure, 1);
        consumer.acceptNaively(toMeasure, 100);
        consumer.acceptNaively(toMeasure, 1000);
        consumer.acceptNaively(toMeasure, 10000);
    }

    /**
     * Performs and logs the timings required in Question 2b.
     *
     * @param consumer The consumer containing the {@link StringRepeater#repeatString(String, int)}} method
     * @param toMeasure The string to measure
     */
    public void preciseMeasure(MeasuredBiConsumer<String, Integer> consumer, String toMeasure) {
        consumer.acceptPrecisely(toMeasure, 1);
        consumer.acceptPrecisely(toMeasure, 100);
        consumer.acceptPrecisely(toMeasure, 1000);
        consumer.acceptPrecisely(toMeasure, 10000);
    }

    /**
     * <p>An extension of {@link java.util.function.BiConsumer} which provides additional functionality to measure the
     * amount of time taken for the {@link #accept(Object, Object)} method to run.</p>
     *
     * <p>I've chosen to implement the measuring mechanism in this way solely for the purpose of expandability.
     * Rather than having to create timing mechanisms for any individual method which you wish to measure the time
     * taken to run, you can pass any amount of code into this consumer, and you will have the ability to time anything
     * given to it.</p>
     *
     * @param <T> The type of the first argument of the operation
     * @param <U> The type of the second argument of the operation
     */
    @FunctionalInterface
    public interface MeasuredBiConsumer<T, U> {

        /**
         * <p>The default timeunit used for both {@link #acceptNaively(Object, Object)} and
         * {@link #acceptPrecisely(Object, Object)} to convert the result to, if nothing is specified.</p>
         *
         * <p>Field name is upper case because static and final are implicit with interfaces. Google Java Style Guide
         * sets out that field names that are static and final should be upper case, with words separated by
         * underscores.</p>
         */
        TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

        /**
         * <p>The default number of times the {@link #acceptPrecisely(Object, Object)} will repeat to produce the average,
         * if nothing is specified.</p>
         *
         * <p>Field name is upper case because static and final are implicit with interfaces. Google Java Style Guide says
         * that field names that are static and final should be upper case, with words separated by underscores.</p>
         */
        int DEFAULT_NO_REPEATS = 1000;


        /**
         * The consumer the user must implement.
         *
         * @param t Any given parameter
         */
        void accept(T t, U u);

        /**
         * Overloaded method for {@link #acceptNaively(Object, Object, TimeUnit)} (defaults to seconds).
         *
         * @param t Object to measure
         * @param u Object to measure
         *
         * @return The amount of time taken to execute (in seconds)
         */
        default double acceptNaively(T t, U u) {
            return acceptNaively(t, u, DEFAULT_TIMEUNIT);
        }

        /**
         * <p>Measures the execution time for the {@link #accept(Object, Object)} method implemented by the user to.</p>
         *
         * <p>The accept method will only be ran once, which will then be converted into the appropriate {@link TimeUnit}
         * specified in the {@code unit} parameter.</p>
         *
         * @param t Object to measure
         * @param u Object to measure
         * @param unit The time unit specified
         *
         * @return The amount of time to execute in nanoseconds
         */
        default double acceptNaively(T t, U u, TimeUnit unit) {
            long start = System.nanoTime();
            accept(t, u);
            long end = System.nanoTime();

            long time = end - start;
            double timeConverted = unit.convert(time);

            logNaively(t, u, timeConverted, unit);

            return timeConverted;
        }

        /**
         * Overloaded method for {@link #acceptPrecisely(Object, Object, TimeUnit, int)} (defaults to seconds).
         *
         * @param t Object to measure
         * @param u Object to measure
         *
         * @return The amount of time taken to execute (in seconds)
         */
        default double acceptPrecisely(T t, U u) {
            return acceptPrecisely(t, u, DEFAULT_TIMEUNIT, DEFAULT_NO_REPEATS);
        }

        /**
         * Measures the execution time for the {@link #accept(Object, Object)} method implemented by the user to.
         * The accept method will be ran the amount of times specified in the {@code noRepeats} parameter,
         * which will then be converted into the appropriate {@link TimeUnit}.
         *
         * @param t Object to measure
         * @param u Object to measure
         * @param unit Time unit to convert to
         * @param noRepeats The number of times to repeat the accept method before calculating an average.
         *
         * @return The amount of time to execute in nanoseconds
         */
        default double acceptPrecisely(T t, U u, TimeUnit unit, int noRepeats) {
            long start = System.nanoTime();

            for (int i = 0; i < noRepeats; i++) {
                accept(t, u);
            }

            long end = System.nanoTime();
            long avg = (end - start) / noRepeats;

            double avgConverted = unit.convert(avg);

            logPrecisely(t, u, avgConverted, unit, noRepeats);

            return avgConverted;
        }

        default void logNaively(T t, U u, double time, TimeUnit unit) {
            System.out.println("The naively measured time of execution using the input values of \"" + t + "\" and "
                    + "\""  + u + "\" is: " + time + " " + unit.getName() + "!");
        }

        default void logPrecisely(T t, U u, double time, TimeUnit unit, int noRepeats) {
            System.out.println("The precisely measured time of execution using the input values of \"" + t + "\" and "
                    + "\""  + u + "\" over " + noRepeats + " repeats is: " + time + " " + unit.getName() + "!");
        }

        /**
         * <p>Simple enum showing units of time</p>
         *
         * <p>This is mainly here for my own personal analysis, where rather than dealing
         * with E-8, I can visualise the amounts in an interesting way.</p>
         *
         * <p>NOTE: The conversion unit provided is relative to nano seconds.</p>
         *
         */
        @SuppressWarnings("unused")
        enum TimeUnit {
            SECONDS(1_000_000_000, "seconds"),
            MILLISECONDS(1_000_000, "milliseconds"),
            MICROSECONDS(1_000, "microseconds"),
            NANOSECONDS(1, "nanoseconds");

            private final long conversionUnit;
            private final String name;

            /**
             * Constructor initialising the enum.
             *
             * @param conversionUnit The unit of conversion (note, only works in comparison to nanoseconds)
             * @param name The name to be printed
             */
            TimeUnit(int conversionUnit, String name) {
                this.conversionUnit = conversionUnit;
                this.name = name;
            }

            /**
             * Converts any given time (given in nanoseconds) to the current time unit.
             *
             * @param time Time (in nanoseconds)
             * @return Converted time (in the given timeunit)
             */
            public double convert(long time) {
                return (double) time / (double) conversionUnit;
            }

            /**
             * Simple getter that returns the number you need to divide the time measured in nanoseconds by.
             *
             * @return The conversion unit
             */
            public long getConversionUnit() {
                return conversionUnit;
            }

            /**
             * Simple getter that returns the name of the given timeunit
             *
             * @return The name
             */
            public String getName() {
                return name;
            }
        }
    }
}
