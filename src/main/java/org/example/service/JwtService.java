package org.example.service;

import java.util.List;

public interface JwtService {

    String generateAccessToken(String username, List<String> roles);

    String generateRefreshToken(String username);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);

    String getUsernameFromAccessToken(String token);
    
    String getUsernameFromRefreshToken(String token);

    List<String> getRolesFromAccessToken(String token);

}
