package com.pcpl.carbon.authserver.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal response envelope for the authserver's REST endpoints, mirroring the shape used
 * across the other Carbon Modernized services (success / message / error / data) so clients
 * see a consistent response. Kept local to the authserver because it does not depend on the
 * pcpl-sdk (adding the SDK would also pull in a second @Entity mapped to cb_app_features and
 * clash with the authserver's own AppFeature entity).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private boolean success;
    private String message;
    private String error;
    private Object data;
}
