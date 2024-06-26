package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Id;
import seedu.address.model.person.Person;
import seedu.address.model.person.YearJoined;
import seedu.address.model.transaction.Transaction;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final PayBack payBack;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Transaction> filteredTransactions;
    private Person lastMentionedPerson;

    /**
     * Initializes a ModelManager with the given PayBack and userPrefs.
     */
    public ModelManager(ReadOnlyPayBack payBack, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(payBack, userPrefs);

        logger.fine("Initializing with PayBack: " + payBack + " and user prefs " + userPrefs);

        this.payBack = new PayBack(payBack);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.payBack.getPersonList());
        filteredTransactions = new FilteredList<>(this.payBack.getTransactionList());
    }

    public ModelManager() {
        this(new PayBack(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getPayBackFilePath() {
        return userPrefs.getPayBackFilePath();
    }

    @Override
    public void setPayBackFilePath(Path payBackFilePath) {
        requireNonNull(payBackFilePath);
        userPrefs.setPayBackFilePath(payBackFilePath);
    }

    //=========== PayBack ================================================================================

    @Override
    public void setPayBack(ReadOnlyPayBack payBack) {
        this.payBack.resetData(payBack);
    }

    @Override
    public ReadOnlyPayBack getPayBack() {
        return payBack;
    }

    //=========== Person =================================================================================

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return payBack.hasPerson(person);
    }

    @Override
    public List<Person> getDuplicatePersons(Person person) {
        requireNonNull(person);
        return payBack.getDuplicatePersons(person);
    }

    @Override
    public boolean hasPersonId(Id id) {
        requireNonNull(id);
        return payBack.hasPersonId(id);
    }

    @Override
    public void deletePerson(Person target) {
        payBack.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        payBack.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        payBack.setPerson(target, editedPerson);
    }

    @Override
    public int getLastIdOnYear(YearJoined yearJoined) {
        requireNonNull(yearJoined);
        return payBack.getLastIdOnYear(yearJoined);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedPayBack}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Transaction ============================================================================

    @Override
    public boolean hasTransaction(Transaction transaction) {
        requireNonNull(transaction);
        return payBack.hasTransaction(transaction);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        payBack.addTransaction(transaction);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        payBack.removeTransaction(transaction);
    }

    //=========== Filtered Transaction List Accessors ========================================================

    /**
     * Returns an unmodifiable view of the list of {@code Transaction} backed by the internal list of
     * {@code versionedPayBack}
     */
    @Override
    public ObservableList<Transaction> getFilteredTransactionList() {
        return filteredTransactions;
    }

    @Override
    public void updateFilteredTransactionList(Predicate<Transaction> predicate) {
        requireNonNull(predicate);
        filteredTransactions.setPredicate(predicate);
    }

    @Override
    public void setLastMentionedPerson(Person person) {
        this.lastMentionedPerson = person;
    }
    @Override
    public Person getLastMentionedPerson() {
        return lastMentionedPerson;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return payBack.equals(otherModelManager.payBack)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredTransactions.equals(otherModelManager.filteredTransactions);
    }

}
