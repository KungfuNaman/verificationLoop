public class PossibleNullDereference {
    public static void main(String[] args) {
        String data = possiblyNull(true);
        // Attempt to access a method on a potentially null object.
        System.out.println(data.length());
    }

    public static String possiblyNull(boolean returnNull) {
        if (returnNull) {
            return null;
        } else {
            return "Hello, World!";
        }
    }
}