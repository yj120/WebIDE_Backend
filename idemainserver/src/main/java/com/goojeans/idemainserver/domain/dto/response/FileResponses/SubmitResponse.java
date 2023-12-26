package com.goojeans.idemainserver.domain.dto.response.FileResponses;

import com.goojeans.idemainserver.util.FileExtension;
import com.goojeans.idemainserver.util.SubmitResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitResponse {

    private String result;

    public SubmitResult changeToEnum() {
        if (this.result.equals("CORRECT")){
            return SubmitResult.CORRECT;
        } else if (this.result.equals("WRONG")){
            return SubmitResult.WRONG;
        } else if (this.result.equals("TIMEOUT")){
            return SubmitResult.TIMEOUT;
        }
        return SubmitResult.ERROR;
    }
}
