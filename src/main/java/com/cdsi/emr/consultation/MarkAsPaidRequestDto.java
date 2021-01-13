package com.cdsi.emr.consultation;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class MarkAsPaidRequestDto {
    @Size()
    private List<Long> consultationIdList = new ArrayList<>();
}
