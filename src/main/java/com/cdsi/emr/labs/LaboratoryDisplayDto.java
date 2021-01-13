package com.cdsi.emr.labs;

import java.util.List;
import lombok.Data;

@Data
public class LaboratoryDisplayDto {
	
	private String indexStr;
    private String labTypeName;
    private String labTypeNormalValue;
    private List<LaboratoryHeader> headerListFinal;
    private List<String> timeListFinal;
    private List<String> valueListFinal;
    
}
