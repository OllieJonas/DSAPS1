public class Question2 {

    static final int DEFAULT_TEST_REPEATS = 1000;

    private final int testRepeats;

    public static void main(String[] args) {
        Question2 question = new Question2();

        StringRepeater repeater = new StringRepeater();
        String toRepeat = "hello";

        question.naiveMeasure(repeater, toRepeat);
        question.preciseMeasure(repeater, toRepeat);
    }

    public Question2() {
        this(DEFAULT_TEST_REPEATS);
    }

    public Question2(int testRepeats) {
        this.testRepeats = testRepeats;
    }



    public void naiveMeasure(StringRepeater repeater, String toRepeat) {
        measureNaively(repeater, toRepeat, 1);
        measureNaively(repeater, toRepeat, 100);
        measureNaively(repeater, toRepeat, 1000);
        measureNaively(repeater, toRepeat, 10000);
    }

    public void preciseMeasure(StringRepeater repeater, String toRepeat) {
        preciseMeasure(repeater, toRepeat, testRepeats);
    }

    public void preciseMeasure(StringRepeater repeater, String toRepeat, int testRepeats) {
        measurePrecisely(repeater, toRepeat, 1, testRepeats);
        measurePrecisely(repeater, toRepeat, 100, testRepeats);
        measurePrecisely(repeater, toRepeat, 1000, testRepeats);
        measurePrecisely(repeater, toRepeat, 10000, testRepeats);
    }

    private void measureNaively(StringRepeater repeater, String toRepeat, int methodRepeats) {
        long start = System.nanoTime();
        repeater.repeatString(toRepeat, methodRepeats);
        long end = System.nanoTime();

        long time = end - start;
        double timeConverted = toSeconds(time);

        logNaively(toRepeat, methodRepeats, timeConverted);
    }

    private void measurePrecisely(StringRepeater repeater, String toRepeat, int methodRepeats, int testRepeats) {
        long start = System.nanoTime();

        for (int i = 0; i < testRepeats; i++) {
            repeater.repeatString(toRepeat, methodRepeats);
        }

        long end = System.nanoTime();
        long avg = (end - start) / testRepeats;

        double avgConverted = toSeconds(avg);

        logPrecisely(toRepeat, methodRepeats, avgConverted, testRepeats);
    }


    private void logNaively(String input, int noRepeats, double time) {
        System.out.println("The naively measured time of execution from the method repeatString()" +
                " using the input values of \"" + input + "\" and " + "\""  + noRepeats + "\" is: " +
                time + " seconds!");
    }

    private void logPrecisely(String t, int u, double time, int noRepeats) {
        System.out.println("The average precisely measured time of execution from the method repeatString()" +
                " using the input values of \"" + t + "\" and " + "\""  + u + "\" over " + noRepeats +
                " repeats is: " + time + " seconds!");
    }

    private double toSeconds(long timeNano) {
        return (double) timeNano / (double) 1_000_000_000;
    }
}
