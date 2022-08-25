package pl.fissst.pubmed;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.fissst.pubmed.services.PubMedService;

@SpringBootTest
public class PubMedServiceTests {
    @Autowired
    private PubMedService service;


    @Test
    void getAuthorsTest() throws JsonProcessingException {

        assert service.getDoctors(" acne")!=null;

    }

}
