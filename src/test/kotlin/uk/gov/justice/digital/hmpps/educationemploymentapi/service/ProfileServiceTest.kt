package uk.gov.justice.digital.hmpps.educationemploymentapi.service

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.educationemploymentapi.config.AlreadyExistsException
import uk.gov.justice.digital.hmpps.educationemploymentapi.config.NotFoundException
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.AbilityToWorkImpactedBy
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.Action
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.ActionStatus
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.ActionTodo
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.ActionsRequired
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.CircumstanceChangesRequiredToWork
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.Profile
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.ProfileStatus
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.QualificationsAndTraining
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.SupportAccepted
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.SupportDeclined
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.SupportToWorkDeclinedReason
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.WorkExperience
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.WorkImpacts
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.WorkInterests
import uk.gov.justice.digital.hmpps.educationemploymentapi.data.jsonprofile.WorkTypesOfInterest
import uk.gov.justice.digital.hmpps.educationemploymentapi.persistence.model.ReadinessProfile
import uk.gov.justice.digital.hmpps.educationemploymentapi.persistence.repository.ReadinessProfileRespository
import java.time.LocalDateTime

class ProfileServiceTest {
  private val readinessProfileRepository: ReadinessProfileRespository = mock()
  private lateinit var profileService: ProfileService

  val modifiedTime = LocalDateTime.now()

  val booleanTrue = true
  val booleanFalse = false

  val createdBy = "sacintha-raj"
  val updatedBy = "phil-whils"
  val workTypesOfInterestOther = "freelance"
  val jobOfParticularInterests = "architect"
  val previousWorkOrVolunteering_NONE = "NONE"
  val qualificationAndTrainingOther = "MBA"
  val newOffenderId = "A1245BC"
  val updatedOffenderId = "A1245BC"
  val emptyString = ""
  val createdByString = "createdBy"
  val offenderIdString = "offenderId"
  val bookingIdString = "bookingId"
  val newNotes = "new notes"

  val newBookingId = 123456L
  val updatedBookingId = 123457L

  val action = Action(ActionTodo.BANK_ACCOUNT, ActionStatus.COMPLETED)

  val profileStatus_NO_RIGHT_TO_WORK = ProfileStatus.NO_RIGHT_TO_WORK

  val supportDeclinedReasonList = listOf(SupportToWorkDeclinedReason.FULL_TIME_CARER)
  val circumstanceChangesRequiredToWorkList = listOf(CircumstanceChangesRequiredToWork.DEPENDENCY_SUPPORT)
  val actionList = listOf(action)
  val abilityToWorkImpactedByList = listOf(AbilityToWorkImpactedBy.CARING_RESPONSIBILITIES)
  val workTypesOfInterestList = listOf(WorkTypesOfInterest.CONSTRUCTION)
  val qualificationsAndTrainingList = listOf(QualificationsAndTraining.ADVANCED_EDUCATION)

  val actionsRequired = ActionsRequired(
    updatedBy, modifiedTime, actionList
  )
  val workImpacts = WorkImpacts(
    updatedBy, modifiedTime, abilityToWorkImpactedByList, booleanTrue, booleanTrue, booleanTrue
  )
  val workInterests = WorkInterests(
    updatedBy, modifiedTime, workTypesOfInterestList, workTypesOfInterestOther, jobOfParticularInterests
  )
  val workExperience = WorkExperience(
    updatedBy,
    modifiedTime,
    previousWorkOrVolunteering_NONE,
    qualificationsAndTrainingList,
    qualificationAndTrainingOther
  )

  val supportDeclined: SupportDeclined = SupportDeclined(
    createdBy, modifiedTime, supportDeclinedReasonList, emptyString, circumstanceChangesRequiredToWorkList, emptyString
  )
  val supportAccepted: SupportAccepted = SupportAccepted(
    actionsRequired, workImpacts, workInterests, workExperience
  )
  val profile: Profile = Profile(profileStatus_NO_RIGHT_TO_WORK, supportDeclined, supportAccepted)
  val readinessProfile = ReadinessProfile(createdBy, newOffenderId, newBookingId, profile, booleanTrue)
  val updatedReadinessProfile = ReadinessProfile(updatedBy, newOffenderId, updatedBookingId, profile, booleanTrue)

  @BeforeEach
  fun beforeEach() {
    profileService = ProfileService(readinessProfileRepository)
  }
  @Test
  fun `makes a call to the repository to save the readiness profile`() = runTest {
    whenever(readinessProfileRepository.save(any())).thenReturn(readinessProfile)
    whenever(readinessProfileRepository.existsById(any())).thenReturn(booleanFalse)

    val rProfile = profileService.createProfileForOffender(createdBy, newOffenderId, newBookingId, profile)
    val argumentCaptor = ArgumentCaptor.forClass(ReadinessProfile::class.java)
    verify(readinessProfileRepository).save(argumentCaptor.capture())
    assertThat(rProfile).extracting(createdByString, offenderIdString, bookingIdString)
      .contains(createdBy, newOffenderId, newBookingId)
  }

  @Test
  fun `throws an exception when a call is made to the repository to save the readiness profile`() {
    assertThatExceptionOfType(AlreadyExistsException::class.java).isThrownBy {
      runBlocking {
        whenever(readinessProfileRepository.save(any())).thenReturn(readinessProfile)
        whenever(readinessProfileRepository.existsById(any())).thenReturn(booleanTrue)

        profileService.createProfileForOffender(createdBy, newOffenderId, newBookingId, profile)
      }
    }.withMessageContaining("Readiness profile already exists for offender")
  }

  @Test
  fun `makes a call to the repository to update the readiness profile`() = runTest {
    whenever(readinessProfileRepository.save(any())).thenReturn(updatedReadinessProfile)
    whenever(readinessProfileRepository.findById(any())).thenReturn(updatedReadinessProfile)

    val rProfile = profileService.updateProfileForOffender(createdBy, newOffenderId, newBookingId, profile)
    val argumentCaptor = ArgumentCaptor.forClass(ReadinessProfile::class.java)
    verify(readinessProfileRepository).save(argumentCaptor.capture())
    assertThat(rProfile).extracting(createdByString, offenderIdString, bookingIdString)
      .contains(updatedBy, newOffenderId, updatedBookingId)
  }

  @Test
  fun `throws an exception when a call is made to the repository to update the readiness profile`() {
    assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
      runBlocking {
        whenever(readinessProfileRepository.save(any())).thenReturn(readinessProfile)
        whenever(readinessProfileRepository.findById(any())).thenReturn(null)

        profileService.updateProfileForOffender(createdBy, newOffenderId, newBookingId, profile)
      }
    }.withMessageContaining("Readiness profile does not exist for offender")
  }

  @Test
  fun `throws an exception when a call is made to the repository to add a note to an non existing readiness profile`() {
    assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
      runBlocking {
//        whenever(readinessProfileRepository.save(any())).thenReturn(readinessProfile)
        whenever(readinessProfileRepository.findById(any())).thenReturn(null)

        val rProfile = profileService.addProfileNoteForOffender(createdBy, newOffenderId, ActionTodo.BANK_ACCOUNT, newNotes)
      }
    }.withMessageContaining("Readiness profile does not exist for offender")
  }
  @Test
  fun `throws an exception when a call is made to the repository to retreive a note from a non existing readiness profile`() {
    assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
      runBlocking {
        whenever(readinessProfileRepository.findById(any())).thenReturn(null)
        profileService.getProfileNotesForOffender(createdBy, ActionTodo.BANK_ACCOUNT)
      }
    }.withMessageContaining("Readiness profile does not exist for offender")
  }
}
