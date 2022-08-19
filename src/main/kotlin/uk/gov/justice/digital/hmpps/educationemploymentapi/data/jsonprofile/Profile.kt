package uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile

import javax.validation.Valid

data class Profile(
  @Valid val status: ProfileStatus,
  @field:Valid val supportDeclined: SupportDeclined?,
  @field:Valid val supportAccepted: SupportAccepted?
)
