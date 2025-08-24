package com.example.auth.domain.shared;

/** Kumpulan aturan validasi yang dipakai lintas layer. */
public final class ValidationRules {
    private ValidationRules() {}

    /** Huruf, angka, underscore, titik; panjang 3–32. */
    public static final String USERNAME_REGEX = "^[A-Za-z0-9._]{3,32}$";
    public static final String USERNAME_MESSAGE =
            "username must be 3–32 chars; only letters, digits, underscore, or dot";

    // lebih ketat: tidak boleh mulai/akhir dengan . atau _, dan tidak boleh berurutan
     public static final String USERNAME_REGEX_STRICT =
             "^(?=.{3,32}$)(?![_.])(?!.*[_.]{2})[A-Za-z0-9._]+(?<![_.])$";
     public static final String USERNAME_MESSAGE_STRICT =
             "username 3–32 chars; no leading/trailing dot/underscore and no consecutive dot/underscore";
}
