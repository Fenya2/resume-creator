package course.work.model.resume;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserResumeTest {
    private static String YAML_RESUME = """
            about: Опытный Java-разработчик с 5-летним стажем
            additionalInfo: {hasCar: false, hasChildren: true, hasDriverLicence: true}
            contactInfo:
              email: user@example.com
              phone: '+79991234567'
              socialNetworks:
              - {link: 'https://t.me/username', type: TELEGRAM}
              - {link: 'https://vk.com/username', type: VK}
            generalInfo: {birthday: !!timestamp '1991-06-14T19:00:00Z', city: Москва, firstName: Иван,
              lastName: Иванов, photoId: photo123, requiredSalary: 150000}
            """;

    @Test
    void testYamlMarshalling() {
        UserResume resume = createTestResume();
        DumperOptions options = new DumperOptions();
        options.setExplicitStart(false);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Yaml yaml = new Yaml(new Constructor(UserResume.class, new LoaderOptions()));

        // 3. Преобразуем объект в YAML
        String yamlString = yaml.dumpAs(resume, Tag.MAP, null);
        assertEquals(YAML_RESUME, yamlString);
    }

    @Test
    void testYamlDemarshaling() {
        Yaml yaml = new Yaml(new Constructor(UserResume.class, new LoaderOptions()));

        UserResume parsedResume = yaml.load(YAML_RESUME);

        assertNotNull(parsedResume, "Объект не должен быть null");

        assertEquals("Опытный Java-разработчик с 5-летним стажем", parsedResume.getAbout());

        ResumeGeneralInfo generalInfo = parsedResume.getGeneralInfo();
        assertNotNull(generalInfo);
        assertEquals("Иван", generalInfo.getFirstName());
        assertEquals("Иванов", generalInfo.getLastName());
        assertEquals(new Date(1991 - 1900, 5, 15), generalInfo.getBirthday());
        assertEquals("Москва", generalInfo.getCity());
        assertEquals(150000, generalInfo.getRequiredSalary());
        assertEquals("photo123", generalInfo.getPhotoId());

        ContactInfo contactInfo = parsedResume.getContactInfo();
        assertNotNull(contactInfo);
        assertEquals("user@example.com", contactInfo.getEmail());
        assertEquals("+79991234567", contactInfo.getPhone());

        List<SocialNetwork> socialNetworks = contactInfo.getSocialNetworks();
        assertEquals(2, socialNetworks.size(), "Должно быть 2 соцсети");

        SocialNetwork telegram = socialNetworks.get(0);
        assertEquals(SocialNetworkType.TELEGRAM, telegram.getType());
        assertEquals("https://t.me/username", telegram.getLink());

        SocialNetwork vk = socialNetworks.get(1);
        assertEquals(SocialNetworkType.VK, vk.getType());
        assertEquals("https://vk.com/username", vk.getLink());

        ResumeAdditionalInfo additionalInfo = parsedResume.getAdditionalInfo();
        assertNotNull(additionalInfo);
        assertTrue(additionalInfo.isHasDriverLicence());
        assertFalse(additionalInfo.isHasCar());
        assertTrue(additionalInfo.isHasChildren());
    }

    private static UserResume createTestResume() {
        SocialNetwork telegram = new SocialNetwork();
        telegram.setType(SocialNetworkType.TELEGRAM);
        telegram.setLink("https://t.me/username");

        SocialNetwork vk = new SocialNetwork();
        vk.setType(SocialNetworkType.VK);
        vk.setLink("https://vk.com/username");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setPhone("+79991234567");
        contactInfo.setEmail("user@example.com");
        contactInfo.setSocialNetworks(List.of(telegram, vk));

        ResumeAdditionalInfo additionalInfo = new ResumeAdditionalInfo();
        additionalInfo.setHasDriverLicence(true);
        additionalInfo.setHasCar(false);
        additionalInfo.setHasChildren(true);

        ResumeGeneralInfo generalInfo = new ResumeGeneralInfo();
        generalInfo.setFirstName("Иван");
        generalInfo.setLastName("Иванов");
        generalInfo.setBirthday(new Date(91, 5, 15)); // Обратите внимание на месяц (5 = май)
        generalInfo.setCity("Москва");
        generalInfo.setRequiredSalary(150000);
        generalInfo.setPhotoId("photo123");

        UserResume resume = new UserResume();
        resume.setGeneralInfo(generalInfo);
        resume.setAdditionalInfo(additionalInfo);
        resume.setContactInfo(contactInfo);
        resume.setAbout("Опытный Java-разработчик с 5-летним стажем");

        return resume;
    }
}