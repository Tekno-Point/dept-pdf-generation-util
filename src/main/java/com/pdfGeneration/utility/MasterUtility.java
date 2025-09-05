package com.pdfGeneration.utility;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MasterUtility {

    public String getCompanyNameByCode(String code){
        HashMap<String, String> insuranceCompanies = new HashMap<>();
        insuranceCompanies.put("138", "Aegon Religare Life Insurance Company Ltd.");
        insuranceCompanies.put("122", "Aviva Life Insurance Company India Ltd.");
        insuranceCompanies.put("116", "Bajaj Allianz Life Insurance Company Ltd.");
        insuranceCompanies.put("130", "Bharti Axa Life Insurance Company Ltd.");
        insuranceCompanies.put("109", "Birla Sun Life Insurance Company Ltd.");
        insuranceCompanies.put("136", "Canara HSBC OBC Life Insurance Company Ltd.");
        insuranceCompanies.put("140", "DHFL Pramerica Life Insurance Company Ltd.");
        insuranceCompanies.put("147", "Edelweiss Tokio Life Insurance Company Ltd.");
        insuranceCompanies.put("114", "Exide Life Insurance Company Ltd.");
        insuranceCompanies.put("133", "Future Generali India Life Insurance Company Ltd.");
        insuranceCompanies.put("101", "HDFC Standard Life Insurance Company Ltd.");
        insuranceCompanies.put("105", "ICICI Prudential Life Insurance Company Ltd.");
        insuranceCompanies.put("135", "IDBI Federal Life Insurance Company Ltd.");
        insuranceCompanies.put("143", "Indiafirst Life Insurance Company Ltd.");
        insuranceCompanies.put("107", "Kotak Mahindra Old Mutual Life Insurance Limited");
        insuranceCompanies.put("512", "Life Insurance Corporation of India");
        insuranceCompanies.put("104", "Max Life Insurance Company Ltd.");
        insuranceCompanies.put("117", "PNB MetLife India Insurance Company Ltd.");
        insuranceCompanies.put("121", "Reliance Life Insurance Company Ltd.");
        insuranceCompanies.put("127", "Sahara India Insurance Company Ltd.");
        insuranceCompanies.put("111", "SBI Life Insurance Company Ltd.");
        insuranceCompanies.put("128", "Shriram Life Insurance Company Ltd.");
        insuranceCompanies.put("142", "Star Union Dai-ichi Life Insurance Company Ltd.");
        insuranceCompanies.put("110", "TATA AIA Life Insurance Company Ltd.");
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
