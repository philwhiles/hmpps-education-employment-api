package uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile

import java.time.LocalDateTime
import javax.validation.constraints.Size

data class WorkInterests(
  @field:Size(max = 32) val modifiedBy: String,
  val modifiedDateTime: LocalDateTime,
  val workTypesOfInterest: List<WorkTypesOfInterest>,
  @field:Size(max = 200) val workTypesOfInterestOther: String,
  @field:Size(max = 200) val jobOfParticularInterest: String
)
