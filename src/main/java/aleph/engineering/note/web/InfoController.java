package aleph.engineering.note.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Profile({"dev", "local"})
@Controller
public class InfoController {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);
    

    @GetMapping("/")
    public String getInfo(Principal principal, Model model) {
        log.debug("REST request to get Info, Principal {}", principal);
        if (principal instanceof OAuth2AuthenticationToken oauth2Auth) {
            OAuth2User oauth2User = oauth2Auth.getPrincipal();

            String name = oauth2User.getAttribute("name");
            String email = oauth2User.getAttribute("email");
    
            String accessTokenValue = ((OidcUser) oauth2User).getIdToken().getTokenValue();
    
            model.addAttribute("email", email);
            model.addAttribute("name", name);
            model.addAttribute("accessToken", accessTokenValue);
        }
        return "index";
    }
}
