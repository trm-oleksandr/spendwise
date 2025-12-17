package sk.upjs.ics.spendwise.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public String hash(String plain) {
        validateInput(plain, "plain password");
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public boolean verify(String plain, String hash) {
        validateInput(plain, "plain password");
        validateInput(hash, "password hash");
        return BCrypt.checkpw(plain, hash);
    }

    private void validateInput(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be null or empty");
        }
    }
}
