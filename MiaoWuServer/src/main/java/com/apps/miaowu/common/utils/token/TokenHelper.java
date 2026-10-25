package com.apps.miaowu.common.utils.token;

public interface TokenHelper {
      
    /**
     * Create a token for the user id
     * @param id
     * @return
     */
    TokenModel create(String id);
    
    /**
     * Check whether the token exists
     * @param token
     * @return
     */
    boolean check(String token);
    
    /**
     * Extract the token from the auth string sent by the frontend
     * @param authStr
     * @return
     */
    TokenModel get(String authStr);
    
    /**
     * Delete the token of the user id
     * @param mode
     * @return
     */
    boolean delete(String id);
}