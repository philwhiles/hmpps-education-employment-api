package uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile

import java.time.LocalDateTime

data class WorkImpacts(
  val author: String,
  val modifiedDateTime: LocalDateTime,

  val abilityToWorkImpactedBy: List<AbilityToWorkImpactedBy>,
  val caringResponsibilitiesFullTime: Boolean,
  val ableToManageMentalHealth: Boolean,
  val ableToManageDependencies: Boolean
)
