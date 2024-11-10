package primecalculator;

import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class Main {

    //test
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args) + "HELLO ");
        long numero = Long.parseLong(args[0]);
        String redisIP = args[1];
        int redisPort = Integer.parseInt(args[2]);
        Jedis jedis = new Jedis(redisIP, redisPort);

        boolean primeBool = isPrime(numero);
        if (primeBool) {
            jedis.set(numero+"", "true");
        } else {
            jedis.set(numero+"", "false");
        }

        System.out.println("NUMBER " + numero + " IS " + (primeBool ? "PRIME!" : "NOT PRIME!"));
    }



    private static boolean isPrime(long number) {
        if (number <= 1L) return false;
        if (number == 2L || number == 3L) return true;
        if (number % 2L == 0) return false;
        for (long i=3 ; i < Math.sqrt(number); i+=2) {
            if (number % i == 0) return false;
        }
        return true;
    }

}