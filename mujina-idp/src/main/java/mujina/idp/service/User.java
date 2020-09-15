package mujina.idp.service;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -9067986418908847017L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String email;
    
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "given_name")
    private String givenName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "schacHomeOrganization")
    private String schacHomeOrganization;
    @Column(name = "eduPersonPrincipalName")
    private String eduPersonPrincipalName;

    private Date dateCreated;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "authority_id") })
    private Set<Authority> authorities = new HashSet<>();


/*    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Question> questions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Answer> answers;*/

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSchacHomeOrganization() {
		return schacHomeOrganization;
	}

	public void setSchacHomeOrganization(String schacHomeOrganization) {
		this.schacHomeOrganization = schacHomeOrganization;
	}

	public String getEduPersonPrincipalName() {
		return eduPersonPrincipalName;
	}

	public void setEduPersonPrincipalName(String eduPersonPrincipalName) {
		this.eduPersonPrincipalName = eduPersonPrincipalName;
	}
    
/*    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }*/

}
