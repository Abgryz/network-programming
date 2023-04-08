package lb1;

public class Client1 {
    public static void main(String[] args) {
        new TextClient("localhost", 7777).connect();
    }
}
