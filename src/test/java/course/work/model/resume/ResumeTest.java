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

class ResumeTest {
    private static final String YAML_RESUME = """
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
        Resume resume = createTestResume();
        DumperOptions options = new DumperOptions();
        options.setExplicitStart(false);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Yaml yaml = new Yaml(new Constructor(Resume.class, new LoaderOptions()));

        String yamlString = yaml.dumpAs(resume, Tag.MAP, null);
        assertEquals(YAML_RESUME, yamlString);
    }

    @Test
    void testYamlUnmarshaling() {
        Yaml yaml = new Yaml(new Constructor(Resume.class, new LoaderOptions()));

        Resume parsedResume = yaml.load(YAML_RESUME);

        assertNotNull(parsedResume, "Объект не должен быть null");

        assertEquals("Опытный Java-разработчик с 5-летним стажем", parsedResume.getAbout());

        Resume.GeneralInfo generalInfo = parsedResume.getGeneralInfo();
        assertNotNull(generalInfo);
        assertEquals("Иван", generalInfo.getFirstName());
        assertEquals("Иванов", generalInfo.getLastName());
        assertEquals(new Date(1991 - 1900, 5, 15), generalInfo.getBirthday());
        assertEquals("Москва", generalInfo.getCity());
        assertEquals(150000, generalInfo.getRequiredSalary());
        assertEquals("photo123", generalInfo.getPhotoId());

        Resume.ContactInfo contactInfo = parsedResume.getContactInfo();
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

        Resume.AdditionalInfo additionalInfo = parsedResume.getAdditionalInfo();
        assertNotNull(additionalInfo);
        assertTrue(additionalInfo.isHasDriverLicence());
        assertFalse(additionalInfo.isHasCar());
        assertTrue(additionalInfo.isHasChildren());
    }

    private static Resume createTestResume() {
        SocialNetwork telegram = new SocialNetwork();
        telegram.setType(SocialNetworkType.TELEGRAM);
        telegram.setLink("https://t.me/username");

        SocialNetwork vk = new SocialNetwork();
        vk.setType(SocialNetworkType.VK);
        vk.setLink("https://vk.com/username");

        Resume.ContactInfo contactInfo = new Resume.ContactInfo();
        contactInfo.setPhone("+79991234567");
        contactInfo.setEmail("user@example.com");
        contactInfo.setSocialNetworks(List.of(telegram, vk));

        Resume.AdditionalInfo additionalInfo = new Resume.AdditionalInfo();
        additionalInfo.setHasDriverLicence(true);
        additionalInfo.setHasCar(false);
        additionalInfo.setHasChildren(true);

        Resume.GeneralInfo generalInfo = new Resume.GeneralInfo();
        generalInfo.setFirstName("Иван");
        generalInfo.setLastName("Иванов");
        generalInfo.setBirthday(new Date(91, 5, 15));
        generalInfo.setCity("Москва");
        generalInfo.setRequiredSalary(150000);
        generalInfo.setPhotoId("photo123");

        Resume resume = new Resume();
        resume.setGeneralInfo(generalInfo);
        resume.setAdditionalInfo(additionalInfo);
        resume.setContactInfo(contactInfo);
        resume.setAbout("Опытный Java-разработчик с 5-летним стажем");

        return resume;
    }
}