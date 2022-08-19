package uk.gov.justice.digital.hmpps.educationemploymentapi.data

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.Profile
import javax.validation.Valid

data class ReadinessProfileRequestDTO(

  @Schema(description = "Booking Id", example = "12345678")
  val bookingId: Long,

  @field:Valid
  @Schema(description = "Work readiness profile JSON data", example = "{...}")
  val profileData: Profile
)
