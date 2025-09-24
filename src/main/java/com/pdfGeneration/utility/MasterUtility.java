package com.pdfGeneration.utility;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MasterUtility {

    public String getCompanyNameByCode(String code){
        HashMap<String, String> insuranceCompanies = new HashMap<>();
        return insuranceCompanies.getOrDefault(code, "Other");
    }


    public String getPolicyStatusByCode(String code){
        HashMap<String, String> policyStatus = new HashMap<>();
        policyStatus.put("1", "Proposal");
        policyStatus.put("2", "Free Look");
        policyStatus.put("3", "First Premium");
        policyStatus.put("4", "Null & Void");
        policyStatus.put("5", "CFI");
        policyStatus.put("6", "Cancelled");
        policyStatus.put("7", "Declined");
        policyStatus.put("8", "NTU/WD");
        policyStatus.put("9", "Postponed");
        policyStatus.put("10", "Inforce");
        policyStatus.put("11", "Paidup");
        policyStatus.put("12", "Lapsed");
        policyStatus.put("13", "Surrendered");
        policyStatus.put("14", "Maturity Claim");
        policyStatus.put("15", "Death Claim");
        policyStatus.put("16", "Death Claim Settled");
        policyStatus.put("17", "Claim Repudiated");
        policyStatus.put("18", "Joint Life Death Reported");
        policyStatus.put("19", "WOP applied");
        policyStatus.put("20", "Discontinued(ULIP)");
        policyStatus.put("21", "Foreclosed");
        policyStatus.put("22", "Extended lifecover");
        policyStatus.put("23", "Premature Termination");
        return policyStatus.getOrDefault(code, "Rejected");
    }
}
