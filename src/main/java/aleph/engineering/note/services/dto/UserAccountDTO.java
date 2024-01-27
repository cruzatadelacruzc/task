package aleph.engineering.note.services.dto;

import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserAccountDTO {
    private String id;
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Set<String> authorities;
    private boolean activated = false;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public Set<String> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
    public boolean isActivated() {
        return activated;
    }
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "UserAccountDTO [id=" + id + ", login=" + login + ", email=" + email + ", firstName=" + firstName
                + ", lastName=" + lastName + ", imageUrl=" + imageUrl + ", authorities=" + authorities + ", activated="
                + activated + "]";
    }
}
