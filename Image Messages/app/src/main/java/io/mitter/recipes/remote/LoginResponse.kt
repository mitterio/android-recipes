package io.mitter.recipes.remote

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class LoginResponse(
    @JsonProperty("mitterUserId") val userId: String,
    @JsonProperty("mitterUserAuthorization") val userAuth: String
)
