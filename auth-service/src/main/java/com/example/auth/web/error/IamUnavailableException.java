package com.example.auth.web.error;

import com.example.common.web.error.ApiException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class IamUnavailableException extends ApiException {
    public IamUnavailableException(String reason) {
        // opsi A: code mengandung reason (sesuai format lama)
//        super(HttpStatus.SERVICE_UNAVAILABLE, "iam_unavailable:" + reason, "IAM unavailable");
        // opsi B (alternatif): pakai code tetap dan kirim alasan di meta
         super(HttpStatus.SERVICE_UNAVAILABLE, "iam_unavailable", "IAM unavailable", Map.of("reason", reason));
    }
}
