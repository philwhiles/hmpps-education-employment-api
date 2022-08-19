package uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile

import javax.validation.Valid

data class SupportAccepted(
  @field:Valid val actionsRequired: ActionsRequired,
  @field:Valid val workImpacts: WorkImpacts,
  @field:Valid val workInterests: WorkInterests,
  @field:Valid val workExperience: WorkExperience
)
