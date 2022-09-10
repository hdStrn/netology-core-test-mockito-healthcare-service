package ru.netology.patient.service.medical;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceTest {

    @BeforeAll
    public static void beforeTests() {
        System.out.println("Test of MedicalService started");
    }

    @Test
    public void testCheckBloodPressure_whenPressureIsNotEquals_thenSendMessage() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("666"))
                .thenReturn(new PatientInfo(
                        "666",
                        "Ivan",
                        "Ivanov",
                        LocalDate.of(2000, 1, 1),
                        new HealthInfo(new BigDecimal(36.6), new BloodPressure(160, 120))));
        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure("666", new BloodPressure(120, 80));

        Mockito.verify(alertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(alertService).send(argumentCaptor.capture());
        String expectedMessage = "Warning, patient with id: 666, need help";
        assertThat(expectedMessage, equalTo(argumentCaptor.getValue()));
    }

    @Test
    public void testCheckTemperature_whenTemperatureIsAboveLimit_thenSendMessage() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("999"))
                .thenReturn(new PatientInfo(
                        "999",
                        "Petr",
                        "Petrov",
                        LocalDate.of(2000, 1, 1),
                        new HealthInfo(new BigDecimal("38.5"), new BloodPressure(120, 80))));
        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkTemperature("999", new BigDecimal("36.6"));

        Mockito.verify(alertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(alertService).send(argumentCaptor.capture());
        String expectedMessage = "Warning, patient with id: 999, need help";
        assertThat(expectedMessage, equalTo(argumentCaptor.getValue()));
    }

    @Test
    public void testCheckParams_whenParamsAreOk_thenNoMessage() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("777"))
                .thenReturn(new PatientInfo(
                        "777",
                        "Petr",
                        "Petrov",
                        LocalDate.of(2000, 1, 1),
                        new HealthInfo(new BigDecimal("36.8"), new BloodPressure(120, 80))));
        SendAlertService alertService = Mockito.mock(SendAlertService.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure("777", new BloodPressure(120, 80));
        medicalService.checkTemperature("777", new BigDecimal("36.6"));

        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());
    }

    @AfterAll
    public static void afterTests() {
        System.out.println("\nTest of MedicalService completed");
    }
}
