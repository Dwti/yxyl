package com.yanxiu.gphone.student.homework.data;

import com.yanxiu.gphone.student.base.ExerciseBaseResponse;
import com.yanxiu.gphone.student.base.StatusBean;

/**
 * Created by sp on 17-5-20.
 */

public class UpdateUserInfoResponse extends ExerciseBaseResponse {
    private StatusBean status;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }
}
