package jackplay.bootstrap;

import java.util.Objects;

public class PlayGround {
    public final String classFullName;      // com.abc.MyService
    public final String methodFullName;     // com.abc.MyService.myfunction(java.lang.String)
    public final String methodLongName;     // com.abc.MyService.myfunction
    public final String methodShortName;    // myfunction

    final static String INVALID_MESSAGE = "invalid format, correct format is className.methodName()";

    public PlayGround(String methodFullName) {
        if (null == methodFullName
                || methodFullName.length() < 5
                || !methodFullName.endsWith(")")
                || methodFullName.indexOf(' ') >= 0) {
            throwInvalidFormatMessage(methodFullName);
        }

        int lastDot = methodFullName.lastIndexOf('.');
        if (lastDot <= 1 || methodFullName.endsWith(".")) throwInvalidFormatMessage(methodFullName);

        int firstParen = methodFullName.indexOf('(');
        if (firstParen <= 0) throwInvalidFormatMessage(methodFullName);

        int dotBeforeMethodName = methodFullName.substring(0, firstParen).lastIndexOf('.');

        this.classFullName = methodFullName.substring(0, dotBeforeMethodName);
        this.methodFullName = methodFullName;
        this.methodLongName = methodFullName.substring(0, firstParen);
        this.methodShortName = methodFullName.substring(dotBeforeMethodName + 1, firstParen);
    }

    private void throwInvalidFormatMessage(String methodFullName) {
        throw new IllegalArgumentException("[" + methodFullName + "] is " + INVALID_MESSAGE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayGround playGround = (PlayGround) o;
        return Objects.equals(classFullName, playGround.classFullName) &&
                Objects.equals(methodFullName, playGround.methodFullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodFullName);
    }

    @Override
    public String toString() {
        return "PlayGround{" +
                "classFullName='" + classFullName + '\'' +
                ", methodFullName='" + methodFullName + '\'' +
                ", methodLongName='" + methodLongName + '\'' +
                ", methodShortName='" + methodShortName + '\'' +
                '}';
    }
}
