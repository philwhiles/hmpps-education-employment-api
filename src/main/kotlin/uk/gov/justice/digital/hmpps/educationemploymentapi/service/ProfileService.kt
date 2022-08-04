package uk.gov.justice.digital.hmpps.educationemploymentapi.service

import kotlinx.coroutines.flow.first
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.educationemploymentapi.config.AlreadyExistsException
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.Profile
import uk.gov.justice.digital.hmpps.educationemploymentapi.persistence.model.ReadinessProfile
import uk.gov.justice.digital.hmpps.educationemploymentapi.persistence.model.ReadinessProfileFilter
import uk.gov.justice.digital.hmpps.educationemploymentapi.persistence.repository.ReadinessProfileRespository

@Service
class ProfileService(
  private val readinessProfileRepository: ReadinessProfileRespository
) {
  suspend fun createProfileForOffender(offenderId: String, bookingId: Int, profile: Profile): ReadinessProfile {
    if (readinessProfileRepository.existsById(offenderId)) {
      throw AlreadyExistsException(offenderId)
    }
    return readinessProfileRepository.save(ReadinessProfile(offenderId, bookingId, "todo_from_auth", profile, true))
  }

  suspend fun updateProfileForOffender(offenderId: String, bookingId: Int, profile: Profile): ReadinessProfile {
    return readinessProfileRepository.save(ReadinessProfile(offenderId, bookingId, "todo_from_auth", profile, false))
  }
  suspend fun getProfilesForOffenders(offenders: List<String>) = readinessProfileRepository.findForGivenOffenders(ReadinessProfileFilter(offenders))

  suspend fun getProfileForOffender(offenderId: String): ReadinessProfile = readinessProfileRepository.findForGivenOffenders(ReadinessProfileFilter(listOf(offenderId))).first()

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
