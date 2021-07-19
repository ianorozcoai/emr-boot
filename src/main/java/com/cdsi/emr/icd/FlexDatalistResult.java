package com.cdsi.emr.icd;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlexDatalistResult {

//    private List<Icd> results;
	private List<EmrIcd> results;
}