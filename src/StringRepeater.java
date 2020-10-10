@SuppressWarnings({"StringBufferMayBeStringBuilder", "StringRepeatCanBeUsed", "StringConcatenationInLoop", "unused"})
public class StringRepeater {

    public String badRepeatString(String s, int n) {
        String result = "";

        for (int i = 0; i < n; i++) {
            result = result + s;
        }

        return result;
    }

    public String repeatString(String s, int n) {
        StringBuffer buffer = new StringBuffer(s);

        for (int i = 0; i < n; i++) {
            buffer.append(s);
        }

        return buffer.toString();
    }
}
