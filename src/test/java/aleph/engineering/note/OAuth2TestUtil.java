package aleph.engineering.note;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class OAuth2TestUtil {
     

    public static final String ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODk" +
    "wIiwibmFtZSI6IkNhcmxvcyBkZSBsYSBwaW4gZ2FyY2lhIiwiYWRtaW4iOnRydWUsImp0aSI6ImQzNWRmMTRkLTA5ZjYtNDhmZi04" + 
    "YTkzLTdjNmYwMzM5MzE1OSIsImlhdCI6MTU0MTk3MTU4MywiZXhwIjoxNTQxOTc1MTgzfQ.yt0LO0h7e_wiVDEaYrOv4aw5rajRn-K1zidz5iP2ugQ";

    public static OidcUser oidcUserTest (Map<String, Object> claimsMap) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
        if (!claimsMap.containsKey("sub")) {
            claimsMap.put("sub", "manuel");
        }
        if (!claimsMap.containsKey("email")) {
            claimsMap.put("email", "manuel@uci.cu");
        }
        if (!claimsMap.containsKey("preferred_username")) {
            claimsMap.put("preferred_username", "manuel");
        }
        if (claimsMap.containsKey("auth_time")) {   
            issuedAt = (Instant) claimsMap.get("auth_time");
        } else {
            claimsMap.put("auth_time", issuedAt);
        }
        if (claimsMap.containsKey("exp")) {
            expiresAt = (Instant) claimsMap.get("exp");
        } else {
            claimsMap.put("exp", expiresAt);
        }
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = ((Collection<String>)claimsMap.getOrDefault("groups", new ArrayList<>()))
                .stream()
                .filter(authority -> authority.startsWith("TASK_"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

         OidcIdToken token  = new OidcIdToken(ID_TOKEN, issuedAt, expiresAt, claimsMap);
         OidcUserInfo userInfo = new OidcUserInfo(claimsMap);
         return new DefaultOidcUser(authorities, token, userInfo, "preferred_username");
    }
}
