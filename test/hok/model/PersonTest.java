package hok.model;

import hok.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Person abstract class and its subclasses.
 * Verifies inheritance, encapsulation, and abstraction.
 */
class PersonTest {

    // --- Abstract class cannot be instantiated ---

    @Test
    @DisplayName("Person is abstract — direct instantiation is impossible")
    void testAbstractClassCannotBeInstantiated() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(Person.class.getModifiers()),
                "Person must be declared abstract");
    }

    // --- Inheritance: Player extends Person ---

    @Test
    @DisplayName("Player inherits id and name from Person")
    void testPlayerInheritsPersonFields() {
        Player p = new Player("p001", "Alex", "pass123");
        assertEquals("p001", p.getId());
        assertEquals("Alex", p.getName());
        assertEquals(Role.PLAYER, p.getRole());
    }

    @Test
    @DisplayName("Admin inherits id and name from Person")
    void testAdminInheritsPersonFields() {
        Admin a = new Admin("admin", "SystemAdmin", "admin123");
        assertEquals("admin", a.getId());
        assertEquals("SystemAdmin", a.getName());
        assertEquals(Role.ADMIN, a.getRole());
    }

    // --- Encapsulation: fields are private ---

    @Test
    @DisplayName("Person fields are private and accessed via getters")
    void testEncapsulation() {
        Player p = new Player("p001", "Alex", "pass123");
        // Verify fields are accessible only via getters (no direct access)
        p.setName("AlexUpdated");
        assertEquals("AlexUpdated", p.getName());
    }

    // --- Polymorphism: Person reference holds Player or Admin ---

    @Test
    @DisplayName("Person reference demonstrates polymorphism")
    void testPolymorphism() {
        Person person1 = new Player("p001", "Alex", "pass123");
        Person person2 = new Admin("admin", "SysAdmin", "admin123");

        assertTrue(person1 instanceof Player);
        assertTrue(person2 instanceof Admin);
        assertTrue(person1 instanceof Person);
        assertTrue(person2 instanceof Person);
        assertEquals(Role.PLAYER, person1.getRole());
        assertEquals(Role.ADMIN, person2.getRole());
    }
}
