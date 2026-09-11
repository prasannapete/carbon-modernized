package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.Date;

@Entity
@Table(name = "srl_user_token")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken  extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "consumer_uuid")
    private String consumerUuid;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
}
