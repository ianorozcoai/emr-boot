package com.cdsi.emr.patient;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data 
public class PatientMedicalHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "patient_id")
	Patient patient;
	
	private boolean heartDisease;
	private boolean hypertension;
	private boolean bp;
	private boolean lungDisease;
	private boolean asthma;
	private boolean liverDisease;
	private boolean kidneyDisease;
	private boolean uti;
	private boolean bleedingDisorder;
	private boolean cancer;
	private boolean currentMedications;
	private boolean earNoseThroat;
	private boolean eyeVisual;
	private boolean gastro;
	private boolean thyroid;
	private boolean diabetes;
	private boolean allergy;
	private boolean skinDisease;
	private boolean other;
	
	private String heartDiseaseNotes;
	private String hypertensionNotes;
	private String bpNotes;
	private String lungDiseaseNotes;
	private String asthmaNotes;
	private String liverDiseaseNotes;
	private String kidneyDiseaseNotes;
	private String utiNotes;
	private String bleedingDisorderNotes;
	private String cancerNotes;
	private String currentMedicationsNotes;
	private String earNoseThroatNotes;
	private String eyeVisualNotes;
	private String gastroNotes;
	private String thyroidNotes;
	private String diabetesNotes;
	private String allergyNotes;
	private String skinDiseaseNotes;
	private String otherNotes;
	
	private boolean heartDiseaseFamily;
	private boolean hypertensionFamily;
	private boolean lungDiseaseFamily;
	private boolean asthmaFamily;
	private boolean liverDiseaseFamily;
	private boolean kidneyDiseaseFamily;
	private boolean utiFamily;
	private boolean bleedingDisorderFamily;
	private boolean cancerFamily;
	private boolean currentMedicationsFamily;
	private boolean earNoseThroatFamily;
	private boolean eyeVisualFamily;
	private boolean gastroFamily;
	private boolean thyroidFamily;
	private boolean diabetesFamily;
	private boolean allergyFamily;
	private boolean skinDiseaseFamily;
	private boolean otherFamily;
	
	private String heartDiseaseNotesFamily;
	private String hypertensionNotesFamily;
	private String lungDiseaseNotesFamily;
	private String asthmaNotesFamily;
	private String liverDiseaseNotesFamily;
	private String kidneyDiseaseNotesFamily;
	private String utiNotesFamily;
	private String bleedingDisorderNotesFamily;
	private String cancerNotesFamily;
	private String currentMedicationsNotesFamily;
	private String earNoseThroatNotesFamily;
	private String eyeVisualNotesFamily;
	private String gastroNotesFamily;
	private String thyroidNotesFamily;
	private String diabetesNotesFamily;
	private String allergyNotesFamily;
	private String skinDiseaseNotesFamily;
	private String otherNotesFamily;
	
	private boolean smoker;
	private int packsPerDay;
	private int yrsInUse;
	private boolean alcoholDrinker;
	private String alcoholDrinkerNotes;
	private boolean substanceAbuse;
	private String substanceAbuseNotes;
	
	private boolean menarche;
	private String menarcheNotes;
	private boolean pmp;
	private String pmpNotes;
	private boolean lmp;
	private String lmpNotes;
	private boolean aog;
	private String aogNotes;	
	private boolean edd;
	private String eddNotes;
	private String reviewOfSystem;
	private String others2;
	
}
