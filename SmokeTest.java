package petadoption;

import petadoption.db.PetDAO;
import petadoption.db.UserDAO;
import petadoption.exception.InvalidAdoptionException;
import petadoption.exception.PetNotFoundException;
import petadoption.model.AdoptablePet;
import petadoption.service.*;

/** Headless test for GitHub Actions: exercises JDBC, queue, thread and streams against MySQL. */
public class SmokeTest {
    static void check(boolean ok, String msg) {
        if (!ok) { System.err.println("FAIL: " + msg); System.exit(1); }
        System.out.println("PASS: " + msg);
    }

    public static void main(String[] args) throws Exception {
        UserDAO users = new UserDAO();
        PetDAO pets = new PetDAO();
        String u = "rohan";
        users.delete(u);
        pets.delete(101);

        users.register("Rohan Sharma", u, "secret123");
        check(users.login(u, "secret123"), "login with correct password");
        check(!users.login(u, "wrong"), "login with wrong password rejected");
        try { users.register("X", u, "p"); check(false, "duplicate username rejected"); }
        catch (IllegalArgumentException e) { check(true, "duplicate username rejected"); }

        pets.addPet(new AdoptablePet(101, "Bruno", "Dog", 2, 500));
        try { pets.addPet(new AdoptablePet(101, "Dup", "Cat", 1, 10)); check(false, "duplicate pet ID rejected"); }
        catch (IllegalArgumentException e) { check(true, "duplicate pet ID rejected"); }
        try { new AdoptablePet(5, "Neg", "Dog", -1, 10); check(false, "negative age rejected"); }
        catch (IllegalArgumentException e) { check(true, "negative age rejected"); }

        check(pets.findById(101).getName().equals("Bruno"), "search pet by ID");
        try { pets.findById(999); check(false, "missing pet -> not found"); }
        catch (PetNotFoundException e) { check(true, "missing pet -> not found"); }

        AdoptionQueue q = new AdoptionQueue();
        check(q.add(101), "request added to queue");
        AdoptionProcessor worker = new AdoptionProcessor(q, pets, 50, System.out::println, null);
        check(worker.getState() == Thread.State.NEW, "thread state NEW");
        worker.start();
        worker.join();
        check(worker.getState() == Thread.State.TERMINATED, "thread state TERMINATED");
        check(pets.findById(101).isAdopted(), "pet marked adopted in MySQL");

        try { pets.markAdopted(101); check(false, "repeat adoption rejected"); }
        catch (InvalidAdoptionException e) { check(true, "repeat adoption rejected"); }

        StatisticsTask st = new StatisticsTask(pets, System.out::println);
        st.run();
        check(st.getTotal() >= 1 && st.getAdopted() >= 1, "statistics computed with streams");

        pets.delete(101);
        users.delete(u);
        System.out.println("All tests passed.");
    }
}
