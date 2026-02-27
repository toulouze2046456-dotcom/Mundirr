package com.health.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * User entity for authentication and profile management.
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    private String name;
    
    // Profile fields
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String sexualActivityFreq;
    private Double testosteroneScore;

    // Referral fields
    @Column(unique = true)
    private String referralCode;       // Unique invite code (e.g., "emiliano-7f3a")

    private Long referredByUserId;     // UID of the user who invited this user

    // Identity Wall — SHA-256(normalize(cardholderName) + normalize(billingAddress))
    @Column(unique = true)
    private String identityHash;
    
    // Constructors
    public User() {}
    
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // Profile getters and setters
    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
    
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getSexualActivityFreq() { return sexualActivityFreq; }
    public void setSexualActivityFreq(String sexualActivityFreq) { this.sexualActivityFreq = sexualActivityFreq; }
    
    public Double getTestosteroneScore() { return testosteroneScore; }
    public void setTestosteroneScore(Double testosteroneScore) { this.testosteroneScore = testosteroneScore; }

    // Referral getters/setters
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public Long getReferredByUserId() { return referredByUserId; }
    public void setReferredByUserId(Long referredByUserId) { this.referredByUserId = referredByUserId; }

    // Identity Wall getters/setters
    public String getIdentityHash() { return identityHash; }
    public void setIdentityHash(String identityHash) { this.identityHash = identityHash; }
}
