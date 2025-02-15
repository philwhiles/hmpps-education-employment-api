package uk.gov.justice.digital.hmpps.educationemployment.api.resource

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.isNull
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.ControllerAdvice
import uk.gov.justice.digital.hmpps.educationemployment.api.TestData
import uk.gov.justice.digital.hmpps.educationemployment.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.educationemployment.api.helpers.JwtAuthHelper
import uk.gov.justice.digital.hmpps.educationemployment.api.service.ProfileService

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@WebMvcTest(controllers = [SARResourceController::class])
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = [SARResourceController::class, CapturedSpringConfigValues::class])
@WebAppConfiguration
class SARResourceControllerTest {

  @MockitoBean
  private lateinit var profileService: ProfileService

  @Autowired
  private lateinit var mvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  var jwtAuthHelper: JwtAuthHelper = JwtAuthHelper()

  @BeforeEach
  fun reset() {
    reset(profileService)
    SecurityMockMvcConfigurers.springSecurity()

    mvc = MockMvcBuilders
      .standaloneSetup(SARResourceController(profileService))
      .setControllerAdvice(ControllerAdvice())
      .build()
  }

  @Test
  fun `Test Get profile of an Offender for SAR`() {
    whenever(profileService.getProfileForOffenderFilterByPeriod(any(), isNull(), isNull())).thenReturn(TestData.readinessProfileForSAR)

    val result = mvc.perform(
      get("/subject-access-request?prn=A1234AB").accept(APPLICATION_JSON).content(TestData.createProfileJsonRequest)
        .contentType(APPLICATION_JSON).param("oauth2User", "ssss")
        .headers((setAuthorisation(roles = listOf("ROLE_SAR_DATA_ACCESS")))),
    )
      .andExpect(status().isOk)
      .andExpect(content().contentType(APPLICATION_JSON))
      .andReturn()

    val jsonSARProfile = mapper.readTree(result.response.contentAsString)
    val jsonContent = jsonSARProfile.findPath("content")
    assertThat(jsonContent.isMissingNode).isFalse()
    assertThat(jsonContent.get("offenderId").textValue()).isEqualTo(TestData.newOffenderId)
    jsonContent.findPath("profileData").let { jsonProfile ->
      assertThat(jsonProfile.isMissingNode).isFalse()
      assertThat(jsonProfile.get("supportDeclined")).isNotEmpty
      assertThat(jsonProfile.get("supportDeclined_history")).isNotEmpty
      assertThat(jsonProfile.get("supportAccepted_history")).isNotEmpty
    }
    verify(profileService, times(1)).getProfileForOffenderFilterByPeriod(any(), isNull(), isNull())
  }

  internal fun setAuthorisation(
    user: String = "test-client",
    roles: List<String> = listOf(),
  ): (HttpHeaders) = jwtAuthHelper.setAuthorisationForUnitTests(user, roles)
}
