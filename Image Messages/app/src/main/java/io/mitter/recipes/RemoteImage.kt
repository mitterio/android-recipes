package io.mitter.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class RemoteImage(
    @JsonProperty("link") val link: String,
    @JsonProperty("repr") val repr: List<String>
)
