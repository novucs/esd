package net.novucs.esd.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Password {

  private static final transient String ALGORITHM = "PBKDF2WithHmacSHA512";
  private static final transient SecureRandom RANDOM = new SecureRandom();
  private static final transient SecretKeyFactory FACTORY = secretKeyFactory();
  private static final int ITERATION_COUNT = 65_536;
  private static final int KEY_LENGTH = 128;

  private final byte[] hash;
  private final byte[] salt;

  public Password(byte[] hash, byte[] salt) {
    this.hash = Arrays.copyOf(hash, hash.length);
    this.salt = Arrays.copyOf(salt, salt.length);
  }

  private static SecretKeyFactory secretKeyFactory() {
    try {
      return SecretKeyFactory.getInstance(ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(ALGORITHM + " secret key factory does not exist", e);
    }
  }

  private static byte[] pbkdf2(char[] password, byte[] salt) {
    KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
    try {
      return FACTORY.generateSecret(spec).getEncoded();
    } catch (InvalidKeySpecException e) {
      throw new IllegalStateException("Invalid SecretKeyFactory", e);
    }
  }

  public static Password fromPlaintext(String plaintext) {
    byte[] salt = new byte[16];
    RANDOM.nextBytes(salt);
    byte[] hash = pbkdf2(plaintext.toCharArray(), salt);
    return new Password(hash, salt);
  }

  public static Password fromHashAndSalt(String hashAndSalt) {
    String[] pair = hashAndSalt.split("\\.");
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] hash = decoder.decode(pair[0]);
    byte[] salt = decoder.decode(pair[1]);
    return new Password(hash, salt);
  }

  public String toHashAndSalt() {
    Base64.Encoder encoder = Base64.getEncoder();
    String hash = encoder.encodeToString(getHash());
    String salt = encoder.encodeToString(getSalt());
    return hash + "." + salt;
  }

  public boolean authenticate(String password) {
    byte[] check = pbkdf2(password.toCharArray(), salt);
    int zero = 0;
    for (int i = 0; i < check.length; ++i) {
      zero |= hash[i] ^ check[i];
    }
    return zero == 0;
  }

  public byte[] getHash() {
    return Arrays.copyOf(hash, hash.length);
  }

  public byte[] getSalt() {
    return Arrays.copyOf(salt, salt.length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Password password = (Password) o;
    return Arrays.equals(hash, password.hash) && Arrays.equals(salt, password.salt);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(hash);
    result = 31 * result + Arrays.hashCode(salt);
    return result;
  }

  @Override
  public String toString() {
    return "Password{"
        + "hash=" + Arrays.toString(hash)
        + ", salt=" + Arrays.toString(salt)
        + '}';
  }
}
