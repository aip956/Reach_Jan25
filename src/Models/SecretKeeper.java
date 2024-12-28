// Models.SecretKeeper.java

package Models;

import Utils.ValidationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class SecretKeeper {
    private String secretCode;
   
    public SecretKeeper() {
       this.secretCode = initializeSecretCode();
    }

    private String initializeSecretCode() {
        String apiSecret = fetchSecretFromAPI();
        if (apiSecret != null && apiSecret.matches(ValidationUtils.VALID_GUESS_PATTERN)) {
            System.out.println("Secret from API: " + apiSecret);
            return apiSecret;
        } else {
            String localSecret = generateLocalSecret();
            System.out.println("Local Secret: " + localSecret);
            return localSecret;
        }
    }

    private String fetchSecretFromAPI() {
        try {
            URL url = new URL("https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            // return null; // to test generateLocalSecret; enable this and comment out next line
            return response.toString().trim().replace("\n", ""); // remove new lines
        } catch (IOException e) {
            System.out.println("Failed to get API secret: " + e.getMessage());
            return null; // Return null to indicate failure
        }
    }

    // Generate backup secret locally
    private String generateLocalSecret() {
        Random random = new Random();
        StringBuilder localSecret = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            localSecret.append(random.nextInt(7)); // Generate digits 0-7
        }
        return localSecret.toString();
    }

    public String getSecretCode() {
        return secretCode;
    } 
}

