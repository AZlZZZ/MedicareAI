package tn.esprit.medicareaitigerback.services.interfaces;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface IRecommendationDatasetService {

    Resource exportPatientFeatures() throws IOException;

    Resource exportMedicationFeatures() throws IOException;

    Resource exportPatientMedicationInteractions() throws IOException;
}
