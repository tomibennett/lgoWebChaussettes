package io.benett;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;

class MarketDataFetcher {
  public static void main(String[] args) {
    try {
      new MarketDataFetcher().fetch();
    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  private void fetch() throws Exception {
    final var currentDateTimestamp = currentDateTimestamp();

    var request = new Request.Builder()
      .url("wss://ws.exchange.lgo.markets")
      .header("X-LGO-DATE", currentDateTimestamp)
      .header("Authorization", makeAuthorizationHeader(currentDateTimestamp))
      .build();

    final var client = new OkHttpClient();

    try (var response = client.newCall(request).execute()) {
      System.out.println("code: " + response.code());
      System.out.println("response: " + response.body().string());
      System.out.println("headers: " + response.headers());
    }
  }

  private String makeAuthorizationHeader(String currentDateTimestamp) throws Exception {
    final var accessKey = "60715d6f-adc9-4792-9b4d-d705135a6173";
    final var stringToSign = String.format("%s\n%s", currentDateTimestamp, "ws.exchange.lgo.markets/");

    return String.format("LGO %s:%s", accessKey, signString(stringToSign));
}

  String signString(String stringToSign) throws Exception {
    String privateKey =
      "MIIEpQIBAAKCAQEAojp7SlwPi4Ql5JPAbxP6+aG+z8HOGT9BsvSDHR/1gQ8xDXlM" +
      "crd7VtrhyQ31jR3rnc6aPx+lVxSn2YvQWyl/4pBQyV39hzJMoMLDz6TJjmPZOUGl" +
      "o2yAku9TntE70RaQcjf2bQA4ikZkMVesJ8TfGVsun7vaYTjJkXRkwdz4MXpXapqH" +
      "teWNVTjgteDZn3Z+MEA9W5OazmPVD/NsDzD9qBxDW0lwtvkHljjNQFit+dtN6zv8" +
      "th5aLcrcnXuSoXbe6RXRMSxCC0whTKfsveyBDCA+kSSPOgiBJyUuv863KSsvEd0k" +
      "kVB8Ox9U8z/V7/xpDLTNXkcfvIlUvC+3Hqi43wIDAQABAoIBADgttlpGzR9MUO75" +
      "946/xY7C41gAzkVR8YduQyVH1vWtdBgtZDrprS2juMKuMdV/ggNw81tesxwXzBR6" +
      "5VlcYqvru/4vrUcvNPgK2lJCx4WmsCeywxB314KKnFOIM4Wxoa3cEVsn02yW+cVY" +
      "jgZrl7KpL9ki7Xnzd2IGg4na4pwHK6EU+bD8+DV7IsMusum6veuEeKxJk2v6FG/Q" +
      "HZozl6aWQhOcYt2aLYue+Cx+qFnLV+zoCuB6lr2EmtqW5GDoSTk6ANJ3DJkX3Btk" +
      "jApRU5BOkipqWy2u7J8A6v9th0VQCXUA1/9ZD+Mq5PszK4pW4SW9RL2IVSRygLL5" +
      "nnslcXECgYEA0aJ9Itf5c0Te6py8H2eMFFAzj0b13PoXnQ3IgMIaTfVVf1i8Ksqy" +
      "VhWzFQNoy03WxCNyWduEqxTLXL2WLLCXnrsrArl8FE/PZnhH1U/RLxKl+O5xIN3z" +
      "90x70xLeQr/qBNRSOClhzToINNToHUQCgAEvDtZsNAERD5OJ99RdfBkCgYEAxhvZ" +
      "0WdT90s051oFW0rQSB9ZPD7M9oQ0NdJENy2gXzO97QlwT9xcC3ZjF6Oh2h3ZC62k" +
      "E32DKqV/gD6g6ANXW92z08Co+zUjFdKY1h7N1s/oxAc+vm+jfJnXKo72W4pehKkb" +
      "A6+PTvPLLOi0WDMG8c13ZALC662qxOZLd9V9e7cCgYEAsRGKmS/L5+04TPrue6g+" +
      "zbmgk1jguzITV/kYbomFJmwfN12AMrTbRZM2nH0wuuiYiztVj0i4GrmJvF/2xPC0" +
      "YMK/ZaG+iHmROYBHTIoKqrQZtNXSgGx5cV0NChBZ5A+uXz3n5MAvd+WYoOdk17Nm" +
      "WTCmyuWap6JvArUgSFD8VaECgYEAio1x8lVM0ThlAKTh+C5Dqx1ZoJvfZ02g4j9z" +
      "fA/KCKs8WqpuRTw9l7qtpRvJF64mXVeM2CDA+rOSj5O9n2au004j9aXZyQ8pwZpv" +
      "T9ltZp40Ed1rUW7srTk+1cH0pKMKZceLYDGJjdNNttPtRX4yjiyAIo8X2hK0y06x" +
      "W1cRktMCgYEAkIwoKjIDfj2EaKzCcxBSpxIrx2+6gB1qmC9v8PLRG4JDJTqj9gj+" +
      "HNI5gCSXj3NLzLFpnC/obG7G9jommp8uh0ojNzuyb0sc5y9SujeNdNbBKGkObprD" +
      "3LydCtIeSZ2KdhxBpWNEqa3PafBE+M/F0RnCGtm+cMm+oLw8VvktHhg=";

    byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKey);

    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedPrivateKey); // not a PKCS8 encoded key...
    RSAPrivateKeySpec
    Signature privateSignature = Signature.getInstance("SHA256withRSA");
    privateSignature.initSign(KeyFactory.getInstance("RSA").generatePrivate(spec));
    privateSignature.update(stringToSign.getBytes("UTF-8"));

    byte[] signedString = privateSignature.sign();

    return signedString.toString(); // have to encode it in hexadecimal first
  }

  @NotNull
  private String currentDateTimestamp() {
    return String.valueOf(System.currentTimeMillis());
  }
}