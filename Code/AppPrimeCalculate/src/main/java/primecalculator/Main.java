package primecalculator;

import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class Main {

    //test
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args) + " This is my arguments ");
        long numero = Long.parseLong(args[0]);
        String redisIP = args[1];
        int redisPort = Integer.parseInt(args[2]);
        boolean bIsPrime = false;

        boolean primeBool = isPrime(numero);

        try (Jedis jedis = new Jedis(redisIP, redisPort)){
            String pingResponse = jedis.ping();
            System.out.println("PrimeCalculator connected on redis "+redisIP+":"+redisPort +" resposta comando ping "+pingResponse);
            jedis.set(numero+"", Boolean.toString(bIsPrime));
        }
        catch (Exception ex){
            System.out.println("Error connecting on redis "+redisIP+":"+redisPort+". Details: "+ex.getMessage());
            ex.printStackTrace();
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