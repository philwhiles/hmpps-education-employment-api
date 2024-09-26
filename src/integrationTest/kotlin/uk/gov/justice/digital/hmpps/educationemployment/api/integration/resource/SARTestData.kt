package uk.gov.justice.digital.hmpps.educationemployment.api.integration.resource

import uk.gov.justice.digital.hmpps.educationemployment.api.integration.util.TestData

object SARTestData {
  val knownPRN = "A1234BB"
  val bookingIdIfKnownPRN = TestData.newBookingId

  val unknownPRN = "A1234BD"

  val knownCRN = "X08769"
}

const val SAR_ROLE = "ROLE_SAR_DATA_ACCESS"
const val INCORRECT_SAR_ROLE = "WRONG_SAR_DATA_ACCESS"
