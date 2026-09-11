package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response;

import lombok.Data;

@Data
public class AssignOfferResponse {
    public String offerId;
    public String referenceId;
    public String title;
    public String description;
    public String validFrom;
    public String validTo;
}

