package tn.esprit.medicareaitigerback.utilities;

public class Log {
    public static Log instance;

    private Log() {
    }

    static {
        try {
            instance = new Log();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating singleton instance of Log");
        }
    }

    public void i(String value) {
        System.out.println(value);
    }

    public void i(String tag, String value) {
        System.out.println(tag + ":::" + value);
    }

    public void e(String value) {
        System.out.println("Error:$value");
    }

    public void e(String tag, String value) {
        System.out.println(tag + ":::" + value);
    }
}