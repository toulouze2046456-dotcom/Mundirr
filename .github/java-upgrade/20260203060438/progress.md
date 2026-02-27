# Upgrade Progress

  ### ✅ Generate Upgrade Plan
  - [[View Log]](logs/1.generatePlan.log)

  ### ✅ Confirm Upgrade Plan
  - [[View Log]](logs/2.confirmPlan.log)

  ### ❗ Setup Development Environment
  - [[View Log]](logs/3.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  #### Errors
  - Uncommitted changes found, please stash, commit or discard them first. You'd better let the user to choose the action to take.
  
  
  - ###
    ### ✅ Install JDK 21
  </details>

  ### ❗ Setup Development Environment
  - [[View Log]](logs/4.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  #### Errors
  - Uncommitted changes found, please stash, commit or discard them first. You'd better let the user to choose the action to take.
  </details>

  ### ❗ Setup Development Environment
  - [[View Log]](logs/5.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  #### Errors
  - Uncommitted changes found, please stash, commit or discard them first. You'd better let the user to choose the action to take.
  </details>

  ### ✅ Setup Development Environment
  - [[View Log]](logs/6.setupEnvironment.log)
  
  > There are uncommitted changes in the project before upgrading, which have been stashed according to user setting "appModernization.uncommittedChangesAction".

  ### ✅ PreCheck
  - [[View Log]](logs/7.precheck.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Precheck - Build project
    - [[View Log]](logs/7.1.precheck-buildProject.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvn clean test-compile -q -B -fn`
    </details>
  
    ### ✅ Precheck - Validate CVEs
    - [[View Log]](logs/7.2.precheck-validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### CVE issues
    - Dependency `org.springframework.security:spring-security-crypto:6.2.0` has **1** known CVEs:
      - [CVE-2025-22228](https://github.com/advisories/GHSA-mg83-c7gq-rv5c): Spring Security Does Not Enforce Password Length
        - **Severity**: **HIGH**
        - **Details**: BCryptPasswordEncoder.matches(CharSequence,String) will incorrectly return true for passwords larger than 72 characters as long as the first 72 characters are the same.
    </details>
  
    ### ✅ Precheck - Run tests
    - [[View Log]](logs/7.3.precheck-runTests.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Test result
    | Total | Passed | Failed | Skipped | Errors |
    |-------|--------|--------|---------|--------|
    | 0 | 0 | 0 | 0 | 0 |
    </details>
  </details>

  ### ⏳ Upgrade project to use `Java 21` ...Running
  
  
  - ###
    ### ⏳ Upgrade using Agent ...Running
  
    ### ✅ Build Project
    - [[View Log]](logs/8.2.buildProject.log)
    
    - Build result: 100% Java files compiled
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvn clean test-compile -q -B -fn`
    </details>

  ### ✅ Validate & Fix
  
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Validate CVEs
    - [[View Log]](logs/9.1.validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Checked Dependencies
      - java:*:21
    </details>
  
    ### ✅ Validate Code Behavior Changes
    - [[View Log]](logs/9.2.validateBehaviorChanges.log)
  
    ### ✅ Run Tests
    - [[View Log]](logs/9.3.runTests.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Test result
    | Total | Passed | Failed | Skipped | Errors |
    |-------|--------|--------|---------|--------|
    | 0 | 0 | 0 | 0 | 0 |
    </details>
  </details>

  ### ✅ Summarize Upgrade
  - [[View Log]](logs/10.summarizeUpgrade.log)