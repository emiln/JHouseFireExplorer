/**
 * Multiply-With-Carry Random generates very fast pseudo-random numbers.
 */
public class MWCRand {
    public static void init(int x) {
        int i;
        Q[0] = x;
        Q[1] = x + phi;
        Q[2] = x + phi + phi;
        for (i = 3; i < 4096; i++) {
            Q[i] = Q[i - 3] ^ Q[i - 2] ^ phi ^ i;
        }
    }
    public static void init(int x, int c, int phi) {
    MWCRand.c = c;
    MWCRand.phi = phi;
        int i;
        Q[0] = x;
        Q[1] = x + phi;
        Q[2] = x + phi + phi;
        for (i = 3; i < 4096; i++) {
            Q[i] = Q[i - 3] ^ Q[i - 2] ^ phi ^ i;
        }
    }
    public static int rand() {
        long t, a = 18782L;
        int i = 4095;
        int x, r = 0xfffffffe;
        i = (i + 1) & 4095;
        t = a * Q[i] + c;
        c = (int)(t >> 32);
        x = (int)(t + c);
        if (x < c) {
            x++;
            c++;
        }
        return (Q[i] = r - x);
    }
    public static void main(String[] args) {
        try {
            int max = Integer.parseInt(args[0]);
            int x = Integer.parseInt(args[1]);
            MWCRand.init(x);
            for(int i = 0; i < max; i++) {
                System.out.println(i + ": " + MWCRand.rand());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private static int[] Q = new int[4096];
    private static int phi = 0x9e3779b9;
    private static int c = 362436;
}
