package uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile

import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime

@Validated
data class WorkExperience(
  val modifiedBy: String,
  val modifiedDateTime: LocalDateTime,

  val previousWorkOrVolunteering: String,
  val qualificationsAndTraining: List<QualificationsAndTraining>,
  val qualificationsAndTrainingOther: String
)
