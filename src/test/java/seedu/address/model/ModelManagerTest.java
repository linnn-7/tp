package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.exceptions.TransactionNotFoundException;
import seedu.address.testutil.PayBackBuilder;
import seedu.address.testutil.TransactionBuilder;
import seedu.address.testutil.TypicalTransactions;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new PayBack(), new PayBack(modelManager.getPayBack()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setPayBackFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setPayBackFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setPayBackFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setPayBackFilePath(null));
    }

    @Test
    public void setPayBackFilePath_validPath_setsPayBackFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setPayBackFilePath(path);
        assertEquals(path, modelManager.getPayBackFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInPayBack_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInPayBack_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasTransaction_nullTransaction_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasTransaction(null));
    }

    @Test
    public void hasTransaction_transactionNotInPayBack_returnsFalse() {
        assertFalse(modelManager.hasTransaction(TypicalTransactions.TRANSACTION_1));
    }

    @Test
    public void hasTransaction_transactionInPayBack_returnsTrue() {
        modelManager.addTransaction(TypicalTransactions.TRANSACTION_1);
        assertTrue(modelManager.hasTransaction(TypicalTransactions.TRANSACTION_1));
    }

    @Test
    public void getFilteredTransactionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredTransactionList().remove(0));
    }

    @Test
    public void updateFilteredTransactionList_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateFilteredTransactionList(null));
    }

    @Test
    public void addTransaction_transactionNotAlreadyInPayBack_transactionAddedAndFilteredListUpdated() {
        // Add a transaction
        Transaction transactionToAdd = new TransactionBuilder().build();
        modelManager.addTransaction(transactionToAdd);

        // Verify that the transaction is added to the PayBack
        PayBack expectedPayBack = new PayBack();
        expectedPayBack.addTransaction(transactionToAdd);
        assertEquals(expectedPayBack, modelManager.getPayBack());

        // Verify that the filtered transaction list is updated to show all transactions
        assertEquals(expectedPayBack.getTransactionList(), modelManager.getFilteredTransactionList());
    }

    @Test
    public void deleteTransaction_transactionInPayback_transactionDeleted() {
        Transaction transactionToRemove = new TransactionBuilder().build();
        modelManager.addTransaction(transactionToRemove);
        modelManager.deleteTransaction(transactionToRemove);

        PayBack expectedPayBack = new PayBack();

        assertEquals(expectedPayBack, modelManager.getPayBack());
    }

    @Test
    public void deleteTransaction_transactionNotInPayback_throwsTransactionNotFoundException() {
        Transaction transactionToRemove = new TransactionBuilder().build();

        assertThrows(TransactionNotFoundException.class, () -> modelManager.deleteTransaction(transactionToRemove));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        PayBack payBack = new PayBackBuilder().withPerson(ALICE).withPerson(BENSON).build();
        PayBack differentPayBack = new PayBack();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(payBack, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(payBack, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different payBack -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentPayBack, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(payBack, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setPayBackFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(payBack, differentUserPrefs)));
    }
}
