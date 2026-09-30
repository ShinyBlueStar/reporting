package com.sample.system.reporting.service.dataaccess.entity.query;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "report_party")
public class PartyReportEntityQuery extends BaseReportEntityQuery {

    @Column(name = "national_code", length = 20)
    private String nationalCode;

    @Column(name = "party_no", length = 50)
    private String partyNo;

    @Column(name = "party_name", length = 150)
    private String partyName;

    @Column(name = "party_family", length = 150)
    private String partyFamily;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "report_date")
    private Instant reportDate;

    @Lob
    @Column(name = "payload")
    private String payload;
}
