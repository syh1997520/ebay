import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.homework.DemoApplication;
import com.homework.code.entity.User;
import com.homework.code.service.AccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DemoApplication.class)
class AccessServiceTest {

    @Autowired
    private AccessService accessService;

    @MockBean
    private ObjectMapper objectMapper;


    @Test
    void addUserAccess() {
        User user = new User(3L, "NewUser", "user");
        List<String> resources = Arrays.asList("resource B", "resource C");

        accessService.addUserAccess(user.getUserId(), resources);

        assertTrue(accessService.hasAccess(2L, "resource B"));
    }

    @Test
    void hasAccess() {
        assertFalse(accessService.hasAccess(1L, "non-existent resource"));
    }

}
