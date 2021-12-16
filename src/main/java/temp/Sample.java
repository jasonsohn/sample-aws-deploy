package temp;

public class Sample {
    public static void main(String[] args) {
        System.out.println("Sample Java Project in AWS");
        for (int i = 0; i < 10; i++) {
            System.out.println("Test : " + i);
            try {
                Thread.sleep(1000); // 1초
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Finished");
    }
}
