package com.health.controller;

import com.health.entity.User;
import com.health.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing user profile data (biometrics).
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);

        return userRepository.findById(userId)
            .map(user -> {
                java.util.Map<String, Object> response = new java.util.HashMap<>();
                response.put("id", user.getId());
                response.put("height", user.getHeight() != null ? user.getHeight() : "");
                response.put("weight", user.getWeight() != null ? user.getWeight() : "");
                response.put("age", user.getAge() != null ? user.getAge() : "");
                response.put("gender", user.getGender() != null ? user.getGender() : "Male");
                response.put("sexualActivityFreq", user.getSexualActivityFreq() != null ? user.getSexualActivityFreq() : "");
                // Return null if no testosterone score - don't fake 640
                response.put("testosteroneScore", user.getTestosteroneScore());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveProfile(@RequestBody Map<String, Object> profileData, HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);

        return userRepository.findById(userId)
            .map(user -> {
                if (profileData.containsKey("height")) {
                    user.setHeight(String.valueOf(profileData.get("height")));
                }
                if (profileData.containsKey("weight")) {
                    user.setWeight(String.valueOf(profileData.get("weight")));
                }
                if (profileData.containsKey("age")) {
                    user.setAge(String.valueOf(profileData.get("age")));
                }
                if (profileData.containsKey("gender")) {
                    user.setGender(String.valueOf(profileData.get("gender")));
                }
                if (profileData.containsKey("sexualActivityFreq")) {
                    user.setSexualActivityFreq(String.valueOf(profileData.get("sexualActivityFreq")));
                }
                if (profileData.containsKey("testosteroneScore")) {
                    try {
                        user.setTestosteroneScore(Double.valueOf(String.valueOf(profileData.get("testosteroneScore"))));
                    } catch (NumberFormatException e) {
                        // Ignore invalid testosterone score
                    }
                }
                
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Profile saved successfully"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
