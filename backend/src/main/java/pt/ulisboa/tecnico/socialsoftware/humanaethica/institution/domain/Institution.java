package pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.springframework.security.crypto.keygen.KeyGenerators;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

@Entity
@Table(name = "institutions")
public class Institution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    private String nif;

    private boolean valid = false;

    private String confirmationToken = "";
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "institution", orphanRemoval = true, fetch= FetchType.EAGER)
    private List<Member> members = new ArrayList<>();

    private LocalDateTime tokenGenerationDate;

    public Institution(){
    }

    public Institution(String name, String email, String nif){
        setEmail(email);
        setName(name);
        setNIF(nif);
        generateConfirmationToken();
    }

    public void addMember(Member member){
        this.members.add(member);
    }

    public List<Member> getMembers() {
        return members;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNIF() {
        return nif;
    }

    public void setNIF(String nIF) {
        nif = nIF;
    }

    public void validate() {
        this.valid = true;
    }

    public boolean isValid() {
        return valid;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getTokenGenerationDate() {
        return tokenGenerationDate;
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        this.tokenGenerationDate = tokenGenerationDate;
    }

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(DateHandler.now());
        setConfirmationToken(token);
        return token;
    }
}
